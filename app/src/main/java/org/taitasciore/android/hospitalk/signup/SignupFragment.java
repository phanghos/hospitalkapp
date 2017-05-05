package org.taitasciore.android.hospitalk.signup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.taitasciore.android.event.SetDateEvent;
import org.taitasciore.android.event.UploadImageEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.DatePickerDialogFragment;
import org.taitasciore.android.hospitalk.EncodingUtils;
import org.taitasciore.android.hospitalk.HospitalkApp;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StorageUtils;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.User;
import org.taitasciore.android.model.UserRegistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 25/04/17.
 */

public class SignupFragment extends Fragment implements SignupContract.View {

    public static final int REQUEST_UPLOAD_IMAGE = 002;

    private String mCountryId = "";
    private String mStateId = "";
    private String mCityId = "";
    private int mCountryPos;
    private int mStatePos;
    private int mCityPos;
    private String mEncodedImage;
    private ArrayList<LocationResponse.Country> mCountries;
    private ArrayList<LocationResponse.State> mStates;
    private ArrayList<LocationResponse.City> mCities;

    private SignupContract.Presenter mPresenter;

    private CallbackManager mCallbackMngr;
    private AccessToken mAccessToken;

    private GoogleApiClient mGoogleApiClient;

    private ArrayAdapter<String> mAdapterCountry;
    private ArrayAdapter<String> mAdapterState;
    private ArrayAdapter<String> mAdapterCity;

    private ProgressDialog mDialog;

    @BindView(R.id.loginButton) LoginButton mLoginBtn;
    @BindView(R.id.btnFacebook) Button mBtnFacebook;
    @BindView(R.id.btnGoogle) Button mBtnGoogle;

