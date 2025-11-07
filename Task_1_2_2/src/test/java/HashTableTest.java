import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class HashTableTest {

    private HashTable<String, Integer> table;

    @BeforeEach
    void setUp() {
        table = new HashTable<>();
        table.put("one", 1);
        table.put("two", 2);
        table.put("three", 3);
    }

    @Test
    void testSizeAndIsEmpty() {
        assertEquals(3, table.size());
        assertFalse(table.isEmpty());
        table.clear();
        assertTrue(table.isEmpty());
    }

    @Test
    void testPutAndGet() {
        assertEquals(1, table.get("one"));
        assertEquals(2, table.get("two"));
        assertNull(table.get("four"));
    }

    @Test
    void testUpdateValue() {
        table.put("one", 10);
        assertEquals(10, table.get("one"));
        assertEquals(3, table.size());
    }

    @Test
    void testRemoveByKey() {
        assertEquals(2, table.remove("two"));
        assertNull(table.get("two"));
        assertEquals(2, table.size());
    }

    @Test
    void testRemoveByKeyAndValue() {
        assertTrue(table.remove("one", 1));
        assertFalse(table.remove("three", 100));
        assertNull(table.get("one"));
    }

    @Test
    void testContainsKey() {
        assertTrue(table.containsKey("three"));
        assertFalse(table.containsKey("four"));
    }

    @Test
    void testClear() {
        table.clear();
        assertEquals(0, table.size());
        assertNull(table.get("one"));
    }

    @Test
    void testEqualsAndHashCode() {
        HashTable<String, Integer> other = new HashTable<>();
        other.put("one", 1);
        other.put("two", 2);
        other.put("three", 3);
        assertEquals(table, other);
        assertEquals(table.hashCode(), other.hashCode());
        other.put("four", 4);
        assertNotEquals(table, other);
    }

    @Test
    void testToStringFormat() {
        String s = table.toString();
        assertTrue(s.startsWith("{") && s.endsWith("}"));
        assertTrue(s.contains("one") && s.contains("two") && s.contains("three"));
    }

    @Test
    void testIteratorTraversal() {
        int count = 0;
        for (Map.Entry<String, Integer> e : table) {
            assertNotNull(e.getKey());
            assertNotNull(e.getValue());
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testIteratorConcurrentModification() {
        Iterator<Map.Entry<String, Integer>> it = table.iterator();
        table.put("new", 99);
        assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    void testIteratorRemove() {
        Iterator<Map.Entry<String, Integer>> it = table.iterator();
        assertTrue(it.hasNext());
        Map.Entry<String, Integer> e = it.next();
        it.remove();
        assertFalse(table.containsKey(e.getKey()));
    }
}
