package hexlet.code.controller;

import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.UserRepository;
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
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testGetLabelsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetLabels() throws Exception {
        mockMvc.perform(get("/api/labels")
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetLabelById() throws Exception {
        var label = labelRepository.findByName("bug").orElseThrow();

        mockMvc.perform(get("/api/labels/" + label.getId())
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(label.getId()))
                .andExpect(jsonPath("$.name").value(label.getName()));
    }

    @Test
    void testCreateLabel() throws Exception {
        mockMvc.perform(post("/api/labels")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(labelJson("new label")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("new label"));
    }

    @Test
    void testCreateLabelWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/labels")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(labelJson("ab")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateLabel() throws Exception {
        var id = createLabel("old label");

        mockMvc.perform(put("/api/labels/" + id)
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(labelJson("updated label")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated label"));
    }

    @Test
    void testUpdateLabelWithInvalidData() throws Exception {
        var id = createLabel("invalid update label");

        mockMvc.perform(put("/api/labels/" + id)
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(labelJson("ab")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteLabel() throws Exception {
        var id = createLabel("delete label");

        mockMvc.perform(delete("/api/labels/" + id)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/labels/" + id)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCannotDeleteLabelWithTask() throws Exception {
        var labelId = createLabel("task label");
        var assigneeId = userRepository.findByEmail("hexlet@example.com").orElseThrow().getId();

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson(assigneeId, labelId)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/labels/" + labelId)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isConflict());
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

    private Long createLabel(String name) throws Exception {
        var result = mockMvc.perform(post("/api/labels")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(labelJson(name)))
                .andExpect(status().isCreated())
                .andReturn();

        var response = result.getResponse().getContentAsString();
        return Long.parseLong(response.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    private String labelJson(String name) {
        return """
                {
                    "name": "%s"
                }
                """.formatted(name);
    }

    private String taskJson(Long assigneeId, Long labelId) {
        return """
                {
                    "index": 100,
                    "assignee_id": %d,
                    "title": "Task with label",
                    "content": "Content",
                    "status": "draft",
                    "label_ids": [%d]
                }
                """.formatted(assigneeId, labelId);
    }
}
