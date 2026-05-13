package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class TaskCreateDTO {

    private Integer index;

    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @JsonProperty("label_ids")
    private Set<Long> labelIds;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String description) {
        this.content = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String taskStatus) {
        this.status = taskStatus;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assignee) {
        this.assigneeId = assignee;
    }

    public Set<Long> getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(Set<Long> labelIds) {
        this.labelIds = labelIds;
    }
}
