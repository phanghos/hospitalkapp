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
import retrofit2.Response;

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
                .subscribe(new DisposableObserver<Response<ArrayList<LocationResponse.Country>>>() {
                    @Override
                    public void onNext(Response<ArrayList<LocationResponse.Country>> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) mView.showCountries(response.body());
                            else mView.showNetworkFailedError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showNetworkError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void searchServices(String countryId, String cityId, String serviceId,
                                String reviewOrder, String reviewRank, final int offset, String query) {
        Log.i("country", countryId);
        Log.i("city", cityId);
        Log.i("service", serviceId);
        Log.i("order", reviewOrder);
        Log.i("rank", reviewRank);
        Log.i("query", query);
        Log.i("debug", "fetching services...");

        mView.showProgress();

        mService.searchServices(countryId, cityId, serviceId, reviewOrder, reviewRank,
                offset, LIMIT, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<SearchResponse>>() {
                    @Override
                    public void onNext(Response<SearchResponse> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                ArrayList<ServiceResponse.Service> services = response.body().getServices();
                                ArrayList<City> cities = response.body().getCities();
                                ArrayList<ServiceFilter> servicesFilter = response.body().getServicesFilter();

                                if (cities != null) mView.showCities(cities);
                                if (servicesFilter != null) mView.showServicesFilter(servicesFilter);

                                if (offset == 0 && services != null) {
                                    mView.showServices(services);
                                } else if (services != null) {
                                    mView.addServices(services);
                                }

                                mView.incrementOffset();

                                if (services != null && services.size() > 0) mView.showFab();
                                else mView.showNoResultsMessage();
                            } else {
                                mView.showNetworkFailedError();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showNetworkError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
