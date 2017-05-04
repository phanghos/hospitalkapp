package org.taitasciore.android.hospitalk.menu;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.taitasciore.android.hospitalk.HospitalkApp;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StorageUtils;
import org.taitasciore.android.hospitalk.login.LoginFragment;
import org.taitasciore.android.hospitalk.mainmenu.MainMenuFragment;
import org.taitasciore.android.hospitalk.profile.ProfileFragment;
import org.taitasciore.android.hospitalk.signup.SignupFragment;
import org.taitasciore.android.network.HospitalkApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 25/04/17.
 */

public class MenuFragment extends Fragment implements MenuContract.View {

    @BindView(R.id.btnLogin) Button mBtnLogin;
    @BindView(R.id.btnShare) ShareButton mBtnShare;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, v);

        if (StorageUtils.isUserLogged(getActivity())) {
            mBtnLogin.setText("Ver perfil");
        } else {
            mBtnLogin.setText("Iniciar sesión");
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        initListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        clearListener();
    }

    @Override
    public void bindPresenter(MenuContract.Presenter presenter) {

    }

    @Override
    public void launchMainMenuFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MainMenuFragment()).commit();
    }

    @Override
    public void launchLoginFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .addToBackStack(null).commit();
    }

    @Override
    public void launchSignUpFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SignupFragment())
                .addToBackStack(null).commit();
    }

    @Override
    public void launchProfileFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ProfileFragment())
                .addToBackStack(null).commit();
    }

    @Override
    public void showConfirmDialog() {
        String msg = "";

        if (StorageUtils.isUserLogged(getActivity())) msg = "cerrar sesión";

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .theme(Theme.LIGHT)
                .title("Confirmar")
                .content("¿Está seguro de que desea salir?")
                .positiveText("sí")
                .negativeText("no")
                .neutralText(msg)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getActivity().finish();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        StorageUtils.removeUser(getActivity());
                        signoutFacebook();
                        signoutGoogle();
                        getActivity().finish();
                    }
                });

        builder.show();
    }

    @Override
    public void initListener() {
        mBtnShare.setShareContent(new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(HospitalkApi.BASE_URL))
                .setImageUrl(Uri.parse(HospitalkApi.BASE_URL + "public/frontend/images/logo.png"))
                .setContentTitle("ScoutService.com")
                .setContentDescription(getResources().getString(R.string.compartir_pagina_scout))
                .build());
    }

    @Override
    public void clearListener() {
        mBtnShare.setOnClickListener(null);
    }

    @Override
    public void shareContent() {

    }

    @Override
    public void signoutFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    public void signoutGoogle() {
        final GoogleApiClient googleApiClient = ((HospitalkApp) getActivity()
                .getApplication()).getGoogleApiClient();

        if (googleApiClient != null && googleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        ((HospitalkApp) getActivity().getApplication())
                                .setGoogleApiClient(googleApiClient);
                    }
                }
            });
        }
    }

    @OnClick(R.id.btnHome) void onHomeClicked() {
        launchMainMenuFragment();
    }

    @OnClick(R.id.btnLogin) void onLoginClicked() {
        if (StorageUtils.isUserLogged(getActivity())) {
            launchProfileFragment();
        } else {
            launchLoginFragment();
        }
    }

    @OnClick(R.id.btnSignUp) void onSignUpClicked() {
        launchSignUpFragment();
    }

    @OnClick(R.id.btnLogout) void onLogoutClicked() {
        showConfirmDialog();
    }
}
