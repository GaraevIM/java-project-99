package hexlet.code.specification;

import hexlet.code.model.Task;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<Task> build(
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

    private static Specification<Task> titleContains(String titleCont) {
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

    private static Specification<Task> hasAssignee(Long assigneeId) {
        return (root, query, builder) -> {
            if (assigneeId == null) {
                return null;
            }

            return builder.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    private static Specification<Task> hasStatus(String status) {
        return (root, query, builder) -> {
            if (status == null || status.isBlank()) {
                return null;
            }

            return builder.equal(root.get("taskStatus").get("slug"), status);
        };
    }

    private static Specification<Task> hasLabel(Long labelId) {
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
