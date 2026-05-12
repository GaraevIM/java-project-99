package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            TaskStatusRepository taskStatusRepository,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    public Task create(TaskCreateDTO data) {
        var status = taskStatusRepository.findBySlug(data.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));

        var task = new Task();
        task.setIndex(data.getIndex());
        task.setTitle(data.getTitle());
        task.setContent(data.getContent());
        task.setTaskStatus(status);

        if (data.getAssigneeId() != null) {
            var assignee = userRepository.findById(data.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignee(assignee);
        }

        return taskRepository.save(task);
    }

    public Task update(Long id, TaskUpdateDTO data) {
        var task = getById(id);

        if (data.getIndex() != null) {
            task.setIndex(data.getIndex());
        }

        if (data.getTitle() != null) {
            task.setTitle(data.getTitle());
        }

        if (data.getContent() != null) {
            task.setContent(data.getContent());
        }

        if (data.getStatus() != null) {
            var status = taskStatusRepository.findBySlug(data.getStatus())
                    .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
            task.setTaskStatus(status);
        }

        if (data.getAssigneeId() != null) {
            var assignee = userRepository.findById(data.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignee(assignee);
        }

        return taskRepository.save(task);
    }

    public void delete(Long id) {
        var task = getById(id);
        taskRepository.delete(task);
    }
}
