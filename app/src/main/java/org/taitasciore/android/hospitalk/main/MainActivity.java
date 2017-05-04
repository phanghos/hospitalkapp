package org.taitasciore.android.hospitalk.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.AttachPresenterEvent;
import org.taitasciore.android.event.RequestCityEvent;
import org.taitasciore.android.event.RequestCountryEvent;
import org.taitasciore.android.event.RequestDeterminedLocationEvent;
import org.taitasciore.android.event.RequestLocationEvent;
import org.taitasciore.android.event.SavePresenterEvent;
import org.taitasciore.android.event.SendCityEvent;
import org.taitasciore.android.event.SendCountryEvent;
import org.taitasciore.android.event.SendDeterminedLocationEvent;
import org.taitasciore.android.event.SendLocationEvent;
import org.taitasciore.android.event.UploadImageEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.hospitalk.CustomClickableSpan;
import org.taitasciore.android.hospitalk.HospitalkApp;
import org.taitasciore.android.hospitalk.PresenterState;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.close.CloseContract;
import org.taitasciore.android.hospitalk.close.ClosePresenter;
import org.taitasciore.android.hospitalk.dialog.ContactDialogFragment;
import org.taitasciore.android.hospitalk.dialog.FooterDialogFragment;
import org.taitasciore.android.hospitalk.hospital.HospitalDetailsContract;
import org.taitasciore.android.hospitalk.hospital.HospitalDetailsPresenter;
import org.taitasciore.android.hospitalk.hospital.SearchHospitalsContract;
import org.taitasciore.android.hospitalk.hospital.SearchHospitalsPresenter;
import org.taitasciore.android.hospitalk.login.LoginContract;
import org.taitasciore.android.hospitalk.login.LoginPresenter;
import org.taitasciore.android.hospitalk.mainmenu.MainMenuContract;
import org.taitasciore.android.hospitalk.mainmenu.MainMenuFragment;
import org.taitasciore.android.hospitalk.mainmenu.MainMenuPresenter;
import org.taitasciore.android.hospitalk.menu.MenuFragment;
import org.taitasciore.android.hospitalk.review.ReviewDetailsContract;
import org.taitasciore.android.hospitalk.review.ReviewDetailsPresenter;
import org.taitasciore.android.hospitalk.review.ReviewsContract;
import org.taitasciore.android.hospitalk.review.ReviewsPresenter;
import org.taitasciore.android.hospitalk.review.WriteReviewFragment;
import org.taitasciore.android.hospitalk.service.SearchServicesContract;
import org.taitasciore.android.hospitalk.service.SearchServicesPresenter;
import org.taitasciore.android.hospitalk.service.ServiceDetailsContract;
import org.taitasciore.android.hospitalk.service.ServiceDetailsPresenter;
import org.taitasciore.android.hospitalk.signup.SignupContract;
import org.taitasciore.android.hospitalk.signup.SignupPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements
        LocationContract.View,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private double lat;
    private double lon;
    private String mCountryId = "";
    private String mCity = "";
    private boolean mDeterminedLocation;

    private PresenterState mPresenterState;

    private LocationContract.Presenter mLocationPresenter;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @BindView(R.id.ping) ImageView mPing;
    @BindView(R.id.tvHeader) TextView mTvHeader;
    @BindView(R.id.btnMenu) ToggleButton mBtnToggle;
    @BindView(R.id.tvFooter) TextView mTvFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        restorePresenter();
        setSpans();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainMenuFragment()).commit();
        } else {
            lat = savedInstanceState.getDouble("lat");
            lon = savedInstanceState.getDouble("lon");
            mCountryId = savedInstanceState.getString("country");
            mCity = savedInstanceState.getString("city");
        }

        if (lat == 0 && lon == 0) {
            setupGoogleApiClient();
        } else if (!mCountryId.isEmpty() && !mCity.isEmpty()) {
            updateLocation(mCountryId, mCity);
            showPing();
        }

        mBtnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                Fragment f = getSupportFragmentManager().findFragmentByTag("menu");

                if (f != null) {
                    onBackPressed();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new MenuFragment(), "menu")
                            .addToBackStack(null).commit();
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("lat", lat);
        outState.putDouble("lon", lon);
        outState.putString("country", mCountryId);
        outState.putString("city", mCity);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void setupGoogleApiClient() {
        if (ActivityUtils.isGoogleApiAvailable(this)) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                    GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            ((HospitalkApp) getApplication()).setGoogleApiClient(mGoogleApiClient);
        } else {
            lat = 40.4116767;
            lon = -3.7130291;
            updateLocation("ES", "Madrid");
            showPing();
            EventBus.getDefault().post(new SendLocationEvent(lat, lon));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationPresenter != null) mLocationPresenter.unbindView();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenterState;
    }

    @Override
    public void bindPresenter(LocationContract.Presenter presenter) {
        mLocationPresenter = presenter;
    }

    public void restorePresenter() {
        mPresenterState = (PresenterState) getLastCustomNonConfigurationInstance();

        if (mPresenterState == null) {
            mPresenterState = new PresenterState();
            mLocationPresenter = new LocationPresenter(this);
            mPresenterState.setLocationPresenter(mLocationPresenter);
        } else {
            mLocationPresenter = mPresenterState.getLocationPresenter();
            mLocationPresenter.bindView(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendDeterminedLocation(RequestDeterminedLocationEvent event) {
        EventBus.getDefault().post(new SendDeterminedLocationEvent(mDeterminedLocation));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendCountry(RequestCountryEvent event) {
        EventBus.getDefault().post(new SendCountryEvent(mCountryId));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendLocation(RequestLocationEvent event) {
        EventBus.getDefault().post(new SendLocationEvent(lat, lon));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendCountryAndCity(RequestCityEvent event) {
        EventBus.getDefault().post(new SendCityEvent(mCity));
    }

    @Override
    public void showPing() {
        mPing.setVisibility(View.VISIBLE);
    }

    @Override
    public void showHeaderLoading() {
        mTvHeader.setText("Obteniendo ubicación");
    }

    @Override
    public void updateLocation(String code, String city) {
        mCity = city;
        mDeterminedLocation = true;
        mTvHeader.setText(code + ", " + city);
        EventBus.getDefault().post(new SendLocationEvent(lat, lon));
    }

    @Override
    public void setCountry(String countryId) {
        mCountryId = countryId;
    }

    @Override
    public void setSpans() {
        String footer = mTvFooter.getText().toString();
        Log.i("footer", footer);
        SpannableStringBuilder ssb = new SpannableStringBuilder(footer);
        CustomClickableSpan clickableSpan;
        int start;
        int end;

        start = footer.indexOf(getString(R.string.footer_contacto));
        end = start + getString(R.string.footer_contacto).length();
        clickableSpan = new CustomClickableSpan(this) {
            @Override
            public void onClick(View view) {
                showContactDialog();
            }
        };
        ssb.setSpan(clickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        start = footer.indexOf(getString(R.string.footer_terminos));
        end = start + getString(R.string.footer_terminos).length();
        clickableSpan = new CustomClickableSpan(this) {
            @Override
            public void onClick(View view) {
                FooterDialogFragment f = FooterDialogFragment.newInstance(
                        getString(R.string.footer_terminos), 1);
                showFooterDialog(f);
            }
        };
        ssb.setSpan(clickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        start = footer.indexOf(getString(R.string.footer_privacidad));
        end = start + getString(R.string.footer_privacidad).length();
        clickableSpan = new CustomClickableSpan(this) {
            @Override
            public void onClick(View view) {
                FooterDialogFragment f = FooterDialogFragment.newInstance(
                        getString(R.string.footer_privacidad), 2);
                showFooterDialog(f);
            }
        };
        ssb.setSpan(clickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        start = footer.indexOf(getString(R.string.footer_datos));
        end = start + getString(R.string.footer_datos).length();
        clickableSpan = new CustomClickableSpan(this) {
            @Override
            public void onClick(View view) {
                FooterDialogFragment f = FooterDialogFragment.newInstance(
                        getString(R.string.footer_datos), 3);
                showFooterDialog(f);
            }
        };
        ssb.setSpan(clickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        mTvFooter.setText(ssb, TextView.BufferType.SPANNABLE);
        mTvFooter.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void showContactDialog() {
        ContactDialogFragment f = new ContactDialogFragment();
        f.show(getSupportFragmentManager(), "contact");
    }

    @Override
    public void showFooterDialog(FooterDialogFragment f) {
        f.show(getSupportFragmentManager(), "footer");
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLocationFailedError() {
        Toast.makeText(this, getString(R.string.location_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("debug", "onActivityResult in activity");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WriteReviewFragment.REQUEST_UPLOAD_IMAGE) {
            EventBus.getDefault().post(new UploadImageEvent(resultCode, data));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("debug", "onRequestPermissionsResult in activity");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ActivityUtils.REQUEST_LOCATION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        } else if (requestCode == ActivityUtils.REQUEST_WRITE_EXTERNAL_STORAGE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("debug", "google api client connected");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityUtils.isLocationPermissionGranted(this)) {
                requestLocationUpdates();
            } else {
                ActivityUtils.requestLocationPermission(this);
            }
        } else {
            requestLocationUpdates();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void requestLocationUpdates() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            onLocationChanged(mLastLocation);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(500);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.i("position", lat + " - " + lon);
        EventBus.getDefault().post(new SendLocationEvent(lat, lon));
        if (!mDeterminedLocation) mLocationPresenter.getLocation(lat, lon);
    }
}
