package com.najung.todo.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@DisplayName("View 컨트롤러 - todo-list")
@WebMvcTest(TodoController.class)
class TodoControllerTest {
    private final MockMvc mvc;

    public TodoControllerTest(@Autowired MockMvc mvc) {
        this.mvc =mvc;
    }

    @Disabled("미완성")
    @DisplayName("todo-list 등록")
    @Test
    public void givenToDoInfo_whenRequesting_thenSavesNewTodo() throws Exception{
        // Given

        // When & Then


    }

    @DisplayName("todo-list 조회")
    @Test
    public void givenNoting_whenRequestTodoListView_thenReturnTodoListView() throws Exception{
        // Given

        // When & Then
        mvc.perform(get("/todo"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("todo/index"))
                .andExpect(model().attributeExists("todo"));

    }

    @Disabled("미완성")
    @DisplayName("todo-list 수정")
    @Test
    public void givenUpdateTodo_whenRequesting_thenReturnTodoListView() throws Exception{
        // Given

        // When & Then


    }

    @Disabled("미완성")
    @DisplayName("todo-list 삭제")
    @Test
    public void givenTodoIdToDelete_whenRequesting_thenDeleteTodo() throws Exception{
        // Given

        // When & Then


    }

}