package org.taitasciore.android.hospitalk.service;

import android.util.Log;

import org.taitasciore.android.model.City;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.SearchResponse;
import org.taitasciore.android.model.ServiceFilter;
import org.taitasciore.android.model.ServiceResponse;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by roberto on 24/04/17.
 */

public class SearchServicesPresenter implements SearchServicesContract.Presenter {

    public static final int LIMIT = 10;

    private SearchServicesContract.View mView;
    private HospitalkService mService;

    public SearchServicesPresenter(SearchServicesContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(SearchServicesContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getCountries() {
        Log.i("debug", "fetching countries...");

        mView.showProgress();

        mService.getCountries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ArrayList<LocationResponse.Country>>() {
                    @Override
                    public void onNext(ArrayList<LocationResponse.Country> countries) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showCountries(countries);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) mView.hideProgress();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void searchServices(String countryId, String cityId, String serviceId,
                                String reviewOrder, String reviewRank, final int offset) {
        Log.i("debug", "fetching services...");

        mView.showProgress();

        mService.searchServices(countryId, cityId, serviceId, reviewOrder, reviewRank,
                offset, LIMIT, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<SearchResponse>() {
                    @Override
                    public void onNext(SearchResponse searchResponse) {
                        ArrayList<ServiceResponse.Service> services = searchResponse.getServices();
                        ArrayList<City> cities = searchResponse.getCities();
                        ArrayList<ServiceFilter> servicesFilter = searchResponse.getServicesFilter();

                        if (mView != null) {
                            mView.hideProgress();
                            mView.showCities(cities);
                            mView.showServicesFilter(servicesFilter);
                            if (offset == 0) mView.showServices(services);
                            else mView.addServices(services);
                            mView.incrementOffset();
                            if (services.size() > 0) {
                                mView.showFab();
                            }
                            else {
                                mView.showNoResultsMessage();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) mView.hideProgress();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
