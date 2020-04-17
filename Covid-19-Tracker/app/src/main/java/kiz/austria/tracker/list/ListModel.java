package kiz.austria.tracker.list;

import java.util.ArrayList;

import kiz.austria.tracker.model.Countries;

public class ListModel<T extends Countries> {

    private final ArrayList<T> coverages = new ArrayList<>();

    public void addCoverage(T coverage) {
        if (coverages.contains(coverage)) {
            return;
        }
        coverages.add(coverage);
    }

    public ArrayList<T> getCoverages() {
        return coverages;
    }
}
