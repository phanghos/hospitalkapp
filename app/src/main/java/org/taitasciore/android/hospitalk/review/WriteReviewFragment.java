package org.taitasciore.android.hospitalk.review;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.UploadImageEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.EncodingUtils;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StarUtils;
import org.taitasciore.android.hospitalk.StorageUtils;
import org.taitasciore.android.model.Activity;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.NewReview;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 26/04/17.
 */

public class WriteReviewFragment extends Fragment implements WriteReviewContract.View {

    public static final int REQUEST_UPLOAD_IMAGE = 002;

    private int mActivityId;
    private int mActivityPos;
    private int mCompanyId;
    private int mCompanyPos;
    private int mServiceId;
    private int mServicePos;
    private String mCountryId;
    private int mCountryPos;
    private String mStateId = "";
    private int mStatePos;
    private String mCityId = "";
    private int mCityPos;
    private int mStarNumber = 1;
    private String mTitle;
    private String mDescription;
    private String mEncodedImage;
    private boolean mAcceptedConditions;
    private ArrayList<Activity> mActivities;
    private ArrayList<Hospital> mCompanies;
    private ArrayList<ServiceResponse.Service> mServices;
    private ArrayList<LocationResponse.Country> mCountries;
    private ArrayList<LocationResponse.State> mStates;
    private ArrayList<LocationResponse.City> mCities;

    private WriteReviewContract.Presenter mPresenter;

    private ArrayAdapter<String> mAdapterActivity;
    private ArrayAdapter<String> mAdapterCompany;
    private ArrayAdapter<String> mAdapterService;
    private ArrayAdapter<String> mAdapterCountry;
    private ArrayAdapter<String> mAdapterState;
    private ArrayAdapter<String> mAdapterCity;

    private ProgressDialog mDialog;

    @BindView(R.id.lyAvg) LinearLayout mLyAvg;
    @BindView(R.id.spActivity) Spinner mSpActivity;
    @BindView(R.id.spCompany) Spinner mSpCompany;
    @BindView(R.id.spService) Spinner mSpService;
    @BindView(R.id.spCountry) SearchableSpinner mSpCountry;
    @BindView(R.id.spState) SearchableSpinner mSpState;
    @BindView(R.id.spCity) SearchableSpinner mSpCity;
    @BindView(R.id.etTitle) EditText mEtTitle;
    @BindView(R.id.etDescription) EditText mEtDescription;
    @BindView(R.id.tvCharCounter) TextView mTvCharCounter;
    @BindView(R.id.cbConditions) CheckBox mCbConditions;
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
        View v = inflater.inflate(R.layout.fragment_write_review, container, false);
        ButterKnife.bind(this, v);
        initAdapters();
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
        if (mPresenter == null) mPresenter = new WriteReviewPresenter(this);
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

        if (mServices == null || mCountries == null) {
            mPresenter.getServicesAndCountries();
        } else {
            showServices(mServices);
            showCountries(mCountries);
            mSpService.setSelection(mServicePos, false);
            mSpCountry.setSelection(mCountryPos, false);
        }

        if (mActivities != null) {
            showActivities(mActivities);
            mSpActivity.setSelection(mActivityPos, false);
        }

        if (mCompanies != null) {
            showCompanies(mCompanies);
            mSpCompany.setSelection(mCompanyPos, false);
        }

        if (mStates != null) {
            showStates(mStates);
            mSpState.setSelection(mStatePos, false);
        }

        if (mCities != null) {
            showCities(mCities);
            mSpCity.setSelection(mCityPos, false);
        }

        StarUtils.resetStars(mLyAvg);
        addStars();
        fillStars(mStarNumber);

        initListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        mActivityPos = mSpActivity.getSelectedItemPosition();
        mCompanyPos = mSpCompany.getSelectedItemPosition();
        mServicePos = mSpService.getSelectedItemPosition();
        mCountryPos = mSpCountry.getSelectedItemPosition();
        mStatePos = mSpState.getSelectedItemPosition();
        mCityPos = mSpCity.getSelectedItemPosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("debug", "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPLOAD_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                Log.i("debug", "result ok");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("debug", "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void bindPresenter(WriteReviewContract.Presenter presenter) {
        mPresenter = presenter;
        mPresenter.bindView(this);
    }

    @OnClick(R.id.btnSendReview) void onSendClicked() {
        if (!StorageUtils.isUserLogged(getActivity())) {
            showUserNotLoggedError();
            showMenu();
            return;
        }

        NewReview review = new NewReview();
        review.setUser(StorageUtils.getUser(getActivity()).getIdUser());
        review.setActivity(mActivityId);
        review.setCompany(mCompanyId);
        review.setService(mServiceId);
        review.setTitle(mEtTitle.getText().toString());
        review.setDescription(mEtDescription.getText().toString());
        review.setValue(mStarNumber);
        review.setImage(mEncodedImage);
        review.setAcceptedConditions(mCbConditions.isChecked());
        mPresenter.sendReview(review);
    }

    @Override
    public void showProgress() {
        mDialog = ActivityUtils.showProgressDialog(getActivity());
    }

    @Override
    public void hideProgress() {
        if (mDialog != null) mDialog.dismiss();
    }

    @Override
    public void initAdapters() {
        List<String> list = new ArrayList<>();
        list.add("Actividad");
        mAdapterActivity = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, list);
        mAdapterActivity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpActivity.setAdapter(mAdapterActivity);

        list = new ArrayList<>();
        list.add("Empresa");
        mAdapterCompany = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, list);
        mAdapterCompany.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCompany.setAdapter(mAdapterCompany);

