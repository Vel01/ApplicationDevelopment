package kiz.austria.tracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;

import java.util.ArrayList;

import kiz.austria.tracker.R;
import kiz.austria.tracker.model.FragmentTag;
import kiz.austria.tracker.util.TrackerDialog;
import kiz.austria.tracker.util.TrackerKeys;
import kiz.austria.tracker.util.TrackerUtility;

public class MainActivity extends BaseActivity implements
        TrackerDialog.OnDialogListener,
        Inflatable,
//        NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationViewEx.OnNavigationItemSelectedListener {

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

    private static final int PHILIPPINES_FRAGMENT = 0;
    private static final int DROP_FRAGMENT = 1;
    private static final int CASES_FRAGMENT = 2;
    private static final int MORE_FRAGMENT = 3;
    //views
    private ViewGroup mRoot;
    //references
    private Drawer mDrawer;
    private BottomNavigationViewEx mBottomNavigationViewEx;
    //fragments
    private GlobalFragment mGlobalFragment;
    private PhilippinesFragment mPhilippinesFragment;
    private DropFragment mDropFragment;
    private CasesFragment mCasesFragment;
    private MoreFragment mMoreFragment;
    //custom backStack
    private ArrayList<String> mFragmentTags = new ArrayList<>();
    private ArrayList<FragmentTag> mFragments = new ArrayList<>();
    private int mTapToClose = 0;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_nav_philippines: {
                if (mPhilippinesFragment == null) {
                    mPhilippinesFragment = new PhilippinesFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.fade_in_start_activity, R.anim.fade_out_end_activity);
                    transaction.add(R.id.fragment_container, mPhilippinesFragment, getString(R.string.tag_fragment_philippines));
                    transaction.commit();
                    mFragmentTags.add(getString(R.string.tag_fragment_philippines));
                    mFragments.add(new FragmentTag(mPhilippinesFragment, getString(R.string.tag_fragment_philippines)));
                } else {
                    mFragmentTags.remove(getString(R.string.tag_fragment_philippines));
                    mFragmentTags.add(getString(R.string.tag_fragment_philippines));
                }
                item.setChecked(true);
                setFragmentVisibilities(getString(R.string.tag_fragment_philippines));
                break;
            }
            case R.id.bottom_nav_drop: {
                if (mDropFragment == null) {
                    mDropFragment = new DropFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_container, mDropFragment, getString(R.string.tag_fragment_drop));
                    transaction.commit();
                    mFragmentTags.add(getString(R.string.tag_fragment_drop));
                    mFragments.add(new FragmentTag(mDropFragment, getString(R.string.tag_fragment_drop)));
                } else {
                    mFragmentTags.remove(getString(R.string.tag_fragment_drop));
                    mFragmentTags.add(getString(R.string.tag_fragment_drop));
                }
                item.setChecked(true);
                setFragmentVisibilities(getString(R.string.tag_fragment_drop));
                break;
            }
            case R.id.bottom_nav_cases: {
                if (mCasesFragment == null) {
                    mCasesFragment = new CasesFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_container, mCasesFragment, getString(R.string.tag_fragment_cases));
                    transaction.commit();
                    mFragmentTags.add(getString(R.string.tag_fragment_cases));
                    mFragments.add(new FragmentTag(mCasesFragment, getString(R.string.tag_fragment_cases)));
                } else {
                    mFragmentTags.remove(getString(R.string.tag_fragment_cases));
                    mFragmentTags.add(getString(R.string.tag_fragment_cases));
                }
                item.setChecked(true);
                setFragmentVisibilities(getString(R.string.tag_fragment_cases));
                break;
            }
            case R.id.bottom_nav_more: {
                if (mMoreFragment == null) {
                    mMoreFragment = new MoreFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_container, mMoreFragment, getString(R.string.tag_fragment_more));
                    transaction.commit();
                    mFragmentTags.add(getString(R.string.tag_fragment_more));
                    mFragments.add(new FragmentTag(mMoreFragment, getString(R.string.tag_fragment_more)));
                } else {
                    mFragmentTags.remove(getString(R.string.tag_fragment_more));
                    mFragmentTags.add(getString(R.string.tag_fragment_more));
                }
                item.setChecked(true);
                setFragmentVisibilities(getString(R.string.tag_fragment_more));
                break;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar(false);

        //======= BottomNavigationView Instantiation & Initialization =======//
        mBottomNavigationViewEx = findViewById(R.id.bottom_nav_view);
        initBottomNavigationView();

        //================== Host Animation ==================/
        mRoot = findViewById(R.id.main_activity);

        //================ Global Initial Instantiation ==================//
        if (savedInstanceState == null) {
            initGlobalFragment();
        }

        //================ Material Drawer Instantiation ===========//
        initMaterialDrawer();
        if (savedInstanceState != null) {
            int currentSelection = savedInstanceState.getInt(TrackerKeys.STATE_SELECTION_DRAWER);
            mDrawer.setSelection(currentSelection);
        }
    }

    private void initBottomNavigationView() {
        mBottomNavigationViewEx.setOnNavigationItemSelectedListener(this);
        mBottomNavigationViewEx.enableAnimation(false);
    }

    private void setFragmentVisibilities(String tag) {

        if (tag.equals(getString(R.string.tag_fragment_philippines)))
            showBottomNavigation();
        else if (tag.equals(getString(R.string.tag_fragment_drop)))
            showBottomNavigation();
        else if (tag.equals(getString(R.string.tag_fragment_cases)))
            showBottomNavigation();
        else if (tag.equals(getString(R.string.tag_fragment_more)))
            showBottomNavigation();
        else if (tag.equals(getString(R.string.tag_fragment_global)))
            hideBottomNavigation();

        for (int i = 0; i < mFragments.size(); i++) {

            if (tag.equals(mFragments.get(i).getTag())) {
                //show
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.show(mFragments.get(i).getFragment());
                transaction.commit();
            } else {
                //don't show
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(mFragments.get(i).getFragment());
                transaction.commit();
            }
        }
        setNavigationIcon(tag);
    }

    private void setNavigationIcon(String tag) {
        Menu menu = mBottomNavigationViewEx.getMenu();
        MenuItem menuItem;
        if (tag.equals(getString(R.string.tag_fragment_philippines))) {
            menuItem = menu.getItem(PHILIPPINES_FRAGMENT);
            menuItem.setChecked(true);
        } else if (tag.equals(getString(R.string.tag_fragment_drop))) {
            menuItem = menu.getItem(DROP_FRAGMENT);
            menuItem.setChecked(true);
        } else if (tag.equals(getString(R.string.tag_fragment_cases))) {
            menuItem = menu.getItem(CASES_FRAGMENT);
            menuItem.setChecked(true);
        } else if (tag.equals(getString(R.string.tag_fragment_more))) {
            menuItem = menu.getItem(MORE_FRAGMENT);
            menuItem.setChecked(true);
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
                            //TODO: reset backstack here...
                            mFragmentTags.clear();
                            mFragmentTags = new ArrayList<>();
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
        if (mPhilippinesFragment == null) {
            mPhilippinesFragment = new PhilippinesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in_start_activity, R.anim.fade_out_end_activity);
            transaction.add(R.id.fragment_container, mPhilippinesFragment, getString(R.string.tag_fragment_philippines));
            transaction.commit();
            mFragmentTags.add(getString(R.string.tag_fragment_philippines));
            mFragments.add(new FragmentTag(mPhilippinesFragment, getString(R.string.tag_fragment_philippines)));
        } else {
            mFragmentTags.remove(getString(R.string.tag_fragment_philippines));
            mFragmentTags.add(getString(R.string.tag_fragment_philippines));
        }
        setFragmentVisibilities(getString(R.string.tag_fragment_philippines));
    }

    private void initGlobalFragment() {
        onTapToCloseReset();

        if (mGlobalFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mGlobalFragment).commitAllowingStateLoss();
        }

        mGlobalFragment = new GlobalFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in_start_activity, R.anim.fade_out_end_activity);
        transaction.add(R.id.fragment_container, mGlobalFragment, getString(R.string.tag_fragment_global));
        transaction.commitAllowingStateLoss();
        mFragmentTags.add(getString(R.string.tag_fragment_global));
        mFragments.add(new FragmentTag(mGlobalFragment, getString(R.string.tag_fragment_global)));

        setFragmentVisibilities(getString(R.string.tag_fragment_global));
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
                || (fragment == getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_fragment_philippines)))
                || (fragment == getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_fragment_drop)))
                || (fragment == getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_fragment_cases))
                || (fragment == getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_fragment_more))))) {

            if (mDrawer.isDrawerOpen()) {
                mDrawer.closeDrawer();
                return;
            }

            int backStackCount = mFragmentTags.size();
            if (backStackCount > 1) {
                //Nav backwards
                String topFragmentTag = mFragmentTags.get(backStackCount - 1);
                String newTopFragmentTag = mFragmentTags.get(backStackCount - 2);
                setFragmentVisibilities(newTopFragmentTag);

                mFragmentTags.remove(topFragmentTag);
                onTapToCloseReset();
            } else if (backStackCount == 1) {
                Toast.makeText(this, "1 more click to exit", Toast.LENGTH_SHORT).show();
                onTapToClose();
            }

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

    private void hideBottomNavigation() {
        if (mBottomNavigationViewEx != null) {
            mBottomNavigationViewEx.setVisibility(View.GONE);
        }
    }

    private void showBottomNavigation() {
        if (mBottomNavigationViewEx != null) {
            mBottomNavigationViewEx.setVisibility(View.VISIBLE);
        }
    }

}
