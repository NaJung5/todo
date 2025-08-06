package com.najung.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    @Async("taskExecutor")
    public void saveTodoLog(Long memberId, String action, String content) {
        System.out.println("[로그] 사용자: " + memberId + ", 동작: " + action + ", 내용: " + content);
    }
}
