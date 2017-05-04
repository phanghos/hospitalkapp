package org.taitasciore.android.hospitalk.service;

import android.util.Log;

import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.Service;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by roberto on 25/04/17.
 */

public class ServiceDetailsPresenter implements ServiceDetailsContract.Presenter {

    public static final int LIMIT = 5;

    private ServiceDetailsContract.View mView;
    private HospitalkService mService;

    public ServiceDetailsPresenter(ServiceDetailsContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(ServiceDetailsContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getServiceDetails(int id) {
        Log.i("debug", "fetching service with id " + id + "...");

        mView.showProgress();

        mService.getServiceDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<Service>>() {
                    @Override
                    public void onNext(Response<Service> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                mView.showServiceDetails(response.body());
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
    public void getServiceReviews(int id, int offset) {
        Log.i("debug", "fetching service reviews...");

        mView.showProgress();

        mService.getServiceReviews(id, offset, LIMIT, 1)
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
                                    mView.incrementOffset();
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
