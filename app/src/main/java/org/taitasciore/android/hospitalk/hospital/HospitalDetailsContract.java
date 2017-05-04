package org.taitasciore.android.hospitalk.hospital;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;

/**
 * Created by roberto on 19/04/17.
 */

public interface HospitalDetailsContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void incrementServicesOffset();
        void incrementReviewsOffset();
        void showMainContent();
        void showHospitalDetails(Hospital hospital);
        void setServices(ArrayList<ServiceResponse.Service> list);
        void showServices();
        void hideServices();
        void setReviews(ArrayList<Review> list);
        void showReviews();
        void hideReviews();
        void launchNewServiceFragment();
        void showNetworkError();
        void showNetworkFailedError();
        void showPlayServicesError();
        void showLocationNotAvailableError();
        void showNoServicesError();
        void showNoReviewsError();
        void showUserNotLoggedError();
    }

    interface Presenter extends BasePresenter<View> {

        void getHospitalDetails(int id);
        void getHospitalServices(int id, int offset);
        void getHospitalReviews(int id, int offset);
    }
}
