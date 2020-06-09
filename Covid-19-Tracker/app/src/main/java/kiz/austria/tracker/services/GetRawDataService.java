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
    private JSONRawData mRawDataDOHFromHerokuapp;
    private JSONRawData mRawDataPhilippinesFromHerokuapp;
    private JSONRawData mRawDataCountriesFromHerokuapp;

    @Override
    public void onDownloadCompleteFromApify(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mRawDataFromApify.isCancelled()) {
            Log.d(TAG, "onDownloadComplete() received data from apify link.");
            mReceiver.onReceivedApifyData(true, data);
            mReceiver.onDataCompleted();
        }
    }

    @Override
    public void onDownloadCompleteDOHDataFromHerokuapp(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mRawDataDOHFromHerokuapp.isCancelled()) {
            Log.d(TAG, "onDownloadComplete() received DOH data from herokuapp link.");
            mReceiver.onReceivedDOHDropHerokuappData(true, data);
            mReceiver.onDataCompleted();
        }
    }

    @Override
    public void onDownloadCompletePhilippinesDataFromHerokuapp(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mRawDataPhilippinesFromHerokuapp.isCancelled()) {
            Log.d(TAG, "onDownloadComplete() received Philippines data from herokuapp link.");
            mReceiver.onReceivedPhilippinesHerokuappData(true, data);
            mReceiver.onDataCompleted();
        }
    }

    @Override
    public void onDownloadCompleteCountriesDataFromHerokuapp(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mRawDataPhilippinesFromHerokuapp.isCancelled()) {
            Log.d(TAG, "onDownloadComplete() received Countries data from herokuapp link.");
            mReceiver.onReceivedCountriesHerokuappData(true, data);
            mReceiver.onDataCompleted();
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
        mRawDataDOHFromHerokuapp = new JSONRawData(this);
        mRawDataPhilippinesFromHerokuapp = new JSONRawData(this);
        mRawDataCountriesFromHerokuapp = new JSONRawData(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() service is now running on different thread.");
        new Thread(() -> {
            mRawDataFromApify.execute(Addresses.Link.DATA_PHILIPPINES_FROM_APIFY);
            mRawDataDOHFromHerokuapp.execute(Addresses.Link.DATA_PHILIPPINES_DOHDATA_DROP_FROM_HEROKUAPP);
            mRawDataPhilippinesFromHerokuapp.execute(Addresses.Link.DATA_PHILIPPINES_FROM_HEROKUAPP);
            mRawDataCountriesFromHerokuapp.execute(Addresses.Link.DATA_COUNTRIES_FROM_HEROKUAPP);
        }).start();
        return START_STICKY;
    }

    public interface RawDataReceiver {

        void onDataCompleted();

        void onReceivedApifyData(boolean isReceived, String data);

        void onReceivedDOHDropHerokuappData(boolean isReceived, String data);

        void onReceivedPhilippinesHerokuappData(boolean isReceived, String data);

        void onReceivedCountriesHerokuappData(boolean isReceived, String data);

    }

    public class RawDataServiceBinder extends Binder {
        public GetRawDataService getInstance() {
            return GetRawDataService.this;
        }
    }

    public void shutdown() {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() was stopped by host!");
        if (mRawDataFromApify != null) mRawDataFromApify.cancel(true);
        if (mRawDataDOHFromHerokuapp != null) mRawDataDOHFromHerokuapp.cancel(true);
        if (mRawDataPhilippinesFromHerokuapp != null) mRawDataPhilippinesFromHerokuapp.cancel(true);
        if (mRawDataCountriesFromHerokuapp != null) mRawDataCountriesFromHerokuapp.cancel(true);
    }
}
