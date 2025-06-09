package com.najung.todo.dto;

import com.najung.todo.domain.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class CustomUserDetails extends User {

    private final Member member;

    public CustomUserDetails(Member member) {
        super(member.getUserId(), member.getUserPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
