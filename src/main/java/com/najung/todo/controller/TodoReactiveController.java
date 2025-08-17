package com.najung.todo.controller;

import com.najung.todo.domain.Todo;
import com.najung.todo.dto.CustomUserDetails;
import com.najung.todo.dto.TodoDto;
import com.najung.todo.dto.request.TodoSearchRequest;
import com.najung.todo.repository.TodoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.OutputStreamWriter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoReactiveController {
    private final TodoQueryRepository todoQueryRepository;

    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<StreamingResponseBody> exportCsv(TodoSearchRequest req,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails.getMember().getId();
        StreamingResponseBody body = output -> {
            try (var writer = new OutputStreamWriter(output)) {
                writer.write("id, content, complete, important\n");
                int page = 0;
                Page<Todo> slice;
                do {
                    slice = todoQueryRepository.searchTodos(memberId, req, PageRequest.of(page++, 500));
                    for (Todo t : slice.getContent()) {
                        writer.write("%d, %s, %s, %s\n".formatted(
                                t.getId(),
                                t.getContent(),
                                t.getComplete(),
                                t.getImportant()
                        ));
                    }
                    writer.flush();
                } while (!slice.isLast());
            }
            ;
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = todos.csv")
                .body(body);
    }

    @GetMapping("/rx")
    public Mono<Page<TodoDto>> listRx(TodoSearchRequest req,
                                      Pageable pageable,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMember().getId();
        return Mono.fromCallable(() -> todoQueryRepository.searchTodos(memberId, req, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .map(p -> p.map(TodoDto::from));
    }
}
