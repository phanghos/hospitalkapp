package org.taitasciore.android.hospitalk.profile;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.User;

import java.util.ArrayList;

/**
 * Created by roberto on 30/04/17.
 */

public interface ProfileContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void initRecyclerView();
        void initAdapter();
        void incrementReviewsOffset();
        void showMainContent();
        void showUserInfo(User user);
        void showUserCountry(String country);
        void setReviews(ArrayList<Review> reviews);
        void showReviews();
        void hideReviews();
        void showNetworkError();
        void showNetworkFailedError();
        void showNoMoreReviewsError();
        void showUserNotLoggedError();
        void showUserNotAvailableError();
    }

    interface Presenter extends BasePresenter<View> {

        void getUserInfo(int userId);
        void getCountries(String countryId);
        void getUserReviews(int userId, int offset);
    }
}
