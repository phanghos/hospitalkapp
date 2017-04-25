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
                .subscribe(new DisposableObserver<Hospital>() {
                    @Override
                    public void onNext(Hospital hospital) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showHospitalDetails(hospital);
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
    public void getHospitalServices(int id, int offset) {
        mService.getCompanyServices(id, offset, LIMIT, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ServiceResponse>() {
                    @Override
                    public void onNext(ServiceResponse serviceResponse) {
                        mView.setServices(serviceResponse.getServices());
                        mView.showServices();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getHospitalReviews(int id, int offset) {
        mService.getCompanyReviews(id, offset, LIMIT, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ArrayList<Review>>() {
                    @Override
                    public void onNext(ArrayList<Review> reviews) {
                        mView.setReviews(reviews);
                        mView.showReviews();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
