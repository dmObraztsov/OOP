class HeapUtils {
    private final int[] a;
    private int size;

    HeapUtils(int[] a, int size) {
        this.a = a;
        this.size = size;
    }

    void setSize(int size) {
        this.size = size;
    }

    int parent(int i) {
        return (i - 1) / 2;
    }

    int left(int i) {
        return 2 * i + 1;
    }

    void swap(int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    void siftDown(int i) {
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

            swap(i, largest);
            i = largest;
        }
    }


}