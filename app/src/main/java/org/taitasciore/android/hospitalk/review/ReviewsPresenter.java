package org.taitasciore.android.hospitalk.review;

import android.util.Log;

import org.taitasciore.android.model.Review;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by roberto on 18/04/17.
 */

public class ReviewsPresenter implements ReviewsContract.Presenter {

    public static final int LIMIT = 10;

    private ReviewsContract.View mView;
    private HospitalkService mService;

    public ReviewsPresenter(ReviewsContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(ReviewsContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getReviews(int offset, double lat, double lon) {
        mView.showProgress();

        mService.getReviews(offset, LIMIT, 1, lat, lon)
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
                                    mView.showReviews(response.body());
                                    mView.incrementOffset();
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
