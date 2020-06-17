package kiz.austria.tracker.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import java.util.Objects;

import kiz.austria.tracker.R;

public class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected AppBarLayout mAppBarLayout;
    protected ActionBarDrawerToggle mActionBarDrawerToggle;

    protected void activateToolbar(boolean homeEnable) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            mToolbar = findViewById(R.id.toolbar);
            mAppBarLayout = findViewById(R.id.actionbar_container);
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

    protected void activateToolbar(boolean homeEnable, boolean titleEnable) {
        activateToolbar(homeEnable);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(titleEnable);
    }

}
