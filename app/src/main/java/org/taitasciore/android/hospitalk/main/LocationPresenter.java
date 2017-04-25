package org.taitasciore.android.hospitalk.main;

import android.util.Log;

import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.network.HospitalkService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

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
                .subscribe(new DisposableObserver<LocationResponse>() {
                    @Override
                    public void onNext(LocationResponse locationResponse) {
                        LocationResponse.Country country = locationResponse.getCountry();
                        LocationResponse.City city = locationResponse.getCity();
                        mView.showPing();
                        mView.updateLocation(country.getWebcode(), city.getCityname());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
