package com.najung.todo.dto;

import com.najung.todo.domain.Member;

/**
 * DTO for {@link Member}
 */
public record MemberDto(
        Long sno
) {
    public static MemberDto of(Long sno) {
        return new MemberDto(sno);
    }

    public static MemberDto from(Member entity) {
        return new MemberDto(
                entity.getSno()
        );
    }


}