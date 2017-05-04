package org.taitasciore.android.hospitalk.review;

import android.util.Log;

import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.NewReview;
import org.taitasciore.android.model.SearchResponse;
import org.taitasciore.android.model.SendReviewResponse;
import org.taitasciore.android.model.ServiceResponse;
import org.taitasciore.android.model.WriteReviewData;
import org.taitasciore.android.network.HospitalkService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by roberto on 26/04/17.
 */

public class WriteReviewPresenter implements WriteReviewContract.Presenter {

    private WriteReviewContract.View mView;
    private HospitalkService mService;

    public WriteReviewPresenter(WriteReviewContract.View view) {
        bindView(view);
        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();
        mService = new HospitalkService(client);
    }

    @Override
    public void bindView(WriteReviewContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getServicesAndCountries() {
        Log.i("debug", "fetching services and countries...");

        mView.showProgress();

        Observable<Response<ServiceResponse>> obsServices = mService.getServices();
        Observable<Response<ArrayList<LocationResponse.Country>>> obsCountries = mService.getCountries();

        Observable.zip(obsServices, obsCountries, new BiFunction<Response<ServiceResponse>,
                Response<ArrayList<LocationResponse.Country>>, Response<WriteReviewData>>() {
            @Override
            public Response<WriteReviewData> apply(@NonNull Response<ServiceResponse> serviceResponse,
                                         @NonNull Response<ArrayList<LocationResponse.Country>> countries) throws Exception {
                if (serviceResponse.isSuccessful() && countries.isSuccessful()) {
                    WriteReviewData data = new WriteReviewData();
                    data.setServices(serviceResponse.body().getServices());
                    data.setCountries(countries.body());
                    return Response.success(data);
                } else {
                    return Response.error(400, null);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<WriteReviewData>>() {
                    @Override
                    public void onNext(Response<WriteReviewData> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                mView.showServices(response.body().getServices());
                                mView.showCountries(response.body().getCountries());
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
    public void getCompanies(String countryId, String stateId, String cityId) {
        Log.i("debug", "fetching companies for country " + countryId + " state " +
                stateId + " city " + cityId + "...");

        mView.showProgress();

        mService.getCompanies(countryId, stateId, cityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<SearchResponse>>() {
                    @Override
                    public void onNext(Response<SearchResponse> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                mView.showCompanies(response.body().getCompanies());
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
    public void getStatesAndCompanies(String countryId, String stateId, String cityId) {
        Log.i("debug", "fetching states and companies for country " + countryId + "...");

        Observable<Response<ArrayList<LocationResponse.State>>> obsStates = mService.getStates(countryId);
        Observable<Response<SearchResponse>> obsCompanies = mService.getCompanies(countryId, stateId, cityId);

        mView.showProgress();

        Observable.zip(obsStates, obsCompanies, new BiFunction<Response<ArrayList<LocationResponse.State>>,
                Response<SearchResponse>, Response<WriteReviewData>>() {
            @Override
            public Response<WriteReviewData> apply(@NonNull Response<ArrayList<LocationResponse.State>> states,
                                         @NonNull Response<SearchResponse> searchResponse) throws Exception {
                if (states.isSuccessful() && searchResponse.isSuccessful()) {
                    WriteReviewData data = new WriteReviewData();
                    data.setStates(states.body());
                    data.setCompanies(searchResponse.body().getCompanies());
                    return Response.success(data);
                } else {
                    return Response.error(400, null);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<WriteReviewData>>() {
                    @Override
                    public void onNext(Response<WriteReviewData> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                mView.showStates(response.body().getStates());
                                mView.showCompanies(response.body().getCompanies());
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
    public void getCitiesAndCompanies(String countryId, String stateId, String cityId) {
        Log.i("debug", "fetching cities and companies for country " + countryId +
                " and state " + stateId + "...");

        Observable<Response<ArrayList<LocationResponse.City>>> obsCities = mService.getCities(countryId, stateId);
        Observable<Response<SearchResponse>> obsCompanies = mService.getCompanies(countryId, stateId, cityId);

        mView.showProgress();

        Observable.zip(obsCities, obsCompanies, new BiFunction<Response<ArrayList<LocationResponse.City>>,
                Response<SearchResponse>, Response<WriteReviewData>>() {
            @Override
            public Response<WriteReviewData> apply(@NonNull Response<ArrayList<LocationResponse.City>> cities,
                                         @NonNull Response<SearchResponse> searchResponse) throws Exception {
                if (cities.isSuccessful() && searchResponse.isSuccessful()) {
                    WriteReviewData data = new WriteReviewData();
                    data.setCities(cities.body());
                    data.setCompanies(searchResponse.body().getCompanies());
                    return Response.success(data);
                } else {
                    return Response.error(400, null);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<WriteReviewData>>() {
                    @Override
                    public void onNext(Response<WriteReviewData> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                mView.showCities(response.body().getCities());
                                mView.showCompanies(response.body().getCompanies());
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
    public boolean validate(NewReview review) {
        boolean valid = true;

        if (review.getActivity() == 0) {
            mView.showActivityError();
            valid = false;
        }
        if (review.getCompany() == 0) {
            mView.showCompanyError();
            valid = false;
        }
        if (review.getService() == 0) {
            mView.showServiceError();
            valid = false;
        }
        if (review.getTitle() == null || review.getTitle().isEmpty()) {
            mView.showTitleError();
            valid = false;
        }
        if (review.getDescription() == null || review.getDescription().isEmpty()) {
            mView.showDescriptionError();
            valid = false;
        }
        if (!review.getAcceptedConditions()) {
            mView.showConditionsError();
            valid = false;
        }

        return valid;
    }

    @Override
    public void sendReview(NewReview review) {
        if (!validate(review)) return;

        mView.showProgress();

        Log.i("user", review.getUser()+"");
        Log.i("activity", review.getActivity()+"");
        Log.i("company", review.getCompany()+"");
        Log.i("service", review.getService()+"");
        Log.i("title", review.getTitle());
        Log.i("description", review.getDescription());
        Log.i("value", review.getValue()+"");

        mService.sendReview(review)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<SendReviewResponse>>() {
                    @Override
                    public void onNext(Response<SendReviewResponse> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) mView.showSendReviewSuccess();
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
}
