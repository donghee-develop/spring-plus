package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoQueryRes;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional // readOnly 오류
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        // todo 낫 파운드
        User user = User.fromAuthUser(authUser);
        System.out.println("user + " + user);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail(),user.getNickname())
        );
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodos(int page, int size, LocalDate startDate, LocalDate endDate, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : LocalDate.MIN.atStartOfDay();
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        Page<Todo> todos = todoRepository.searchTodos(keyword, startDateTime, endDateTime, pageable);

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail(), todo.getUser().getNickname()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public TodoResponse getTodo(long todoId) {
        todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));
        return todoRepository.getTodo(todoId);
    }

    public Page<TodoQueryRes> getTodosQuery(int page, int size, LocalDate startDate, LocalDate endDate, String keyword, String query) {
        return todoRepository.getTodosQuery(page,size,startDate,endDate,keyword,query);
    }
}
