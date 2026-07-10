package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(Long id);

    User create(UserCreateDTO data);

    User update(Long id, UserUpdateDTO data);

    void delete(Long id);

    boolean isOwner(Long id, String email);

    void createAdminIfNotExists();
}
