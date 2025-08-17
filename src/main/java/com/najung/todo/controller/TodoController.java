package com.najung.todo.controller;

import com.najung.todo.dto.CustomUserDetails;
import com.najung.todo.dto.request.TodoRequest;
import com.najung.todo.dto.request.TodoSearchRequest;
import com.najung.todo.dto.response.PagedResponse;
import com.najung.todo.dto.response.TodoResponse;
import com.najung.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/todos")
@RestController
public class TodoController {

    private final TodoService todoService;

    @Operation(summary = "todo 조회", description = "로그인한 유저의 todo 목록 반환")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<TodoResponse> getTodos(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute TodoSearchRequest searchRequest,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long memberId = userDetails.getMember().getId();
        Page<TodoResponse> page = todoService.searchTodo(memberId, searchRequest, pageable).map(TodoResponse::from);

        return PagedResponse.of(page);
    }

    @Operation(summary = "todo 저장", description = "로그인한 유저의 todo 생성")
    @PostMapping
    public ResponseEntity<?> postTodo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TodoRequest todoRequest) {

        Long memberId = userDetails.getMember().getId();
        todoService.saveTodo(memberId, todoRequest);
        return ResponseEntity.ok("저장 되었습니다.");
    }

    @Operation(summary = "todo 수정", description = "로그인한 유저의 특정 todo 수정")
    @PutMapping("/{todoId}")
    public ResponseEntity<?> updateTodo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long todoId,
            @RequestBody TodoRequest todoRequest) {

        Long memberId = userDetails.getMember().getId();
        todoService.updateTodoWithRetry(memberId, todoId, todoRequest);
        return ResponseEntity.ok("수정 되었습니다.");
    }

    @Operation(summary = "마감일 수정", description = "todo의 마감일 변경")
    @PatchMapping("/{todoId}/due-date")
    public ResponseEntity<?> updateDueDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long todoId,
            @RequestBody TodoRequest todoRequest) {

        Long memberId = userDetails.getMember().getId();
        todoService.updateDueDate(memberId, todoId, todoRequest);
        return ResponseEntity.ok("일정이 수정되었습니다.");
    }

    @Operation(summary = "todo 삭제", description = "특정 todo 삭제")
    @DeleteMapping("/{todoId}")
    public ResponseEntity<?> deleteTodo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long todoId) {

        Long memberId = userDetails.getMember().getId();
        boolean isDeleted = todoService.deleteTodo(todoId, memberId);
        if (isDeleted) {
            return ResponseEntity.ok("삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제할 todo가 없습니다.");
        }
    }
}
