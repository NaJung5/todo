package com.najung.todo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Table
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    public RefreshToken(Member member, String token) {
        this.member = member;
        this.token = token;
    }

    protected RefreshToken() {}

}