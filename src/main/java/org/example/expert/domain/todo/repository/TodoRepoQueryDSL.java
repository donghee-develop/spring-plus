package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoQueryRes;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface TodoRepoQueryDSL {
    public TodoResponse getTodo(long todoId);

    Page<TodoQueryRes> getTodosQuery(int page, int size, LocalDate startDate, LocalDate endDate, String keyword, String query);
}
