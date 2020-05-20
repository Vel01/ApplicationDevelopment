package kiz.austria.tracker.broadcast;

import android.app.Application;

public class TrackerApplication extends Application {

    private static TrackerApplication instance;

    public static synchronized TrackerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.listener = listener;
    }
}
