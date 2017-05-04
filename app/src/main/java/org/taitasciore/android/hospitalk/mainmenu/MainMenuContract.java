package org.taitasciore.android.hospitalk.mainmenu;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;

/**
 * Created by roberto on 18/04/17.
 */

public interface MainMenuContract {

    interface View extends BaseView<Presenter> {

        void launchReviewsFragment();
        void launchSearchHospitalsFragment();
        void launchWriteOpinionFragment();
        void launchSearchServicesFragment();
        void launchCloseFragment();
        void showUserNotLoggedError();
        void showLocationNotDeterminedError();
    }

    interface Presenter extends BasePresenter<View> {

    }
}
