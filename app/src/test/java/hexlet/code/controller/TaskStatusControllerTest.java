package hexlet.code.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import java.util.Map;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        userService.createAdminIfNotExists();
        taskStatusService.createDefaultStatuses();
        labelService.createDefaultLabels();
    }

    @Test
    void testGetTaskStatusesWithoutToken() throws Exception {
        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTaskStatuses() throws Exception {
        var expectedIds = taskStatusRepository.findAll()
                .stream()
                .map(TaskStatus::getId)
                .sorted()
                .toList();

        var result = mockMvc.perform(get("/api/task_statuses")
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andReturn();

        var responseBody = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        var actualIds = extractIds(responseBody);

        assertThat(actualIds).containsExactlyElementsOf(expectedIds);
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
        var body = Map.of(
                "username", email,
                "password", password
        );

        var result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
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

        var response = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        return response.get("id").asText();
    }

    private String taskStatusJson(String name, String slug) throws Exception {
        var body = Map.of(
                "name", name,
                "slug", slug
        );

        return objectMapper.writeValueAsString(body);
    }

    private String updateTaskStatusJson(String name) throws Exception {
        var body = Map.of(
                "name", name
        );

        return objectMapper.writeValueAsString(body);
    }

    private java.util.List<Long> extractIds(JsonNode responseBody) {
        return StreamSupport.stream(responseBody.spliterator(), false)
                .map(node -> node.get("id").asLong())
                .sorted()
                .toList();
    }
}
