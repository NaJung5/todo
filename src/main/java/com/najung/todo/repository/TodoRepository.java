package com.najung.todo.repository;

import com.najung.todo.domain.QToDo;
import com.najung.todo.domain.Todo;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface TodoRepository extends
        JpaRepository<Todo, Long>,
        QuerydslPredicateExecutor<Todo>,
        QuerydslBinderCustomizer<QToDo> {

    Page<Todo> findByIdContaining(Long id, Pageable pageable);

    @Override
    default void customize(QuerydslBindings bindings, QToDo toDo) {
        bindings.bind(toDo.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(toDo.important).first(SimpleExpression::eq);
    }
}
