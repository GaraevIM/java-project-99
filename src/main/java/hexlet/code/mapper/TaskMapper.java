package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assigneeId", ignore = true)
    @Mapping(target = "taskLabelIds", ignore = true)
    Task map(TaskCreateDTO dto);
}
