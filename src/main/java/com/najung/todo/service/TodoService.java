package com.najung.todo.service;

import com.najung.todo.domain.Todo;
import com.najung.todo.domain.Member;
import com.najung.todo.dto.TodoDto;
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

    @Transactional(readOnly = true)
    public Page<TodoDto> searchTodo(Long memberId, Pageable pageable){
        return todoRepository.findByMember_sno(memberId, pageable).map(TodoDto::from);
    }


    public void saveTodo(TodoDto dto) {
        Member member = memberRepository.getReferenceById(dto.memberDto().sno());
        todoRepository.save(dto.toEntity(member));

    }

    public void updateTodo(Long todoId, TodoDto dto) {
        try {
            Todo todo = todoRepository.getReferenceById(todoId);
            Member member = memberRepository.getReferenceById(dto.memberDto().sno());
            if(todo.getMember().equals(member)){
                if (dto.complete() != null) todo.setComplete(dto.complete());
                if (dto.important() != null) todo.setImportant(dto.important());
                if (dto.content() != null) todo.setContent(dto.content());
            }
        } catch (EntityNotFoundException e) {
            log.warn("변경 할 todo-list 가 없습니다. - dto: {}", dto);
        }
    }

    public void deleteTodo(Long todoId, Long memberId) {
        todoRepository.findByIdAndMember_sno(todoId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 정보 입니다. 삭제에 실패했습니다."));
        todoRepository.deleteById(todoId);
    }
}
