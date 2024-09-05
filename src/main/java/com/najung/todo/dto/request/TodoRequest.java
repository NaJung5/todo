package com.najung.todo.dto.request;

import com.najung.todo.dto.TodoDto;

import java.time.LocalDateTime;

public record TodoRequest(
        String content,
        String complete,
        String important
) {

    public static TodoRequest of(String content, String complete, String important){
        return new TodoRequest(content, complete, important);
    }

//    public TodoDto toDto

}
