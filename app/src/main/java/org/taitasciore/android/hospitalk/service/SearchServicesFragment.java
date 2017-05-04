package org.taitasciore.android.hospitalk.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.RequestCountryEvent;
import org.taitasciore.android.event.RequestDeterminedLocationEvent;
import org.taitasciore.android.event.RequestLocationEvent;
import org.taitasciore.android.event.SendCountryEvent;
import org.taitasciore.android.event.SendDeterminedLocationEvent;
import org.taitasciore.android.event.SendLocationEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.hospital.SearchHospitalsPresenter;
import org.taitasciore.android.model.City;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.ServiceFilter;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 24/04/17.
 */

public class SearchServicesFragment extends Fragment implements SearchServicesContract.View {

    private double lat;
    private double lon;
    private int mOffset;
    private String mCountryId = "";
    private int mCountryPos;
    private String mCityId = "";
    private int mCityPos;
    private String mActivityId = "";
    private int mActivityPos;
    private String mReviewOrder = "";
    private int mReviewOrderPos;
    private String mReviewRank = "";
    private int mReviewRankPos;
    private String mQuery = "";
    private boolean mChangedCountry;
    private boolean mDeterminedLocation;
    private ArrayList<LocationResponse.Country> mCountries;
    private ArrayList<City> mCities;
    private ArrayList<ServiceFilter> mServicesFilter;
    private ArrayList<ServiceResponse.Service> mServices;

    private SearchServicesContract.Presenter mPresenter;

    private ArrayAdapter<String> mAdapterRatings;
    private ArrayAdapter<String> mAdapterOptions;
    private ArrayAdapter<String> mAdapterCountries;
    private ArrayAdapter<String> mAdapterCities;
    private ArrayAdapter<String> mAdapterActivities;

    private ProgressDialog mDialog;

    @BindView(R.id.list) RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutMngr;
    private SearchServiceAdapter mAdapter;

    @BindView(R.id.tvSearch) AutoCompleteTextView mTvSearch;
    @BindView(R.id.btnSearch) Button mBtnSearch;

    @BindView(R.id.tvResetFilters) TextView mTvResetFilters;

    @BindView(R.id.fab) FloatingActionButton mFab;

    @BindView(R.id.cbCloseServices) CheckBox mCbCloseServices;

