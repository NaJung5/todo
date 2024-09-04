package com.najung.todo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@ToString
@Table
@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String content; // 내용
    @Setter
    @Column(nullable = false)
    private String complete; // 완료여부
    @Setter
    @Column(nullable = false)
    private String important; // 중요도

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일자

    @CreatedBy
    @Column(nullable = false, length = 100, updatable = false)
    private String createdBy; // 생성자

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime modifiedAt; // 수정일시

    @LastModifiedBy
    @Column(nullable = true, length = 100)
    private String modifiedBy; // 수정자

    protected Todo() {}

    public Todo(String content, String complete, String important, String createdBy) {
        this.content = content;
        this.complete = complete;
        this.important = important;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }


}

