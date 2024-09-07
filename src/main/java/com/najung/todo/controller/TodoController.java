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

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/todo")
@RestController
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public Page<TodoResponse> todo(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                   @RequestParam Long userId) {

        return todoService.searchTodo(userId, pageable).map(TodoResponse::from);
    }

    @PostMapping("/members/{memberId}/todos")
    public ResponseEntity<?> postTodo(@PathVariable Long memberId,
                                      @RequestBody TodoRequest todoRequest) {
        todoService.saveTodo(memberId, todoRequest);
        return ResponseEntity.ok("저장 되었습니다.");
    }

    @PostMapping("/members/{memberId}/todo/{todoId}/multiple")
    public ResponseEntity<?> multipleTodo(@PathVariable Long memberId,
                                          @PathVariable Long todoId,
                                          @RequestBody TodoRequest todoRequest) {
        todoService.saveMultipleTodo(memberId, todoId, todoRequest);
        return ResponseEntity.ok("저장 되었습니다.");
    }

    @PutMapping("/member/{memberId}/todo/{todoId}")
    public ResponseEntity<?> updateTodo(@PathVariable Long todoId,
                                        @PathVariable Long memberId,
                                        @RequestBody TodoRequest todoRequest) {
        todoService.updateTodo(todoId, memberId, todoRequest);
        return ResponseEntity.ok("수정 되었습니다.");
    }

    @PatchMapping("/members/{memberId}/todo/{todoId}/due-date")
    public ResponseEntity<?> updateDueDate(@PathVariable Long memberId,
                                           @PathVariable Long todoId,
                                           @RequestBody TodoRequest todoRequest) {

        todoService.updateDueDate(memberId, todoId, todoRequest);
        return ResponseEntity.ok("일정이 수정되었습니다.");
    }


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
