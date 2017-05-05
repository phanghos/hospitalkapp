package org.taitasciore.android.hospitalk.signup;

import android.util.Log;

import org.apache.commons.validator.routines.EmailValidator;
import org.taitasciore.android.model.Error;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.User;
import org.taitasciore.android.model.UserRegistration;
import org.taitasciore.android.network.HospitalkService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by roberto on 25/04/17.
 */

public class SignupPresenter implements SignupContract.Presenter {

    private SignupContract.View mView;
    private HospitalkService mService;

    public SignupPresenter(SignupContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(SignupContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getCountries() {
        mView.showProgress();

        mService.getCountries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ArrayList<LocationResponse.Country>>>() {
                    @Override
                    public void onNext(Response<ArrayList<LocationResponse.Country>> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) mView.showCountries(response.body());
                            else mView.showNetworkFailedError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showNetworkError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getStates(String countryId) {
        mView.showProgress();

        mService.getStates(countryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ArrayList<LocationResponse.State>>>() {
                    @Override
                    public void onNext(Response<ArrayList<LocationResponse.State>> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) mView.showStates(response.body());
                            else mView.showNetworkFailedError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showNetworkError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getCities(String countryId, String stateId) {
        mView.showProgress();

        mService.getCities(countryId, stateId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ArrayList<LocationResponse.City>>>() {
                    @Override
                    public void onNext(Response<ArrayList<LocationResponse.City>> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) mView.showCities(response.body());
                            else mView.showNetworkFailedError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showNetworkError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public boolean validate(UserRegistration user) {
        boolean valid = true;

        if (user.getName() == null || user.getName().isEmpty()) {
            valid = false;
            mView.showNameError();
        }
        if (user.getFirstLastName() == null || user.getFirstLastName().isEmpty()) {
            valid = false;
            mView.showFirstLastNameError();
        }
        if (user.getCountry() == null || user.getCountry().equals("")) {
            valid = false;
            mView.showCountryError();
        }
        if (user.getState() == null || user.getState().equals("")) {
            valid = false;
            mView.showStateError();
        }
        if (user.getCity() == null || user.getCity().equals("")) {
            valid = false;
            mView.showCityError();
        }
        if (user.getPhone() != null && !user.getPhone().isEmpty() && user.getPhone().length() < 9) {
            valid = false;
            mView.showPhoneLengthError();
        }
        if (user.getBirthday() == null || user.getBirthday().isEmpty()) {
            valid = false;
            mView.showBirthdayError();
        }
        if (user.getMail() == null || user.getMail().isEmpty()) {
            valid = false;
            mView.showEmailError();
        }
        if (user.getMail() != null && !user.getMail().isEmpty()) {
            EmailValidator validator = EmailValidator.getInstance();

            if (!validator.isValid(user.getMail())) {
                valid = false;
                mView.showEmailFormatError();
            }
        }
        if (user.getPsw() == null || user.getPsw().isEmpty()) {
            valid = false;
            mView.showPasswordError();
        }
        if (user.getPswConfirm() == null || user.getPswConfirm().isEmpty()) {
            valid = false;
            mView.showPasswordConfirmError();
        }
        if (user.getPsw() != null && !user.getPsw().isEmpty() &&
                user.getPswConfirm() != null && !user.getPswConfirm().isEmpty() &&
                !user.getPsw().equals(user.getPswConfirm())) {
            valid = false;
            mView.showPasswordNoMatchError();
        }
        if (user.getPsw() != null && !user.getPsw().isEmpty() &&
                user.getPswConfirm() != null && !user.getPswConfirm().isEmpty() &&
                user.getPsw().equals(user.getPswConfirm()) && user.getPsw().length() < 6) {
            valid = false;
            mView.showPasswordLengthError();
        }

        return valid;
    }

    @Override
    public void register(final UserRegistration user) {
        if (!validate(user)) return;

        Log.i("debug", "updating profile for user " + user.getId() + "...");

        Log.i("user id", user.getId()+"");
        Log.i("name", user.getName()+"");
        Log.i("first last name", user.getFirstLastName()+"");
        Log.i("second last name", user.getSecondLastName()+"");
        Log.i("country id", user.getCountry()+"");
        Log.i("state id", user.getState()+"");
        Log.i("city id", user.getCity()+"");
        Log.i("phone", user.getPhone()+"");
        Log.i("birthday", user.getBirthday()+"");
        Log.i("email", user.getMail()+"");
        Log.i("password", user.getPsw());
        Log.i("image", user.getImage()+"");

        mView.showProgress();

        mService.register(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<User>>() {
                    @Override
                    public void onNext(Response<User> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                mView.showSignupSuccess(user.getMail());
                            } else if (response.code() == 400) {
                                mView.showEmailResponseError();
                            } else {
                                mView.showNetworkFailedError();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showNetworkError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void registerSocial(UserRegistration user) {
        mView.showProgress();

        mService.registerSocial(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<User>>() {
                    @Override
                    public void onNext(Response<User> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                User user = response.body();
                                mView.saveUser(user);
                                mView.showGreetings(user.getName());
                                mView.showMenu();
                            } else {
                                mView.showNetworkFailedError();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showNetworkError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
