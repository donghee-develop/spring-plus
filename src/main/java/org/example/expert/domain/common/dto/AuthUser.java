package org.example.expert.domain.common.dto;

import lombok.Getter;
import lombok.ToString;
import org.example.expert.domain.user.enums.UserRole;

@Getter
@ToString
public class AuthUser {

    private final Long id;
    private final String email;
    private final String nickname;
    private final UserRole userRole;

    public AuthUser(Long id, String email,String nickname, UserRole userRole) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.userRole = userRole;
    }
}
