package org.taitasciore.android.hospitalk.hospital;

import android.util.Log;

import org.taitasciore.android.model.Activity;
import org.taitasciore.android.model.City;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.SearchHospitalData;
import org.taitasciore.android.model.SearchResponse;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

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
    public void getCities(String countryId) {
        Log.i("debug", "fetching cities for country " + countryId + "...");

        mView.showProgress();

        mService.getCompanyCities(countryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ArrayList<City>>>() {
                    @Override
                    public void onNext(Response<ArrayList<City>> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) mView.showCities(response.body());
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
    public void searchHospitalsAndCities(String countryId, String cityId, String serviceId, String reviewOrder, String reviewRank, final int offset, String query) {
        Log.i("country", countryId);
        Log.i("city", cityId);
        Log.i("service", serviceId);
        Log.i("order", reviewOrder);
        Log.i("rank", reviewRank);
        Log.i("query", query);
        Log.i("debug", "fetching hospitals and cities for country " + countryId + "...");

        Observable<Response<ArrayList<City>>> cities = mService.getCompanyCities(countryId);
        Observable<Response<SearchResponse>> searchResponse = mService.searchHospitals(countryId,
                cityId, serviceId, reviewOrder, reviewRank, offset, LIMIT, query);

        mView.showProgress();

        Observable.zip(cities, searchResponse, new BiFunction<Response<ArrayList<City>>,
                Response<SearchResponse>, Response<SearchHospitalData>>() {
            @Override
            public Response<SearchHospitalData> apply(
                    @NonNull Response<ArrayList<City>> cities,
                    @NonNull Response<SearchResponse> searchResponse) throws Exception {
                if (cities.isSuccessful() && searchResponse.isSuccessful()) {
                    SearchHospitalData data = new SearchHospitalData(
                            cities.body(), searchResponse.body());
                    return Response.success(data);
                } else {
                    return Response.error(400, null);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<SearchHospitalData>>() {
                    @Override
                    public void onNext(Response<SearchHospitalData> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                SearchHospitalData data = response.body();

                                ArrayList<City> cities = data.getCities();
                                ArrayList<Hospital> companies = data.getSearchResponse().getCompanies();
                                ArrayList<Activity> activities = data.getSearchResponse().getActivities();

                                mView.showCities(cities);
                                mView.showActivities(activities);
                                if (offset == 0) mView.showHospitals(companies);
                                else mView.addHospitals(companies);
                                mView.incrementOffset();
                                if (companies.size() > 0) {
                                    mView.showFab();
                                } else {
                                    mView.showNoResultsMessage();
                                }
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

    @Override
    public void searchHospitals(String countryId, String cityId, String serviceId,
                                String reviewOrder, String reviewRank, final int offset, String query) {
        Log.i("country", countryId);
        Log.i("city", cityId);
        Log.i("service", serviceId);
        Log.i("order", reviewOrder);
        Log.i("rank", reviewRank);
        Log.i("query", query);
        Log.i("debug", "fetching hospitals...");

        mView.showProgress();

        mService.searchHospitals(countryId, cityId, serviceId, reviewOrder, reviewRank,
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
                                ArrayList<Hospital> companies = response.body().getCompanies();
                                ArrayList<Activity> activities = response.body().getActivities();

                                mView.showActivities(activities);
                                if (offset == 0) mView.showHospitals(companies);
                                else mView.addHospitals(companies);
                                mView.incrementOffset();
                                if (companies.size() > 0) {
                                    mView.showFab();
                                } else {
                                    mView.showNoResultsMessage();
                                }
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
