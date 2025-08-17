package com.najung.todo.service;

import com.najung.todo.domain.Member;
import com.najung.todo.dto.CustomUserDetails;
import com.najung.todo.repository.MemberRepository;
import com.najung.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoAsyncControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    TodoRepository todoRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TodoService todoService;

    @Test
    void givenAsyncEndpoint_whenConcurrentRequests_thenFinishUnderTimeout() throws Exception {
        // Given
        var customUser = testUser();
        int concurrent = 100;

        // When
        var futures = IntStream.range(0, concurrent).mapToObj(i -> CompletableFuture.runAsync(() -> {
            try {
                mockMvc.perform(get("/api/todos/async").param("page", "0").param("size",
                        "20").with(user(customUser))).andExpect(status().isOk());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        })).toList();

        var all = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));

        // Then
        assertTimeoutPreemptively(Duration.ofSeconds(5), all::join);
    }

    private CustomUserDetails testUser() {
        return memberRepository.findByUserId("najung").map(CustomUserDetails::new).orElseGet(() -> {
            Member member = memberRepository.saveAndFlush(Member.of("najung", "1q2w3e", "najung", "najung@gmail.com"));
            return new CustomUserDetails(member);
        });
    }
}
