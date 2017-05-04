package org.taitasciore.android.hospitalk.close;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;

/**
 * Created by roberto on 18/04/17.
 */

public interface CloseContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void incrementBestRatedOffset();
        void setBestRatedHospitals(ArrayList<Hospital> list);
        void showBestRatedHospitals();
        void hideBestRatedHospitals();
        void incrementWorstRatedOffset();
        void setWorstRatedHospitals(ArrayList<Hospital> list);
        void showWorstRatedHospitals();
        void hideWorstRatedHospitals();
        void incrementPopularServicesOffset();
        void setPopularServices(ArrayList<ServiceResponse.Service> list);
        void showPopularServices();
        void hidePopularServices();
        void addBestRatedHospitals(ArrayList<Hospital> hospitals);
        void addWorstRatedHospitals(ArrayList<Hospital> hospitals);
        void addPopularServices(ArrayList<ServiceResponse.Service> services);
        void showNetworkError();
        void showNetworkFailedError();
        void showNoMoreHospitalsError();
        void showNoMoreServicesError();
    }

    interface Presenter extends BasePresenter<View> {

        void getBestRatedHospitals(int offset, double lat, double lon);
        void getWorstRatedHospitals(int offset, double lat, double lon);
        void getPopularServices(int offset, double lat, double lon);
    }
}
