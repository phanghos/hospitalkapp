package org.taitasciore.android.hospitalk.close;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.AttachPresenterEvent;
import org.taitasciore.android.event.RequestCountryAndCityEvent;
import org.taitasciore.android.event.RequestLocationEvent;
import org.taitasciore.android.event.SavePresenterEvent;
import org.taitasciore.android.event.SendCityEvent;
import org.taitasciore.android.event.SendLocationEvent;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 18/04/17.
 */

public class CloseFragment extends Fragment implements CloseContract.View {

    private double lat;
    private double lon;
    private String mCity;
    private int mOffsetBest;
    private int mOffsetWorst;
    private int mOffsetPopular;
    private ArrayList<Hospital> mBestRated;
    private ArrayList<Hospital> mWorstRated;
    private ArrayList<ServiceResponse.Service> mPopularServices;

    private CloseContract.Presenter mPresenter;

    private CloseHospitalAdapter mAdapterBest;
    private CloseHospitalAdapter mAdapterWorst;
    private CloseServiceAdapter mAdapterServices;

    private ProgressDialog mDialog;

    @BindView(R.id.ly1) LinearLayout mLyBest;
    @BindView(R.id.ly2) LinearLayout mLyWorst;
    @BindView(R.id.ly3) LinearLayout mLyPopular;

    private TextView mTvCloseBest;
    private TextView mTvCloseWorst;
    private TextView mTvClosePopular;

    private ImageView mArrowBest;
    private ImageView mArrowWorst;
    private ImageView mArrowPopular;

