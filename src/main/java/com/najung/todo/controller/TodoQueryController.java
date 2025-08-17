package com.najung.todo.controller;

import com.najung.todo.dto.CustomUserDetails;
import com.najung.todo.dto.TodoDto;
import com.najung.todo.dto.request.TodoSearchRequest;
import com.najung.todo.service.TodoQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoQueryController {
    private final TodoQueryService todoQueryService;

    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<Page<TodoDto>>> listAsync(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                      TodoSearchRequest todoSearchRequest,
                                                                      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long memberId = userDetails.getMember().getId();
        return todoQueryService.searchAsync(memberId, todoSearchRequest, pageable).thenApply(ResponseEntity::ok);
    }


}
