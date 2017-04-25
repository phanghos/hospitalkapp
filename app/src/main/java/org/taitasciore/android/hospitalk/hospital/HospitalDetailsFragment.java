package org.taitasciore.android.hospitalk.hospital;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.taitasciore.android.event.AttachPresenterEvent;
import org.taitasciore.android.event.SavePresenterEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.review.SmallReviewAdapter;
import org.taitasciore.android.hospitalk.service.ServiceAdapter;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 19/04/17.
 */

public class HospitalDetailsFragment extends Fragment implements HospitalDetailsContract.View {

    private int mOffsetServices;
    private int mOffsetReviews;
    private int mOffsetPhotos;
    private Hospital mHospital;
    private ArrayList<ServiceResponse.Service> mServices;
    private ArrayList<Review> mReviews;

    private HospitalDetailsContract.Presenter mPresenter;

    private SmallReviewAdapter mReviewAdapter;
    private ServiceAdapter mServiceAdapter;

    private ProgressDialog mDialog;

    @BindView(R.id.listServices) RecyclerView mRecyclerViewServices;
    @BindView(R.id.listReviews) RecyclerView mRecyclerViewReviews;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hospital_details, container, false);
        ButterKnife.bind(this, v);
        initViews();
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("hospital")) {
                mHospital = (Hospital) savedInstanceState.getSerializable("hospital");
            }
            if (savedInstanceState.containsKey("services")) {
                mServices = (ArrayList<ServiceResponse.Service>)
                        savedInstanceState.getSerializable("services");
            }
            if (savedInstanceState.containsKey("reviews")) {
                mReviews = (ArrayList<Review>)
                        savedInstanceState.getSerializable("reviews");
            }
            mOffsetServices = savedInstanceState.getInt("offset_services");
            mOffsetReviews = savedInstanceState.getInt("offset_reviews");
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mHospital != null) outState.putSerializable("hospital", mHospital);
        if (mServices != null) outState.putSerializable("services", mServices);
        if (mReviews != null) outState.putSerializable("reviews", mReviews);
        outState.putInt("offset_services", mOffsetServices);
        outState.putInt("offset_reviews", mOffsetReviews);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new AttachPresenterEvent(this));

        if (mHospital == null) {
            mPresenter.getHospitalDetails(getArguments().getInt("id"));
        } else {
            showHospitalDetails(mHospital);
            if (mServices != null) setServices(mServices);
            if (mReviews != null) setReviews(mReviews);
        }
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
    public void showHospitalDetails(Hospital hospital) {
        mHospital = hospital;
        mTvName.setText(hospital.getComapnyName());
        mTvCity.setText(hospital.getCompanyCity());
        mTvActivityName.setText(hospital.getActivityName());

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

    private void initViews() {
        initArrows();
        initRecyclerViews();
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
    }
}
