package com.example.heapsort;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        int[] a;
        if (args.length > 0) {
            a = new int[args.length];
            for (int i = 0; i < args.length; i++) {
                a[i] = Integer.parseInt(args[i]);
            }
        } else {
            a = new int[] {5, 1, 5, 3, 2, 9, 0, -4};
        }
        HeapSort.sort(a);
        System.out.println(Arrays.toString(a));
    }
}
