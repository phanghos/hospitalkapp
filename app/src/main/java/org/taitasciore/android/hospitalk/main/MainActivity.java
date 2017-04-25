package org.taitasciore.android.hospitalk.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.AttachPresenterEvent;
import org.taitasciore.android.event.RequestCountryAndCityEvent;
import org.taitasciore.android.event.RequestLocationEvent;
import org.taitasciore.android.event.SavePresenterEvent;
import org.taitasciore.android.event.SendCityEvent;
import org.taitasciore.android.event.SendLocationEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.hospitalk.PresenterState;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.close.CloseContract;
import org.taitasciore.android.hospitalk.close.ClosePresenter;
import org.taitasciore.android.hospitalk.hospital.HospitalDetailsContract;
import org.taitasciore.android.hospitalk.hospital.HospitalDetailsPresenter;
import org.taitasciore.android.hospitalk.hospital.SearchHospitalsContract;
import org.taitasciore.android.hospitalk.hospital.SearchHospitalsPresenter;
import org.taitasciore.android.hospitalk.menu.MenuContract;
import org.taitasciore.android.hospitalk.menu.MenuFragment;
import org.taitasciore.android.hospitalk.menu.MenuPresenter;
import org.taitasciore.android.hospitalk.review.ReviewDetailsContract;
import org.taitasciore.android.hospitalk.review.ReviewDetailsPresenter;
import org.taitasciore.android.hospitalk.review.ReviewsContract;
import org.taitasciore.android.hospitalk.review.ReviewsPresenter;
import org.taitasciore.android.hospitalk.service.SearchServicesContract;
import org.taitasciore.android.hospitalk.service.SearchServicesPresenter;
import org.taitasciore.android.hospitalk.service.ServiceDetailsContract;
import org.taitasciore.android.hospitalk.service.ServiceDetailsPresenter;

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
    private String mCountry;
    private String mCity;
    private boolean mDeterminedLocation;

    private PresenterState mPresenterState;

    private LocationContract.Presenter mLocationPresenter;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @BindView(R.id.ping) ImageView mPing;
    @BindView(R.id.tvHeader) TextView mTvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        restorePresenter();

        if (savedInstanceState == null) {
            MenuFragment f = new MenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MenuFragment()).commit();
        } else {
            lat = savedInstanceState.getDouble("lat");
            lon = savedInstanceState.getDouble("lon");
            mCountry = savedInstanceState.getString("country");
            mCity = savedInstanceState.getString("city");
        }

        if (lat == 0 && lon == 0) {
            setupGoogleApiClient();
        } else if (mCountry != null && mCity != null) {
            updateLocation(mCountry, mCity);
        }
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
        outState.putString("country", mCountry);
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
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        if (apiAvailability.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        } else {
            lat = 40.4116767;
            lon = -3.7130291;
            EventBus.getDefault().post(new SendLocationEvent(lat, lon));
            mLocationPresenter.getLocation(lat, lon);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationPresenter != null) mLocationPresenter.unbindView();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        //return mLocationPresenter;
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
    public void attachPresenter(AttachPresenterEvent event) {
        BaseView view = event.fragment;

        if (view instanceof MenuContract.View) {
            MenuContract.Presenter presenter = mPresenterState.getMenuPresenter();
            if (presenter == null) presenter = new MenuPresenter((MenuContract.View) view);
            view.bindPresenter(presenter);
        } else if (view instanceof ReviewsContract.View) {
            ReviewsContract.Presenter presenter = mPresenterState.getReviewsPresenter();
            if (presenter == null) presenter = new ReviewsPresenter((ReviewsContract.View) view);
            view.bindPresenter(presenter);
        } else if(view instanceof ReviewDetailsContract.View) {
            ReviewDetailsContract.Presenter presenter = mPresenterState.getReviewDetailsPresenter();
            if (presenter == null) presenter = new ReviewDetailsPresenter((ReviewDetailsContract.View) view);
            view.bindPresenter(presenter);
        } else if (view instanceof CloseContract.View) {
            CloseContract.Presenter presenter = mPresenterState.getClosePresenter();
            if (presenter == null) presenter = new ClosePresenter((CloseContract.View) view);
            view.bindPresenter(presenter);
        } else if (view instanceof HospitalDetailsContract.View) {
            HospitalDetailsContract.Presenter presenter = mPresenterState.getHospitalDetailsPresenter();
            if (presenter == null) presenter = new HospitalDetailsPresenter((HospitalDetailsContract.View) view);
            view.bindPresenter(presenter);
        } else if (view instanceof SearchHospitalsContract.View) {
            SearchHospitalsContract.Presenter presenter = mPresenterState.getSearchHospitalsPresenter();
            if (presenter == null) presenter = new SearchHospitalsPresenter((SearchHospitalsContract.View) view);
            view.bindPresenter(presenter);
        } else if (view instanceof SearchServicesContract.View) {
            SearchServicesContract.Presenter presenter = mPresenterState.getSearchServicesPresenter();
            if (presenter == null) presenter = new SearchServicesPresenter((SearchServicesContract.View) view);
            view.bindPresenter(presenter);
        } else if (view instanceof ServiceDetailsContract.View) {
            ServiceDetailsContract.Presenter presenter = mPresenterState.getServiceDetailsPresenter();
            if (presenter == null) presenter = new ServiceDetailsPresenter((ServiceDetailsContract.View) view);
            view.bindPresenter(presenter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void savePresenter(SavePresenterEvent event) {
        BasePresenter presenter = event.presenter;

        if (presenter instanceof MenuContract.Presenter) {
            mPresenterState.setMenuPresenter((MenuContract.Presenter) presenter);
        } else if (presenter instanceof ReviewsContract.Presenter) {
            mPresenterState.setReviewsPresenter((ReviewsContract.Presenter) presenter);
        } else if (presenter instanceof ReviewDetailsContract.Presenter) {
            mPresenterState.setReviewDetailsPresenter((ReviewDetailsContract.Presenter) presenter);
        } else if (presenter instanceof CloseContract.Presenter) {
            mPresenterState.setClosePresenter((CloseContract.Presenter) presenter);
        } else if (presenter instanceof HospitalDetailsContract.Presenter) {
            mPresenterState.setHospitalDetailsPresenter((HospitalDetailsContract.Presenter) presenter);
        } else if (presenter instanceof SearchHospitalsContract.Presenter) {
            mPresenterState.setSearchHospitalsPresenter((SearchHospitalsContract.Presenter) presenter);
        } else if (presenter instanceof SearchServicesContract.Presenter) {
            mPresenterState.setSearchServicesPresenter((SearchServicesContract.Presenter) presenter);
        } else if (presenter instanceof ServiceDetailsContract.Presenter) {
            mPresenterState.setServiceDetailsPresenter((ServiceDetailsContract.Presenter) presenter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendLocation(RequestLocationEvent event) {
        EventBus.getDefault().post(new SendLocationEvent(lat, lon));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendCountryAndCity(RequestCountryAndCityEvent event) {
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
        mCountry = code;
        mCity = city;
        mDeterminedLocation = true;
        mTvHeader.setText(code + ", " + city);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ActivityUtils.REQUEST_LOCATION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
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
