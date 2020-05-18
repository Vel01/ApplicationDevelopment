package kiz.austria.tracker.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import kiz.austria.tracker.R;

public class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    protected void activateToolbar(boolean homeEnable) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            mToolbar = findViewById(R.id.toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
                actionBar = getSupportActionBar();
                getSupportActionBar().setTitle(getString(R.string.app_name));
                getSupportActionBar().setElevation(0);
            }

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(homeEnable);
            }
        }
    }

}
