package org.taitasciore.android.hospitalk.close;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.taitasciore.android.event.LoadMoreBestRatedHospitalsEvent;
import org.taitasciore.android.event.LoadMoreServicesEvent;
import org.taitasciore.android.event.LoadMoreWorstRatedHospitalsEvent;
import org.taitasciore.android.event.RequestCityEvent;
import org.taitasciore.android.event.RequestLocationEvent;
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
    private String mCity = "";
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_close, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new ClosePresenter(this);
        else mPresenter.bindView(this);
    }

    @Override
    public void onDetach() {
        hideProgress();
        super.onDetach();
        if (mPresenter != null) mPresenter.bindView(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        initListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        clearListeners();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadMoreBestRatedHospitals(LoadMoreBestRatedHospitalsEvent event) {
        mPresenter.getBestRatedHospitals(mOffsetBest, lat, lon);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadMoreWorstRatedHospitals(LoadMoreWorstRatedHospitalsEvent event) {
        mPresenter.getWorstRatedHospitals(mOffsetWorst, lat, lon);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadMoreServices(LoadMoreServicesEvent event) {
        mPresenter.getPopularServices(mOffsetPopular, lat, lon);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new RequestLocationEvent());
        EventBus.getDefault().post(new RequestCityEvent());

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
        mAdapterBest = new CloseHospitalAdapter(
                getActivity(), list, CloseHospitalAdapter.TYPE_BEST_RATED);
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
        mAdapterWorst = new CloseHospitalAdapter(
                getActivity(), list, CloseHospitalAdapter.TYPE_WORST_RATED);
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

    @Override
    public void addBestRatedHospitals(ArrayList<Hospital> hospitals) {
        for (Hospital h : hospitals) mAdapterBest.add(h);
        mBestRated = mAdapterBest.getList();
    }

    @Override
    public void addWorstRatedHospitals(ArrayList<Hospital> hospitals) {
        for (Hospital h : hospitals) mAdapterWorst.add(h);
        mWorstRated = mAdapterWorst.getList();
    }

    @Override
    public void addPopularServices(ArrayList<ServiceResponse.Service> services) {
        for (ServiceResponse.Service s : services) mAdapterServices.add(s);
        mPopularServices = mAdapterServices.getList();
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNetworkFailedError() {
        Toast.makeText(getActivity(),getString(R.string.network_failed_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoMoreHospitalsError() {
        Toast.makeText(getActivity(), "No hay hospitales que mostrar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoMoreServicesError() {
        Toast.makeText(getActivity(), "No hay servicios que mostrar", Toast.LENGTH_SHORT).show();
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
        mRecyclerViewBest.setNestedScrollingEnabled(false);
        mRecyclerViewWorst.setNestedScrollingEnabled(false);
        mRecyclerViewPopular.setNestedScrollingEnabled(false);
        Drawable divider = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        mRecyclerViewBest.addItemDecoration(new DividerItemDecoration(divider));
        mRecyclerViewWorst.addItemDecoration(new DividerItemDecoration(divider));
        mRecyclerViewPopular.addItemDecoration(new DividerItemDecoration(divider));
    }

    private void initListeners() {
        mLyBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("current pos", lat + " - " + lon);

                if (mBestRated == null) {
                    mPresenter.getBestRatedHospitals(mOffsetBest, lat, lon);
                } else if (mRecyclerViewBest.getVisibility() == View.GONE) {
                    showBestRatedHospitals();
                } else {
                    hideBestRatedHospitals();
                }
            }
        });

        mLyWorst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("current pos", lat + " - " + lon);

                if (mWorstRated == null) {
                    mPresenter.getWorstRatedHospitals(mOffsetWorst, lat, lon);
                } else if (mRecyclerViewWorst.getVisibility() == View.GONE) {
                    showWorstRatedHospitals();
                } else {
                    hideWorstRatedHospitals();
                }
            }
        });

        mLyPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("current pos", lat + " - " + lon);

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

    private void clearListeners() {
        mLyBest.setOnClickListener(null);
        mLyWorst.setOnClickListener(null);
        mLyPopular.setOnClickListener(null);
    }
}
