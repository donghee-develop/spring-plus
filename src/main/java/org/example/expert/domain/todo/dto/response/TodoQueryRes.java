package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoQueryRes {
    private final String title;
    private final Long managersCount;
    private final Long commentsCount;

    public TodoQueryRes(String title, Long managersCount, Long commentsCount) {
        this.title = title;
        this.managersCount = managersCount;
        this.commentsCount = commentsCount;
    }
}
