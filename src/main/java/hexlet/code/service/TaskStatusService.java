package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import java.util.List;

public interface TaskStatusService {

    List<TaskStatus> getAll();

    TaskStatus getById(Long id);

    TaskStatus create(TaskStatusCreateDTO data);

    TaskStatus update(Long id, TaskStatusUpdateDTO data);

    void delete(Long id);

    void createDefaultStatuses();
}
