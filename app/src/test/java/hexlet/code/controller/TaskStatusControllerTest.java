package hexlet.code.controller;

import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Test
    void testGetTaskStatusesWithoutToken() throws Exception {
        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTaskStatuses() throws Exception {
        mockMvc.perform(get("/api/task_statuses")
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetTaskStatusById() throws Exception {
        var status = taskStatusRepository.findBySlug("draft").orElseThrow();

        mockMvc.perform(get("/api/task_statuses/" + status.getId())
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(status.getId()))
                .andExpect(jsonPath("$.name").value(status.getName()))
                .andExpect(jsonPath("$.slug").value(status.getSlug()));
    }

    @Test
    void testCreateTaskStatus() throws Exception {
        mockMvc.perform(post("/api/task_statuses")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskStatusJson("New", "new")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.slug").value("new"));
    }

    @Test
    void testCreateTaskStatusWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/task_statuses")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskStatusJson("", "")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        var id = createTaskStatus("Old", "old");

        mockMvc.perform(put("/api/task_statuses/" + id)
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTaskStatusJson("Updated")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.slug").value("old"));
    }

    @Test
    void testUpdateTaskStatusWithInvalidData() throws Exception {
        var id = createTaskStatus("InvalidUpdate", "invalid_update");

        mockMvc.perform(put("/api/task_statuses/" + id)
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTaskStatusJson("")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteTaskStatus() throws Exception {
        var id = createTaskStatus("Delete", "delete");

        mockMvc.perform(delete("/api/task_statuses/" + id)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/task_statuses/" + id)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isNotFound());
    }

    private String getAuthHeader() throws Exception {
        return "Bearer " + login("hexlet@example.com", "qwerty");
    }

    private String login(String email, String password) throws Exception {
        var body = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        var result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    private String createTaskStatus(String name, String slug) throws Exception {
        var result = mockMvc.perform(post("/api/task_statuses")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskStatusJson(name, slug)))
                .andExpect(status().isCreated())
                .andReturn();

        var response = result.getResponse().getContentAsString();
        return response.replaceAll(".*\"id\":(\\d+).*", "$1");
    }

    private String taskStatusJson(String name, String slug) {
        return """
                {
                    "name": "%s",
                    "slug": "%s"
                }
                """.formatted(name, slug);
    }

    private String updateTaskStatusJson(String name) {
        return """
                {
                    "name": "%s"
                }
                """.formatted(name);
    }
}
