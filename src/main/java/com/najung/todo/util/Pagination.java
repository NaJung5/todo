package com.najung.todo.util;

import lombok.Getter;

@Getter
public class Pagination {
    private final int page;
    private final int size;
    private final int totalPages;
    private final long totalElements;

    public Pagination(int page, int size, int totalPages, long totalElements) {
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

}
