package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    public List<User> index() {
        return userService.getAll();
    }

    @GetMapping("/api/users/{id}")
    public User show(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/api/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody UserCreateDTO data) {
        return userService.create(data);
    }

    @PutMapping("/api/users/{id}")
    public User update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO data,
            Authentication authentication
    ) {
        if (!userService.isOwner(id, authentication.getName())) {
            throw new AccessDeniedException("Access denied");
        }

        return userService.update(id, data);
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        if (!userService.isOwner(id, authentication.getName())) {
            throw new AccessDeniedException("Access denied");
        }

        userService.delete(id);
    }
}
