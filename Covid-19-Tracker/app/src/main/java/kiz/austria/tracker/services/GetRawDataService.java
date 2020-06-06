package kiz.austria.tracker.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import kiz.austria.tracker.data.Addresses;
import kiz.austria.tracker.data.JSONRawData;

public class GetRawDataService extends Service implements JSONRawData.OnDownloadComplete {

    private static final String TAG = "GetRawDataService";

    private RawDataReceiver mReceiver;//callback
    private IBinder mIBinder = new RawDataServiceBinder();
    private JSONRawData mRawDataFromApify;

    @Override
    public void onDownloadComplete(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mRawDataFromApify.isCancelled()) {
            Log.d(TAG, "onDownloadComplete() received data from apify link.");
            mReceiver.onReceivedApifyData(true, data);
        }
    }

    public void registerClientReceiver(Activity receiver) {
        mReceiver = (RawDataReceiver) receiver;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onCreate() {
        mRawDataFromApify = new JSONRawData(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() service is now running on different thread.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mRawDataFromApify.execute(Addresses.Link.DATA_PHILIPPINES_FROM_APIFY);
            }
        }).start();
        return START_STICKY;
    }

    public interface RawDataReceiver {
        void onReceivedApifyData(boolean isReceived, String data);

    }

    public class RawDataServiceBinder extends Binder {
        public GetRawDataService getInstance() {
            return GetRawDataService.this;
        }
    }
}
