package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.todo.dto.response.TodoQueryRes;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepoQueryDSL{
    private final JPAQueryFactory queryFactory;

    @Override
    public TodoResponse getTodo(long todoId) {
        /*
                Todo todo = todoRepository.findByIdWithUser(todoId)
                select * from todo t join user u on t.user_id = u.user_id where t.id = 1;
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));
         */
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo res = queryFactory
                .selectFrom(todo)
                .join(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return new TodoResponse(Objects.requireNonNull(res));
    }

    @Override
    public Page<TodoQueryRes> getTodosQuery(int page, int size, LocalDate startDate, LocalDate endDate, String keyword, String query) {
        // title, managerCount, commentsCount
        QTodo todo = QTodo.todo;
        QComment comment = QComment.comment;

        BooleanBuilder where = new BooleanBuilder();

        if (query != null && keyword != null && !keyword.isBlank()) {
            switch (query) {
                case "title" -> where.and(todo.title.containsIgnoreCase(keyword));
                case "user" -> where.and(todo.user.nickname.containsIgnoreCase(keyword));
            }
        }

        if (startDate != null) {
            where.and(todo.createdAt.goe(startDate.atStartOfDay()));
        }
        if (endDate != null) {
            where.and(todo.createdAt.loe(endDate.atTime(LocalTime.MAX)));
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<TodoQueryRes> content = queryFactory
                .select(Projections.constructor(
                        TodoQueryRes.class,
                        todo.title,
                        todo.managers.size().longValue(),
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.todo.eq(todo))
                ))
                .from(todo)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(todo.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
