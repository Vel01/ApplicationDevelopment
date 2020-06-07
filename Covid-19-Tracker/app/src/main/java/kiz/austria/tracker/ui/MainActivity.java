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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.google.android.material.navigation.NavigationView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;

import kiz.austria.tracker.R;
import kiz.austria.tracker.broadcast.ConnectivityReceiver;
import kiz.austria.tracker.broadcast.TrackerApplication;
import kiz.austria.tracker.data.DownloadedData;
import kiz.austria.tracker.services.GetRawDataService;
import kiz.austria.tracker.util.TrackerDialog;
import kiz.austria.tracker.util.TrackerKeys;
import kiz.austria.tracker.util.TrackerUtility;

public class MainActivity extends BaseActivity implements
        GetRawDataService.RawDataReceiver,
        TrackerDialog.OnDialogListener,
        Inflatable,
        NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "MainActivity";

    @Override
    public void onDialogPositiveEvent(int id, Bundle args) {

    }

    @Override
    public void onDialogNegativeEvent(int id, Bundle args) {
        if (id == TrackerKeys.ACTION_DIALOG_ON_BACK_PRESSED) {
            TrackerUtility.finishFade(this, mRoot);
        }
    }

    @Override
    public void onDialogCancelEvent(int id) {

    }

    @Override
    public void onInflateCountriesFragment() {
        Log.e(TAG, "inflateCountriesFragment: inflate CountriesFragment");
        initCountriesFragment();
    }

    @Override
    public void onInflateGlobalFragment() {
        Log.e(TAG, "inflateCountriesFragment: inflate GlobalFragment");
        initGlobalFragment();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }


    //variables
    private int mTapToClose = 0;
    //references
    private ConnectivityReceiver receiver;

    private Drawer mDrawer;
    //Service
    private ServiceConnection mDownloadDataServiceConnection;
    private ViewGroup mRoot;
    private Intent mDownloadDataServiceIntent;
    private GetRawDataService mGetRawDataService;
    private boolean mIsServiceBound = false;


    private boolean isRawFromApifyCompleted = false;
    private boolean isRawDOHFromHerokuappCompleted = false;
    private boolean isRawPhilippinesFromHerokuappCompleted = false;
    private boolean isRawCountriesFromHerokuappCompleted = false;
    private View mSplashScreen;
    private ViewGroup mRootSplash;
    private boolean show = false;

    @Override
    public void onDataCompleted() {
        if (isRawFromApifyCompleted
                && isRawDOHFromHerokuappCompleted
                && isRawPhilippinesFromHerokuappCompleted
                && isRawCountriesFromHerokuappCompleted) {
            Log.d(TAG, "onDataCompleted() is completed ");
            //remove splash screen here....
            mSplashScreen.clearAnimation();
            show = false;
            toggle();
            initGlobalFragment();
//            TrackerUtility.runFadeAnimationOn(mRoot, true);

        }
    }

    private void toggle() {
        Transition transition = new Fade();
        transition.setDuration(500);
        transition.addTarget(R.id.tracker_splash);

        TransitionManager.beginDelayedTransition(mRootSplash, transition);
        mSplashScreen.setVisibility(show ? View.VISIBLE : View.GONE);
    }

