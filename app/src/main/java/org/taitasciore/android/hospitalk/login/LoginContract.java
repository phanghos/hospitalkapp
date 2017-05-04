package org.taitasciore.android.hospitalk.login;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.model.User;
import org.taitasciore.android.model.UserRegistration;

/**
 * Created by roberto on 25/04/17.
 */

public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void showLoginError();
        void showLoginFailedError();
        void facebookLogin();
        void saveUser(User user);
        void showGreetings(String name);
        void showMenu();
        void showPlayServicesNotAvailableError();
        void showFacebookLoginError();
        void showGoogleLoginError();
        void showNetworkError();
        void showNetworkFailedError();
    }

    interface Presenter extends BasePresenter<View> {

        boolean validate(String email, String psw);
        void login(String email, String psw);
        void registerSocial(UserRegistration userRegistration);
    }
}
