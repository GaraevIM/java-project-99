package hexlet.code.model;

import java.lang.reflect.Field;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    @Test
    void testGettersAndSetters() {
        var task = new Task();

        task.setIndex(10);
        task.setTitle("Title");
        task.setContent("Content");

        var status = new TaskStatus();
        status.setSlug("draft");
        task.setTaskStatus(status);

        var user = new User();
        setId(user, 5L);
        task.setAssignee(user);

        var label = new Label();
        setId(label, 11L);
        task.setLabels(Set.of(label));

        assertEquals(10, task.getIndex());
        assertEquals("Title", task.getTitle());
        assertEquals("Content", task.getContent());

        assertEquals(status, task.getTaskStatus());
        assertEquals(user, task.getAssignee());
        assertEquals(Set.of(label), task.getLabels());

        assertEquals("draft", task.getStatus());
        assertEquals(5L, task.getAssigneeId());
        assertEquals(Set.of(11L), task.getTaskLabelIds());
    }

    @Test
    void testNullDerivedGetters() {
        var task = new Task();

        assertNull(task.getStatus());
        assertNull(task.getAssigneeId());
        assertTrue(task.getTaskLabelIds().isEmpty());
    }

    @Test
    void testEqualsAndHashCode() throws Exception {
        var first = new Task();
        var second = new Task();

        Field id = Task.class.getDeclaredField("id");
        id.setAccessible(true);

        id.set(first, 1L);
        id.set(second, 1L);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        assertEquals(first, first);
        assertNotEquals(first, null);
        assertNotEquals(first, new Object());
    }

    private void setId(Object object, Long value) {
        try {
            Field field = object.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
