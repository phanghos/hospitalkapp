package org.taitasciore.android.hospitalk.review;

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
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.LoadMoreReviewsEvent;
import org.taitasciore.android.event.RequestLocationEvent;
import org.taitasciore.android.event.SendLocationEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.model.Review;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reviews, container, false);
        ButterKnife.bind(this, v);
        initRecyclerView();
        initAdapter();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadMoreReviews(LoadMoreReviewsEvent event) {
        mPresenter.getReviews(mOffset, lat, lon);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new ReviewsPresenter(this);
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
        EventBus.getDefault().post(new RequestLocationEvent());

        if (mReviews == null) {
            mPresenter.getReviews(mOffset, lat, lon);
        } else {
            showReviews(mReviews);
        }
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
    public void initAdapter() {
        mAdapter = new ReviewAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showReviews(ArrayList<Review> reviews) {
        try {
            for (Review r : reviews) mAdapter.add(r);
            mReviews = mAdapter.getList();
        } catch (ConcurrentModificationException e){}
    }

    @Override
    public void incrementOffset() {
        mOffset += ReviewsPresenter.LIMIT;
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
    public void showNoMoreReviewsError() {
        Toast.makeText(getActivity(), "No hay más opiniones que mostrar", Toast.LENGTH_SHORT).show();
    }
}
