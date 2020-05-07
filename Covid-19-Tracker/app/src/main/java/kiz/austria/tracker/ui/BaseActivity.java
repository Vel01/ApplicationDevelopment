package kiz.austria.tracker.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import kiz.austria.tracker.R;

public class BaseActivity extends AppCompatActivity {

    protected void activateToolbar(boolean homeEnable) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
                getSupportActionBar().setElevation(0);
            }

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(homeEnable);
            }
        }
    }

}
