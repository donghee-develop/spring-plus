package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public record TodoQueryRes(String title, Long managersCount, Long commentsCount) {
}
