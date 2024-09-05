package com.najung.todo.dto.response;

import com.najung.todo.dto.TodoDto;

import java.time.LocalDateTime;

public record TodoResponse(
        Long id,
        String content,
        String complete,
        String important,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static TodoResponse from(TodoDto dto) {
        return new TodoResponse(
                dto.id(),
                dto.content(),
                dto.complete(),
                dto.important(),
                dto.createdAt(),
                dto.modifiedAt()
        );
    }
}
