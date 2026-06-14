package hexlet.code.dto;

import jakarta.validation.constraints.Size;

public class TaskUpdateDTO extends BaseTaskDTO {

    @Override
    @Size(min = 1)
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    @Size(min = 1)
    public String getStatus() {
        return super.getStatus();
    }
}
