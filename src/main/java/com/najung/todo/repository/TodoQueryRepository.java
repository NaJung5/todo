package com.najung.todo.repository;

import com.najung.todo.domain.Todo;
import com.najung.todo.dto.request.TodoSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoQueryRepository  {
    Page<Todo> searchTodos(Long memberSno, TodoSearchRequest todoSearchRequest, Pageable pageable);


}
