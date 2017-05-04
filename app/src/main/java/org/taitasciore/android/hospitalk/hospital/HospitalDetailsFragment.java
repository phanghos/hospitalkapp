package org.taitasciore.android.hospitalk.hospital;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.LoadMoreReviewsEvent;
import org.taitasciore.android.event.LoadMoreServicesEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.MapFragment;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StorageUtils;
import org.taitasciore.android.hospitalk.review.SmallReviewAdapter;
import org.taitasciore.android.hospitalk.review.WriteReviewFragment;
import org.taitasciore.android.hospitalk.service.ServiceAdapter;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 19/04/17.
 */

public class HospitalDetailsFragment extends Fragment implements HospitalDetailsContract.View {

    private int mOffsetServices;
    private int mOffsetReviews;
    private int mOffsetPhotos;
    private double lat;
    private double lon;
    private Hospital mHospital;
    private ArrayList<ServiceResponse.Service> mServices;
    private ArrayList<Review> mReviews;

    private HospitalDetailsContract.Presenter mPresenter;

    private SmallReviewAdapter mReviewAdapter;
    private ServiceAdapter mServiceAdapter;

    private ProgressDialog mDialog;

    @BindView(R.id.listServices) RecyclerView mRecyclerViewServices;
    @BindView(R.id.listReviews) RecyclerView mRecyclerViewReviews;

    @BindView(R.id.mainContent) LinearLayout mMainContent;
    @BindView(R.id.lyMap) LinearLayout mLyMap;

    @BindView(R.id.tvSearch) AutoCompleteTextView mTvSearch;
    @BindView(R.id.btnSearch) Button mBtnSearch;

    @BindView(R.id.btnAddService) Button mBtnAddService;

    @BindView(R.id.tvCompanyName) TextView mTvName;
    @BindView(R.id.tvCompanyCity) TextView mTvCity;
    @BindView(R.id.tvActivityName) TextView mTvActivityName;
    @BindView(R.id.btnVerMapa) ToggleButton mBtnVerMapa;

    @BindView(R.id.tvServicios) TextView mTvServices;
    @BindView(R.id.tvOpiniones) TextView mTvReviews;
    @BindView(R.id.tvFotos) TextView mTvPhotos;

    @BindView(R.id.arrowServicios) ImageView mArrowServices;
    @BindView(R.id.arrowOpiniones) ImageView mArrowReviews;
    @BindView(R.id.arrowFotos) ImageView mArrowPhotos;

    @BindView(R.id.progressExcelente) ProgressBar mPbExc;
    @BindView(R.id.progressMuyBueno) ProgressBar mPbMuyBueno;
    @BindView(R.id.progressNormal) ProgressBar mPbNormal;
    @BindView(R.id.progressMalo) ProgressBar mPbMalo;
    @BindView(R.id.progressMuyMalo) ProgressBar mPbMuyMalo;

    public static HospitalDetailsFragment newInstance(int id) {
        HospitalDetailsFragment f = new HospitalDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hospital_details, container, false);
        ButterKnife.bind(this, v);
        initViews();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new HospitalDetailsPresenter(this);
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
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadMoreServices(LoadMoreServicesEvent event) {
        mPresenter.getHospitalServices(getArguments().getInt("id"), mOffsetServices);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadMoreReviews(LoadMoreReviewsEvent event) {
        mPresenter.getHospitalServices(getArguments().getInt("id"), mOffsetReviews);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mHospital == null) {
            mPresenter.getHospitalDetails(getArguments().getInt("id"));
        } else {
            showHospitalDetails(mHospital);
            showMainContent();
            if (mServices != null) setServices(mServices);
            if (mReviews != null) setReviews(mReviews);
        }
    }

