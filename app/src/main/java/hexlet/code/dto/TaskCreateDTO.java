package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;

public class TaskCreateDTO extends BaseTaskDTO {

    @Override
    @NotBlank
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    @NotBlank
    public String getStatus() {
        return super.getStatus();
    }
}