    @BindView(R.id.etName) EditText mName;
    @BindView(R.id.etFirstLastName) EditText mFirstLastName;
    @BindView(R.id.etSecondLastName) EditText mSecondLastName;
    @BindView(R.id.etPhone) EditText mPhone;
    @BindView(R.id.etBirthday) EditText mBirthday;
    @BindView(R.id.etMail) EditText mEmail;
    @BindView(R.id.etPsw) EditText mPsw;
    @BindView(R.id.etPswConfirm) EditText mPswConfirm;
    @BindView(R.id.spCountry) SearchableSpinner mSpCountry;
    @BindView(R.id.spState) SearchableSpinner mSpState;
    @BindView(R.id.spCity) SearchableSpinner mSpCity;
    @BindView(R.id.ivPreview) ImageView mImagePreview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, v);
        initAdapters();
        mBirthday.setInputType(InputType.TYPE_NULL);
        mSpCountry.setTitle("Seleccione un país");
        mSpCountry.setPositiveButton("cerrar");
        mSpState.setTitle("Seleccione un estado");
        mSpState.setPositiveButton("cerrar");
        mSpCity.setTitle("Seleccione una ciudad");
        mSpCity.setPositiveButton("cerrar");
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new SignupPresenter(this);
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
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCountries == null) {
            mPresenter.getCountries();
        }
        else {
            showCountries(mCountries);
            mSpCountry.setSelection(mCountryPos, false);
        }

        if (mStates != null) {
            showStates(mStates);
            mSpState.setSelection(mStatePos, false);
        }
        if (mCities != null) {
            showCities(mCities);
            mSpCity.setSelection(mCityPos, false);
        }

        initListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        clearListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setDate(SetDateEvent event) {
        int year = event.year;
        int month = event.month;
        int day = event.day;
        mBirthday.setText(day + "-" + month + "-" + year);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadImage(UploadImageEvent event) {
        if (event.resultCode == getActivity().RESULT_OK) {
            Log.i("debug", "result ok");
            Log.i("data", event.data.toString());
            showImagePreview(event.data.getData());
            mEncodedImage = EncodingUtils.encodeImage(getActivity(), event.data.getData());
            if (mEncodedImage != null) {
                Log.i("encoded image", mEncodedImage);
            } else {
                mEncodedImage = "";
            }
        } else {
            Log.i("debug", "result cancelled");
        }
    }

    @Override
    public void bindPresenter(SignupContract.Presenter presenter) {
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
    public void initAdapters() {
        List<String> list = new ArrayList();
        list.add("País");
        mAdapterCountry = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, list);
        mAdapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCountry.setAdapter(mAdapterCountry);

        list = new ArrayList<>();
        list.add("Estado/Provincia");
        mAdapterState = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, list);
        mAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpState.setAdapter(mAdapterState);

        list = new ArrayList<>();
        list.add("Ciudad");
        mAdapterCity = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, list);
        mAdapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCity.setAdapter(mAdapterCity);
    }

    @Override
    public void initListeners() {
        mSpCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mCountryId = mCountries.get(pos - 1).getCountryid();
                    mCountryPos = pos;
                    mPresenter.getStates(mCountryId);
                    mStateId = "";
                    mStatePos = 0;
                    mCityId = "";
                    mCityPos = 0;
                    mSpState.setSelection(0, false);
                    mSpCity.setSelection(0, false);
                } else {
                    mCountryId = "";
                    mCountryPos = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mStateId = mStates.get(pos - 1).getStateid();
                    mStatePos = pos;
                    mPresenter.getCities(mCountryId, mStateId);
                    mCityId = "";
                    mCityPos = 0;
                    mSpCity.setSelection(0, false);
                } else {
                    mStateId = "";
                    mStatePos = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mCityId = mCities.get(pos - 1).getCityid();
                    mCityPos = pos;
                } else {
                    mCityId = "";
                    mCityPos = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean checked) {
                if (checked) {
                    showDatePicker();
                }
            }
        });

        mBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        mBtnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAccessToken = AccessToken.getCurrentAccessToken();

                if (mAccessToken == null) {
                    LoginManager.getInstance().logInWithReadPermissions(
                            SignupFragment.this, Arrays.asList("public_profile", "email"));
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
    public void clearListeners() {
        mSpCountry.setOnItemSelectedListener(null);
        mSpState.setOnItemSelectedListener(null);
        mSpCity.setOnItemSelectedListener(null);
        mBirthday.setOnFocusChangeListener(null);
        mBirthday.setOnClickListener(null);
    }

    @Override
    public void showCountries(ArrayList<LocationResponse.Country> countries) {
        mCountries = countries;
        for (LocationResponse.Country c : countries) {
            mAdapterCountry.add(c.getCountryname());
        }
    }

    @Override
    public void showStates(ArrayList<LocationResponse.State> states) {
        mStates = states;
        mAdapterState.clear();
        mAdapterState.add("Estado/Provincia");
        for (LocationResponse.State s : states) {
            mAdapterState.add(s.getStatename());
        }
    }

    @Override
    public void showCities(ArrayList<LocationResponse.City> cities) {
        mCities = cities;
        mAdapterCity.clear();
        mAdapterCity.add("Ciudad");
        for (LocationResponse.City c : cities) {
            mAdapterCity.add(c.getCityname());
        }
    }

    @Override
    public void launchImageUpload() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        getActivity().startActivityForResult(i, REQUEST_UPLOAD_IMAGE);
    }

    @Override
    public void showImagePreview(Uri uri) {
        mImagePreview.setImageURI(uri);
        mImagePreview.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDatePicker() {
        String birthday = mBirthday.getText().toString();
        DatePickerDialogFragment f;

        if (birthday.isEmpty()) {
            f = new DatePickerDialogFragment();
        } else {
            f = DatePickerDialogFragment.newInstance(birthday);
        }

        f.show(getActivity().getSupportFragmentManager(), "date");
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

    @OnClick(R.id.btnSignup) void onSignupClicked() {
        UserRegistration ur = new UserRegistration();
        ur.setId(0);
        ur.setName(mName.getText().toString());
        ur.setFirstLastName(mFirstLastName.getText().toString());
        ur.setSecondLastName(mSecondLastName.getText().toString());
        ur.setCountry(mCountryId);
        ur.setState(mStateId);
        ur.setCity(mCityId);
        ur.setPhone(mPhone.getText().toString());
        ur.setImage(mEncodedImage);

        String birthday = mBirthday.getText().toString();
        if (!birthday.isEmpty()) {
            String[] splitBirthday = birthday.split("-");
            birthday = splitBirthday[2] + "-" + splitBirthday[1] + "-" + splitBirthday[0];
        }
        ur.setBirthday(birthday);

        ur.setMail(mEmail.getText().toString());
        ur.setPsw(mPsw.getText().toString());
        ur.setPswConfirm(mPswConfirm.getText().toString());
        mPresenter.register(ur);
    }

    @OnClick(R.id.btnUploadImage) void onUploadImageClicked() {
        if (ActivityUtils.isWriteExternalStoragePermissionGranted(getActivity())) {
            launchImageUpload();
        } else {
            ActivityUtils.requestWriteExternalStoragePermission(getActivity());
        }
    }

    @Override
    public void showNameError() {
        mName.setError(getString(R.string.signup_error));
    }

    @Override
    public void showFirstLastNameError() {
        mFirstLastName.setError(getString(R.string.signup_error));
    }

    @Override
    public void showPhoneLengthError() {
        mPhone.setError("El campo debe tener al menos 9 caracteres");
    }

    @Override
    public void showBirthdayError() {
        mBirthday.setError(getString(R.string.signup_error));
    }

    @Override
    public void showCountryError() {
        Toast.makeText(getActivity(), "Debe seleccionar un país", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showStateError() {
        Toast.makeText(getActivity(), "Debe seleccionar un estado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCityError() {
        Toast.makeText(getActivity(), "Debe seleccionar una ciudad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmailError() {
        mEmail.setError(getString(R.string.signup_error));
    }

    @Override
    public void showEmailFormatError() {
        mEmail.setError(getString(R.string.email_format_error));
    }

    @Override
    public void showEmailResponseError() {
        mEmail.setError(getString(R.string.email_response_error));
    }

    @Override
    public void showPasswordError() {
        mPsw.setError(getString(R.string.signup_error));
    }

    @Override
    public void showPasswordConfirmError() {
        mPswConfirm.setError(getString(R.string.signup_error));
    }

    @Override
    public void showPasswordNoMatchError() {
        mPsw.setError("Las contraseñas no coinciden");
        mPswConfirm.setError("Las contraseñas no coinciden");
    }

    @Override
    public void showPasswordLengthError() {
        mPsw.setError("El campo debe tener al menos 9 caracteres");
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
    public void showSignupSuccess(String email) {
        String msg = "Se envió un email a " + email + ". Por favor confirmar para validar usuario";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }
}
