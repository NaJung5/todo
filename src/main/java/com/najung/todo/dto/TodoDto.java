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
        LocalDateTime modifiedAt
) {
    public static TodoDto of(Long id, MemberDto memberDto, String content, String complete, String important) {
        return new TodoDto(id, memberDto, content, complete, important, LocalDateTime.now(), null);
    }

    public static TodoDto from(Todo entity) {
        return new TodoDto(
                entity.getId(),
                MemberDto.from(entity.getMember()),
                entity.getContent(),
                entity.getComplete(),
                entity.getImportant(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }

    public Todo toEntity(){
        return Todo.of(
                memberDto.toEntity(),
                content,
                complete,
                important
        );
    }



}
