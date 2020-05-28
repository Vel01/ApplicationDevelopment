package kiz.austria.tracker.util;

import java.util.Collections;
import java.util.List;

import kiz.austria.tracker.model.PHTrend;

public class TrackerSort {

//    public static void quickSort(List<PHTrend> trends, int start, int end) {
//        if (end - start < 2) {
//            return;
//        }
//
//        int pivotIndex = partition(trends, start, end);
//        quickSort(trends, start, pivotIndex);
//        quickSort(trends, pivotIndex + 1, end);
//    }
//
//    private static int partition(List<PHTrend> trends, int start, int end) {
//        // This is using the first element as the pivot
//        int pivot = Integer.parseInt(trends.get(start).getInfected());//input[start];
//        int i = start;
//        int j = end;
//
//        while (i < j) {
//
//            // NOTE: empty loop body
//            while (i < j && Integer.parseInt(trends.get(--j).getInfected()) >= pivot) ;
//            if (i < j) {
//                //input[i] = input[j];
//                Collections.swap(trends, i, j);
//            }
//
//            // NOTE: empty loop body
//            while (i < j && Integer.parseInt(trends.get(++i).getInfected()) <= pivot) ;
//            if (i < j) {
////                input[j] = input[i];
//                Collections.swap(trends, j, i);
//            }
//
//        }
//
////        input[j] = pivot;
//        Collections.swap(trends, j, start);
//        return j;
//
//    }


    public static void quickSort(List<PHTrend> trends, String flag, int start, int end) {
        if (end - start < 2) {
            return;
        }

        int pivotIndex = partition(trends, flag, start, end);
        quickSort(trends, flag, start, pivotIndex);
        quickSort(trends, flag, pivotIndex + 1, end);
    }

    private static int setPivot(List<PHTrend> trends, String flag, int start) {
        switch (flag) {
            case "INFECTED":
                return Integer.parseInt(trends.get(start).getInfected());//input[start];
            case "RECOVERED":
                return Integer.parseInt(trends.get(start).getRecovered());//input[start];
            case "DECEASED":
                return Integer.parseInt(trends.get(start).getDeceased());//input[start];
        }
        return 0;
    }


    private static int partition(List<PHTrend> trends, String flag, int start, int end) {
        // This is using the first element as the pivot
        int pivot = setPivot(trends, flag, start);

        int i = start;
        int j = end;

        while (i < j) {

            // NOTE: empty loop body
//            while (i < j && Integer.parseInt(trends.get(--j).getInfected()) >= pivot) ;
            switch (flag) {
                case "INFECTED":
                    while (i < j && Integer.parseInt(trends.get(--j).getInfected()) >= pivot) ;
                    break;
                case "RECOVERED":
                    while (i < j && Integer.parseInt(trends.get(--j).getRecovered()) >= pivot) ;
                    break;
                case "DECEASED":
                    while (i < j && Integer.parseInt(trends.get(--j).getDeceased()) >= pivot) ;
                    break;
            }

            if (i < j) {
                //input[i] = input[j];
                Collections.swap(trends, i, j);
            }

            // NOTE: empty loop body
//            while (i < j && Integer.parseInt(trends.get(++i).getInfected()) <= pivot) ;
            switch (flag) {
                case "INFECTED":
                    while (i < j && Integer.parseInt(trends.get(++i).getInfected()) >= pivot) ;
                    break;
                case "RECOVERED":
                    while (i < j && Integer.parseInt(trends.get(++i).getRecovered()) >= pivot) ;
                    break;
                case "DECEASED":
                    while (i < j && Integer.parseInt(trends.get(++i).getDeceased()) >= pivot) ;
                    break;
            }
            if (i < j) {
//                input[j] = input[i];
                Collections.swap(trends, j, i);
            }

        }

//        input[j] = pivot;
        Collections.swap(trends, j, start);
        return j;

    }
}
