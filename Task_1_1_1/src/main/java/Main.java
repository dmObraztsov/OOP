import java.util.Arrays;

/**
 * Демонстрация работы пирамидальной сортировки (HeapSort).
 * Читает числа из аргументов командной строки или использует демо-массив.
 */
public class Main {

    /**
     * Точка входа приложения.
     * Если переданы аргументы, каждый должен быть целым числом.
     *
     * @param args числа для сортировки; если пусто — используется демо-массив
     */
    public static void main(String[] args) {
        int[] a;
        if (args.length > 0) {
            a = new int[args.length];
            for (int i = 0; i < args.length; i++) {
                a[i] = Integer.parseInt(args[i]);
            }
        } else {
            a = new int[]{5, 1, 5, 3, 2, 9, 0, -4}; // demo
        }
        HeapSort.sort(a);
        System.out.println(Arrays.toString(a));
    }
}
