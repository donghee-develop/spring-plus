package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
public record TodoSaveResponse(Long id, String title, String contents, String weather, UserResponse user) {

}
