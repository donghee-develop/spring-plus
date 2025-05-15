package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepoQueryDSL{

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    // weather
    @Query("""
        select t from Todo t 
        where (:keyword is null or t.weather like %:keyword% or t.contents like %:keyword%)
        and (:startDate is null or t.modifiedAt >= :startDate)
        and (:endDate is null or t.modifiedAt <= :endDate)
        order by t.modifiedAt desc
""")
    Page<Todo> searchTodos(String keyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
