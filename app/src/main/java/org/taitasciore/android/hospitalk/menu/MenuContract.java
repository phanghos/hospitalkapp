package org.taitasciore.android.hospitalk.menu;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;

/**
 * Created by roberto on 25/04/17.
 */

public class MenuContract {

    interface View extends BaseView<Presenter> {

        void launchMainMenuFragment();
        void launchLoginFragment();
        void launchSignUpFragment();
        void launchProfileFragment();
        void showConfirmDialog();
        void initListener();
        void clearListener();
        void shareContent();
        void signoutFacebook();
        void signoutGoogle();
    }

    interface Presenter extends BasePresenter<View> {

    }
}
