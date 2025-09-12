package heapsort;

public final class HeapSort {
    private HeapSort() {
    }

    public static void sort(int[] a) {
        if (a == null) throw new NullPointerException("Input array must not be null");
        int n = a.length;

        for (int i = HeapUtils.parent(n - 1); i >= 0; i--) {
            HeapUtils.siftDown(a, i, n);
        }
        for (int end = n - 1; end > 0; end--) {
            HeapUtils.swap(a, 0, end);
            HeapUtils.siftDown(a, 0, end);
        }
    }
}
