package kiz.austria.tracker.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import kiz.austria.tracker.R;
import kiz.austria.tracker.broadcast.ConnectivityReceiver;
import kiz.austria.tracker.broadcast.TrackerApplication;
import kiz.austria.tracker.data.DownloadedData;
import kiz.austria.tracker.data.services.GetRawDataService;
import kiz.austria.tracker.util.TrackerUtility;

public class SplashScreen extends AppCompatActivity implements GetRawDataService.RawDataReceiver, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "SplashScreen";
    //references
    private ConnectivityReceiver receiver;

    private boolean isRawFromApifyCompleted = false;
    private boolean isRawDOHFromHerokuappCompleted = false;
    private boolean isRawPhilippinesFromHerokuappCompleted = false;
    private boolean isRawCountriesFromHerokuappCompleted = false;
    private boolean isRawCasesFromHerokuappCompleted = false;

    private ServiceConnection mDownloadDataServiceConnection;
    private Intent mDownloadDataServiceIntent;
    private GetRawDataService mGetRawDataServiceReference;
    private boolean mIsServiceBound = false;
    private boolean mIsSplashPaused = false;
    private boolean mIsDownloadCompletedInForeground = true;

    private ViewGroup mRootSplash;

    @Override
    public void onDataCompleted() {
        if (mIsDownloadCompletedInForeground) {
            if (isRawFromApifyCompleted
                    && isRawDOHFromHerokuappCompleted
                    && isRawPhilippinesFromHerokuappCompleted
                    && isRawCountriesFromHerokuappCompleted
                    && isRawCasesFromHerokuappCompleted) {
                Log.d(TAG, "onDataCompleted() is completed ");
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in_start_activity, R.anim.fade_out_end_activity);
            }
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (!mIsSplashPaused) {
                startService(mDownloadDataServiceIntent);
                bindDownloadDataService();
            }
        } else {
            TrackerUtility.message(this, "No Internet Connection",
                    R.drawable.ic_signal_wifi_off, R.color.md_white_1000,
                    R.color.toast_connection_lost);
            unbindDownloadDataService();
            if (!mIsServiceBound) stopService(mDownloadDataServiceIntent);
        }
    }

    @Override
    public void onReceivedApifyData(boolean isReceived, String data) {
        isRawFromApifyCompleted = isReceived;
        if (isReceived) {
            Log.d(TAG, "onReceivedApifyData() data received by host activity.");
            DownloadedData.getInstance().saveApifyData(data);
        }
    }

    @Override
    public void onReceivedDOHDropHerokuappData(boolean isReceived, String data) {
        isRawDOHFromHerokuappCompleted = isReceived;
        if (isReceived) {
            Log.d(TAG, "onReceivedDOHDropHerokuappData() data received by host activity.");
            DownloadedData.getInstance().saveDOHData(data);
        }
    }

    @Override
    public void onReceivedPhilippinesHerokuappData(boolean isReceived, String data) {
        isRawPhilippinesFromHerokuappCompleted = isReceived;
        if (isReceived) {
            Log.d(TAG, "onReceivedPhilippinesHerokuappData() data received by host activity.");
            DownloadedData.getInstance().savePhilippinesData(data);
        }
    }

    @Override
    public void onReceivedCountriesHerokuappData(boolean isReceived, String data) {
        isRawCountriesFromHerokuappCompleted = isReceived;
        if (isReceived) {
            Log.d(TAG, "onReceivedCountriesHerokuappData() data received by host activity.");
            DownloadedData.getInstance().saveCountriesData(data);
        }
    }

    @Override
    public void onReceivedCasesHerokuappData(boolean isReceived, String data) {
        isRawCasesFromHerokuappCompleted = isReceived;
        if (isReceived) {
            Log.d(TAG, "onReceivedCountriesHerokuappData() data received by host activity.");
            DownloadedData.getInstance().saveCasesData(data);
        }
    }

    private void bindDownloadDataService() {
        if (mDownloadDataServiceConnection == null) {
            mDownloadDataServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Log.d(TAG, "onServiceConnected() service is now bound");
                    GetRawDataService.RawDataServiceBinder binder = (GetRawDataService.RawDataServiceBinder) service;
                    mGetRawDataServiceReference = binder.getInstance();
                    mGetRawDataServiceReference.registerClientReceiver(SplashScreen.this);
                    mIsServiceBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mIsServiceBound = false;
                }
            };
        }

        bindService(mDownloadDataServiceIntent, mDownloadDataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindDownloadDataService() {
        if (mIsServiceBound) {
            Log.d(TAG, "unbindDownloadDataService() service is unbound");
            unbindService(mDownloadDataServiceConnection);
            mIsServiceBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mRootSplash = findViewById(R.id.tracker_splash);
        mDownloadDataServiceIntent = new Intent(this, GetRawDataService.class);
        initTrackerListener();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        mIsSplashPaused = true;
        mIsDownloadCompletedInForeground = true;
        onDataCompleted();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume() was called!");
        registerTrackerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsDownloadCompletedInForeground = false;
        unregisterTrackerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
        unbindDownloadDataService();
        if (!mIsServiceBound) mGetRawDataServiceReference.shutdown();
    }

    private void unregisterTrackerReceiver() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    private void registerTrackerReceiver() {
        IntentFilter filter = new IntentFilter();
        //note: ConnectivityManager.CONNECTIVITY_ACTION is deprecated in api 28 above
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        receiver = new ConnectivityReceiver();
        registerReceiver(receiver, filter);
    }

    private void initTrackerListener() {
        TrackerApplication.getInstance().setConnectivityListener(this);
    }
}