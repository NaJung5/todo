package com.najung.todo.service;

import com.najung.todo.domain.Member;
import com.najung.todo.dto.request.MemberRequest;
import com.najung.todo.repository.MemberRepository;
import com.najung.todo.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
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

    public String login(String userId, String userPw) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (!passwordEncoder.matches(userPw, member.getUserPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.createToken(member.getUserId(), member.getRoles().toString());
    }
}
