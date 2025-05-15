package org.example.expert.domain.user.controller;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class UserTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DataSource dataSource;
    private static final int USER_COUNT = 1_000_000;
    private static String targetNickname = "";
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Order(1)
    @DisplayName("1. 100만유저 삽입")
    void insertUsersFast() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO users (email, password, nickname, user_role) VALUES (?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (long i = 0; i < USER_COUNT; i++) {
                    UUID uuid = UUID.randomUUID();
                    String nickname = "nick_" + uuid;

                    if (i == USER_COUNT - 1) {
                        targetNickname = nickname; // 마지막 닉네임 저장
                    }
                    ps.setString(1, uuid + "@test.com");
                    ps.setString(2, "pw");
                    ps.setString(3, nickname);
                    ps.setString(4, "USER");
                    ps.addBatch();

                    if (i % 10_000 == 0) {
                        ps.executeBatch();
                        conn.commit();
                    }
                }
                ps.executeBatch();
                conn.commit();
            }
        }
    }

    @Test
        @Order(2)
        @DisplayName("2. 닉네임 정확 일치 조회 성능 테스트")
        void userSelectTest () {
            // when
            long start = System.currentTimeMillis();
            Optional<User> result = userRepository.findByNickname(targetNickname);
            long end = System.currentTimeMillis();

            // then
            assertTrue(result.isPresent(), "닉네임 조회 실패");
            System.out.println("조회 시간(ms): " + (end - start));
        }
    }
