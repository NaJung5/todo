package com.najung.todo.repository;

import com.najung.todo.domain.Member;
import com.najung.todo.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByMember(Member member);
    @Modifying
    @Query("UPDATE RefreshToken r SET r.token = :token WHERE r.member.id = :memberId")
    void updateTokenByMemberId(@Param("token") String token, @Param("memberId") Long memberId);
}
