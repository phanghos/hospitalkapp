package org.taitasciore.android.hospitalk.menu;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;

/**
 * Created by roberto on 18/04/17.
 */

public interface MenuContract {

    interface View extends BaseView<Presenter> {

        void launchOpinionsActivity();
        void launchSearchHospitalsActivity();
        void launchWriteOpinionActivity();
        void launchSearchServicesActivity();
        void launchCloseActivity();
    }

    interface Presenter extends BasePresenter<View> {

    }
}
