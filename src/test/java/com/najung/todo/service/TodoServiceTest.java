package com.najung.todo.service;

import com.najung.todo.domain.Member;
import com.najung.todo.domain.Todo;
import com.najung.todo.dto.TodoDto;
import com.najung.todo.dto.MemberDto;
import com.najung.todo.repository.MemberRepository;
import com.najung.todo.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


@DisplayName("비지니스 로직 - todo-list")
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks private TodoService sut;
    @Mock private TodoRepository todoRepository;
    @Mock private MemberRepository memberRepository;

    @DisplayName("유저ID를 받아, 해당 유저가 작성한 todo-list를 저장 ")
    @Test
    void givenNickname_whenSearchTodos_thenReturnsTodoList() {

        // Given

        TodoDto dto = createTodoDto();
        given(todoRepository.save(any(Todo.class))).willReturn(createTodo());

        // When
        sut.saveTodo(dto);

        // Then
        then(todoRepository).should().save(any(Todo.class));
    }

    @DisplayName("유저ID를 받아, 해당 유저가 작성한 todo-list를 반환 ")
    @Test
    void givenTodoInfo_whenSavingTodo_thenSaveTodo() {

        // Given
        Long memberSno = 1L;
        Pageable pageable = Pageable.ofSize(20);
        given(todoRepository.findByMember_sno(memberSno, pageable)).willReturn(Page.empty());
        // When
        Page<TodoDto> todos = sut.searchTodo(memberSno, pageable);

        // Then
        assertThat(todos).isEmpty();
        then(todoRepository).should().findByMember_sno(memberSno, pageable);
    }

    @DisplayName("유저의 ID와 todo의 ID를 받아, 해당 유저가 작성한 아이템 한개를 업데이트한다.")
    @Test
    void givenTodoInfo_whenUpdateTodo_thenUpdateTodo() {

        // Given
        Todo todo = createTodo();
        TodoDto dto = createTodoDto("content", "complete", "important");

        given(todoRepository.getReferenceById(dto.id())).willReturn(todo);
        // When
        sut.updateTodo(dto.memberDto().sno(), dto);
        // Then
        then(todoRepository).should().getReferenceById(dto.id());

    }

    @DisplayName("todo의 ID를 받아, 해당 유저가 작성한 아이템 한개를 삭제 한다.")
    @Test
    void givenTodoId_whenDeleteTodo_thenDeleteTo() {

        // Given
        Long todoId = 1L;
        Long memberId = 1L;

        Todo todo = createTodo();
        given(todoRepository.findByIdAndMember_sno(todoId, memberId)).willReturn(Optional.of(todo));
        willDoNothing().given(todoRepository).deleteById(todoId);

        // When
        sut.deleteTodo(todoId, memberId);

        // Then
        then(todoRepository).should().findByIdAndMember_sno(todoId, memberId);
        then(todoRepository).should().deleteById(todoId);

    }

    private Member createMember(){
        return Member.of(
                "najung",
                "12345",
                "najung"

        );
    }

    private Todo createTodo(){
        return Todo.of(
                createMember(),
                "content",
                "complete",
                "important"
        );
    }

    private TodoDto createTodoDto() {
        return createTodoDto("dddd", "N", "L");
    }
    private TodoDto createTodoDto(String content, String complete, String important) {
        return TodoDto.of(
                1L,
                createUserDto(),
                content,
                complete,
                important
        );
    }

    public MemberDto createUserDto(){
        return MemberDto.of(
                1L
        );
    }
}