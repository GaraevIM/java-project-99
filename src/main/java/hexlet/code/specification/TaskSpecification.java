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
        return Specification.where(fetchRelations())
                .and(titleContains(titleCont))
                .and(hasAssignee(assigneeId))
                .and(hasStatus(status))
                .and(hasLabel(labelId));
    }

    private static Specification<Task> fetchRelations() {
        return (root, query, builder) -> {
            var resultType = query.getResultType();

            if (resultType != Long.class && resultType != long.class) {
                root.fetch("taskStatus", JoinType.LEFT);
                root.fetch("assignee", JoinType.LEFT);
                root.fetch("labels", JoinType.LEFT);
                query.distinct(true);
            }

            return builder.conjunction();
        };
    }

    private static Specification<Task> titleContains(String titleCont) {
        return (root, query, builder) -> {
            if (titleCont == null || titleCont.isBlank()) {
                return builder.conjunction();
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
                return builder.conjunction();
            }

            return builder.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    private static Specification<Task> hasStatus(String status) {
        return (root, query, builder) -> {
            if (status == null || status.isBlank()) {
                return builder.conjunction();
            }

            return builder.equal(root.get("taskStatus").get("slug"), status);
        };
    }

    private static Specification<Task> hasLabel(Long labelId) {
        return (root, query, builder) -> {
            if (labelId == null) {
                return builder.conjunction();
            }

            query.distinct(true);
            var labels = root.join("labels", JoinType.INNER);

            return builder.equal(labels.get("id"), labelId);
        };
    }
}
