package hexlet.code.controller;

import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static final String VALID_USER_JSON = """
            {
                "email": "test@example.com",
                "firstName": "Test",
                "lastName": "User",
                "password": "qwerty"
            }
            """;

    private static final String INVALID_USER_JSON = """
            {
                "email": "bad-email",
                "password": "qw"
            }
            """;

    private static final String UPDATE_JSON = """
            {
                "email": "updated@example.com",
                "password": "new-password"
            }
            """;

    @Test
    void testGetUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetUserById() throws Exception {
        var user = userRepository.findAll().getFirst();

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.updatedAt").doesNotExist());
    }

    @Test
    void testCreateUser() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_USER_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testCreateUserWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_USER_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser() throws Exception {
        var result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_USER_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        var response = result.getResponse().getContentAsString();
        var id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(put("/api/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testUpdateUserWithInvalidData() throws Exception {
        var user = userRepository.findAll().getFirst();

        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_USER_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUser() throws Exception {
        var result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_USER_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        var response = result.getResponse().getContentAsString();
        var id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPasswordIsNotReturned() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("qwerty"))))
                .andExpect(content().string(not(containsString("password"))));
    }
}
