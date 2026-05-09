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

    @Test
    void testLogin() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("hexlet@example.com", "qwerty")))
                .andExpect(status().isOk())
                .andExpect(content().string(not("")))
                .andExpect(content().string(not(containsString("password"))));
    }

    @Test
    void testLoginWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("hexlet@example.com", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUsersWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", getAdminAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetUserById() throws Exception {
        var user = userRepository.findAll().getFirst();

        mockMvc.perform(get("/api/users/" + user.getId())
                        .header("Authorization", getAdminAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.updatedAt").doesNotExist());
    }

    @Test
    void testCreateUser() throws Exception {
        var email = "create-user@example.com";

        mockMvc.perform(post("/api/users")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson(email, "Create", "User", "qwerty")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value("Create"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testCreateUserWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/users")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("bad-email", "Bad", "User", "qw")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser() throws Exception {
        var email = "update-user@example.com";
        var id = createUser(email);
        var token = login(email, "qwerty");

        mockMvc.perform(put("/api/users/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson("updated-user@example.com", "new-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated-user@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testUpdateAnotherUserForbidden() throws Exception {
        var firstUserId = createUser("first-forbidden@example.com");
        createUser("second-forbidden@example.com");
        var secondUserToken = login("second-forbidden@example.com", "qwerty");

        mockMvc.perform(put("/api/users/" + firstUserId)
                        .header("Authorization", "Bearer " + secondUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson("forbidden-update@example.com", "new-password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateUserWithInvalidData() throws Exception {
        var email = "invalid-update-user@example.com";
        var id = createUser(email);
        var token = login(email, "qwerty");

        mockMvc.perform(put("/api/users/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson("bad-email", "qw")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUser() throws Exception {
        var email = "delete-user@example.com";
        var id = createUser(email);
        var token = login(email, "qwerty");

        mockMvc.perform(delete("/api/users/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + id)
                        .header("Authorization", getAdminAuthHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteAnotherUserForbidden() throws Exception {
        var firstUserId = createUser("first-delete-forbidden@example.com");
        createUser("second-delete-forbidden@example.com");
        var secondUserToken = login("second-delete-forbidden@example.com", "qwerty");

        mockMvc.perform(delete("/api/users/" + firstUserId)
                        .header("Authorization", "Bearer " + secondUserToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPasswordIsNotReturned() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", getAdminAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("qwerty"))))
                .andExpect(content().string(not(containsString("password"))));
    }

    private String getAdminAuthHeader() throws Exception {
        return "Bearer " + login("hexlet@example.com", "qwerty");
    }

    private String login(String email, String password) throws Exception {
        var result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    private String createUser(String email) throws Exception {
        var result = mockMvc.perform(post("/api/users")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson(email, "Test", "User", "qwerty")))
                .andExpect(status().isCreated())
                .andReturn();

        var response = result.getResponse().getContentAsString();
        return response.replaceAll(".*\"id\":(\\d+).*", "$1");
    }

    private String loginJson(String email, String password) {
        return """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);
    }

    private String userJson(String email, String firstName, String lastName, String password) {
        return """
                {
                    "email": "%s",
                    "firstName": "%s",
                    "lastName": "%s",
                    "password": "%s"
                }
                """.formatted(email, firstName, lastName, password);
    }

    private String updateJson(String email, String password) {
        return """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);
    }
}
