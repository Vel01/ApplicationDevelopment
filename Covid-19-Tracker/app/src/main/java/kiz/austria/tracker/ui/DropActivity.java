package kiz.austria.tracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kiz.austria.tracker.R;
import kiz.austria.tracker.model.DOHDrop;
import kiz.austria.tracker.util.TrackerKeys;
import kiz.austria.tracker.util.TrackerUtility;

public class DropActivity extends BaseActivity implements TextBuilder {

    private static final String TAG = "DropActivity";
    @BindView(R.id.tv_drop_code)
    TextView mCode;
    @BindView(R.id.tv_drop_gender_age)
    TextView mGenderAge;
    @BindView(R.id.tv_drop_date_reported)
    TextView mReported;
    @BindView(R.id.tv_drop_status)
    TextView mStatus;
    @BindView(R.id.tv_drop_admitted)
    TextView mAdmitted;
    @BindView(R.id.tv_drop_region)
    TextView mRegion;
    @BindView(R.id.tv_drop_province_label)
    TextView mProvinceLabel;
    @BindView(R.id.tv_drop_province)
    TextView mProvince;
    @BindView(R.id.tv_drop_location_label)
    TextView mLocationLabel;
    @BindView(R.id.tv_drop_location)
    TextView mLocation;
    @BindView(R.id.tv_drop_latitude_label)
    TextView mLatitudeLabel;
    @BindView(R.id.tv_drop_latitude)
    TextView mLatitude;
    @BindView(R.id.tv_drop_longitude_label)
    TextView mLongitudeLabel;
    @BindView(R.id.tv_drop_longitude)
    TextView mLongitude;
    //ButterKnife
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop);
        activateToolbar(true, false);
        mUnbinder = ButterKnife.bind(this);
        initIntentData();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            DOHDrop data = intent.getParcelableExtra(TrackerKeys.KEY_DROP_DOH_DATA);
            if (data != null) {
                String code = data.getCasesCode();
                String gender = data.getSex();
                String age = data.getAge();
                String reported = data.getDateReported();
                String recovered = data.getRecoveredOn();
                String died = data.getDateDied();
                String admitted = data.getIsAdmitted();
                String province = data.getProvCityRes();
                String location = data.getLocation();
                String latitude = data.getLatitude();
                String longitude = data.getLongitude();
                mCode.setText(code);
                mGenderAge.setText(textBuilder(gender, age));
                formatDate(reported);
                statusBuilder(recovered, died);
                mAdmitted.setText(textBuilder(admitted));
                inconsistentDataBuilder(province, location, latitude, longitude);
            }
        }
    }

    @Override
    public String textBuilder(String gender, String age) {
        if (gender.toLowerCase().equals("m"))
            return "Male - " + age + " years old";
        else if (gender.toLowerCase().equals("f"))
            return "Female - " + age + " years old";
        return "";
    }

    @Override
    public String textBuilder(String admitted) {
        return (!admitted.isEmpty()) ? admitted : "N/A";
    }

    @Override
    public void formatDate(String reported) {
        try {
            mReported.setText(TrackerUtility.formatDateReported(reported));
        } catch (ParseException e) {
            mReported.setText("N/A");
        }
    }

    @Override
    public void statusBuilder(String recovered, String died) {
        if (!recovered.isEmpty()) {
            mStatus.setText("Recovered");
            mStatus.setBackground(getResources().getDrawable(R.drawable.rounded_status_recovered));
        } else if (!died.isEmpty()) {
            mStatus.setText("Died");
            mStatus.setBackground(getResources().getDrawable(R.drawable.rounded_status_died));
        } else {
            mStatus.setText("Active");
            mStatus.setBackground(getResources().getDrawable(R.drawable.rounded_status_active));
        }
    }

    @Override
    public void inconsistentDataBuilder(String province, String location, String latitude, String longitude) {
        if (!province.isEmpty()) mProvince.setText(province);
        else {
            mProvinceLabel.setVisibility(View.GONE);
            mProvince.setVisibility(View.GONE);
        }

        if (!location.isEmpty()) mLocation.setText(location);
        else {
            mLocationLabel.setVisibility(View.GONE);
            mLocation.setVisibility(View.GONE);
        }

        if (!latitude.isEmpty()) mLatitude.setText(latitude);
        else {
            mLatitudeLabel.setVisibility(View.GONE);
            mLatitude.setVisibility(View.GONE);
        }

        if (!longitude.isEmpty()) mLongitude.setText(longitude);
        else {
            mLongitudeLabel.setVisibility(View.GONE);
            mLongitude.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}