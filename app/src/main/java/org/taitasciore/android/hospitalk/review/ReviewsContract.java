package org.taitasciore.android.hospitalk.review;

import org.taitasciore.android.model.Review;
import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;

import java.util.ArrayList;

/**
 * Created by roberto on 18/04/17.
 */

public interface ReviewsContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void initAdapter();
        void showReviews(ArrayList<Review> reviews);
        void incrementOffset();
        void showNetworkError();
        void showNetworkFailedError();
        void showNoMoreReviewsError();
    }

    interface Presenter extends BasePresenter<View> {

        void getReviews(int offset, double lat, double lon);
    }
}
