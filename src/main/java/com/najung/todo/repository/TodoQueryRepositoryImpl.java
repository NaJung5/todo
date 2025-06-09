package com.najung.todo.repository;

import com.najung.todo.domain.QTodo;
import com.najung.todo.domain.Todo;
import com.najung.todo.dto.request.TodoSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Component
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Todo> searchTodos(Long memberId, TodoSearchRequest request, Pageable pageable) {
        QTodo todo = QTodo.todo;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(todo.member.id.eq(memberId));

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            OrderSpecifier<?> spec = getOrderSpecifier(order, todo);
            if (spec != null) {
                orders.add(spec);
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier<>(Order.DESC, todo.createdAt));
        }

        if (request.getCompleted() != null && !request.getCompleted().isBlank()) {
            builder.and(todo.complete.eq(request.getCompleted()));
        }

        if (request.getImportant() != null && !request.getImportant().isBlank()) {
            builder.and(todo.important.eq(request.getImportant()));
        }

        if (request.getStartDate() != null) {
            builder.and(todo.dueDate.goe(request.getStartDate().atStartOfDay()));
        }

        if (request.getEndDate() != null) {
            builder.and(todo.dueDate.loe(request.getEndDate().atStartOfDay()));
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            builder.and(todo.content.containsIgnoreCase(request.getKeyword()));
        }

        List<Todo> content = jpaQueryFactory
                .selectFrom(todo)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .fetch();

        Long totalResult = jpaQueryFactory
                .select(todo.count())
                .from(todo)
                .where(builder)
                .fetchOne();

        long total = (totalResult != null) ? totalResult : 0L;

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?> getOrderSpecifier(Sort.Order order, QTodo todo) {
        return switch (order.getProperty()) {
            case "createdAt" ->
                    new OrderSpecifier<>(order.getDirection().isAscending() && order.getDirection().name().equals("ASC")
                            ? Order.ASC
                            : Order.DESC,
                            todo.createdAt
                    );
            case "important" ->
                    new OrderSpecifier<>(order.getDirection().isAscending() && order.getDirection().name().equals("ASC")
                            ? Order.ASC
                            : Order.DESC,
                            todo.important
                    );
            case "dueDate" ->
                    new OrderSpecifier<>(order.getDirection().isAscending() && order.getDirection().name().equals("ASC")
                            ? Order.ASC
                            : Order.DESC,
                            todo.dueDate
                    );
            default -> null;
        };
    }

}