    @Override
    public void bindPresenter(HospitalDetailsContract.Presenter presenter) {
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
    public void incrementServicesOffset() {
        mOffsetServices += HospitalDetailsPresenter.LIMIT;
    }

    @Override
    public void incrementReviewsOffset() {
        mOffsetReviews += HospitalDetailsPresenter.LIMIT;
    }

    @Override
    public void showMainContent() {
        mMainContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void showHospitalDetails(Hospital hospital) {
        mHospital = hospital;
        lat = hospital.getLatitide();
        lon = hospital.getLongitude();
        mTvName.setText(hospital.getComapnyName()+"");
        mTvCity.setText(hospital.getCompanyCity()+"");
        mTvActivityName.setText(hospital.getActivityName()+"");

        if (hospital.getRatingDetail().size() > 0) {
            Hospital.RatingDetail ratingDetail = hospital.getRatingDetail().get(0);

            switch (ratingDetail.getValue()) {
                case 1:
                    mPbExc.setProgress(ratingDetail.getTotal() * 10);
                    break;
                case 2:
                    mPbMuyBueno.setProgress(ratingDetail.getTotal() * 10);
                    break;
                case 3:
                    mPbNormal.setProgress(ratingDetail.getTotal() * 10);
                    break;
                case 4:
                    mPbMalo.setProgress(ratingDetail.getTotal() * 10);
                    break;
                case 5:
                    mPbMuyMalo.setProgress(ratingDetail.getTotal() * 10);
            }
        }
    }

    @Override
    public void setServices(ArrayList<ServiceResponse.Service> list) {
        mServices = list;
        mServiceAdapter = new ServiceAdapter(getActivity(), list);
        mRecyclerViewServices.setAdapter(mServiceAdapter);
    }

    @Override
    public void showServices() {
        mArrowServices.setImageResource(R.drawable.up_arrow_4);
        mRecyclerViewServices.setAlpha(0f);
        mRecyclerViewServices.setVisibility(View.VISIBLE);
        mRecyclerViewServices.animate().alpha(1f);
    }

    @Override
    public void hideServices() {
        mArrowServices.setImageResource(R.drawable.down_arrow_4);
        mRecyclerViewServices.setAlpha(1f);
        mRecyclerViewServices.setVisibility(View.GONE);
        mRecyclerViewServices.animate().alpha(0f);
    }

    @Override
    public void setReviews(ArrayList<Review> list) {
        mReviews = list;
        mReviewAdapter = new SmallReviewAdapter(getActivity(), list);
        mRecyclerViewReviews.setAdapter(mReviewAdapter);
    }

    @Override
    public void showReviews() {
        mArrowReviews.setImageResource(R.drawable.up_arrow_4);
        mRecyclerViewReviews.setAlpha(0f);
        mRecyclerViewReviews.setVisibility(View.VISIBLE);
        mRecyclerViewReviews.animate().alpha(1f);
    }

    @Override
    public void hideReviews() {
        mArrowReviews.setImageResource(R.drawable.down_arrow_4);
        mRecyclerViewReviews.setAlpha(1f);
        mRecyclerViewReviews.setVisibility(View.GONE);
        mRecyclerViewReviews.animate().alpha(0f);
    }

    @Override
    public void launchNewServiceFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new WriteReviewFragment())
                .addToBackStack(null).commit();
    }

    @OnClick(R.id.btnAddService) void onNewServiceClicked() {
        if (StorageUtils.isUserLogged(getActivity())) {
            launchNewServiceFragment();
        } else {
            showUserNotLoggedError();
        }
    }

    @OnClick(R.id.btnSearch) void onSearchClicked() {
        String query = mTvSearch.getText().toString();
        if (!query.isEmpty()) {
            SearchHospitalsFragment f = SearchHospitalsFragment.newInstance(query);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, f).addToBackStack(null).commit();
        }
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    @Override
    public void showNetworkFailedError() {
        Toast.makeText(getActivity(), getString(R.string.network_failed_error), Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    @Override
    public void showPlayServicesError() {
        Toast.makeText(getActivity(), getString(R.string.play_services_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLocationNotAvailableError() {
        Toast.makeText(getContext(), getString(R.string.location_not_available_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoServicesError() {
        Toast.makeText(getActivity(), "No hay servicios que mostrar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoReviewsError() {
        Toast.makeText(getActivity(), "No hay opiniones que mostrar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUserNotLoggedError() {
        Toast.makeText(getActivity(), getString(R.string.user_not_logged_error), Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        initArrows();
        initRecyclerViews();
        initMapListener();
    }

    private void initArrows() {
        mArrowServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mServices == null) {
                    mPresenter.getHospitalServices(getArguments().getInt("id"), mOffsetServices);
                } else if (mRecyclerViewServices.getVisibility() == View.GONE) {
                    showServices();
                } else {
                    hideServices();
                }
            }
        });

        mArrowReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReviews == null) {
                    mPresenter.getHospitalReviews(getArguments().getInt("id"), mOffsetReviews);
                } else if (mRecyclerViewReviews.getVisibility() == View.GONE) {
                    showReviews();
                } else {
                    hideReviews();
                }
            }
        });

        mArrowPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initRecyclerViews() {
        mRecyclerViewServices.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewServices.setNestedScrollingEnabled(false);
        mRecyclerViewReviews.setNestedScrollingEnabled(false);
        Drawable divider = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        mRecyclerViewServices.addItemDecoration(new DividerItemDecoration(divider));
        mRecyclerViewReviews.addItemDecoration(new DividerItemDecoration(divider));
    }

    private void initMapListener() {
        mBtnVerMapa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    if (ActivityUtils.isGoogleApiAvailable(getActivity())) {
                        if (lat != 0 && lon != 0) {
                            MapFragment f = MapFragment.newInstance(lat, lon);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mapContainer, f).commit();
                            mLyMap.setVisibility(View.VISIBLE);
                        } else {
                            showLocationNotAvailableError();
                        }
                    } else {
                        showPlayServicesError();
                        mLyMap.setVisibility(View.GONE);
                    }
                }
                 else {
                    mLyMap.setVisibility(View.GONE);
                }
            }
        });
    }
}
