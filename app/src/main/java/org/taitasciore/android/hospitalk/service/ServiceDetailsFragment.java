package org.taitasciore.android.hospitalk.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.MapFragment;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StarUtils;
import org.taitasciore.android.hospitalk.StorageUtils;
import org.taitasciore.android.hospitalk.review.SmallReviewAdapter;
import org.taitasciore.android.hospitalk.review.WriteReviewFragment;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.Service;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 25/04/17.
 */

public class ServiceDetailsFragment extends Fragment implements ServiceDetailsContract.View {

    private int mOffset;
    private double lat;
    private double lon;
    private Service mService;
    private ArrayList<Review> mReviews;

    private ServiceDetailsContract.Presenter mPresenter;

    private ProgressDialog mDialog;

    @BindView(R.id.list) RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutMngr;
    private SmallReviewAdapter mAdapter;

    @BindView(R.id.mainContent) LinearLayout mMainContent;
    @BindView(R.id.lyMap) LinearLayout mLyMap;

    @BindView(R.id.tvServiceName) TextView mTvServiceName;
    @BindView(R.id.tvCompanyName) TextView mTvCompanyName;
    @BindView(R.id.tvLocation) TextView mTvLocation;
    @BindView(R.id.lyAvg) LinearLayout mLyAvg;
    @BindView(R.id.arrowOpiniones) ImageView mArrowReviews;
    @BindView(R.id.btnVerMapa) ToggleButton mBtnVerMapa;
    @BindView(R.id.btnWriteReview) Button mBtnWriteReview;

    public static ServiceDetailsFragment newInstance(int id) {
        ServiceDetailsFragment f = new ServiceDetailsFragment();
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
        View v = inflater.inflate(R.layout.fragment_service_details, container, false);
        ButterKnife.bind(this, v);
        initRecyclerView();
        initMapListener();
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("service")) {
                mService = (Service) savedInstanceState.getSerializable("service");
            }
            if (savedInstanceState.containsKey("reviews")) {
                mReviews = (ArrayList<Review>)
                        savedInstanceState.getSerializable("reviews");
            }

            mOffset = savedInstanceState.getInt("offset");
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new ServiceDetailsPresenter(this);
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

        if (mService == null) {
            mPresenter.getServiceDetails(getArguments().getInt("id"));
        } else {
            showServiceDetails(mService);
            if (mReviews != null) setReviews(mReviews);
            showMainContent();
        }
    }

    @Override
    public void bindPresenter(ServiceDetailsContract.Presenter presenter) {
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
    public void incrementOffset() {
        mOffset += ServiceDetailsPresenter.LIMIT;
    }

    @Override
    public void initRecyclerView() {
        mLayoutMngr = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutMngr);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new SmallReviewAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showMainContent() {
        mMainContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void showServiceDetails(Service service) {
        mService = service;
        lat = service.getCompanyLatitude();
        lon = service.getCompanyLongitude();
        mTvServiceName.setText(service.getServiceName());
        mTvCompanyName.setText(service.getCompanyName());
        mTvLocation.setText(service.getCompanyCity() + ", " + service.getCompanyCountry());
        StarUtils.resetStars(mLyAvg);
        StarUtils.addStars(getActivity(), 10, mLyAvg);
        StarUtils.fillStars(getActivity(), service.getServiceAverage(), mLyAvg);
    }

    @Override
    public void setReviews(ArrayList<Review> reviews) {
        mReviews = reviews;
        for (Review r : reviews) mAdapter.add(r);
    }

    @Override
    public void showReviews() {
        mArrowReviews.setImageResource(R.drawable.up_arrow_4);
        mRecyclerView.setAlpha(0f);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.animate().alpha(1f);
    }

    @Override
    public void hideReviews() {
        mArrowReviews.setImageResource(R.drawable.down_arrow_4);
        mRecyclerView.setAlpha(1f);
        mRecyclerView.setVisibility(View.GONE);
        mRecyclerView.animate().alpha(0f);
    }

    @Override
    public void launchWriteReviewFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new WriteReviewFragment())
                .addToBackStack(null).commit();
    }

    @OnClick(R.id.btnWriteReview) void onWriteReviewClicked() {
        if (StorageUtils.isUserLogged(getActivity())) {
            launchWriteReviewFragment();
        } else {
            showUserNotLoggedError();
        }
    }

    @OnClick(R.id.arrowOpiniones) void onReviewsClicked() {
        if (mRecyclerView.getVisibility() == View.GONE) showReviews();
        else hideReviews();
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
        Toast.makeText(getActivity(), getString(R.string.location_not_available_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoReviewsError() {
        Toast.makeText(getActivity(), "No hay opiniones que mostrar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUserNotLoggedError() {
        Toast.makeText(getActivity(), getString(R.string.user_not_logged_error), Toast.LENGTH_SHORT).show();
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
