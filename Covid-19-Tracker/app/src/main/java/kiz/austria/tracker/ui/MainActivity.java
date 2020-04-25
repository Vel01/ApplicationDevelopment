package kiz.austria.tracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import kiz.austria.tracker.R;

public class MainActivity extends AppCompatActivity implements InflateFragment {

    private static final String TAG = "MainActivity";


    @Override
    public void inflateCountriesFragment() {
        Log.d(TAG, "inflateCountriesFragment: inflate CountriesFragment");
        initCountriesFragment();
    }

    @Override
    public void inflateGlobalFragment() {
        Log.d(TAG, "inflateCountriesFragment: inflate GlobalFragment");
        initGlobalFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            initGlobalFragment();
        }
        Log.d(TAG, "onCreate: ended");
    }

    private void initGlobalFragment() {
        Log.d(TAG, "onCreate: initGlobalFragment");
        GlobalFragment fragment = new GlobalFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, getString(R.string.tag_fragment_global));
        transaction.addToBackStack(getString(R.string.tag_fragment_global));
        transaction.commit();
    }

    private void initCountriesFragment() {
        CountriesFragment fragment = new CountriesFragment();
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
