package com.najung.todo.repository;

import com.najung.todo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface MemberRepository extends JpaRepository<Member, Long>{
    Optional<Member> findByUserId(String userId);
    boolean existsByUserId(String userId);


}
