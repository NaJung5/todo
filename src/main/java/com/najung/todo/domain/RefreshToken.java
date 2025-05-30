package com.najung.todo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Table
@Entity
public class RefreshToken {
    @Id
    private String userId;

    @Column(nullable = false)
    private String token;

    private LocalDateTime expiryDate;

    protected RefreshToken(){

    }

    public RefreshToken(String userId, String token, LocalDateTime expiryDate) {
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
    }

}