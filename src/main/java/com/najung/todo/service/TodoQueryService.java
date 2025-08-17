package com.najung.todo.service;

import com.najung.todo.domain.Todo;
import com.najung.todo.dto.TodoDto;
import com.najung.todo.dto.request.TodoSearchRequest;
import com.najung.todo.repository.TodoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TodoQueryService {

    private final TodoQueryRepository todoQueryRepository;

    @Async("ioPool")
    public CompletableFuture<Page<TodoDto>> searchAsync(Long memberSno, TodoSearchRequest req, Pageable pageable) {
        Page<Todo> page = todoQueryRepository.searchTodos(memberSno, req, pageable);
        return CompletableFuture.completedFuture(page.map(TodoDto::from));
    }
}
