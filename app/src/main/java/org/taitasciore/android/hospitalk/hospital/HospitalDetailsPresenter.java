package org.taitasciore.android.hospitalk.hospital;

import android.util.Log;

import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.ServiceResponse;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by roberto on 19/04/17.
 */

public class HospitalDetailsPresenter implements HospitalDetailsContract.Presenter {

    public static final int LIMIT = 5;

    private HospitalDetailsContract.View mView;
    private HospitalkService mService;

    public HospitalDetailsPresenter(HospitalDetailsContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(HospitalDetailsContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getHospitalDetails(int id) {
        Log.i("debug", "fetching hospital for id " + id + "...");

        mView.showProgress();

        mService.getHospitalDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<Hospital>>() {
                    @Override
                    public void onNext(Response<Hospital> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                mView.showHospitalDetails(response.body());
                                mView.showMainContent();
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
    public void getHospitalServices(int id, int offset) {
        Log.i("debug", "fetching hospital services for id " + id + "...");

        mView.showProgress();

        mService.getCompanyServices(id, offset, LIMIT, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ServiceResponse>>() {
                    @Override
                    public void onNext(Response<ServiceResponse> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                if (response.body().getServices().size() > 0) {
                                    mView.incrementServicesOffset();
                                    mView.setServices(response.body().getServices());
                                    mView.showServices();
                                } else {
                                    mView.showNoServicesError();
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
    public void getHospitalReviews(int id, int offset) {
        Log.i("debug", "fetching hospital reviews for id " + id + "...");

        mView.showProgress();

        mService.getCompanyReviews(id, offset, LIMIT, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ArrayList<Review>>>() {
                    @Override
                    public void onNext(Response<ArrayList<Review>> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                if (response.body().size() > 0) {
                                    mView.incrementReviewsOffset();
                                    mView.setReviews(response.body());
                                    mView.showReviews();
                                } else {
                                    mView.showNoReviewsError();
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
