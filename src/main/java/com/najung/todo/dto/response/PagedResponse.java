package com.najung.todo.dto.response;

import com.najung.todo.util.Pagination;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PagedResponse<T> {
    private final List<T> data;
    private final Pagination pagination;

    public PagedResponse(List<T> data, Pagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                new Pagination(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalPages(),
                        page.getTotalElements()
                )
        );
    }


}
