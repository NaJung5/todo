package com.najung.todo.dto.request;

import com.najung.todo.dto.MemberDto;
import com.najung.todo.dto.TodoDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Todo 요청에 대한 정보를 담고 있는 DTO")
public record TodoRequest(
        @Schema(description = "할 일의 내용", example = "런닝 하기")
        String content,
        @Schema(description = "할 일의 완료 여부", example = "Y or N")
        String complete,
        @Schema(description = "할 일의 우선 순위", example = "L or M or H")
        String important,
        @Schema(description = "반복 횟수", example = "Y or N")
        int count,
        @Schema(description = "마감일", example = "2024-09-09")
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
