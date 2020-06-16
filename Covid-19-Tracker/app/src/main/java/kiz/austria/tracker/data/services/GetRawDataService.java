package kiz.austria.tracker.data.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import kiz.austria.tracker.data.Addresses;
import kiz.austria.tracker.data.RawDataDownloader;

public class GetRawDataService extends Service implements RawDataDownloader.OnDownloadComplete {

    private static final String TAG = "GetRawDataService";

    private RawDataReceiver mReceiver;//callback
    private IBinder mIBinder = new RawDataServiceBinder();

    private RawDataDownloader mRawDataDownloader;

    @Override
    public void onDownloadCompleteFromApify(String data, RawDataDownloader.DownloadStatus status) {
        if (status == RawDataDownloader.DownloadStatus.OK && !mRawDataDownloader.isCancelled()) {
            Log.d(TAG, "onDownloadComplete() received data from apify link.");
            mReceiver.onReceivedApifyData(true, data);
            mReceiver.onDataCompleted();
        }
    }

    @Override
    public void onDownloadCompleteDOHDataFromHerokuapp(String data, RawDataDownloader.DownloadStatus status) {
        if (status == RawDataDownloader.DownloadStatus.OK && !mRawDataDownloader.isCancelled()) {
            Log.d(TAG, "onDownloadComplete() received DOH data from herokuapp link.");
            mReceiver.onReceivedDOHDropHerokuappData(true, data);
            mReceiver.onDataCompleted();
        }
    }

    @Override
    public void onDownloadCompletePhilippinesDataFromHerokuapp(String data, RawDataDownloader.DownloadStatus status) {
        if (status == RawDataDownloader.DownloadStatus.OK && !mRawDataDownloader.isCancelled()) {
            Log.d(TAG, "onDownloadComplete() received Philippines data from herokuapp link.");
            mReceiver.onReceivedPhilippinesHerokuappData(true, data);
            mReceiver.onDataCompleted();
        }
    }

    @Override
    public void onDownloadCompleteCountriesDataFromHerokuapp(String data, RawDataDownloader.DownloadStatus status) {
        if (status == RawDataDownloader.DownloadStatus.OK && !mRawDataDownloader.isCancelled()) {
            Log.d(TAG, "onDownloadComplete() received Countries data from herokuapp link.");
            mReceiver.onReceivedCountriesHerokuappData(true, data);
            mReceiver.onDataCompleted();
        }
    }

    @Override
    public void onDownloadCompleteCasesDataFromHerokuapp(String data, RawDataDownloader.DownloadStatus status) {
        if (status == RawDataDownloader.DownloadStatus.OK && !mRawDataDownloader.isCancelled()) {
            Log.d(TAG, "onDownloadComplete() received Countries data from herokuapp link.");
            mReceiver.onReceivedCasesHerokuappData(true, data);
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
        mRawDataDownloader = new RawDataDownloader(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() service is now running on different thread.");
        mRawDataDownloader.execute(Addresses.Link.DATA_PHILIPPINES_FROM_APIFY,
                Addresses.Link.DATA_PHILIPPINES_DOH_DROP_FROM_HEROKUAPP,
                Addresses.Link.DATA_PHILIPPINES_FROM_HEROKUAPP,
                Addresses.Link.DATA_COUNTRIES_FROM_HEROKUAPP,
                Addresses.Link.DATA_PHILIPPINES_CASES_FROM_HEROKUAPP);

        return START_STICKY;
    }

    public interface RawDataReceiver {

        void onDataCompleted();

        void onReceivedApifyData(boolean isReceived, String data);

        void onReceivedDOHDropHerokuappData(boolean isReceived, String data);

        void onReceivedPhilippinesHerokuappData(boolean isReceived, String data);

        void onReceivedCountriesHerokuappData(boolean isReceived, String data);

        void onReceivedCasesHerokuappData(boolean isReceived, String data);

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
        if (mRawDataDownloader != null) mRawDataDownloader.cancel(true);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        shutdown();
    }
}
