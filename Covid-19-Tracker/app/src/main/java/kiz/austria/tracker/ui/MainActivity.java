package kiz.austria.tracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import kiz.austria.tracker.R;
import kiz.austria.tracker.data.Addresses;
import kiz.austria.tracker.data.CountriesDataParser;
import kiz.austria.tracker.data.JSONRawData;
import kiz.austria.tracker.data.NationDataParser;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerDialog;
import kiz.austria.tracker.util.TrackerKeys;

public class MainActivity extends AppCompatActivity implements
        TrackerDialog.OnDialogEventListener,
        CountriesDataParser.OnDataAvailable,
        NationDataParser.OnDataAvailable,
        OnInflateFragmentListener {

    private static final String TAG = "MainActivity";

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        CountriesFragment fragment = (CountriesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if ((fragment == null)) {
            super.onBackPressed();
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

    @Override
    public void onDialogPositiveEvent(int id, Bundle args) {

    }

    @Override
    public void onDialogNegativeEvent(int id, Bundle args) {
        switch (id) {
            case TrackerKeys.ACTION_DIALOG_ON_BACK_PRESSED:
                finish();
                break;
        }
    }

    @Override
    public void onDialogCancelEvent(int id) {

    }

    @Override
    public void onDataAvailable(Nation coverage, JSONRawData.DownloadStatus status) {
        args.putParcelable(getString(R.string.intent_global), coverage);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if ((fragment == null)) {
            initGlobalFragment();
        }
    }

    @Override
    public void onDataAvailable(ArrayList<Nation> nations, JSONRawData.DownloadStatus status) {
        args.putParcelableArrayList(getString(R.string.intent_countries), nations);
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

    //reference
    private Bundle args = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: was called!");

        NationDataParser<Nation> nationNationDataParser = NationDataParser.getInstance(this);
        nationNationDataParser.execute(Addresses.Link.DATA_GLOBAL);

        CountriesDataParser countryNationDataParser = CountriesDataParser.getInstance(this);
        countryNationDataParser.execute(Addresses.Link.DATA_COUNTRIES);

    }

    private void initGlobalFragment() {
        GlobalFragment fragment = new GlobalFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, getString(R.string.tag_fragment_global));
        transaction.addToBackStack(getString(R.string.tag_fragment_global));
        transaction.commit();
    }

    private void initCountriesFragment() {
        CountriesFragment fragment = new CountriesFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, getString(R.string.tag_fragment_countries));
        transaction.addToBackStack(getString(R.string.tag_fragment_countries));
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mnu_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
