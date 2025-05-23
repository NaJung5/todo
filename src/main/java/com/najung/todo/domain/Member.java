package com.najung.todo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Table
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sno; // 회원 번호

    @Setter
    @Column(length = 50, unique = true, nullable = false)
    private String userId;

    @Setter
    @Column(nullable = false)
    private String userPassword;

    @Setter
    @Column(nullable = false)
    private String mail;

    @Setter
    @Column(nullable = false)
    private String nickname;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 가입일자

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime modifiedAt; // 수정일시

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    protected Member() {
    }


    private Member(String userId, String userPassword, String nickname, String mail, List<String> roles) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.mail = mail;
        this.roles = roles;
    }

    public static Member of(String userId, String userPassword, String nickname, String mail) {
        Member member = new Member();
        member.userId = userId;
        member.userPassword = userPassword;
        member.nickname = nickname;
        member.mail = mail;
        member.createdAt = LocalDateTime.now();
        member.roles = List.of("ROLE_USER");
        return member;
    }
}
