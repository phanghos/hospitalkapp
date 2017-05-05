package org.taitasciore.android.hospitalk.service;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.model.Activity;
import org.taitasciore.android.model.City;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.ServiceFilter;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;

/**
 * Created by roberto on 24/04/17.
 */

public interface SearchServicesContract {

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
        void showServicesFilter(ArrayList<ServiceFilter> servicesFilter);
        void showServices(ArrayList<ServiceResponse.Service> hospitals);
        void addServices(ArrayList<ServiceResponse.Service> hospitals);
        void blockFilter();
        void unblockFilter();
        void showCountryNotSelectedError();
        void showNetworkError();
        void showNetworkFailedError();
    }

    interface Presenter extends BasePresenter<View> {

        void getCountries();
        void searchServices(String countryId, String cityId, String serviceId, String reviewOrder,
                             String reviewRank, int offset, String query);
    }
}
