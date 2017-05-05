package org.taitasciore.android.hospitalk.main;

import android.util.Log;

import com.google.android.gms.location.LocationRequest;

import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.network.HospitalkService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by roberto on 18/04/17.
 */

public class LocationPresenter implements LocationContract.Presenter {

    private LocationContract.View mView;
    private HospitalkService mService;

    public LocationPresenter(LocationContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(LocationContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getLocation(double lat, double lon) {
        Log.i("fetching location...", lat + " - " + lon);

        mView.showHeaderLoading();

        mService.getLocation(lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<LocationResponse>>() {
                    @Override
                    public void onNext(Response<LocationResponse> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            if (response.isSuccessful()) {
                                LocationResponse locationResponse = response.body();
                                LocationResponse.Country country = locationResponse.getCountry();
                                LocationResponse.City city = locationResponse.getCity();
                                mView.showPing();
                                mView.updateLocation(country.getWebcode(), city.getCityname());
                                mView.setCountry(country.getCountryid());
                                mView.setLocation(city.getLatitude(), city.getLongitude());
                            } else {
                                mView.showPing();
                                mView.updateLocation("ES", "Madrid");
                                mView.showLocationFailedError();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showPing();
                            mView.updateLocation("ES", "Madrid");
                            mView.setCountry("ESP");
                            mView.setLocation(40.4116767, -3.7130291);
                            mView.showNetworkError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
