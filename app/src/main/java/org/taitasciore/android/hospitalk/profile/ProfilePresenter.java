package org.taitasciore.android.hospitalk.profile;

import android.util.Log;

import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.User;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by roberto on 30/04/17.
 */

public class ProfilePresenter implements ProfileContract.Presenter {

    public static final int LIMIT = 5;

    private ProfileContract.View mView;
    private HospitalkService mService;

    public ProfilePresenter(ProfileContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(ProfileContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getUserInfo(int userId) {
        Log.i("debug", "fetching user info for user " + userId + "...");

        mView.showProgress();

        mService.getUserInfo(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<User>>() {
                    @Override
                    public void onNext(Response<User> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                mView.showUserInfo(response.body());
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
    public void getCountries(final String countryId) {
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

                            if (response.isSuccessful()) {
                                for (LocationResponse.Country c : response.body()) {
                                    if (c.getCountryid().equalsIgnoreCase(countryId)) {
                                        mView.showUserCountry(c.getCountryname());
                                    }
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
    public void getUserReviews(int userId, int offset) {
        Log.i("debug", "fetching user reviews");

        mView.showProgress();

        mService.getUserReviews(userId, offset, LIMIT)
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
                                    mView.setReviews(response.body());
                                    mView.showReviews();
                                    mView.incrementReviewsOffset();
                                } else {
                                    mView.showNoMoreReviewsError();
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
