package com.najung.todo.service;

import com.najung.todo.domain.Member;
import com.najung.todo.domain.RefreshToken;
import com.najung.todo.dto.request.LoginRequest;
import com.najung.todo.dto.request.MemberRequest;
import com.najung.todo.dto.response.TokenResponse;
import com.najung.todo.repository.MemberRepository;
import com.najung.todo.repository.RefreshTokenRepository;
import com.najung.todo.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void saveMember(MemberRequest req) {
        if (memberRepository.existsByUserId(req.userId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String encodePw = passwordEncoder.encode(req.userPassword());
        Member member = Member.of(
                req.userId(),
                encodePw,
                req.nickname(),
                req.mail()
        );
        memberRepository.save(member);

    }

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByUserId(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (!passwordEncoder.matches(request.userPassword(), member.getUserPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String userId = member.getUserId();
        String accessToken = jwtTokenProvider.createToken(userId, String.join(",", member.getRoles()));
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        refreshTokenRepository.save(new RefreshToken(
                userId,
                refreshToken,
                LocalDateTime.now().plusDays(14))
        );
        return new TokenResponse(accessToken, refreshToken, jwtTokenProvider.getAccessTokenExpiryTime());
    }

    public TokenResponse reissue(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        String userId = jwtTokenProvider.getUserId(refreshToken);

        RefreshToken saved = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 Refresh Token이 없습니다."));

        if (!saved.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.createToken(userId, "ROLE_USER");

        return new TokenResponse(newAccessToken, refreshToken, jwtTokenProvider.getAccessTokenExpiryTime());
    }

}
