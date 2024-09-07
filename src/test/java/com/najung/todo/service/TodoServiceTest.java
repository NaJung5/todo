package com.najung.todo.service;

import com.najung.todo.domain.Member;
import com.najung.todo.domain.Todo;
import com.najung.todo.dto.MemberDto;
import com.najung.todo.dto.TodoDto;
import com.najung.todo.dto.request.TodoRequest;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


@DisplayName("비지니스 로직 - todo-list")
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks
    private TodoService sut;
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private MemberRepository memberRepository;

    @DisplayName("유저ID를 받아, 해당 유저가 작성한 todo를 저장")
    @Test
    void givenUserId_whenSearchTodos_thenReturnsTodoList() {
        // Given
        TodoRequest req = createTodoRequest("test", "N", "N", 1, null);
        Long memberId = 1L;
        Member member = createMember();

        given(memberRepository.getReferenceById(memberId)).willReturn(member);
        given(todoRepository.save(any(Todo.class))).willReturn(createTodo());

        // When
        sut.saveTodo(memberId, req);

        // Then
        then(todoRepository).should().save(any(Todo.class));
    }

    @DisplayName("todo의 id를 받아 동일 아이템을 반복 등록한다.")
    @Test
    void givenTodoInfo_whenSaveMultipleTodo_thenSaveTodo() {
        // Given
        Long memberId = 1L;
        Long todoId = 1L;
        LocalDate dueDate = LocalDate.parse("2024-09-06");

        Todo todo = createTodo();
        Member member = createMember();
        TodoRequest req = createTodoRequest("test", "N", "N", 1, dueDate);

        given(memberRepository.getReferenceById(memberId)).willReturn(member);
        given(todoRepository.getReferenceById(todoId)).willReturn(todo);

        // When
        sut.saveMultipleTodo(memberId, todoId, req);
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
        Member member = createMember();
        Long todoId = 1L;
        Long memberId = 1L;
        TodoRequest req = createTodoRequest("content", "complete", "important", 1, null);

        given(todoRepository.getReferenceById(todoId)).willReturn(todo);
        given(memberRepository.getReferenceById(memberId)).willReturn(member);

        // When
        sut.updateTodo(todoId, memberId, req);
        // Then
        then(todoRepository).should().getReferenceById(todoId);

    }

    @DisplayName("todo의 ID를 받아, 해당 유저가 작성한 아이템 한개를 삭제 한다.")
    @Test
    void givenTodoId_whenDeleteTodo_thenDeleteTo() {
        // Given
        Long todoId = 1L;
        Long memberId = 1L;

        willReturn(1).given(todoRepository).deleteByIdAndMember_Sno(todoId, memberId);

        // When
        boolean result = sut.deleteTodo(todoId, memberId);

        // Then
        assertThat(result).isTrue();
        then(todoRepository).should().deleteByIdAndMember_Sno(todoId, memberId);
    }

    @DisplayName("todo 의 ID를 받아, 다음날로 일정을 변경한다.")
    @Test
    void givenTodoId_whenUpdateDueDate_thenUpdateDueDate() {

        // Given
        Long todoId = 1L;
        Long memberId = 1L;
        LocalDate dueDate = LocalDate.parse("2024-09-06");
        Todo todo = createTodo();
        TodoRequest req = createTodoRequest(null, null, null, 1, dueDate);
        given(todoRepository.getReferenceById(todoId)).willReturn(todo);
        // When
        sut.updateDueDate(memberId, todoId, req);
        // Then
        then(todoRepository).should().getReferenceById(todoId);


    }

    private Member createMember() {
        return Member.of(
                "najung",
                "12345",
                "najung"

        );
    }

    private Todo createTodo() {
        return Todo.of(
                createMember(),
                "content",
                "complete",
                "important",
                LocalDateTime.parse("2024-09-09T00:00:00")
        );
    }

    private TodoRequest createTodoRequest(String content,
                                          String complete,
                                          String important,
                                          int count,
                                          LocalDate dueDate) {
        return TodoRequest.of(
                content,
                complete,
                important,
                dueDate,
                count
        );

    }

    private TodoDto createTodoDto(String content, String complete, String important, LocalDateTime dueDate) {
        return TodoDto.of(
                createUserDto(),
                content,
                complete,
                important,
                dueDate
        );
    }

    public MemberDto createUserDto() {
        return MemberDto.of(
                1L
        );
    }
}