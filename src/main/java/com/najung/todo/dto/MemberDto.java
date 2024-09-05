package com.najung.todo.dto;

import com.najung.todo.domain.Member;

/**
 * DTO for {@link Member}
 */
public record MemberDto(
        Long sno,
        String nickname
) {
    public static MemberDto of(Long id, String nickname) {
        return new MemberDto(id, nickname);
    }

    public static MemberDto from(Member entity) {
        return new MemberDto(
                entity.getSno(),
                entity.getNickname()
        );
    }

    public Member toEntity(){
        return Member.of(
                "najung",
                "1234",
                "najung"

        );
    }
}