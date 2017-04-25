package org.taitasciore.android.hospitalk.hospital;

import android.util.Log;

import org.taitasciore.android.model.Activity;
import org.taitasciore.android.model.City;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.SearchResponse;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by roberto on 24/04/17.
 */

public class SearchHospitalsPresenter implements SearchHospitalsContract.Presenter {

    public static final int LIMIT = 10;

    private SearchHospitalsContract.View mView;
    private HospitalkService mService;

    public SearchHospitalsPresenter(SearchHospitalsContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(SearchHospitalsContract.View view) {
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
    public void searchHospitals(String countryId, String cityId, String serviceId,
                                String reviewOrder, String reviewRank, final int offset) {
        Log.i("debug", "fetching hospitals...");

        mView.showProgress();

        mService.searchHospitals(countryId, cityId, serviceId, reviewOrder, reviewRank,
                offset, LIMIT, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<SearchResponse>() {
                    @Override
                    public void onNext(SearchResponse searchResponse) {
                        ArrayList<Hospital> companies = searchResponse.getCompanies();
                        ArrayList<City> cities = searchResponse.getCities();
                        ArrayList<Activity> activities = searchResponse.getActivities();

                        if (mView != null) {
                            mView.hideProgress();
                            mView.showCities(cities);
                            mView.showActivities(activities);
                            if (offset == 0) mView.showHospitals(companies);
                            else mView.addHospitals(companies);
                            mView.incrementOffset();
                            if (companies.size() > 0) {
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
