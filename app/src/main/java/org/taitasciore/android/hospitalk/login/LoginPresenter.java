package org.taitasciore.android.hospitalk.login;

import android.util.Log;

import org.taitasciore.android.model.User;
import org.taitasciore.android.model.UserRegistration;
import org.taitasciore.android.network.HospitalkService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by roberto on 25/04/17.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;
    private HospitalkService mService;

    public LoginPresenter(LoginContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(LoginContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public boolean validate(String email, String psw) {
        return !(email.isEmpty() || psw.isEmpty());
    }

    @Override
    public void login(String email, String psw) {
        if (!validate(email, psw)) {
            mView.showLoginError();
            return;
        }

        mView.showProgress();

        mService.login(email, psw)
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
                            } else if (response.code() == 404) {
                                mView.showLoginFailedError();
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
    public void registerSocial(UserRegistration userRegistration) {
        mView.showProgress();

        mService.registerSocial(userRegistration)
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
