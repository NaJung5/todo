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

import java.util.Objects;

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
        todoRepository.save(dto.toEntity());

    }

    public void updateTodo(TodoDto dto) {
        try {
            Todo todo = todoRepository.getReferenceById(dto.id());
            if (dto.complete() != null) todo.setComplete(dto.complete());
            if (dto.important() != null) todo.setImportant(dto.important());
            if (dto.content() != null) todo.setContent(dto.content());
        } catch (EntityNotFoundException e) {
            log.warn("변경 할 todo-list 가 없습니다. - dto: {}", dto);
        }
    }

    public void deleteTodo(long todoId) {
        todoRepository.deleteById(todoId);
    }
}
