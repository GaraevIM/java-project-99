package hexlet.code.controller;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @GetMapping("/api/task_statuses")
    public List<TaskStatus> index() {
        return taskStatusService.getAll();
    }

    @GetMapping("/api/task_statuses/{id}")
    public TaskStatus show(@PathVariable Long id) {
        return taskStatusService.getById(id);
    }

    @PostMapping("/api/task_statuses")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus create(@Valid @RequestBody TaskStatusCreateDTO data) {
        return taskStatusService.create(data);
    }

    @PutMapping("/api/task_statuses/{id}")
    public TaskStatus update(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateDTO data
    ) {
        return taskStatusService.update(id, data);
    }

    @DeleteMapping("/api/task_statuses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusService.delete(id);
    }
}
