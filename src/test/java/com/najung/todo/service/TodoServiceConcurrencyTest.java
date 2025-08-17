package com.najung.todo.service;

import com.najung.todo.domain.Member;
import com.najung.todo.domain.Todo;
import com.najung.todo.dto.request.TodoRequest;
import com.najung.todo.repository.MemberRepository;
import com.najung.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TodoServiceConcurrencyTest {

    @Autowired
    TodoRepository todoRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TodoService todoService;

    @Test
    void givenConcurrentUpdate_whenUsesOptimisticLock_thenWithRetry() throws Exception {
        // Given
        Member member = memberRepository.findByUserId("najung").orElseGet(() -> memberRepository.saveAndFlush(Member.of(
                "najung",
                "1q2w3e",
                "najung",
                "najung@gmail.com")));

        Todo todo = todoRepository.saveAndFlush(Todo.of(member,
                "content",
                "complete",
                "important",
                LocalDate.now(),
                LocalDate.now(),
                LocalDateTime.now()));

        Long memberId = member.getId();
        Long todoId = todo.getId();
        int threads = 10;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        // When
        try {
            for (int i = 0; i < threads; i++) {
                es.submit(() -> {
                    try {
                        TodoRequest req = TodoRequest.of("content", "N", "H", LocalDate.now(), LocalDate.now(), null);
                        todoService.updateTodoWithRetry(memberId, todoId, req);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            boolean completed = latch.await(5, TimeUnit.SECONDS);
            assertTrue(completed, "작업들이 제한 시간 내에 끝나지 않았습니다.");
        } finally {
            es.shutdown();
            es.awaitTermination(3, TimeUnit.SECONDS);
        }

        // Then
        Optional<Todo> refreshedOpt = todoRepository.findById(todoId);
        assertTrue(refreshedOpt.isPresent());

        Todo refreshed = refreshedOpt.get();
        assertNotNull(refreshed.getVersions());
    }
}
