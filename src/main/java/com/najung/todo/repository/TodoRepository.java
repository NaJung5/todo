package com.najung.todo.repository;

import com.najung.todo.domain.QTodo;
import com.najung.todo.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface TodoRepository extends
        JpaRepository<Todo, Long>,
        QuerydslPredicateExecutor<Todo>,
        QuerydslBinderCustomizer<QTodo> {

    Page<Todo> findById(Long id, Pageable pageable);

    Optional<Todo> findByIdAndMember_Id(Long id, Long sno);

    @Override
    default void customize(QuerydslBindings bindings, QTodo toDo) {

    }

}
