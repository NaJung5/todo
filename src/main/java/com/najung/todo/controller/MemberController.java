package com.najung.todo.controller;

import com.najung.todo.dto.request.LoginRequest;
import com.najung.todo.dto.request.MemberRequest;
import com.najung.todo.dto.request.TodoRequest;
import com.najung.todo.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@RestController
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "회원 가입")
    @PostMapping("/signup")
    public ResponseEntity<?> postTodo(@Valid @RequestBody MemberRequest memberRequest) {
        memberService.saveMember(memberRequest);
        return ResponseEntity.ok("가입 되었습니다.");
    }

}
