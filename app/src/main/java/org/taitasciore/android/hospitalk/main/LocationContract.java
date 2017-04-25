package org.taitasciore.android.hospitalk.main;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;

/**
 * Created by roberto on 18/04/17.
 */

public interface LocationContract {

    interface View extends BaseView<Presenter> {

        void showPing();
        void showHeaderLoading();
        void updateLocation(String code, String city);
    }

    interface Presenter extends BasePresenter<View> {

        void getLocation(double lat, double lon);
    }
}
