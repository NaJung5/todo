package com.najung.todo.dto.request;

import com.najung.todo.dto.MemberDto;
import com.najung.todo.dto.TodoDto;

public record TodoRequest(
        String content,
        String complete,
        String important
) {

    public static TodoRequest of(String content, String complete, String important){
        return new TodoRequest(content, complete, important);
    }



    public TodoDto toDto(MemberDto memberDto){
        return TodoDto.of(
                memberDto,
                content,
                complete,
                important
        );
    }


}