        list = new ArrayList<>();
        list.add("Servicio");
        mAdapterService = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, list);
        mAdapterService.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpService.setAdapter(mAdapterService);

        list = new ArrayList<>();
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
        mSpActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mActivityId = Integer.parseInt(mActivities.get(pos - 1).getIdActivity());
                } else {
                    mActivityId = 0;
                }

                mActivityPos = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    Hospital company = mCompanies.get(pos - 1);
                    mCompanyId = company.getIdCompany();

                    ArrayList<Activity> activities = new ArrayList<>();
                    Activity act = new Activity();
                    act.setIdActivity(company.getIdActivity());
                    act.setActivityName(company.getActivityName());
                    activities.add(act);
                    showActivities(activities);
                    mSpActivity.setSelection(0, true);
                } else {
                    mCompanyId = 0;
                }

                mCompanyPos = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mServiceId = mServices.get(pos - 1).getIdCompaniesServices();
                } else {
                    mServiceId = 0;
                }

                mServicePos = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mCountryId = mCountries.get(pos - 1).getCountryid();
                    mCountryPos = pos;
                    mPresenter.getStatesAndCompanies(mCountryId, mStateId, mCityId);
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
                    mPresenter.getCitiesAndCompanies(mCountryId, mStateId, mCityId);
                } else {
                    mStateId = "";
                }

                if (mCountryId != null) {

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
                    mPresenter.getCompanies(mCountryId, mStateId, mCityId);
                } else {
                    mCityId = "";
                }

                if (mCountryId != null) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mEtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTvCharCounter.setText(mEtDescription.getText().toString().length() + "/1000");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        initStarListeners();
    }

    @Override
    public void clearListeners() {
        mSpActivity.setOnItemSelectedListener(null);
        mSpCompany.setOnItemSelectedListener(null);
        mSpService.setOnItemSelectedListener(null);
        mSpCountry.setOnItemSelectedListener(null);
        mSpState.setOnItemSelectedListener(null);
        mSpCity.setOnItemSelectedListener(null);

        for (int i = 0; i < mLyAvg.getChildCount(); i++) {
            mLyAvg.getChildAt(i).setOnClickListener(null);
        }
    }

    @Override
    public void showActivities(ArrayList<Activity> activities) {
        mActivities = activities;
        mAdapterActivity.clear();
        mAdapterActivity.add("Actividad");
        for (Activity a : activities) mAdapterActivity.add(a.getActivityName());
    }

    @Override
    public void showCompanies(ArrayList<Hospital> companies) {
        mCompanies = companies;
        mAdapterCompany.clear();
        mAdapterCompany.add("Empresa");
        for (Hospital h : companies) {
            mAdapterCompany.add(h.getCompanyName());
        }
    }

    @Override
    public void showServices(ArrayList<ServiceResponse.Service> services) {
        mServices = services;
        for (ServiceResponse.Service s : services) mAdapterService.add(s.getServiceName());
    }

    @Override
    public void showCountries(ArrayList<LocationResponse.Country> countries) {
        mCountries = countries;
        for (LocationResponse.Country c : countries) mAdapterCountry.add(c.getCountryname());
    }

    @Override
    public void showStates(ArrayList<LocationResponse.State> states) {
        mStates = states;
        mAdapterState.clear();
        mAdapterState.add("Estado/Provincia");
        for (LocationResponse.State s : states) mAdapterState.add(s.getStatename());
    }

    @Override
    public void showCities(ArrayList<LocationResponse.City> cities) {
        mCities = cities;
        mAdapterCity.clear();
        mAdapterCity.add("Ciudad");
        for (LocationResponse.City c : cities) mAdapterCity.add(c.getCityname());
    }

    @Override
    public void addStars() {
        StarUtils.addStars(getActivity(), 5, mLyAvg);
    }

    @Override
    public void fillStars(int number) {
        mStarNumber = number;
        StarUtils.fillStars(getActivity(), number, mLyAvg);
    }

    @Override
    public void resetStars() {
        for (int i = 0; i < mLyAvg.getChildCount(); i++) {
            ImageView star = (ImageView) mLyAvg.getChildAt(i);
            star.setImageResource(R.drawable.star_grey);
        }
    }

    @Override
    public void initStarListeners() {
        for (int i = 0; i < mLyAvg.getChildCount(); i++) {
            ImageView star = (ImageView) mLyAvg.getChildAt(i);
            final int pos = i;
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetStars();
                    fillStars(pos + 1);
                }
            });
        }
    }

    @Override
    public void launchImageUploadIntent() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        getActivity().startActivityForResult(i, REQUEST_UPLOAD_IMAGE);
    }

    @Override
    public void showImagePreview(Uri uri) {
        mImagePreview.setImageURI(uri);
        mImagePreview.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btnUploadImage) void onImageUploadClicked() {
        if (ActivityUtils.isWriteExternalStoragePermissionGranted(getActivity())) {
            launchImageUploadIntent();
        } else {
            ActivityUtils.requestWriteExternalStoragePermission(getActivity());
        }
    }

    @Override
    public void showActivityError() {
        Toast.makeText(getActivity(), "Debe seleccionar una actividad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCompanyError() {
        Toast.makeText(getActivity(), "Debe seleccionar una empresa", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showServiceError() {
        Toast.makeText(getActivity(), "Debe seleccionar un servicio", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity(), "Debe seleccioanr una ciudad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTitleError() {
        mEtTitle.setError(getString(R.string.campo_obligatorio));
    }

    @Override
    public void showDescriptionError() {
        mEtDescription.setError(getString(R.string.campo_obligatorio));
    }

    @Override
    public void showConditionsError() {
        Toast.makeText(getActivity(), "Debe aceptar las condiciones", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUserNotLoggedError() {
        Toast.makeText(getActivity(), getString(R.string.user_not_logged_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMenu() {
        getActivity().onBackPressed();
    }

    @Override
    public void showSendReviewSuccess() {
        Toast.makeText(getActivity(), getString(R.string.send_review_success), Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNetworkFailedError() {
        Toast.makeText(getActivity(), getString(R.string.network_failed_error), Toast.LENGTH_SHORT).show();
    }
}
