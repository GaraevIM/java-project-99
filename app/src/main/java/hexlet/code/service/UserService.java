package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User create(UserCreateDTO data) {
        var user = new User();
        user.setFirstName(data.getFirstName());
        user.setLastName(data.getLastName());
        user.setEmail(data.getEmail());
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        return userRepository.save(user);
    }

    public User update(Long id, UserUpdateDTO data) {
        var user = getById(id);

        if (data.getFirstName() != null) {
            user.setFirstName(data.getFirstName());
        }

        if (data.getLastName() != null) {
            user.setLastName(data.getLastName());
        }

        if (data.getEmail() != null) {
            user.setEmail(data.getEmail());
        }

        if (data.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(data.getPassword()));
        }

        return userRepository.save(user);
    }

    public void delete(Long id) {
        var user = getById(id);
        userRepository.delete(user);
    }

    public boolean isOwner(Long id, String email) {
        return getById(id).getEmail().equals(email);
    }

    public void createAdminIfNotExists() {
        if (!userRepository.existsByEmail("hexlet@example.com")) {
            var data = new UserCreateDTO();
            data.setEmail("hexlet@example.com");
            data.setPassword("qwerty");
            create(data);
        }
    }
}
