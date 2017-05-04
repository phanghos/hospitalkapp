package org.taitasciore.android.hospitalk.service;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.Service;

import java.util.ArrayList;

/**
 * Created by roberto on 25/04/17.
 */

public interface ServiceDetailsContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void incrementOffset();
        void initRecyclerView();
        void showMainContent();
        void showServiceDetails(Service service);
        void setReviews(ArrayList<Review> reviews);
        void showReviews();
        void hideReviews();
        void launchWriteReviewFragment();
        void showNetworkError();
        void showNetworkFailedError();
        void showPlayServicesError();
        void showLocationNotAvailableError();
        void showNoReviewsError();
        void showUserNotLoggedError();
    }

    interface Presenter extends BasePresenter<View> {

        void getServiceDetails(int id);
        void getServiceReviews(int id, int offset);
    }
}
