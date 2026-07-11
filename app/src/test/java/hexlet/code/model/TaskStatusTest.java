package hexlet.code.model;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskStatusTest {

    @Test
    void testGettersAndSetters() {
        var status = new TaskStatus();

        status.setName("Draft");
        status.setSlug("draft");

        assertEquals("Draft", status.getName());
        assertEquals("draft", status.getSlug());
    }

    @Test
    void testEqualsAndHashCode() throws Exception {
        var first = new TaskStatus();
        var second = new TaskStatus();

        Field id = TaskStatus.class.getDeclaredField("id");
        id.setAccessible(true);

        id.set(first, 1L);
        id.set(second, 1L);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        assertEquals(first, first);
        assertNotEquals(null, first);
        assertNotEquals(first, new Object());
    }
}
