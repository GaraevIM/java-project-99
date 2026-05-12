package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceConflictException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    private final TaskRepository taskRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository, TaskRepository taskRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
    }

    public List<TaskStatus> getAll() {
        return taskStatusRepository.findAll();
    }

    public TaskStatus getById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
    }

    public TaskStatus create(TaskStatusCreateDTO data) {
        var status = new TaskStatus();
        status.setName(data.getName());
        status.setSlug(data.getSlug());
        return taskStatusRepository.save(status);
    }

    public TaskStatus update(Long id, TaskStatusUpdateDTO data) {
        var status = getById(id);

        if (data.getName() != null) {
            status.setName(data.getName());
        }

        if (data.getSlug() != null) {
            status.setSlug(data.getSlug());
        }

        return taskStatusRepository.save(status);
    }

    public void delete(Long id) {
        var status = getById(id);

        if (taskRepository.existsByTaskStatus(status)) {
            throw new ResourceConflictException("Task status has tasks");
        }

        taskStatusRepository.delete(status);
    }

    public void createDefaultStatuses() {
        createIfNotExists("Draft", "draft");
        createIfNotExists("ToReview", "to_review");
        createIfNotExists("ToBeFixed", "to_be_fixed");
        createIfNotExists("ToPublish", "to_publish");
        createIfNotExists("Published", "published");
    }

    private void createIfNotExists(String name, String slug) {
        if (!taskStatusRepository.existsBySlug(slug)) {
            var data = new TaskStatusCreateDTO();
            data.setName(name);
            data.setSlug(slug);
            create(data);
        }
    }
}
