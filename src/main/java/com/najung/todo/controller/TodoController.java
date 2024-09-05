package com.najung.todo.controller;

import com.najung.todo.dto.MemberDto;
import com.najung.todo.dto.request.TodoRequest;
import com.najung.todo.dto.response.TodoResponse;
import com.najung.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
                                   @RequestParam Long userId){

        return todoService.searchTodo(userId, pageable).map(TodoResponse::from);
    }

    @PostMapping("/new/{memberId}")
    public ResponseEntity<?> postTodo(@PathVariable Long memberId,
                                      @RequestBody TodoRequest todoRequest){
        todoService.saveTodo(todoRequest.toDto(MemberDto.of(memberId)));
        return ResponseEntity.ok("저장 되었습니다.");
    }

    @PostMapping("/update/{todoId}/{memberId}")
    public ResponseEntity<?> updateTodo(@PathVariable Long todoId,
                                        @PathVariable Long memberId,
                                        @RequestBody TodoRequest todoRequest){
        MemberDto memberDto = MemberDto.of(memberId);
        todoService.updateTodo(todoId, todoRequest.toDto(memberDto));
        return ResponseEntity.ok("수정 되었습니다.");
    }

    @DeleteMapping("/delete/{todoId}/{memberId}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long todoId,
                                        @PathVariable Long memberId){
        todoService.deleteTodo(todoId, memberId);
        return ResponseEntity.ok("삭제되었습니다.");
    }
}
