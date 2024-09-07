package com.najung.todo.dto.request;

import com.najung.todo.dto.MemberDto;
import com.najung.todo.dto.TodoDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodoRequest(
        String content,
        String complete,
        String important,
        int count,
        LocalDate dueDate
) {

    public LocalDateTime toLocalDateTime(){
        return dueDate.atStartOfDay();
    }

    public static TodoRequest of(String content, String complete, String important, LocalDate dueDate, int count){
        return new TodoRequest(content, complete, important, count, dueDate);
    }



    public TodoDto toDto(MemberDto memberDto, TodoRequest req){
        return TodoDto.of(
                memberDto,
                content != null ? content : req.content(),
                complete != null ? complete : req.complete(),
                important!= null ? important : req.important(),
                dueDate != null ? toLocalDateTime() : LocalDateTime.now()
        );
    }


}
