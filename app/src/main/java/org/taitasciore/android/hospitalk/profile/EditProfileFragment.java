package org.taitasciore.android.hospitalk.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.SetDateEvent;
import org.taitasciore.android.event.SetUserEvent;
import org.taitasciore.android.event.UploadImageEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.DatePickerDialogFragment;
import org.taitasciore.android.hospitalk.EncodingUtils;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.User;
import org.taitasciore.android.model.UserRegistration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 02/05/17.
 */

public class EditProfileFragment extends Fragment implements EditProfileContract.View {

    public static final int REQUEST_UPLOAD_IMAGE = 002;

    private User mUser;
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

    private EditProfileContract.Presenter mPresenter;

    private ArrayAdapter<String> mAdapterCountry;
    private ArrayAdapter<String> mAdapterState;
    private ArrayAdapter<String> mAdapterCity;

    private ProgressDialog mDialog;

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

    public static EditProfileFragment newInstance(User user) {
        EditProfileFragment f = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setRetainInstance(true);
        mUser = (User) getArguments().getSerializable("user");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);
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
        if (mPresenter == null) mPresenter = new EditProfilePresenter(this);
        else mPresenter.bindView(this);
    }

    @Override
    public void onDetach() {
        hideProgress();
        super.onDetach();
        if (mPresenter != null) mPresenter.unbindView();
    }

    @Override
    public void onResume() {
        super.onResume();

        int pos;

        if (mUser.getName() != null && !mUser.getName().isEmpty()) {
            mName.setText(mUser.getName()+"");
        }
        if (mUser.getFirstLastName() != null && !mUser.getFirstLastName().isEmpty()) {
            mFirstLastName.setText(mUser.getFirstLastName()+"");
        }
        if (mUser.getSecondLastName() != null && !mUser.getSecondLastName().isEmpty()) {
            mSecondLastName.setText(mUser.getSecondLastName()+"");
        }
        if (mUser.getPhone() != null && !mUser.getPhone().isEmpty()) {
            mPhone.setText(mUser.getPhone()+"");
        }
        if (mUser.getBirthday() != null && !mUser.getBirthday().isEmpty()) {
            String[] splitBirthday = mUser.getBirthday().split("-");
            mBirthday.setText(splitBirthday[2] + "-" + splitBirthday[1] + "-" + splitBirthday[0]);
        }
        if (mUser.getEmail() != null && !mUser.getEmail().isEmpty()) {
            mEmail.setText(mUser.getEmail()+"");
        }

        if (mCountries == null) {
            mPresenter.getCountries();
        }
        else {
            showCountries(mCountries);
            if (mUser.getIdCountry() != null && !mUser.getIdCountry().isEmpty()) {
                pos = getCountryPosition(mUser.getIdCountry());

                if (pos > -1) {
                    mCountryId = mCountries.get(pos).getCountryid();
                    mCountryPos = pos + 1;
                } else {
                    mCountryPos = 0;
                }
            }
            mSpCountry.setSelection(mCountryPos, false);
        }

        if (mStates != null) {
            showStates(mStates);
            mSpState.setSelection(mStatePos, false);
            if (mUser.getIdState() != 0) {
                pos = getStatePosition(mUser.getIdState()+"");

                if (pos > -1) {
                    mStateId = mStates.get(pos).getStateid();
                    mStatePos = pos + 1;
                } else {
                    mStatePos = 0;
                }
                mSpState.setSelection(mStatePos, false);
            }
        }
        if (mCities != null) {
            showCities(mCities);
            if (mUser.getIdCity() != 0) {
                pos = getCityPosition(mUser.getIdCity()+"");

                if (pos > -1) {
                    mCityId = mCities.get(pos).getCityid();
                    mCityPos = pos + 1;
                } else {
                    mCityPos = 0;
                }
            }
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
    public void bindPresenter(EditProfileContract.Presenter presenter) {
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
        if (mUser.getIdCountry() != null && !mUser.getIdCountry().isEmpty()) {
            int pos = getCountryPosition(mUser.getIdCountry());

            if (pos > -1) {
                mCountryId = mCountries.get(pos).getCountryid();
                mCountryPos = pos;
                mSpCountry.setSelection(pos + 1, false);
                Log.i("country id", mCountryId);
            }
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
        if (mUser.getIdState() != 0) {
            int pos = getStatePosition(mUser.getIdState()+"");

            if (pos > -1) {
                mStateId = mStates.get(pos).getStateid();
                mStatePos = pos;
                mSpState.setSelection(pos + 1, false);
                Log.i("state id", mStateId+"");
            }
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
        if (mUser.getIdCity() != 0) {
            int pos = getCityPosition(mUser.getIdCity()+"");

            if (pos > -1) {
                mCityId = mCities.get(pos).getCityid();
                mCityPos = pos;
                mSpCity.setSelection(pos + 1, false);
                Log.i("city id", mCityId+"");
            }
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

    @OnClick(R.id.btnUpdateProfile) void onUpdateProfileClicked() {
        UserRegistration ur = new UserRegistration();
        ur.setId(mUser.getIdUser());
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
        mPresenter.updateProfile(ur);
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
    public void showNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNetworkFailedError() {
        Toast.makeText(getActivity(), getString(R.string.network_failed_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUpdateProfileSuccess() {
        Toast.makeText(getActivity(), getString(R.string.update_profile_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void launchProfileFragment(User user) {
        EventBus.getDefault().post(new SetUserEvent(user));
        getActivity().onBackPressed();
    }

    private int getCountryPosition(String countryId) {
        int i = 0;

        for (LocationResponse.Country c : mCountries) {
            if (c.getCountryid().equalsIgnoreCase(countryId)) return i;
            i++;
        }

        return -1;
    }

    private int getStatePosition(String stateId) {
        int i = 0;

        for (LocationResponse.State s : mStates) {
            if (s.getStateid().equalsIgnoreCase(stateId)) return i;
            i++;
        }

        return -1;
    }

    private int getCityPosition(String cityId) {
        int i = 0;

        for (LocationResponse.City c : mCities) {
            if (c.getCityid().equalsIgnoreCase(cityId)) return i;
            i++;
        }

        return -1;
    }
}
