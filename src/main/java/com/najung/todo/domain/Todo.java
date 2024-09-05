package com.najung.todo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@ToString
@Table(name = "todo")
@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name = "sno")
    @ManyToOne(optional = false)
    private Member member; // 유저 아이디

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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime modifiedAt; // 수정일시


    protected Todo() {}

    public Todo(Member member, String content, String complete, String important) {
        this.member = member;
        this.content = content;
        this.complete = complete;
        this.important = important;
        this.createdAt = LocalDateTime.now();
    }
    public static Todo of(Member member, String content, String complete, String important) {
        return new Todo(member, content, complete, important);
    }


}

