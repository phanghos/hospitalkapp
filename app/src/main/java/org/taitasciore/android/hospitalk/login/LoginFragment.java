package org.taitasciore.android.hospitalk.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.HospitalkApp;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StorageUtils;
import org.taitasciore.android.hospitalk.signup.SignupFragment;
import org.taitasciore.android.model.User;
import org.taitasciore.android.model.UserRegistration;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 25/04/17.
 */

public class LoginFragment extends Fragment implements LoginContract.View,
        GoogleApiClient.OnConnectionFailedListener {

    private LoginContract.Presenter mPresenter;

    private CallbackManager mCallbackMngr;
    private AccessToken mAccessToken;

    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog mDialog;

    @BindView(R.id.etMail) EditText mEtMail;
    @BindView(R.id.etPsw) EditText mEtPsw;

    @BindView(R.id.loginButton) LoginButton mLoginBtn;
    @BindView(R.id.btnFacebook) Button mBtnFacebook;
    @BindView(R.id.btnGoogle) Button mBtnGoogle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        mCallbackMngr = CallbackManager.Factory.create();

        mLoginBtn.setReadPermissions("public_profile", "email");
        mLoginBtn.setFragment(this);

        mLoginBtn.registerCallback(mCallbackMngr, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("fb success", loginResult.toString());
                mAccessToken = loginResult.getAccessToken();
                facebookLogin();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.i("fb error", error.getMessage()+"");
                showFacebookLoginError();
            }
        });

        mGoogleApiClient = ((HospitalkApp) getActivity().getApplication()).getGoogleApiClient();

        mBtnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAccessToken = AccessToken.getCurrentAccessToken();

                if (mAccessToken == null) {
                    LoginManager.getInstance().logInWithReadPermissions(
                            LoginFragment.this, Arrays.asList("public_profile", "email"));
                } else {
                    facebookLogin();
                }
            }
        });

        mBtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityUtils.isGoogleApiAvailable(getActivity())) {
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                        signIn();
                    }
                } else {
                    showPlayServicesNotAvailableError();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new LoginPresenter(this);
        else mPresenter.bindView(this);
    }

    @Override
    public void onDetach() {
        hideProgress();
        super.onDetach();
        if (mPresenter != null) mPresenter.unbindView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackMngr.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @OnClick(R.id.btnLogin) void onLoginClicked() {
        String email = mEtMail.getText().toString();
        String psw = mEtPsw.getText().toString();
        mPresenter.login(email, psw);
    }

    @OnClick(R.id.btnSignup) void onSignupClicked() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SignupFragment())
                .addToBackStack(null).commit();
    }

    @Override
    public void bindPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
        mPresenter.bindView(this);
    }

    @Override
    public void showProgress() {
        mDialog = ActivityUtils.showProgressDialog(getActivity());
    }

    @Override
    public void hideProgress() {
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
    }

    @Override
    public void showLoginError() {
        Toast.makeText(getActivity(), "Debe rellenar ambos campos", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoginFailedError() {
        Toast.makeText(getActivity(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void facebookLogin() {
        AccessToken token = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            Log.e("error", response.getError().getErrorMessage());
                            showFacebookLoginError();
                        } else {
                            Log.i("fb response", response.getRawResponse());

                            try {
                                String firstName = object.getString("first_name");
                                String lastName = object.getString("last_name");
                                String email = object.getString("email");
                                UserRegistration ur = new UserRegistration();
                                ur.setName(firstName);
                                ur.setFirstLastName(lastName);
                                ur.setMail(email);
                                mPresenter.registerSocial(ur);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showFacebookLoginError();
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 001);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("debug", "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            UserRegistration ur = new UserRegistration();
            ur.setName(acct.getGivenName());
            ur.setFirstLastName(acct.getFamilyName());
            ur.setMail(acct.getEmail());
            mPresenter.registerSocial(ur);
        } else if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_FAILED) {
            // Signed out, show unauthenticated UI.
            showGoogleLoginError();
        }
    }

    @Override
    public void saveUser(User user) {
        StorageUtils.saveUser(getActivity(), user);
    }

    @Override
    public void showGreetings(String name) {
        Toast.makeText(getActivity(), "Bienvenido, " + name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMenu() {
        FragmentManager fm = getActivity().getSupportFragmentManager();

        while (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate();
        }
    }

    @Override
    public void showPlayServicesNotAvailableError() {
        Toast.makeText(getActivity(), getString(R.string.play_services_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFacebookLoginError() {
        Toast.makeText(getActivity(), getString(R.string.facebook_login_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showGoogleLoginError() {
        Toast.makeText(getActivity(), getString(R.string.google_login_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNetworkFailedError() {
        Toast.makeText(getActivity(), getString(R.string.network_failed_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
