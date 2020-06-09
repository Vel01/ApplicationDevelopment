package kiz.austria.tracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;

import kiz.austria.tracker.R;
import kiz.austria.tracker.util.TrackerDialog;
import kiz.austria.tracker.util.TrackerKeys;
import kiz.austria.tracker.util.TrackerUtility;

public class MainActivity extends BaseActivity implements
        TrackerDialog.OnDialogListener,
        Inflatable,
        NavigationView.OnNavigationItemSelectedListener{

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

    private Drawer mDrawer;
    private ViewGroup mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar(false);

        //================== Host Animation ==================/
        mRoot = findViewById(R.id.main_activity);

        //================ Global Initial Instantiation ==================//
        if (savedInstanceState == null){
            initGlobalFragment();
        }

        //================ Material Drawer Instantiation ===========//
        initMaterialDrawer();
        if (savedInstanceState != null) {
            int currentSelection = savedInstanceState.getInt(TrackerKeys.STATE_SELECTION_DRAWER);
            mDrawer.setSelection(currentSelection);
        }
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
