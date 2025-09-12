package heapsort;

final class HeapUtils {
    private HeapUtils() {
    }

    public static void siftDown(int[] a, int i, int size) {
        while (true) {
            int left = left(i);
            int right = left + 1;
            int largest = i;

            if (left < size && a[left] > a[largest]) {
                largest = left;
            }
            if (right < size && a[right] > a[largest]) {
                largest = right;
            }
            if (largest == i) {
                break;
            }

            swap(a, i, largest);
            i = largest;
        }
    }

    public static int parent(int i) {
        return (i - 1) / 2;
    }

    public static int left(int i) {
        return 2 * i + 1;
    }

    public static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
