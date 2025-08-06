package com.najung.todo.util;

import com.najung.todo.domain.Todo;
import com.najung.todo.dto.TodoDto;

import java.time.LocalDateTime;
import java.util.Objects;

public class LogFormatter {

    public static String formatCreateLog(TodoDto dto) {
        return String.format("할 일 생성: \"%s\"", dto.content());
    }

    public static String formatUpdateLog(Todo before, Todo after) {
        StringBuilder sb = new StringBuilder();

        if (!Objects.equals(before.getContent(), after.getContent())) {
            sb.append(String.format("제목: \"%s\" → \"%s\"\n", before.getContent(), after.getContent()));
        }

        if (!Objects.equals(before.getContent(), after.getContent())) {
            sb.append(String.format("내용: \"%s\" → \"%s\"\n", before.getContent(), after.getContent()));
        }

        if (!Objects.equals(before.getDueDate(), after.getDueDate())) {
            sb.append(String.format("마감일: %s → %s\n",
                    format(before.getDueDate()), format(after.getDueDate())));
        }

        return sb.isEmpty() ? "변경 없음" : sb.toString().trim();
    }

    public static String formatDeleteLog(Todo todo) {
        return String.format("할 일 삭제: \"%s\"", todo.getContent());
    }

    private static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toString() : "없음";
    }
}
