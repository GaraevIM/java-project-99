package hexlet.code.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Task;
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
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

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
    void testGetTasksWithoutToken() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTasks() throws Exception {
        var expectedIds = taskRepository.findAll()
                .stream()
                .map(Task::getId)
                .sorted()
                .toList();

        var result = mockMvc.perform(get("/api/tasks")
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
    void testGetTaskById() throws Exception {
        var id = createTask("Show task", "Show content", "draft");

        mockMvc.perform(get("/api/tasks/" + id)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Show task"))
                .andExpect(jsonPath("$.content").value("Show content"))
                .andExpect(jsonPath("$.status").value("draft"));
    }

    @Test
    void testCreateTask() throws Exception {
        var assigneeId = userRepository.findByEmail("hexlet@example.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson(
                                12,
                                assigneeId,
                                "Test title",
                                "Test content",
                                "draft"
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.index").value(12))
                .andExpect(jsonPath("$.assignee_id").value(assigneeId))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.status").value("draft"));
    }

    @Test
    void testCreateTaskWithoutAssignee() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJsonWithoutAssignee(
                                15,
                                "No assignee",
                                "Content",
                                "draft"
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.index").value(15))
                .andExpect(jsonPath("$.assignee_id").doesNotExist())
                .andExpect(jsonPath("$.title").value("No assignee"))
                .andExpect(jsonPath("$.content").value("Content"))
                .andExpect(jsonPath("$.status").value("draft"));
    }

    @Test
    void testCreateTaskWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJsonWithoutAssignee(
                                1,
                                "",
                                "Content",
                                ""
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTask() throws Exception {
        var id = createTask("Old title", "Old content", "draft");

        mockMvc.perform(put("/api/tasks/" + id)
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTaskJson(
                                "New title",
                                "New content"
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.content").value("New content"))
                .andExpect(jsonPath("$.status").value("draft"));
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        var id = createTask("Status title", "Status content", "draft");

        mockMvc.perform(put("/api/tasks/" + id)
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTaskStatusJson("published")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));
    }

    @Test
    void testUpdateTaskWithInvalidData() throws Exception {
        var id = createTask("Invalid title", "Invalid content", "draft");

        mockMvc.perform(put("/api/tasks/" + id)
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTaskJson("", "Content")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteTask() throws Exception {
        var id = createTask("Delete title", "Delete content", "draft");

        mockMvc.perform(delete("/api/tasks/" + id)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/" + id)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCannotDeleteUserWithTask() throws Exception {
        var assigneeId = userRepository.findByEmail("hexlet@example.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson(
                                20,
                                assigneeId,
                                "User related",
                                "Content",
                                "draft"
                        )))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/users/" + assigneeId)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isConflict());
    }

    @Test
    void testCannotDeleteStatusWithTask() throws Exception {
        createTask("Status related", "Content", "draft");
        var status = taskStatusRepository.findBySlug("draft").orElseThrow();

        mockMvc.perform(delete("/api/task_statuses/" + status.getId())
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isConflict());
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

    private Long createTask(String title, String content, String status) throws Exception {
        var result = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJsonWithoutAssignee(
                                10,
                                title,
                                content,
                                status
                        )))
                .andExpect(status().isCreated())
                .andReturn();

        var response = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        return response.get("id").asLong();
    }

    private String taskJson(
            Integer index,
            Long assigneeId,
            String title,
            String content,
            String status
    ) throws Exception {
        var body = Map.of(
                "index", index,
                "assignee_id", assigneeId,
                "title", title,
                "content", content,
                "status", status
        );

        return objectMapper.writeValueAsString(body);
    }

    private String taskJsonWithoutAssignee(
            Integer index,
            String title,
            String content,
            String status
    ) throws Exception {
        var body = Map.of(
                "index", index,
                "title", title,
                "content", content,
                "status", status
        );

        return objectMapper.writeValueAsString(body);
    }

    private String updateTaskJson(String title, String content) throws Exception {
        var body = Map.of(
                "title", title,
                "content", content
        );

        return objectMapper.writeValueAsString(body);
    }

    private String updateTaskStatusJson(String status) throws Exception {
        var body = Map.of(
                "status", status
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
