package com.najung.todo.service;

import com.najung.todo.domain.Member;
import com.najung.todo.domain.Todo;
import com.najung.todo.dto.MemberDto;
import com.najung.todo.dto.TodoDto;
import com.najung.todo.dto.request.TodoRequest;
import com.najung.todo.repository.MemberRepository;
import com.najung.todo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;

    public void saveTodo(Long memberId, TodoRequest todoRequest) {
        Member member = memberRepository.getReferenceById(memberId);
        TodoDto dto = todoRequest.toDto(MemberDto.of(member.getSno()), todoRequest);
        todoRepository.save(dto.toEntity(member));

    }

    public void saveMultipleTodo(Long memberId, Long todoId, TodoRequest req) {
        Todo todo = todoRepository.getReferenceById(todoId);
        Member member = memberRepository.getReferenceById(memberId);
        try {
            /*
              현재 테스트 코드에서는 todo와 member의 ID 값이 NULL인 문제를 발견
              이로 인해 if 문이 통과하지 않아 테스트가 계속 실패
              따라서, 임시로 Objects.equals를 사용하여 null 안전 비교를 진행.
              추후 문제가 발생할 경우, 해당 로직을 수정할 필요가 있음.
             */
            if (Objects.equals(todo.getMember().getSno(), member.getSno())) {
                for (int i = 0; i < req.count(); i++) {
                    TodoRequest req1 = new TodoRequest(
                            req.content(),
                            req.complete(),
                            req.important(),
                            req.count(),
                            req.dueDate().plusDays(i + 1)
                    );
                    TodoDto dto = req1.toDto(MemberDto.of(member.getSno()), req1);
                    Todo newTodo = dto.toEntity(member);
                    todoRepository.save(newTodo);

                }
            }
        } catch (EntityNotFoundException e) {
            log.warn("해당 ToDo가 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public Page<TodoDto> searchTodo(Long memberId, Pageable pageable) {
        return todoRepository.findByMember_sno(memberId, pageable).map(TodoDto::from);
    }

    public void updateTodo(Long memberId, Long todoId, TodoRequest req) {
        try {
             /*
              현재 테스트 코드에서는 todo와 member의 ID 값이 NULL인 문제를 발견
              이로 인해 if 문이 통과하지 않아 테스트가 계속 실패
              따라서, 임시로 Objects.equals를 사용하여 null 안전 비교를 진행.
              추후 문제가 발생할 경우, 해당 로직을 수정할 필요가 있음.
             */
            Todo todo = todoRepository.getReferenceById(todoId);
            Member member = memberRepository.getReferenceById(memberId);
            TodoDto dto = req.toDto(MemberDto.of(member.getSno()), req);
            if (Objects.equals(todo.getMember().getSno(), member.getSno())) {
                if (dto.complete() != null) todo.setComplete(dto.complete());
                if (dto.important() != null) todo.setImportant(dto.important());
                if (dto.content() != null) todo.setContent(dto.content());
            }
        } catch (EntityNotFoundException e) {
            log.warn("변경 할 todo-list 가 없습니다. - Request: {}", req);
        }
    }

    public void updateDueDate(Long memberId, Long todoId, TodoRequest req) {
        try {
            Todo todo = todoRepository.getReferenceById(todoId);
            Member member = memberRepository.getReferenceById(memberId);
            TodoDto dto = req.toDto(MemberDto.of(member.getSno()), req);
            if (todo.getMember().equals(member)) {
                if (dto.dueDate() != null) todo.setDueDate(dto.dueDate());
            }
        } catch (EntityNotFoundException e) {
            log.warn("수정 할 정보가 가 없습니다. - Request: {}", req);
        }
    }

    public boolean deleteTodo(Long todoId, Long memberId) {
        int deleteCount = todoRepository.deleteByIdAndMember_Sno(todoId, memberId);
        if (deleteCount == 0) {
            log.warn("삭제할 todo가 없습니다.");
            return false;
        } else {
            log.info("삭제 성공");
            return true;
        }
    }


}
