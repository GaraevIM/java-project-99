package hexlet.code.dto;

import jakarta.validation.constraints.Size;

public class TaskStatusUpdateDTO {

    @Size(min = 1)
    private String name;

    @Size(min = 1)
    private String slug;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
