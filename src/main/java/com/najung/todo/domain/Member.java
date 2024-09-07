package com.najung.todo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Table
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sno; // 회원 번호

    @Setter
    @Column(length = 50)
    private String userId;

    @Setter
    @Column(nullable = false)
    private String userPassword;

    @Setter
    @Column(nullable = false)
    private String nickname;

    protected Member() {
    }


    private Member(String userId, String userPassword, String nickname) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
    }

    public static Member of(String userId, String userPassword, String nickname) {
        return new Member(userId, userPassword, nickname);
    }
}
