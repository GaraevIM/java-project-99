package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import jakarta.persistence.criteria.JoinType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final UserRepository userRepository;

    private final LabelRepository labelRepository;

    public TaskService(
            TaskRepository taskRepository,
            TaskStatusRepository taskStatusRepository,
            UserRepository userRepository,
            LabelRepository labelRepository
    ) {
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
    }

    public List<Task> getAll(String titleCont, Long assigneeId, String status, Long labelId) {
        var specification = buildSpecification(titleCont, assigneeId, status, labelId);
        return taskRepository.findAll(specification);
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

        if (data.getLabelIds() != null) {
            task.setLabels(getLabelsByIds(data.getLabelIds()));
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

        if (data.getLabelIds() != null) {
            task.setLabels(getLabelsByIds(data.getLabelIds()));
        }

        return taskRepository.save(task);
    }

    public void delete(Long id) {
        var task = getById(id);
        taskRepository.delete(task);
    }

    private Set<Label> getLabelsByIds(Set<Long> ids) {
        var labels = labelRepository.findAllByIdIn(ids);

        if (labels.size() != ids.size()) {
            throw new ResourceNotFoundException("Label not found");
        }

        return new HashSet<>(labels);
    }

    private Specification<Task> buildSpecification(
            String titleCont,
            Long assigneeId,
            String status,
            Long labelId
    ) {
        return Specification.where(titleContains(titleCont))
                .and(hasAssignee(assigneeId))
                .and(hasStatus(status))
                .and(hasLabel(labelId));
    }

    private Specification<Task> titleContains(String titleCont) {
        return (root, query, builder) -> {
            if (titleCont == null || titleCont.isBlank()) {
                return null;
            }

            return builder.like(
                    builder.lower(root.get("title")),
                    "%" + titleCont.toLowerCase() + "%"
            );
        };
    }

    private Specification<Task> hasAssignee(Long assigneeId) {
        return (root, query, builder) -> {
            if (assigneeId == null) {
                return null;
            }

            return builder.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    private Specification<Task> hasStatus(String status) {
        return (root, query, builder) -> {
            if (status == null || status.isBlank()) {
                return null;
            }

            return builder.equal(root.get("taskStatus").get("slug"), status);
        };
    }

    private Specification<Task> hasLabel(Long labelId) {
        return (root, query, builder) -> {
            if (labelId == null) {
                return null;
            }

            query.distinct(true);
            var labels = root.join("labels", JoinType.INNER);
            return builder.equal(labels.get("id"), labelId);
        };
    }
}
