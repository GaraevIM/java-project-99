package hexlet.code.model;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    @Test
    void testGettersAndSetters() {
        var user = new User();

        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("secret");

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertNull(user.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() throws Exception {
        var first = new User();
        var second = new User();

        Field id = User.class.getDeclaredField("id");
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
