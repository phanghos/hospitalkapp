package org.taitasciore.android.hospitalk.service;

import android.util.Log;

import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.Service;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

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
                .subscribe(new DisposableObserver<Service>() {
                    @Override
                    public void onNext(Service service) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showServiceDetails(service);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
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
                .subscribe(new DisposableObserver<ArrayList<Review>>() {
                    @Override
                    public void onNext(ArrayList<Review> reviews) {
                        if (mView != null) {
                            mView.hideProgress();
                            if (reviews.size() > 0) {
                                mView.incrementOffset();
                                mView.setReviews(reviews);
                                mView.showReviews();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
