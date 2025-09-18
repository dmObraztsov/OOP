import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import java.util.Random;


class HeapSortTest {

    @Test
    void handlesEmpty() {
        int[] a = {};
        HeapSort.sort(a);
        assertArrayEquals(new int[]{}, a);
    }

    @Test
    void singleElement() {
        int[] a = {42};
        HeapSort.sort(a);
        assertArrayEquals(new int[]{42}, a);
    }

    @Test
    void duplicates() {
        int[] a = {5, 1, 5, 3, 5, 2};
        int[] ref = a.clone();
        Arrays.sort(ref);
        HeapSort.sort(a);
        assertArrayEquals(ref, a);
    }

    @Test
    void alreadySorted() {
        int[] a = {-3, -1, 0, 2, 4, 9};
        int[] ref = a.clone();
        HeapSort.sort(a);
        assertArrayEquals(ref, a);
    }

    @Test
    void reverseSorted() {
        int[] a = {9, 4, 2, 0, -1, -3};
        int[] ref = a.clone();
        Arrays.sort(ref);
        HeapSort.sort(a);
        assertArrayEquals(ref, a);
    }

    @Test
    void randomLargeEqualsJdkSort() {
        Random rnd = new Random(7);
        int[] a = rnd.ints(200_000, -1_000_000, 1_000_000).toArray();
        int[] ref = a.clone();
        Arrays.sort(ref);
        HeapSort.sort(a);
        assertArrayEquals(ref, a);
    }
}
