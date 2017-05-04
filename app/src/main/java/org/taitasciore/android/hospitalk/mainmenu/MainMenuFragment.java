package org.taitasciore.android.hospitalk.mainmenu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.RequestDeterminedLocationEvent;
import org.taitasciore.android.event.RequestLocationEvent;
import org.taitasciore.android.event.SendDeterminedLocationEvent;
import org.taitasciore.android.event.SendLocationEvent;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StorageUtils;
import org.taitasciore.android.hospitalk.close.CloseFragment;
import org.taitasciore.android.hospitalk.hospital.SearchHospitalsFragment;
import org.taitasciore.android.hospitalk.review.WriteReviewFragment;
import org.taitasciore.android.hospitalk.review.ReviewsFragment;
import org.taitasciore.android.hospitalk.service.SearchServicesFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 18/04/17.
 */

public class MainMenuFragment extends Fragment implements MainMenuContract.View {

    private double lat;
    private double lon;
    private boolean mDeterminedLocation;

    private MainMenuContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new MainMenuPresenter(this);
        else mPresenter.bindView(this);
    }

    @Override
    public void onDetach() {
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
    public void determinedLocation(SendDeterminedLocationEvent event) {
        mDeterminedLocation = event.determined;
    }

    @Subscribe
    public void getLocation(SendLocationEvent event) {
        lat = event.lat;
        lon = event.lon;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new RequestDeterminedLocationEvent());
        EventBus.getDefault().post(new RequestLocationEvent());
    }

    @Override
    public void bindPresenter(MainMenuContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void launchReviewsFragment() {
        EventBus.getDefault().post(new RequestDeterminedLocationEvent());

        if (mDeterminedLocation) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ReviewsFragment())
                    .addToBackStack(null).commit();
        } else {
            showLocationNotDeterminedError();
        }
    }

    @Override
    public void launchSearchHospitalsFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SearchHospitalsFragment())
                .addToBackStack(null).commit();
    }

    @Override
    public void launchWriteOpinionFragment() {
        if (StorageUtils.isUserLogged(getActivity())) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new WriteReviewFragment())
                    .addToBackStack(null).commit();
        } else {
            showUserNotLoggedError();
        }
    }

    @Override
    public void launchSearchServicesFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SearchServicesFragment())
                .addToBackStack(null).commit();
    }

    @Override
    public void launchCloseFragment() {
        EventBus.getDefault().post(new RequestDeterminedLocationEvent());

        if (mDeterminedLocation) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new CloseFragment())
                    .addToBackStack(null).commit();
        } else {
            showLocationNotDeterminedError();
        }
    }

    @Override
    public void showUserNotLoggedError() {
        Toast.makeText(getActivity(), getString(R.string.user_not_logged_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLocationNotDeterminedError() {
        Toast.makeText(getActivity(), getString(R.string.location_not_determined_error),Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.ly_opiniones) void onClickedOpiniones() {
        launchReviewsFragment();
    }

    @OnClick(R.id.ly_hospitales) void onClickedHospitales() {
        launchSearchHospitalsFragment();
    }

    @OnClick(R.id.ly_opinar) void onClickedWriteOpinion() {
        launchWriteOpinionFragment();
    }

    @OnClick(R.id.ly_servicios) void onClickedServices() {
        launchSearchServicesFragment();
    }

    @OnClick(R.id.ly_cerca) void onClickedClose() {
        launchCloseFragment();
    }
}
