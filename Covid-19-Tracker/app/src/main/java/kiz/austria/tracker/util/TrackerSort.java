package kiz.austria.tracker.util;

import java.util.List;

import kiz.austria.tracker.model.PHTrend;

public class TrackerSort {

    public static void quickSort(List<PHTrend> trends, String flag, int start, int end) {
        if (end - start < 2) {
            return;
        }
        int pivotIndex = partition(trends, flag, start, end);
        quickSort(trends, flag, start, pivotIndex);
        quickSort(trends, flag, pivotIndex + 1, end);
    }

    private static int partition(List<PHTrend> trends, String flag, int start, int end) {
        // This is using the first element as the pivot
        int pivot = trends.indexOf(trends.get(start));//input[start];

        int i = start;
        int j = end;

        while (i < j) {

            // NOTE: empty loop body
            switch (flag) {
                case "INFECTED":
                    while (i < j && Integer.parseInt(trends.get(--j).getInfected()) >= Integer.parseInt(trends.get(pivot).getInfected()))
                        ;
                    break;
                case "RECOVERED":
                    while (i < j && Integer.parseInt(trends.get(--j).getRecovered()) >= Integer.parseInt(trends.get(pivot).getRecovered()))
                        ;
                    break;
                case "DECEASED":
                    while (i < j && Integer.parseInt(trends.get(--j).getDeceased()) >= Integer.parseInt(trends.get(pivot).getDeceased()))
                        ;
                    break;
            }
            if (i < j) {
                trends.set(i, trends.get(j));
            }

            // NOTE: empty loop body
            switch (flag) {
                case "INFECTED":
                    while (i < j && Integer.parseInt(trends.get(++i).getInfected()) <= Integer.parseInt(trends.get(pivot).getInfected()))
                        ;
                    break;
                case "RECOVERED":
                    while (i < j && Integer.parseInt(trends.get(++i).getRecovered()) <= Integer.parseInt(trends.get(pivot).getRecovered()))
                        ;
                    break;
                case "DECEASED":
                    while (i < j && Integer.parseInt(trends.get(++i).getDeceased()) <= Integer.parseInt(trends.get(pivot).getDeceased()))
                        ;
                    break;
            }
            if (i < j) {
                trends.set(j, trends.get(i));
            }

        }

        trends.set(j, trends.get(pivot));
        return j;

    }


    public static void insertionSort(List<PHTrend> trends) {
        for (int firstUnsortedIndex = 1; firstUnsortedIndex < trends.size();
             firstUnsortedIndex++) {
            int newElement = trends.indexOf(trends.get(firstUnsortedIndex));

            int i;

            for (i = firstUnsortedIndex; i > 0 && Integer.parseInt(trends.get(i - 1).getInfected()) > Integer.parseInt(trends.get(newElement).getInfected()); i--) {
                trends.set(i, trends.get(i - 1));
            }
            trends.set(i, trends.get(newElement));
        }
    }
}
