package org.taitasciore.android.hospitalk.review;

import android.util.Log;

import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.ReviewResponse;
import org.taitasciore.android.network.HospitalkService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by roberto on 19/04/17.
 */

public class ReviewDetailsPresenter implements ReviewDetailsContract.Presenter {

    private ReviewDetailsContract.View mView;
    private HospitalkService mService;

    public ReviewDetailsPresenter(ReviewDetailsContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(ReviewDetailsContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getReview(int id) {
        Log.i("debug", "fetching reviews for id " + id + "...");

        mView.showProgress();

        mService.getReviewDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ReviewResponse>() {
                    @Override
                    public void onNext(ReviewResponse reviewResponse) {
                        Review review = reviewResponse.getRating();
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showReviewInfo(review);
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
