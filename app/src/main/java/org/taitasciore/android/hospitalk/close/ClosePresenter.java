package org.taitasciore.android.hospitalk.close;

import android.util.Log;

import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.ServiceResponse;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by roberto on 18/04/17.
 */

public class ClosePresenter implements CloseContract.Presenter {

    public static final int LIMIT = 5;

    private CloseContract.View mView;
    private HospitalkService mService;

    public ClosePresenter(CloseContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(CloseContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getBestRatedHospitals(final int offset, double lat, double lon) {
        mView.showProgress();

        mService.getBestRatedHospitals(offset, LIMIT, 1, lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ArrayList<Hospital>>>() {
                    @Override
                    public void onNext(Response<ArrayList<Hospital>> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                if (response.body().size() > 0) {
                                    if (offset == 0) {
                                        mView.setBestRatedHospitals(response.body());
                                        mView.showBestRatedHospitals();
                                    } else {
                                        mView.addBestRatedHospitals(response.body());
                                    }
                                    mView.incrementBestRatedOffset();
                                } else {
                                    mView.showNoMoreHospitalsError();
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
    public void getWorstRatedHospitals(final int offset, double lat, double lon) {
        mView.showProgress();

        mService.getWorstRatedHospitals(offset, LIMIT, 1, lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ArrayList<Hospital>>>() {
                    @Override
                    public void onNext(Response<ArrayList<Hospital>> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                if (response.body().size() > 0) {
                                    if (offset == 0) {
                                        mView.setWorstRatedHospitals(response.body());
                                        mView.showWorstRatedHospitals();
                                    } else {
                                        mView.addWorstRatedHospitals(response.body());
                                    }
                                    mView.incrementWorstRatedOffset();
                                } else {
                                    mView.showNoMoreHospitalsError();
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
    public void getPopularServices(final int offset, double lat, double lon) {
        mView.showProgress();

        mService.getPopularServices(offset, LIMIT, 1, lat, lon)
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
                                    if (offset == 0) {
                                        mView.setPopularServices(response.body().getServices());
                                        mView.showPopularServices();
                                    } else {
                                        mView.addPopularServices(response.body().getServices());
                                    }
                                    mView.incrementPopularServicesOffset();
                                } else {
                                    mView.showNoMoreServicesError();
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
