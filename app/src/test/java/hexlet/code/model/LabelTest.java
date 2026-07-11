package hexlet.code.model;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LabelTest {

    @Test
    void testGettersAndSetters() {
        var label = new Label();

        label.setName("bug");

        assertEquals("bug", label.getName());
        assertNotNull(label.getTasks());
        assertTrue(label.getTasks().isEmpty());
    }

    @Test
    void testEqualsAndHashCode() throws Exception {
        var first = new Label();
        var second = new Label();

        Field id = Label.class.getDeclaredField("id");
        id.setAccessible(true);

        id.set(first, 1L);
        id.set(second, 1L);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        assertEquals(first, first);
        assertNotEquals(first, null);
        assertNotEquals(first, new Object());
    }
}
