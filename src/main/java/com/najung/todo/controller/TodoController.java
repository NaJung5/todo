package com.najung.todo.controller;

import com.najung.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/todo")
@Controller
public class TodoController {

    private final TodoService todoService;


//    @GetMapping
//    public String todo(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
//                       ModelMap map){
//        Long id = 1L;
//        Page<ToDoResponse> todo = todoService.searchTodo(id, pageable).map(ToDoResponse::from);
//        map.addAttribute("todo", todo);
//
//        return "todo/index";
//    }


    @GetMapping
    public String todo(ModelMap map){
        map.addAttribute("todo", List.of());

        return "todo/index";
    }
}
