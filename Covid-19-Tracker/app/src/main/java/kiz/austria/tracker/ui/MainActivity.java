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

public class MainActivity extends AppCompatActivity implements CountriesDataParser.OnDataAvailable,
        NationDataParser.OnDataAvailable,
        OnInflateFragmentListener {

    private static final String TAG = "MainActivity";

    @Override
    public void onDataAvailable(Nation coverage, JSONRawData.DownloadStatus status) {
        GlobalFragment fragment = new GlobalFragment();
        args.putParcelable(getString(R.string.intent_global), coverage);
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, getString(R.string.tag_fragment_global));
        transaction.addToBackStack(getString(R.string.tag_fragment_global));
        transaction.commit();
    }

    @Override
    public void onDataAvailable(ArrayList<Nation> nations, JSONRawData.DownloadStatus status) {
        args.putParcelableArrayList(getString(R.string.intent_countries),  nations);
    }

    @Override
    public void onInflateCountriesFragment() {
        Log.d(TAG, "inflateCountriesFragment: inflate CountriesFragment");
        initCountriesFragment();
    }

    @Override
    public void onInflateGlobalFragment() {
        Log.d(TAG, "inflateCountriesFragment: inflate GlobalFragment");
        initGlobalFragment();
    }

    //vars
    private Bundle args = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ended");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: was called!");
        NationDataParser<Nation> nationNationDataParser = NationDataParser.getInstance(this);
        nationNationDataParser.execute(Addresses.Link.DATA_GLOBAL);

        CountriesDataParser countryNationDataParser = CountriesDataParser.getInstance(this);
        countryNationDataParser.execute(Addresses.Link.DATA_COUNTRIES);
    }

    private void initGlobalFragment() {
        Log.d(TAG, "onCreate: initGlobalFragment");
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

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof OnBackPressed) || !((OnBackPressed) fragment).onBackPressed()) {
            //TODO: AlertDialog, ask user if he/she want's to quit the app.
            finish();
        }
    }
}
