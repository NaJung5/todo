package com.najung.todo.service;

import com.najung.todo.domain.Todo;
import com.najung.todo.domain.Member;
import com.najung.todo.dto.MemberDto;
import com.najung.todo.dto.TodoDto;
import com.najung.todo.dto.request.TodoRequest;
import com.najung.todo.repository.TodoRepository;
import com.najung.todo.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public Page<TodoDto> searchTodo(Long memberId, Pageable pageable){
        return todoRepository.findByMember_sno(memberId, pageable).map(TodoDto::from);
    }

    public void updateTodo(Long todoId, Long memberId, TodoRequest req) {
        try {
            Todo todo = todoRepository.getReferenceById(todoId);
            Member member = memberRepository.getReferenceById(memberId);
            TodoDto dto = req.toDto(MemberDto.of(member.getSno()), req);

            if (todo.getMember().equals(member)) {
                if (dto.complete() != null) todo.setComplete(dto.complete());
                if (dto.important() != null) todo.setImportant(dto.important());
                if (dto.content() != null) todo.setContent(dto.content());
            }
        } catch (EntityNotFoundException e) {
            log.warn("변경 할 todo-list 가 없습니다. - Request: {}", req);
        }
    }

    public void updateDueDate(Long todoId, Long memberId, TodoRequest req) {
        try {
            Todo todo = todoRepository.getReferenceById(todoId);
            Member member = memberRepository.getReferenceById(memberId);
            TodoDto dto = req.toDto(MemberDto.of(member.getSno()), req);

            if(todo.getMember().equals(member)){
                if (dto.dueDate() != null) todo.setDueDate(dto.dueDate());
            }
            log.info("update after: {}", todo.getDueDate());
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

    /**
     * 반복
     * 선택 된 아이템을 특정(Ex 1주일)기간 선택시 해당일 기준 1주일간 동일한 일정이 추가됨
     * 아이템의 번호를 받아 반복 등록 create
     */
}
