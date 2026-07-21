package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Set;

public class TaskDTO {

    private Long id;

    private Integer index;

    private String title;

    private String content;

    private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private Set<Long> taskLabelIds;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long taskId) {
        this.id = taskId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer taskIndex) {
        this.index = taskIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String taskTitle) {
        this.title = taskTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String taskContent) {
        this.content = taskContent;
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

    public void setAssigneeId(Long taskAssigneeId) {
        this.assigneeId = taskAssigneeId;
    }

    public Set<Long> getTaskLabelIds() {
        return taskLabelIds;
    }

    public void setTaskLabelIds(Set<Long> labelIds) {
        this.taskLabelIds = labelIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime taskCreatedAt) {
        this.createdAt = taskCreatedAt;
    }
}
