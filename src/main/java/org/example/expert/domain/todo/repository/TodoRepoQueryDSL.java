package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoQueryRes;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Optional;

public interface TodoRepoQueryDSL {
    TodoResponse getTodo(long todoId);

    Page<TodoQueryRes> getTodosQuery(int page, int size, LocalDate startDate, LocalDate endDate, String keyword, String query);
    Optional<Todo> findByIdWithUser(Long todoId);
}
