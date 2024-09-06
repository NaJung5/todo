package com.najung.todo.dto;

import com.najung.todo.domain.Todo;
import com.najung.todo.domain.Member;

import java.time.LocalDateTime;

public record TodoDto(
        Long id,
        MemberDto memberDto,
        String content,
        String complete,
        String important,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        LocalDateTime dueDate
) {
    public static TodoDto of(Long id, MemberDto memberDto, String content, String complete, String important,LocalDateTime dueDate) {
        return new TodoDto(id, memberDto, content, complete, important, LocalDateTime.now(), null, dueDate);
    }
    public static TodoDto of(MemberDto memberDto, String content, String complete, String important, LocalDateTime dueDate) {
        return new TodoDto(null, memberDto, content, complete, important, LocalDateTime.now(), null, dueDate);
    }
    public static TodoDto from(Todo entity) {
        return new TodoDto(
                entity.getId(),
                MemberDto.from(entity.getMember()),
                entity.getContent(),
                entity.getComplete(),
                entity.getImportant(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDueDate()
        );
    }

    public Todo toEntity(Member member){
        return Todo.of(
                member,
                content,
                complete,
                important,
                dueDate
        );
    }



}
