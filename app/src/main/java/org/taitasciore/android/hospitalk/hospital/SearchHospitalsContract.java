package org.taitasciore.android.hospitalk.hospital;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.model.Activity;
import org.taitasciore.android.model.City;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.LocationResponse;

import java.util.ArrayList;

/**
 * Created by roberto on 24/04/17.
 */

public interface SearchHospitalsContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void showFab();
        void showNoResultsMessage();
        void initRecyclerView();
        void initAdapters();
        void initListeners();
        void clearListeners();
        void incrementOffset();
        void showCountries(ArrayList<LocationResponse.Country> countries);
        void showCities(ArrayList<City> cities);
        void showActivities(ArrayList<Activity> activities);
        void showHospitals(ArrayList<Hospital> hospitals);
        void addHospitals(ArrayList<Hospital> hospitals);
    }

    interface Presenter extends BasePresenter<View> {

        void getCountries();
        void searchHospitals(String countryId, String cityId, String serviceId, String reviewOrder,
                             String reviewRank, int offset);
    }
}
