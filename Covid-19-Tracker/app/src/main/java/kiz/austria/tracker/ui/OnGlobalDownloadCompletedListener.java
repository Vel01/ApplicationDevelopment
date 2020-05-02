package kiz.austria.tracker.ui;

import kiz.austria.tracker.model.Nation;

public interface OnGlobalDownloadCompletedListener {
    void onDataAvailable(Nation nation);
}
