package org.taitasciore.android.hospitalk.close;

import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.ServiceResponse;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

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
    public void getBestRatedHospitals(int offset, double lat, double lon) {
        mView.showProgress();

        mService.getBestRatedHospitals(offset, LIMIT, 1, lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ArrayList<Hospital>>() {
                    @Override
                    public void onNext(ArrayList<Hospital> hospitals) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.setBestRatedHospitals(hospitals);
                            mView.showBestRatedHospitals();
                            mView.incrementBestRatedOffset();
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
    public void getWorstRatedHospitals(int offset, double lat, double lon) {
        mView.showProgress();

        mService.getWorstRatedHospitals(offset, LIMIT, 1, lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ArrayList<Hospital>>() {
                    @Override
                    public void onNext(ArrayList<Hospital> hospitals) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.setWorstRatedHospitals(hospitals);
                            mView.showWorstRatedHospitals();
                            mView.incrementWorstRatedOffset();
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
    public void getPopularServices(int offset, double lat, double lon) {
        mView.showProgress();

        mService.getPopularServices(offset, LIMIT, 1, lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ServiceResponse>() {
                    @Override
                    public void onNext(ServiceResponse serviceResponse) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.setPopularServices(serviceResponse.getServices());
                            mView.showPopularServices();
                            mView.incrementPopularServicesOffset();
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
