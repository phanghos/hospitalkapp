package org.taitasciore.android.hospitalk.service;

import android.app.ProgressDialog;
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
import android.widget.Spinner;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.taitasciore.android.event.AttachPresenterEvent;
import org.taitasciore.android.event.SavePresenterEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.hospital.SearchHospitalAdapter;
import org.taitasciore.android.hospitalk.hospital.SearchHospitalsPresenter;
import org.taitasciore.android.model.City;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.ServiceFilter;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 24/04/17.
 */

public class SearchServicesFragment extends Fragment implements SearchServicesContract.View {

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
    private boolean mChangedCountry;
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

    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.spCountry) Spinner mSpCountry;
    @BindView(R.id.spCity) Spinner mSpCity;
    @BindView(R.id.spRating) Spinner mSpRating;
    @BindView(R.id.spActivity) Spinner mSpActivity;
    @BindView(R.id.spOption) Spinner mSpOption;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_services, container, false);
        ButterKnife.bind(this, v);
        initRecyclerView();
        initAdapters();
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("countries")) {
                mCountries = (ArrayList<LocationResponse.Country>)
                        savedInstanceState.getSerializable("countries");
            }
            if (savedInstanceState.containsKey("cities")) {
                mCities = (ArrayList<City>)
                        savedInstanceState.getSerializable("cities");
            }
            if (savedInstanceState.containsKey("activities")) {
                mServicesFilter = (ArrayList<ServiceFilter>)
                        savedInstanceState.getSerializable("activities");
            }
            if (savedInstanceState.containsKey("hospitals")) {
                mServices = (ArrayList<ServiceResponse.Service>)
                        savedInstanceState.getSerializable("hospitals");
            }

            mOffset = savedInstanceState.getInt("offset");
            mCountryId = savedInstanceState.getString("id_country", "");
            mCountryPos = savedInstanceState.getInt("pos_country");
            mCityId = savedInstanceState.getString("id_city", "");
            mCityPos = savedInstanceState.getInt("pos_city");
            mReviewOrder = savedInstanceState.getString("review_order", "");
            mReviewOrderPos = savedInstanceState.getInt("pos_review_order");
            mActivityId = savedInstanceState.getString("id_activity", "");
            mActivityPos = savedInstanceState.getInt("pos_activity");
            mReviewRank = savedInstanceState.getString("review_rank", "");
            mReviewRankPos = savedInstanceState.getInt("pos_review_rank");
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCountries != null) outState.putSerializable("countries", mCountries);
        if (mCities != null) outState.putSerializable("cities", mCities);
        if (mServicesFilter != null) outState.putSerializable("activities", mServicesFilter);
        if (mServices != null) outState.putSerializable("hospitals", mServices);
        outState.putInt("offset", mOffset);
        outState.putString("id_country", mCountryId);
        outState.putInt("pos_country", mCountryPos);
        outState.putString("id_city", mCityId);
        outState.putInt("pos_city", mCityPos);
        outState.putString("review_order", mReviewOrder);
        outState.putInt("pos_review_order", mReviewOrderPos);
        outState.putString("id_activity", mActivityId);
        outState.putInt("pos_activity", mActivityPos);
        outState.putString("review_rank", mReviewRank);
        outState.putInt("pos_review_rank", mReviewRankPos);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new AttachPresenterEvent(this));

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
        clearListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.unbindView();
            EventBus.getDefault().post(new SavePresenterEvent(mPresenter));
        }
        hideProgress();
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
        mSpCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    mChangedCountry = true;
                    mOffset = 0;
                    mCountryId = mCountries.get(pos - 1).getCountryid();
                    mCountryPos = pos;
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
    }

    @Override
    public void clearListeners() {
        mSpCountry.setOnItemSelectedListener(null);
        mSpCity.setOnItemSelectedListener(null);
        mSpOption.setOnItemSelectedListener(null);
        mSpActivity.setOnItemSelectedListener(null);
        mSpRating.setOnItemSelectedListener(null);
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
        if (mChangedCountry) {
            mCities = cities;
            mAdapterCities.clear();
            mAdapterCities.add("Ciudad");
            for (City c : cities) mAdapterCities.add(c.getCityName());
        }
    }

    @Override
    public void showServicesFilter(ArrayList<ServiceFilter> servicesFilter) {
        if (mChangedCountry) {
            mServicesFilter = servicesFilter;
            mAdapterActivities.clear();
            mAdapterActivities.add("Servicio");
            for (ServiceFilter sf : servicesFilter) mAdapterActivities.add(sf.getServiceName());
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
        for (ServiceResponse.Service s : services) mAdapter.add(s);
        mServices = mAdapter.getList();
    }

    private void search() {
        mPresenter.searchServices(
                mCountryId, mCityId, mActivityId, mReviewOrder, mReviewRank, mOffset);
    }
}
