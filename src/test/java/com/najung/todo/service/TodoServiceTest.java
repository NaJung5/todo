package com.najung.todo.service;

import com.najung.todo.domain.Member;
import com.najung.todo.domain.Todo;
import com.najung.todo.dto.MemberDto;
import com.najung.todo.dto.TodoDto;
import com.najung.todo.dto.request.TodoRequest;
import com.najung.todo.dto.request.TodoSearchRequest;
import com.najung.todo.repository.MemberRepository;
import com.najung.todo.repository.TodoQueryRepository;
import com.najung.todo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;


@DisplayName("비지니스 로직 - todo-list")
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private LogService logService;

    @Mock
    private TodoQueryRepository todoQueryRepository;

    @DisplayName("시작일과 종료일이 같은 경우, todo 1건 저장된다")
    @Test
    void givenSameStartAndEndDate_whenSaveTodo_thenSavesOneTodo() {
        // Given
        LocalDate today = LocalDate.now();
        TodoRequest req = createTodoRequest("내용", "N", "H", today, today, null);
        Member member = createMember();

        given(memberRepository.getReferenceById(member.getId())).willReturn(member);
        given(todoRepository.save(any(Todo.class))).willReturn(createTodo());

        // When
        todoService.saveTodo(member.getId(), req);

        // Then
        then(todoRepository).should().save(any(Todo.class));
        then(logService).should().saveTodoLog(any(), eq("CREATE"), any());


    }

    @DisplayName("시작일과 종료일이 다를 경우, 날짜 수만큼 todo가 저장된다")
    @Test
    void givenMultipleDates_whenSaveTodo_thenSavesMultipleTodos() {
        // Given
        LocalDate start = LocalDate.of(2024, 9, 1);
        LocalDate end = LocalDate.of(2024, 9, 3);

        TodoRequest req = createTodoRequest("내용", "N", "H", start, end, start.atStartOfDay());
        Member member = createMember();

        given(memberRepository.getReferenceById(member.getId())).willReturn(member);
        given(todoRepository.save(any(Todo.class))).willReturn(createTodo());

        // When
        todoService.saveTodo(member.getId(), req);

        // Then
        then(todoRepository).should(times(3)).save(any(Todo.class));
        then(logService).should(times(3)).saveTodoLog(any(), eq("CREATE"), any());

    }

    @DisplayName("날짜가 없으면 오늘 날짜로 todo 1건 저장된다")
    @Test
    void givenNoDates_whenSaveTodo_thenSavesWithToday() {
        // Given
        TodoRequest req = createTodoRequest("내용", "N", "H", null, null, null);
        Member member = createMember();

        given(memberRepository.getReferenceById(member.getId())).willReturn(member);
        given(todoRepository.save(any(Todo.class))).willReturn(createTodo());

        // When
        todoService.saveTodo(member.getId(), req);

        // Then
        then(todoRepository).should().save(any(Todo.class));
        then(logService).should().saveTodoLog(any(), eq("CREATE"), any());

    }


    @DisplayName("존재하지 않는 memberId로 저장 시 예외가 발생한다")
    @Test
    void givenInvalidMemberId_whenSaveTodo_thenThrowsEntityNotFoundException() {
        // Given
        Long invalidMemberId = 999L;
        TodoRequest req = createTodoRequest("내용2", "N", "H", LocalDate.now(), LocalDate.now(), null);

        given(memberRepository.getReferenceById(invalidMemberId))
                .willThrow(new EntityNotFoundException("회원 없음"));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> todoService.saveTodo(invalidMemberId, req));
    }

    @DisplayName("유저 ID와 검색 조건을 받아 todo 리스트를 조회한다")
    @Test
    void givenTodoInfo_whenSavingTodo_thenSaveTodo() {
        // Given
        Member member = createMember();
        Pageable pageable = Pageable.ofSize(20);
        TodoSearchRequest todoSearchRequest = createDefaultSearchRequest();
        given(todoQueryRepository.searchTodos(member.getId(), todoSearchRequest, pageable)).willReturn(Page.empty());
        // When
        Page<TodoDto> todos = todoService.searchTodo(member.getId(), todoSearchRequest, pageable);

        // Then
        assertDoesNotThrow(() -> {
            todoService.searchTodo(member.getId(), todoSearchRequest, pageable);
        });

    }

    @DisplayName("유효하지 않은 memberId로 조회 시 예외가 발생한다")
    @Test
    void givenInvalidMemberId_whenSearchTodo_thenThrowsException() {
        // Given
        Long invalidId = 0L;
        TodoSearchRequest request = createDefaultSearchRequest();
        Pageable pageable = Pageable.ofSize(10);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> todoService.searchTodo(invalidId, request, pageable));
        then(logService).should(never()).saveTodoLog(any(), any(), any());

    }

    @DisplayName("유저의 ID와 todo의 ID를 받아, 해당 유저가 작성한 아이템 한개를 업데이트한다.")
    @Test
    void givenTodoInfo_whenUpdateTodo_thenUpdateTodo() {
        // Given
        Todo todo = createTodo();
        Member member = createMember();
        Long todoId = 1L;
        TodoRequest req = createTodoRequest("내용2", "N", "H", LocalDate.now(), LocalDate.now(), null);

        given(todoRepository.getReferenceById(todoId)).willReturn(todo);

        // When
        todoService.updateTodo(todoId, member.getId(), req);
        // Then
        then(todoRepository).should().getReferenceById(todoId);
        then(logService).should().saveTodoLog(any(), eq("UPDATE"), any());
    }

    @DisplayName("Todo의 작성자 ID와 요청자의 ID가 다르면 예외가 발생한다")
    @Test
    void givenMismatchedMemberId_whenUpdateTodo_thenThrowsException() {
        // Given
        Member originalWriter = createMember();
        Member mismatchMember = createMember(2L);

        Todo todo = createTodo(1L, originalWriter);

        TodoRequest req = createTodoRequest("내용2", "N", "H", LocalDate.now(), LocalDate.now(), null);

        given(todoRepository.getReferenceById(todo.getId())).willReturn(todo);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> todoService.updateTodo(mismatchMember.getId(), todo.getId(), req));
        then(logService).should(never()).saveTodoLog(any(), any(), any());

    }


    @DisplayName("정상적인 Todo를 삭제 시 true를 반환한다")
    @Test
    void givenValidTodoIdAndMemberId_whenDeleteTodo_thenReturnsTrue() {
        // Given
        Member originalWriter = createMember();
        Todo todo = createTodo();

        given(todoRepository.findByIdAndMember_Id(todo.getId(), todo.getMember().getId())).willReturn(Optional.of(todo));

        // When
        boolean result = todoService.deleteTodo(todo.getId(), todo.getMember().getId());

        // Then
        assertTrue(result);
        then(todoRepository).should().findByIdAndMember_Id(todo.getId(), todo.getMember().getId());
        then(todoRepository).should().delete(todo);
        then(logService).should().saveTodoLog(any(), eq("DELETE"), any());

    }
    @DisplayName("존재하지 않는 Todo를 삭제 시 false를 반환한다")
    @Test
    void givenInvalidTodoIdOrMemberId_whenDeleteTodo_thenReturnsFalse() {
        // Given
        Long invalidTodoId = 999L;
        Long memberId = 1L;

        given(todoRepository.findByIdAndMember_Id(invalidTodoId, memberId)).willReturn(Optional.empty());

        // When
        boolean result = todoService.deleteTodo(invalidTodoId, memberId);

        // Then
        assertFalse(result);
        then(todoRepository).should().findByIdAndMember_Id(invalidTodoId, memberId);
        then(todoRepository).should(never()).delete(any());
        then(logService).should(never()).saveTodoLog(any(), any(), any());

    }

    @DisplayName("todo의 ID를 받아, 다음날로 일정을 변경한다.")
    @Test
    void givenValidTodoId_whenUpdateDueDate_thenUpdatesDueDate() {
        // Given
        Long todoId = 1L;
        Long memberId = 1L;
        LocalDate dueDate = LocalDate.of(2024, 9, 6);
        Member member = createMember(memberId);
        Todo todo = createTodo(todoId, member);

        TodoRequest req = createTodoRequest(null, null, null, null, null, dueDate.atStartOfDay());

        given(todoRepository.getReferenceById(todoId)).willReturn(todo);

        // When
        todoService.updateDueDate(memberId, todoId, req);

        // Then
        then(todoRepository).should().getReferenceById(todoId);
        assertEquals(dueDate.atStartOfDay(), todo.getDueDate());
        then(logService).should().saveTodoLog(any(), eq("UPDATE"), any());

    }

    @DisplayName("요청한 유저가 작성자가 아니라면 dueDate 수정 시 예외 발생")
    @Test
    void givenMismatchedMember_whenUpdateDueDate_thenThrowsException() {
        // Given
        Long todoId = 1L;
        Long memberId = 2L;
        LocalDate dueDate = LocalDate.of(2024, 9, 6);

        Member originalWriter = createMember(1L);
        Member otherUser = createMember(2L);

        Todo todo = createTodo(todoId, originalWriter);
        TodoRequest req = createTodoRequest(null, null, null, null, null, dueDate.atStartOfDay());

        given(todoRepository.getReferenceById(todoId)).willReturn(todo);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> todoService.updateDueDate(memberId, todoId, req));
    }

    private Member createMember() {
        return createMember(1L);
    }

    private Member createMember(Long memberId) {
        Member member = Member.of(
                "najung",
                "1q2w3e4r",
                "najung",
                "najung@mail.com"
        );
        ReflectionTestUtils.setField(member, "id", memberId);
        return member;
    }

    private Todo createTodo() {
        return createTodo(1L, createMember());
    }

    private Todo createTodo(Long todoId, Member member) {
        Todo todo = Todo.of(
                member,
                "content",
                "complete",
                "important",
                LocalDate.now(),
                LocalDate.now(),
                LocalDateTime.parse("2024-09-09T00:00:00")
        );
        ReflectionTestUtils.setField(todo, "id", todoId);

        return todo;
    }

    private TodoRequest createTodoRequest(String content,
                                          String complete,
                                          String important,
                                          LocalDate startDate,
                                          LocalDate endDate,
                                          LocalDateTime dueDate) {
        return TodoRequest.of(
                content,
                complete,
                important,
                startDate,
                endDate,
                dueDate

        );

    }


    private TodoDto createTodoDto(String content, String complete, String important, LocalDate startDate, LocalDate endDate, LocalDateTime dueDate) {
        return TodoDto.of(
                createUserDto(),
                content,
                complete,
                important,
                startDate,
                endDate,
                dueDate
        );
    }

    private TodoSearchRequest createSearchRequest(String keyword, String completed, String important,
                                                  LocalDate startDate, LocalDate endDate) {
        TodoSearchRequest request = new TodoSearchRequest();
        ReflectionTestUtils.setField(request, "keyword", keyword);
        ReflectionTestUtils.setField(request, "completed", completed);
        ReflectionTestUtils.setField(request, "important", important);
        ReflectionTestUtils.setField(request, "startDate", startDate);
        ReflectionTestUtils.setField(request, "endDate", endDate);
        return request;
    }

    private TodoSearchRequest createDefaultSearchRequest() {
        return createSearchRequest(
                "공부",
                "Y",
                "H",
                LocalDate.now().minusDays(7),
                LocalDate.now()
        );
    }

    public MemberDto createUserDto() {
        return MemberDto.of(
                1L
        );
    }
}