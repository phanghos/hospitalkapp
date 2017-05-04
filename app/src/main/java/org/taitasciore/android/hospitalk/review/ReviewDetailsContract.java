package org.taitasciore.android.hospitalk.review;

import android.widget.Button;

import org.taitasciore.android.model.Review;
import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;

/**
 * Created by roberto on 19/04/17.
 */

public interface ReviewDetailsContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void setReview(Review review);
        void showReviewInfo(Review review);
        void setRepliesButtonBackground(Button btn, int replies);
        void showMainContent();
        void showNetworkError();
        void showNetworkFailedError();
        void showReviewNotApprovedError();
    }

    interface Presenter extends BasePresenter<View> {

        void getReview(int id);
    }
}
