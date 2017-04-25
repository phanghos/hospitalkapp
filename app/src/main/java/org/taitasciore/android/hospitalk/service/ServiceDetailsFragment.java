package org.taitasciore.android.hospitalk.service;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.taitasciore.android.event.AttachPresenterEvent;
import org.taitasciore.android.event.SavePresenterEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StarUtils;
import org.taitasciore.android.hospitalk.review.ReviewAdapter;
import org.taitasciore.android.hospitalk.review.SmallReviewAdapter;
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
    private Service mService;
    private ArrayList<Review> mReviews;

    private ServiceDetailsContract.Presenter mPresenter;

    private ProgressDialog mDialog;

    @BindView(R.id.list) RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutMngr;
    private SmallReviewAdapter mAdapter;

    @BindView(R.id.tvServiceName) TextView mTvServiceName;
    @BindView(R.id.tvCompanyName) TextView mTvCompanyName;
    @BindView(R.id.tvLocation) TextView mTvLocation;
    @BindView(R.id.lyAvg) LinearLayout mLyAvg;
    @BindView(R.id.arrowOpiniones) ImageView mArrowReviews;

    public static ServiceDetailsFragment newInstance(int id) {
        ServiceDetailsFragment f = new ServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_details, container, false);
        ButterKnife.bind(this, v);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mService != null) outState.putSerializable("service", mService);
        if (mReviews != null) outState.putSerializable("reviews", mReviews);
        outState.putInt("offset", mOffset);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new AttachPresenterEvent(this));

        if (mService == null) {
            mPresenter.getServiceDetails(getArguments().getInt("id"));
        } else {
            showServiceDetails(mService);
            if (mReviews != null) {
                setReviews(mReviews);
            }
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
        mAdapter = new SmallReviewAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showServiceDetails(Service service) {
        mService = service;
        mTvServiceName.setText(service.getServiceName());
        mTvCompanyName.setText(service.getCompanyName());
        mTvLocation.setText(service.getCompanyCity() + ", " + service.getCompanyCountry());
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

    @OnClick(R.id.arrowOpiniones) void onReviewsClicked() {
        if (mRecyclerView.getVisibility() == View.GONE) showReviews();
        else hideReviews();
    }
}
