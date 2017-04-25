package org.taitasciore.android.hospitalk.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.taitasciore.android.event.AttachPresenterEvent;
import org.taitasciore.android.event.RequestLocationEvent;
import org.taitasciore.android.event.SendLocationEvent;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.event.SavePresenterEvent;
import org.taitasciore.android.hospitalk.close.CloseFragment;
import org.taitasciore.android.hospitalk.hospital.SearchHospitalsFragment;
import org.taitasciore.android.hospitalk.review.ReviewsFragment;
import org.taitasciore.android.hospitalk.service.SearchServicesFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 18/04/17.
 */

public class MenuFragment extends Fragment implements MenuContract.View {

    private double lat;
    private double lon;

    private MenuContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, v);
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

    @Subscribe
    public void getLocation(SendLocationEvent event) {
        lat = event.lat;
        lon = event.lon;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new AttachPresenterEvent(this));
        EventBus.getDefault().post(new RequestLocationEvent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.unbindView();
            EventBus.getDefault().post(new SavePresenterEvent(mPresenter));
        }
    }

    @Override
    public void bindPresenter(MenuContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void launchOpinionsActivity() {
        if (lat != 0 && lon != 0) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ReviewsFragment())
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public void launchSearchHospitalsActivity() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SearchHospitalsFragment())
                .addToBackStack(null).commit();
    }

    @Override
    public void launchWriteOpinionActivity() {

    }

    @Override
    public void launchSearchServicesActivity() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SearchServicesFragment())
                .addToBackStack(null).commit();
    }

    @Override
    public void launchCloseActivity() {
        if (lat != 0 && lon != 0) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new CloseFragment())
                    .addToBackStack(null).commit();
        }
    }

    @OnClick(R.id.ly_opiniones) void onClickedOpiniones() {
        launchOpinionsActivity();
    }

    @OnClick(R.id.ly_hospitales) void onClickedHospitales() {
        launchSearchHospitalsActivity();
    }

    @OnClick(R.id.ly_opinar) void onClickedWriteOpinion() {
        launchWriteOpinionActivity();
    }

    @OnClick(R.id.ly_servicios) void onClickedServices() {
        launchSearchServicesActivity();
    }

    @OnClick(R.id.ly_cerca) void onClickedClose() {
        launchCloseActivity();
    }
}
