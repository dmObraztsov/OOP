/**
 * Пирамидальная сортировка (HeapSort).
 * Сортирует целочисленный массив по неубыванию на месте.
 */
public class HeapSort {

    /**
     * Утилитный класс: создание экземпляров не предполагается.
     */
    private HeapSort() {
    }

    /**
     * Сортирует массив целых чисел по неубыванию (in-place) с использованием двоичной кучи.
     *
     * @param a массив для сортировки
     * @throws NullPointerException если {@code a} равен {@code null}
     */
    public static void sort(int[] a) {
        if (a == null) {
            throw new NullPointerException("Input array must not be null");
        }
        int n = a.length;

        HeapUtils heap = new HeapUtils(a, n);

        for (int i = heap.parent(n - 1); i >= 0; i--) {
            heap.siftDown(i);
        }

        for (int end = n - 1; end > 0; end--) {
            heap.swap(0, end);
            heap.setSize(end);
            heap.siftDown(0);
        }
    }
}
