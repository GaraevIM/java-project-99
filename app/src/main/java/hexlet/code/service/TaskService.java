package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Task;
import java.util.List;

public interface TaskService {

    List<Task> getAll(String titleCont, Long assigneeId, String status, Long labelId);

    Task getById(Long id);

    Task create(TaskCreateDTO data);

    Task update(Long id, TaskUpdateDTO data);

    void delete(Long id);
}
