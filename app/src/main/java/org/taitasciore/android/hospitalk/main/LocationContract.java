package org.taitasciore.android.hospitalk.main;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.hospitalk.dialog.FooterDialogFragment;

/**
 * Created by roberto on 18/04/17.
 */

public interface LocationContract {

    interface View extends BaseView<Presenter> {

        void showPing();
        void showHeaderLoading();
        void updateLocation(String code, String city);
        void setCountry(String countryId);
        void setLocation(double lat, double lon);
        void setSpans();
        void showContactDialog();
        void showFooterDialog(FooterDialogFragment f);
        void showNetworkError();
        void showLocationFailedError();
    }

    interface Presenter extends BasePresenter<View> {

        void getLocation(double lat, double lon);
    }
}