    private RecyclerView mRecyclerViewBest;
    private RecyclerView mRecyclerViewWorst;
    private RecyclerView mRecyclerViewPopular;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_close, container, false);
        ButterKnife.bind(this, v);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("best_rated")) {
                mBestRated = (ArrayList<Hospital>)
                        savedInstanceState.getSerializable("best_rated");
            }
            if (savedInstanceState.containsKey("worst_rated")) {
                mWorstRated = (ArrayList<Hospital>)
                        savedInstanceState.getSerializable("worst_rated");
            }
            if (savedInstanceState.containsKey("most_popular")) {
                mPopularServices = (ArrayList<ServiceResponse.Service>)
                        savedInstanceState.getSerializable("most_popular");
            }
            mOffsetBest = savedInstanceState.getInt("offset_best");
            mOffsetWorst = savedInstanceState.getInt("offset_worst");
            mOffsetPopular = savedInstanceState.getInt("offset_popular");
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCity(SendCityEvent event) {
        mCity = event.city;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBestRated != null) outState.putSerializable("best_rated", mBestRated);
        if (mWorstRated != null) outState.putSerializable("worst_rated", mWorstRated);
        if (mPopularServices != null) outState.putSerializable("most_popular", mPopularServices);
        outState.putInt("offset_best", mOffsetBest);
        outState.putInt("offset_worst", mOffsetWorst);
        outState.putInt("offset_popular", mOffsetPopular);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new AttachPresenterEvent(this));
        EventBus.getDefault().post(new RequestLocationEvent());
        EventBus.getDefault().post(new RequestCountryAndCityEvent());

        initViews();
        initRecyclerViews();

        if (mBestRated != null) {
            setBestRatedHospitals(mBestRated);
        }
        if (mWorstRated != null) {
            setWorstRatedHospitals(mWorstRated);
        }
        if (mPopularServices != null) {
            setPopularServices(mPopularServices);
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
    public void bindPresenter(CloseContract.Presenter presenter) {
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
    public void incrementBestRatedOffset() {
        mOffsetBest += ClosePresenter.LIMIT;
    }

    @Override
    public void setBestRatedHospitals(ArrayList<Hospital> list) {
        mBestRated = list;
        mAdapterBest = new CloseHospitalAdapter(getActivity(), list);
        mRecyclerViewBest.setAdapter(mAdapterBest);
    }

    @Override
    public void showBestRatedHospitals() {
        mArrowBest.setImageResource(R.drawable.up_arrow_4);
        mRecyclerViewBest.setAlpha(0f);
        mRecyclerViewBest.setVisibility(View.VISIBLE);
        mRecyclerViewBest.animate().alpha(1f);
    }

    @Override
    public void hideBestRatedHospitals() {
        mArrowBest.setImageResource(R.drawable.down_arrow_4);
        mRecyclerViewBest.setAlpha(1f);
        mRecyclerViewBest.setVisibility(View.GONE);
        mRecyclerViewBest.animate().alpha(0f);
    }

    @Override
    public void incrementWorstRatedOffset() {
        mOffsetWorst += ClosePresenter.LIMIT;
    }

    @Override
    public void setWorstRatedHospitals(ArrayList<Hospital> list) {
        mWorstRated = list;
        mAdapterWorst = new CloseHospitalAdapter(getActivity(), list);
        mRecyclerViewWorst.setAdapter(mAdapterWorst);
    }

    @Override
    public void showWorstRatedHospitals() {
        mArrowWorst.setImageResource(R.drawable.up_arrow_4);
        mRecyclerViewWorst.setAlpha(0f);
        mRecyclerViewWorst.setVisibility(View.VISIBLE);
        mRecyclerViewWorst.animate().alpha(1f);
    }

    @Override
    public void hideWorstRatedHospitals() {
        mArrowWorst.setImageResource(R.drawable.down_arrow_4);
        mRecyclerViewWorst.setAlpha(1f);
        mRecyclerViewWorst.setVisibility(View.GONE);
        mRecyclerViewWorst.animate().alpha(0f);
    }

    @Override
    public void incrementPopularServicesOffset() {
        mOffsetPopular += ClosePresenter.LIMIT;
    }

    @Override
    public void setPopularServices(ArrayList<ServiceResponse.Service> list) {
        mPopularServices = list;
        mAdapterServices = new CloseServiceAdapter(getActivity(), list);
        mRecyclerViewPopular.setAdapter(mAdapterServices);
    }

    @Override
    public void showPopularServices() {
        mArrowPopular.setImageResource(R.drawable.up_arrow_4);
        mRecyclerViewPopular.setAlpha(0f);
        mRecyclerViewPopular.setVisibility(View.VISIBLE);
        mRecyclerViewPopular.animate().alpha(1f);
    }

    @Override
    public void hidePopularServices() {
        mArrowPopular.setImageResource(R.drawable.down_arrow_4);
        mRecyclerViewPopular.setAlpha(1f);
        mRecyclerViewPopular.setVisibility(View.GONE);
        mRecyclerViewPopular.animate().alpha(0f);
    }

    private void initViews() {
        initArrows();

        mTvCloseBest = (TextView) mLyBest.findViewById(R.id.tvCerca);
        mTvCloseWorst = (TextView) mLyWorst.findViewById(R.id.tvCerca);
        mTvClosePopular = (TextView) mLyPopular.findViewById(R.id.tvCerca);

        mTvCloseBest.setText("Hospitales mejor valorados cerca de " + mCity);
        mTvCloseWorst.setText("Hospitales peor valorados cerca de " + mCity);
        mTvClosePopular.setText("Servicios más populares cerca de " + mCity);
    }

    private void initArrows() {
        mArrowBest = (ImageView) mLyBest.findViewById(R.id.arrow);
        mArrowWorst = (ImageView) mLyWorst.findViewById(R.id.arrow);
        mArrowPopular = (ImageView) mLyPopular.findViewById(R.id.arrow);

        mArrowBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBestRated == null) {
                    mPresenter.getBestRatedHospitals(mOffsetBest, lat, lon);
                } else if (mRecyclerViewBest.getVisibility() == View.GONE) {
                    showBestRatedHospitals();
                } else {
                    hideBestRatedHospitals();
                }
            }
        });

        mArrowWorst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWorstRated == null) {
                    mPresenter.getWorstRatedHospitals(mOffsetWorst, lat, lon);
                } else if (mRecyclerViewWorst.getVisibility() == View.GONE) {
                    showWorstRatedHospitals();
                } else {
                    hideWorstRatedHospitals();
                }
            }
        });

        mArrowPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopularServices == null) {
                    mPresenter.getPopularServices(mOffsetPopular, lat, lon);
                } else if (mRecyclerViewPopular.getVisibility() == View.GONE) {
                    showPopularServices();
                } else {
                    hidePopularServices();
                }
            }
        });
    }

    private void initRecyclerViews() {
        mRecyclerViewBest = (RecyclerView)
                mLyBest.findViewById(R.id.list);
        mRecyclerViewWorst = (RecyclerView)
                mLyWorst.findViewById(R.id.list);
        mRecyclerViewPopular = (RecyclerView)
                mLyPopular.findViewById(R.id.list);
        mRecyclerViewBest.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewWorst.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewPopular.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
