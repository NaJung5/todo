package com.najung.todo.repository;

import com.najung.todo.domain.QTodo;
import com.najung.todo.domain.Todo;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface TodoRepository extends
        JpaRepository<Todo, Long>,
        QuerydslPredicateExecutor<Todo>,
        QuerydslBinderCustomizer<QTodo> {

    Page<Todo> findByMember_sno(Long id, Pageable pageable);

    Integer deleteByIdAndMember_Sno(Long id, Long sno);

    @Override
    default void customize(QuerydslBindings bindings, QTodo toDo) {
        bindings.bind(toDo.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(toDo.important).first(SimpleExpression::eq);
    }

}