//    private final int ANIMATION_DURATION = 500;
//
//    public void show() {
//        mSplashScreen.setVisibility(View.VISIBLE);
//        mSplashScreen.animate()
//                .alpha(1f)
//                .setDuration(ANIMATION_DURATION)
//                .setListener(null);
//    }
//
//    private void hide() {
//        mSplashScreen.animate()
//                .alpha(0f)
//                .setDuration(ANIMATION_DURATION)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mSplashScreen.setVisibility(View.GONE);
//                    }
//                });
//    }

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


    private void bindDownloadDataService() {
        if (mDownloadDataServiceConnection == null) {
            mDownloadDataServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Log.d(TAG, "onServiceConnected() service is now bound");
                    GetRawDataService.RawDataServiceBinder binder = (GetRawDataService.RawDataServiceBinder) service;
                    mGetRawDataService = binder.getInstance();
                    mGetRawDataService.registerClientReceiver(MainActivity.this);
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
        Log.e(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSplashScreen = findViewById(R.id.tracker_splash);
        mRootSplash = findViewById(R.id.coordinator_layout);
        mRoot = findViewById(R.id.main_activity);
        show = true;
        toggle();
        activateToolbar(false);

        //================ Service Instantiation ==================//
        mDownloadDataServiceIntent = new Intent(this, GetRawDataService.class);
//        startService(mDownloadDataServiceIntent);
//        bindDownloadDataService();

//        if (savedInstanceState == null) {
////            initGlobalFragment();
//        }

        initTrackerListener();
        if (ConnectivityReceiver.isNotConnected()) {
            TrackerUtility.message(this, "No Internet Connection",
                    R.drawable.ic_signal_wifi_off, R.color.md_white_1000,
                    R.color.toast_connection_lost);
        }

        initMaterialDrawer();
        if (savedInstanceState != null) {
            int currentSelection = savedInstanceState.getInt(TrackerKeys.STATE_SELECTION_DRAWER);
            mDrawer.setSelection(currentSelection);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            startService(mDownloadDataServiceIntent);
            bindDownloadDataService();
        } else {
            TrackerUtility.message(this, "No Internet Connection",
                    R.drawable.ic_signal_wifi_off, R.color.md_white_1000,
                    R.color.toast_connection_lost);
            unbindDownloadDataService();
            if (!mIsServiceBound) stopService(mDownloadDataServiceIntent);
        }
    }

    private void initTrackerListener() {
        TrackerApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(TrackerKeys.STATE_SELECTION_DRAWER, mDrawer.getCurrentSelectedPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume() was called!");
        registerTrackerReceiver();
    }

    private void registerTrackerReceiver() {
        IntentFilter filter = new IntentFilter();
        //note: ConnectivityManager.CONNECTIVITY_ACTION is deprecated in api 28 above
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        receiver = new ConnectivityReceiver();
        registerReceiver(receiver, filter);
    }

    private void initMaterialDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ic_corona_header)
                .build();
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("Global").withIcon(CommunityMaterial.Icon.cmd_earth),
                        new PrimaryDrawerItem().withIdentifier(2).withName("Philippines").withIcon(CommunityMaterial.Icon.cmd_flag),
                        new SectionDrawerItem().withName("Information"),
                        new PrimaryDrawerItem().withIdentifier(3).withName("What is Covid-19?").withIcon(CommunityMaterial.Icon.cmd_comment_question_outline),
                        new PrimaryDrawerItem().withIdentifier(4).withName("About").withIcon(CommunityMaterial.Icon2.cmd_information))
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    //selected item functionality here...
                    switch (position) {
                        case 1:
                            initGlobalFragment();
                            break;
                        case 2:
                            initPhilippinesFragment();
                            break;
                        case -1:
                            TrackerUtility.finishFade(this, mRoot);
                            break;
                    }
                    return false;
                }).build();

        mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        mDrawer.addStickyFooterItem(new PrimaryDrawerItem().withName("Exit").withIcon(CommunityMaterial.Icon.cmd_exit_to_app));
    }

    private void initPhilippinesFragment() {
        PhilippinesFragment fragment = new PhilippinesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in_start_activity, R.anim.fade_out_end_activity);
        transaction.replace(R.id.fragment_container, fragment, getString(R.string.tag_fragment_philippines));
        transaction.addToBackStack(getString(R.string.tag_fragment_philippines));
        transaction.commit();
    }

    private void initGlobalFragment() {
        onTapToCloseReset();
        GlobalFragment fragment = new GlobalFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in_start_activity, R.anim.fade_out_end_activity);
        transaction.replace(R.id.fragment_container, fragment, getString(R.string.tag_fragment_global));
        transaction.addToBackStack(getString(R.string.tag_fragment_global));
        transaction.commitAllowingStateLoss();
    }

    private void initCountriesFragment() {
        CountriesFragment fragment = new CountriesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, getString(R.string.tag_fragment_countries));
        transaction.addToBackStack(getString(R.string.tag_fragment_countries));
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
        unregisterReceiver(receiver);
        unbindDownloadDataService();
        if (!mIsServiceBound) stopService(mDownloadDataServiceIntent);
    }

    private void onTapToClose() {
        mTapToClose++;
    }

    private void onTapToCloseReset() {
        mTapToClose = 0;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if ((fragment == getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_fragment_global)))
                || (fragment == getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_fragment_philippines)))) {

            if (mDrawer.isDrawerOpen()) {
                mDrawer.closeDrawer();
                return;
            }

            onTapToClose();
            if (mTapToClose >= 2) {
                TrackerUtility.finishFade(this, mRoot);
            }
        } else {

            TrackerDialog dialog = new TrackerDialog();

            Bundle args = new Bundle();
            args.putString(TrackerKeys.KEY_STYLE, TrackerKeys.STYLE_DIALOG_NORMAL);
            args.putInt(TrackerKeys.KEY_DIALOG_ID, TrackerKeys.ACTION_DIALOG_ON_BACK_PRESSED);
            args.putString(TrackerKeys.KEY_DIALOG_TITLE, "Do you want to exit?");
            args.putString(TrackerKeys.KEY_DIALOG_MESSAGE, "Use the back navigation instead.");
            args.putInt(TrackerKeys.KEY_DIALOG_POSITIVE_RID, R.string.label_dialog_continue);
            args.putInt(TrackerKeys.KEY_DIALOG_NEGATIVE_RID, R.string.label_dialog_exit);

            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        }
    }

}
