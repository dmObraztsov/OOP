class HeapUtils {

    private final int[] array; // было: a
    private int size;

    HeapUtils(int[] array, int size) { // было: (int[] a, int size)
        this.array = array;
        this.size = size;
    }

    void setSize(int size) {
    private final int[] array; //
    private int size;

    HeapUtils(int[] array, int size) {
        this.array = array;
        this.size = size;
    }

    int parent(int i) {
        return (i - 1) / 2;
    }

    int left(int i) {
        return 2 * i + 1;
    }

    void swap(int i, int j) {
        int t = array[i];
        array[i] = array[j];
        array[j] = t;
    }

    void siftDown(int i) {
        while (true) {
            int left = left(i);
            int right = left + 1;
            int largest = i;

            if (left < size && array[left] > array[largest]) {
                largest = left;
            }
            if (right < size && array[right] > array[largest]) {
                largest = right;
            }
            if (largest == i) {
                break;
            }

            swap(i, largest);
            i = largest;
        }
    }

    void swap(int i, int j) {
        int t = array[i];
        array[i] = array[j];
        array[j] = t;
    }

    void setSize(int size) {
        this.size = size;
    }
}
