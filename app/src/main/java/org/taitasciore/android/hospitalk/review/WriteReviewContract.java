package org.taitasciore.android.hospitalk.review;

import android.net.Uri;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.model.Activity;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.NewReview;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;

/**
 * Created by roberto on 26/04/17.
 */

public interface WriteReviewContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void initAdapters();
        void initListeners();
        void clearListeners();
        void showActivities(ArrayList<Activity> activities);
        void showCompanies(ArrayList<Hospital> companies);
        void showServices(ArrayList<ServiceResponse.Service> services);
        void showCountries(ArrayList<LocationResponse.Country> countries);
        void showStates(ArrayList<LocationResponse.State> states);
        void showCities(ArrayList<LocationResponse.City> cities);
        void addStars();
        void fillStars(int number);
        void resetStars();
        void initStarListeners();
        void launchImageUploadIntent();
        void showImagePreview(Uri uri);
        void showActivityError();
        void showCompanyError();
        void showServiceError();
        void showCountryError();
        void showStateError();
        void showCityError();
        void showTitleError();
        void showDescriptionError();
        void showConditionsError();
        void showUserNotLoggedError();
        void showMenu();
        void showSendReviewSuccess();
        void showCountryNotSelectedError();
        void showNetworkError();
        void showNetworkFailedError();
    }

    interface Presenter extends BasePresenter<View> {

        void getActivitiesAndCountries();
        void getServicesAndCountries();
        void getCompanies(String activityId, String query);
        void getCountries();
        void getStates(String countryId);
        void getCities(String countryId, String stateId);
        boolean validate(NewReview review);
        void sendReview(NewReview review);
    }
}