    @BindView(R.id.spCountry) SearchableSpinner mSpCountry;
    @BindView(R.id.spCity) SearchableSpinner mSpCity;
    @BindView(R.id.spRating) Spinner mSpRating;
    @BindView(R.id.spActivity) Spinner mSpActivity;
    @BindView(R.id.spOption) Spinner mSpOption;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_services, container, false);
        ButterKnife.bind(this, v);
        initRecyclerView();
        initAdapters();

        mSpCountry.setTitle("Seleccione un país");
        mSpCountry.setPositiveButton("cerrar");
        mSpCity.setTitle("Seleccione una ciudad");
        mSpCity.setPositiveButton("cerrar");

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new SearchServicesPresenter(this);
        else mPresenter.bindView(this);
    }

    @Override
    public void onDetach() {
        hideProgress();
        super.onDetach();
        if (mPresenter != null) mPresenter.unbindView();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mSpOption.setSelection(mReviewOrderPos, false);
        mSpRating.setSelection(mReviewRankPos, false);

        if (mCountries == null) {
            mPresenter.getCountries();
        } else {
            showCountries(mCountries);
            mSpCountry.setSelection(mCountryPos, false);
        }
        if (mCities != null) {
            showCities(mCities);
            mSpCity.setSelection(mCityPos, false);
        }
        if (mServicesFilter != null) {
            showServicesFilter(mServicesFilter);
            mSpActivity.setSelection(mActivityPos, false);
        }

        if (mServices != null) addServices(mServices);

        initListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        clearListeners();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void determinedLocation(SendDeterminedLocationEvent event) {
        mDeterminedLocation = event.determined;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getLocation(SendLocationEvent event) {
        lat = event.lat;
        lon = event.lon;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCountry(SendCountryEvent event) {
        mCountryId = event.country;
    }

    @Override
    public void bindPresenter(SearchServicesContract.Presenter presenter) {
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
    public void showFab() {
        mFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoResultsMessage() {
        Toast.makeText(getActivity(), "No hay servicios que mostrar", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fab) void onFabClicked() {
        search();
    }

    @Override
    public void initRecyclerView() {
        mLayoutMngr = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutMngr);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void initAdapters() {
        List<String> list = new ArrayList<>();
        list.add("País");
        mAdapterCountries = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, list);
        mAdapterCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCountry.setAdapter(mAdapterCountries);

        list = new ArrayList<>();
        list.add("Ciudad");
        mAdapterCities = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, list);
        mAdapterCities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCity.setAdapter(mAdapterCities);

        list = new ArrayList<>();
        list.add("Servicio");
        mAdapterActivities = new ArrayAdapter(
                getActivity(), android.R.layout.simple_spinner_item, list);
        mAdapterActivities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpActivity.setAdapter(mAdapterActivities);

        String[] ratings = {
                getString(R.string.n_opiniones),
                getString(R.string.ascendente),
                getString(R.string.descendente)
        };

        mAdapterRatings = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, ratings);
        mAdapterRatings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpRating.setAdapter(mAdapterRatings);

        String[] option = {
                getString(R.string.valoracion),
                getString(R.string.peor_valorado),
                getString(R.string.mejor_valorado)
        };

        mAdapterOptions = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, option);
        mAdapterOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpOption.setAdapter(mAdapterOptions);

        mAdapter = new SearchServiceAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initListeners() {
        mCbCloseServices.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    EventBus.getDefault().post(new RequestDeterminedLocationEvent());
                    EventBus.getDefault().post(new RequestLocationEvent());
                    EventBus.getDefault().post(new RequestCountryEvent());

                    mAdapter = new SearchServiceAdapter(getActivity());
                    mRecyclerView.setAdapter(mAdapter);

                    if (mDeterminedLocation && !mCountryId.equals("") && mCountries != null) {
                        Log.i("country id", mCountryId);
                        int pos = getCountryPosition(mCountryId);

                        if (pos > -1) {
                            blockFilter();
                            mSpCountry.setSelection(pos + 1, false);
                        }
                    }
                } else {
                    unblockFilter();
                }
            }
        });

        mSpCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mChangedCountry = true;
                    mOffset = 0;
                    mCountryId = mCountries.get(pos - 1).getCountryid();
                    mCountryPos = pos;
                    mCityId = "";
                    mCityPos = 0;
                    mSpCity.setSelection(0, false);
                    Log.i("country id", mCountryId);
                    search();
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
                    mOffset = 0;
                    mCityId = mCities.get(pos - 1).getIdCity();
                    mCityPos = pos;
                    Log.i("city id", mCityId + "");
                    search();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mOffset = 0;
                    mActivityId = mServicesFilter.get(pos - 1).getIdService();
                    mActivityPos = pos;
                    Log.i("service filter id", mActivityId + "");
                    search();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mOffset = 0;
                    mReviewRank = pos+"";
                    mReviewRankPos = pos;
                    search();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mOffset = 0;
                    if (pos == 1) mReviewOrder = "ASC";
                    else mReviewOrder = "DESC";
                    mReviewOrderPos = pos;
                    search();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mTvResetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCountryId = "";
                mCountryPos = 0;
                mCityId = "";
                mCityPos = 0;
                mReviewRank = "";
                mReviewRankPos = 0;
                mReviewOrder = "";
                mReviewOrderPos = 0;
                mActivityId = "";
                mActivityPos = 0;
                mQuery = "";

                mSpCountry.setSelection(0, false);
                mSpCity.setSelection(0, false);
                mSpRating.setSelection(0, false);
                mSpActivity.setSelection(0, false);
                mSpOption.setSelection(0, false);
                mTvSearch.setText("");
            }
        });
    }

    @Override
    public void clearListeners() {
        mCbCloseServices.setOnCheckedChangeListener(null);
        mSpCountry.setOnItemSelectedListener(null);
        mSpCity.setOnItemSelectedListener(null);
        mSpOption.setOnItemSelectedListener(null);
        mSpActivity.setOnItemSelectedListener(null);
        mSpRating.setOnItemSelectedListener(null);
        mTvResetFilters.setOnClickListener(null);
    }

    @Override
    public void incrementOffset() {
        mOffset += SearchHospitalsPresenter.LIMIT;
    }

    @Override
    public void showCountries(ArrayList<LocationResponse.Country> countries) {
        mCountries = countries;
        for (LocationResponse.Country c : countries) mAdapterCountries.add(c.getCountryname());
    }

    @Override
    public void showCities(ArrayList<City> cities) {
        mCities = cities;
        mAdapterCities.clear();
        mAdapterCities.add("Ciudad");
        for (City c : cities) mAdapterCities.add(c.getCityName());

        if (mChangedCountry) {

        }
    }

    @Override
    public void showServicesFilter(ArrayList<ServiceFilter> servicesFilter) {
        mServicesFilter = servicesFilter;
        mAdapterActivities.clear();
        mAdapterActivities.add("Servicio");
        for (ServiceFilter sf : servicesFilter) mAdapterActivities.add(sf.getServiceName());

        if (mChangedCountry) {

        }
    }

    @Override
    public void showServices(ArrayList<ServiceResponse.Service> services) {
        mChangedCountry = false;
        mServices = services;
        mAdapter = new SearchServiceAdapter(getActivity(), services);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void addServices(ArrayList<ServiceResponse.Service> services) {
        mChangedCountry = false;
        try {
            for (ServiceResponse.Service s : services) mAdapter.add(s);
            mServices = mAdapter.getList();
        } catch (ConcurrentModificationException e){}
    }

    @Override
    public void blockFilter() {
        mSpCountry.setEnabled(false);
    }

    @Override
    public void unblockFilter() {
        mSpCountry.setEnabled(true);
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNetworkFailedError() {
        Toast.makeText(getActivity(), getString(R.string.network_failed_error), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btnSearch) void onSearchClicked() {
        String query = mTvSearch.getText().toString();
        if (!query.isEmpty()) {
            mQuery = query;
            search();
        }
    }

    private void search() {
        mPresenter.searchServices(
                mCountryId, mCityId, mActivityId, mReviewOrder, mReviewRank, mOffset, mQuery);
    }

    private int getCountryPosition(String countryId) {
        int i = 0;

        for (LocationResponse.Country c : mCountries) {
            if (c.getCountryid().equalsIgnoreCase(countryId)) return i;
            i++;
        }

        return -1;
    }
}
