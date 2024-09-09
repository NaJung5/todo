package com.najung.todo.controller;

import com.najung.todo.dto.request.TodoRequest;
import com.najung.todo.dto.response.TodoResponse;
import com.najung.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/todo")
@RestController
public class TodoController {

    private final TodoService todoService;

    @Operation(summary = "todo 조회", description = "userId를 받아 해당 유저가 작성한 todo의 리스트를 반환")
    @GetMapping
    public Page<TodoResponse> todo(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                   @RequestParam Long userId) {

        return todoService.searchTodo(userId, pageable).map(TodoResponse::from);
    }

    @Operation(summary = "todo 저장", description = "userId를 받아 해당 유저가 작성한 todo를 저장")
    @PostMapping("/members/{memberId}/todos")
    public ResponseEntity<?> postTodo(@PathVariable Long memberId,
                                      @RequestBody TodoRequest todoRequest) {
        todoService.saveTodo(memberId, todoRequest);
        return ResponseEntity.ok("저장 되었습니다.");
    }

    @Operation(summary = "todo 반복", description = "userId와 todoId 받아 해당 유저가 작성한 todo를 반복 저장")
    @PostMapping("/members/{memberId}/todo/{todoId}/multiple")
    public ResponseEntity<?> multipleTodo(@PathVariable Long memberId,
                                          @PathVariable Long todoId,
                                          @RequestBody TodoRequest todoRequest) {
        todoService.saveMultipleTodo(memberId, todoId, todoRequest);
        return ResponseEntity.ok("저장 되었습니다.");
    }

    @Operation(summary = "todo 업데이트", description = "userId와 todoId 받아 해당 유저가 작성한 todo를 업데이트")
    @PutMapping("/member/{memberId}/todo/{todoId}")
    public ResponseEntity<?> updateTodo(@PathVariable Long memberId,
                                        @PathVariable Long todoId,
                                        @RequestBody TodoRequest todoRequest) {
        todoService.updateTodo(memberId, todoId, todoRequest);
        return ResponseEntity.ok("수정 되었습니다.");
    }

    @Operation(summary = "마감일 업데이트", description = "userId와 todoId 받아 해당 유저가 작성한 todo의 마감일을 변경")
    @PatchMapping("/members/{memberId}/todo/{todoId}/due-date")
    public ResponseEntity<?> updateDueDate(@PathVariable Long memberId,
                                           @PathVariable Long todoId,
                                           @RequestBody TodoRequest todoRequest) {

        todoService.updateDueDate(memberId, todoId, todoRequest);
        return ResponseEntity.ok("일정이 수정되었습니다.");
    }

    @Operation(summary = "todo 삭제", description = "userId와 todoId 받아 해당 유저가 작성한 todo를 삭제")
    @DeleteMapping("/delete/{todoId}/{memberId}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long todoId,
                                        @PathVariable Long memberId) {
        boolean isDeleted = todoService.deleteTodo(todoId, memberId);
        if (isDeleted) {
            return ResponseEntity.ok("삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제할 todo가 없습니다.");
        }
    }
}
