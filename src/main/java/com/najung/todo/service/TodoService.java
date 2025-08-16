package com.najung.todo.service;

import com.najung.todo.domain.Member;
import com.najung.todo.domain.Todo;
import com.najung.todo.dto.MemberDto;
import com.najung.todo.dto.TodoDto;
import com.najung.todo.dto.request.TodoRequest;
import com.najung.todo.dto.request.TodoSearchRequest;
import com.najung.todo.repository.MemberRepository;
import com.najung.todo.repository.TodoQueryRepository;
import com.najung.todo.repository.TodoRepository;
import com.najung.todo.util.LogFormatter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class TodoService {

    private static final int MAX_RETRY = 2;

    private final TodoRepository todoRepository;
    private final TodoQueryRepository todoQueryRepository;
    private final MemberRepository memberRepository;
    private final LogService logService;

    @Transactional
    public void saveTodo(Long memberId, TodoRequest todoRequest) {
        Member member = memberRepository.getReferenceById(memberId);
        TodoDto dto = todoRequest.toDto(MemberDto.of(member.getId()), todoRequest);
        LocalDate startDate = todoRequest.startDate() != null ? todoRequest.startDate() : LocalDate.now();
        LocalDate endDate = todoRequest.endDate() != null ? todoRequest.endDate() : startDate;
        LocalDateTime dueDate = todoRequest.dueDate() != null ? todoRequest.dueDate() : startDate.atTime(LocalTime.MAX);

        if (!startDate.isAfter(endDate)) {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                TodoDto newDto = TodoDto.of(
                        dto.memberDto(),
                        dto.content(),
                        dto.complete(),
                        dto.important(),
                        dto.startDate(),
                        dto.endDate(),
                        dueDate
                );
                todoRepository.save(newDto.toEntity(member));

                String log = LogFormatter.formatCreateLog(newDto);
                logService.saveTodoLog(memberId, "CREATE", log);
            }
        } else {
            throw new IllegalArgumentException("시작일은 종료일보다 이후일 수 없습니다.");
        }
    }


    @Transactional(readOnly = true)
    public Page<TodoDto> searchTodo(Long memberId, TodoSearchRequest todoSearchRequest, Pageable pageable) {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 입니다.");
        }
        return todoQueryRepository.searchTodos(memberId, todoSearchRequest, pageable).map(TodoDto::from);
    }

    public void updateTodoWithRetry(Long memberId, Long todoId, TodoRequest req) {
        int attempt = 0;
        while (true) {
            try {
                updateTodo(memberId, todoId, req);
                return;
            } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                if (attempt++ == MAX_RETRY) throw e;
                try {
                    Thread.sleep(50L * (attempt + 1));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    @Transactional
    public void updateTodo(Long memberId, Long todoId, TodoRequest req) {
        try {
            Todo todo = todoRepository.getReferenceById(todoId);
            checkOwner(todo, memberId);
            Todo copyTodo = Todo.copy(todo);
            TodoDto dto = req.toDto(MemberDto.of(memberId), req);

            if (dto.content() != null) todo.setContent(dto.content());
            if (dto.complete() != null) todo.setComplete(dto.complete());
            if (dto.important() != null) todo.setImportant(dto.important());

            String log = LogFormatter.formatUpdateLog(copyTodo, todo);
            logService.saveTodoLog(memberId, "UPDATE", log);


        } catch (EntityNotFoundException e) {
            log.warn("수정할 Todo가 존재하지 않습니다. todoId: {}, memberId: {}", todoId, memberId);
        }
    }


    public void updateDueDate(Long memberId, Long todoId, TodoRequest req) {
        try {
            Todo todo = todoRepository.getReferenceById(todoId);
            checkOwner(todo, memberId);

            TodoDto dto = req.toDto(MemberDto.of(memberId), req);
            Todo copyTodo = Todo.copy(todo);
            if (dto.dueDate() != null) {
                LocalDateTime dueDate = dto.dueDate();
                LocalDate localDate = dueDate.toLocalDate();
                todo.setDueDate(dueDate);
                todo.setStartDate(localDate);
                todo.setEndDate(localDate);

                String log = LogFormatter.formatUpdateLog(copyTodo, todo);
                logService.saveTodoLog(memberId, "UPDATE", log);
            }

        } catch (EntityNotFoundException e) {
            log.warn("수정할 Todo가 존재하지 않습니다. todoId: {}, memberId: {}", todoId, memberId);
        }
    }


    public boolean deleteTodo(Long todoId, Long memberId) {
        Optional<Todo> todo = todoRepository.findByIdAndMember_Id(todoId, memberId);

        if (todo.isEmpty()) {
            log.warn("삭제할 todo가 없습니다. todoId={}, memberId={}", todoId, memberId);
            return false;
        }

        Todo deleteTodo = todo.get();

        todoRepository.delete(deleteTodo);

        String log = LogFormatter.formatDeleteLog(deleteTodo);
        logService.saveTodoLog(memberId, "DELETE", log);

        return true;
    }


    private void checkOwner(Todo todo, Long memberId) {
        if (!todo.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 작업에 대한 권한이 없습니다.");
        }
    }


}
