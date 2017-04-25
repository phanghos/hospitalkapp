package org.taitasciore.android.hospitalk.review;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.event.AttachPresenterEvent;
import org.taitasciore.android.event.RequestLocationEvent;
import org.taitasciore.android.event.SendLocationEvent;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.event.SavePresenterEvent;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 18/04/17.
 */

public class ReviewsFragment extends Fragment implements ReviewsContract.View {

    private double lat;
    private double lon;
    private int mOffset;
    private ArrayList<Review> mReviews;

    private ReviewsContract.Presenter mPresenter;

    private ProgressDialog mDialog;

    @BindView(R.id.list) RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutMngr;
    private ReviewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reviews, container, false);
        ButterKnife.bind(this, v);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("reviews")) {
                mReviews = (ArrayList<Review>) savedInstanceState.getSerializable("reviews");
            }
            mOffset = savedInstanceState.getInt("offset");
        }
        return v;
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
    public void getLocation(SendLocationEvent event) {
        lat = event.lat;
        lon = event.lon;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mReviews != null) outState.putSerializable("reviews", mReviews);
        outState.putInt("offset", mOffset);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new AttachPresenterEvent(this));
        EventBus.getDefault().post(new RequestLocationEvent());
        initRecyclerView();

        if (mReviews == null) {
            mPresenter.getReviews(mOffset, lat, lon);
        } else {
            setReviews(mReviews);
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
    public void bindPresenter(ReviewsContract.Presenter presenter) {
        mPresenter = presenter;
        mPresenter.bindView(this);
    }

    private void initRecyclerView() {
        mLayoutMngr = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutMngr);
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
    public void setReviews(ArrayList<Review> reviews) {
        mReviews = reviews;
        mAdapter = new ReviewAdapter(getActivity(), reviews);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void addReviews(ArrayList<Review> reviews) {

    }

    @Override
    public void incrementOffset() {
        mOffset += ReviewsPresenter.LIMIT;
    }
}
