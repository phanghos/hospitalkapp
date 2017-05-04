package org.taitasciore.android.hospitalk.dialog;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;

/**
 * Created by roberto on 30/04/17.
 */

public interface FooterDialogContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void showFooterContent(String content);
        void showNetworkError();
        void showNetworkFailedError();
    }

    interface Presenter extends BasePresenter<View> {

        void getFooterContent(int pageId);
    }
}
