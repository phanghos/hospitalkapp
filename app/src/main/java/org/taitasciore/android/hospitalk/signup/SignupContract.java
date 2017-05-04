package org.taitasciore.android.hospitalk.signup;

import android.net.Uri;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.User;
import org.taitasciore.android.model.UserRegistration;

import java.util.ArrayList;

/**
 * Created by roberto on 25/04/17.
 */

public interface SignupContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void initAdapters();
        void initListeners();
        void clearListeners();
        void showCountries(ArrayList<LocationResponse.Country> countries);
        void showStates(ArrayList<LocationResponse.State> states);
        void showCities(ArrayList<LocationResponse.City> cities);
        void launchImageUpload();
        void showImagePreview(Uri uri);
        void showDatePicker();
        void facebookLogin();
        void saveUser(User user);
        void showGreetings(String name);
        void showMenu();
        void showPlayServicesNotAvailableError();
        void showNameError();
        void showFirstLastNameError();
        void showPhoneLengthError();
        void showBirthdayError();
        void showCountryError();
        void showStateError();
        void showCityError();
        void showEmailError();
        void showEmailFormatError();
        void showEmailResponseError();
        void showPasswordError();
        void showPasswordConfirmError();
        void showPasswordNoMatchError();
        void showPasswordLengthError();
        void showFacebookLoginError();
        void showGoogleLoginError();
        void showNetworkError();
        void showNetworkFailedError();
        void showSignupSuccess();
    }

    interface Presenter extends BasePresenter<View> {

        void getCountries();
        void getStates(String countryId);
        void getCities(String countryId, String stateId);
        boolean validate(UserRegistration user);
        void register(UserRegistration user);
        void registerSocial(UserRegistration user);
    }
}
