package kiz.austria.tracker.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener listener;

    public ConnectivityReceiver() {
        super();
    }

    public static boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) TrackerApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
            assert capabilities != null;

            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);

        } else {

            NetworkInfo info = manager.getActiveNetworkInfo();
            assert info != null;

            return info.isConnected() || info.isAvailable();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());

            boolean isConnected = capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN));

            listener.onNetworkConnectionChanged(isConnected);
        } else {

            NetworkInfo info = manager.getActiveNetworkInfo();

            boolean isConnected = info != null && (info.isConnected() || info.isAvailable());

            listener.onNetworkConnectionChanged(isConnected);
        }
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
