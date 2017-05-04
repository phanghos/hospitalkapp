package org.taitasciore.android.hospitalk.profile;

import android.content.Context;
import android.net.Uri;
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
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.LoadMoreReviewsEvent;
import org.taitasciore.android.event.SetUserEvent;
import org.taitasciore.android.hospitalk.ProgressDialogFragment;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StorageUtils;
import org.taitasciore.android.hospitalk.review.SmallReviewAdapter;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.User;
import org.taitasciore.android.network.HospitalkApi;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 30/04/17.
 */

public class ProfileFragment extends Fragment implements ProfileContract.View {

    private int mUserId;
    private User mUser;
    private String mCountryName;
    private int mOffset;
    private ArrayList<Review> mReviews;

    private ProfileContract.Presenter mPresenter;

    private SmallReviewAdapter mAdapterReviews;

    private ProgressDialogFragment mDialog;

    @BindView(R.id.listReviews) RecyclerView mRecyclerViewReviews;
    @BindView(R.id.listVotes) RecyclerView mRecyclerViewVotes;

    @BindView(R.id.mainContent) LinearLayout mMainContent;

    @BindView(R.id.arrowOpiniones) ImageView mArrowReviews;

    @BindView(R.id.tvName) TextView mTvName;
    @BindView(R.id.tvLocation) TextView mTvLocation;
    @BindView(R.id.tvPhone) TextView mTvPhone;
    @BindView(R.id.tvEmail) TextView mTvEmail;
    @BindView(R.id.img) SimpleDraweeView mProfileImg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        initRecyclerView();
        initAdapter();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new ProfilePresenter(this);
        else mPresenter.bindView(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadMoreReviews(LoadMoreReviewsEvent event) {
        mPresenter.getUserReviews(mUserId, mOffset);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setUser(SetUserEvent event) {
        mUser = event.user;
        showUserInfo(mUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        User user = StorageUtils.getUser(getActivity());
        if (user == null) {
            showUserNotLoggedError();
            return;
        }

        mUserId = user.getIdUser();

        if (mUser == null) {
            mPresenter.getUserInfo(mUserId);
        } else {
            showUserInfo(mUser);
            if (mCountryName != null) showUserCountry(mCountryName);
            showMainContent();
        }

        if (mReviews != null) setReviews(mReviews);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPresenter != null) mPresenter.unbindView();
    }

    @Override
    public void bindPresenter(ProfileContract.Presenter presenter) {
        mPresenter = presenter;
        mPresenter.bindView(this);
    }

    @Override
    public void showProgress() {
        mDialog = new ProgressDialogFragment();
        mDialog.show(getActivity().getSupportFragmentManager(), "progress");
    }

    @Override
    public void hideProgress() {
        if (mDialog != null) mDialog.dismiss();
    }

    @Override
    public void initRecyclerView() {
        mRecyclerViewReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewReviews.setNestedScrollingEnabled(false);
    }

    @Override
    public void initAdapter() {
        mAdapterReviews = new SmallReviewAdapter(getActivity());
        mRecyclerViewReviews.setAdapter(mAdapterReviews);
    }

    @Override
    public void incrementReviewsOffset() {
        mOffset += ProfilePresenter.LIMIT;
    }

    @Override
    public void showMainContent() {
        mMainContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUserInfo(User user) {
        mUser = user;
        if(mCountryName == null) mPresenter.getCountries(mUser.getIdCountry());
        mTvName.setText(user.getName() + " " + user.getFirstLastName());

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            mTvPhone.setText("Tlf: " + user.getPhone());
        }

        mTvEmail.setText("Correo electrónico: " + user.getEmail());

        String imgUrl = HospitalkApi.BASE_URL + "user/image?id_user=" + mUserId;
        Log.i("image url", imgUrl);
        mProfileImg.setImageURI(Uri.parse(imgUrl));
    }

    @Override
    public void showUserCountry(String country) {
        mCountryName = country;
        mTvLocation.setText("País: " + country);
    }

    @Override
    public void setReviews(ArrayList<Review> reviews) {
        for (Review r : reviews) mAdapterReviews.add(r);
        mReviews = mAdapterReviews.getList();
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

    @OnClick(R.id.btnEditProfile) void onEditProfileClicked() {
        if (mUser == null) {
            showUserNotAvailableError();
            return;
        }

        EditProfileFragment f = EditProfileFragment.newInstance(mUser);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null).commit();
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
        Toast.makeText(getActivity(), "No hay opiniones que mostrar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUserNotLoggedError() {
        Toast.makeText(getActivity(), getString(R.string.user_not_logged_error), Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    @Override
    public void showUserNotAvailableError() {
        Toast.makeText(getActivity(), getString(R.string.user_not_available), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.arrowOpiniones) void onArrowReviewsClicked() {
        if (mRecyclerViewReviews.getVisibility() == View.GONE) {
            if (mReviews == null) mPresenter.getUserReviews(mUserId, mOffset);
            else showReviews();
        } else {
            hideReviews();
        }
    }
}
