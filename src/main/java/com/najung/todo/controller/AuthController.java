package com.najung.todo.controller;

import com.najung.todo.dto.request.LoginRequest;
import com.najung.todo.dto.response.TokenResponse;
import com.najung.todo.repository.RefreshTokenRepository;
import com.najung.todo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final MemberService memberService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(memberService.login(request));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestParam String refreshToken) {
            TokenResponse response = memberService.reissue(refreshToken);
            return ResponseEntity.ok(response);
    }
}
