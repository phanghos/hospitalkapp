package org.taitasciore.android.hospitalk.review;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.StarUtils;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.event.AttachPresenterEvent;
import org.taitasciore.android.event.SavePresenterEvent;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.network.HospitalkApi;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 19/04/17.
 */

public class ReviewDetailsFragment extends Fragment implements ReviewDetailsContract.View {

    private Review mReview;

    private ReviewDetailsContract.Presenter mPresenter;

    private ProgressDialog mDialog;

    @BindView(R.id.tvNombreServicio) TextView mTvServiceName;
    @BindView(R.id.tvCiudadServicio) TextView mTvServiceCity;
    @BindView(R.id.tvNombreHospital) TextView mTvHospitalName;
    @BindView(R.id.tvActividadHospital) TextView mTvHospitalActivity;
    @BindView(R.id.tvTelefono) TextView mTvPhone;
    @BindView(R.id.tvDireccion) TextView mTvAddress;
    @BindView(R.id.tvTituloOpinion) TextView mTvReviewTitle;
    @BindView(R.id.tvUsuario) TextView mTvUsername;
    @BindView(R.id.tvUbicacion) TextView mTvLocation;
    @BindView(R.id.tvFecha) TextView mTvDate;
    @BindView(R.id.tvNombreServicio2) TextView mTvServiceName2;
    @BindView(R.id.tvOpinion) TextView mTvReview;
    @BindView(R.id.lyAvg) LinearLayout mLyAvg;
    @BindView(R.id.img) SimpleDraweeView mImg;
    @BindView(R.id.btnRespuesta) Button mBtnRespuesta;
    @BindView(R.id.btnVotos) Button mBtnVotos;

    public static ReviewDetailsFragment newInstance(int id) {
        ReviewDetailsFragment f = new ReviewDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review_details, container, false);
        ButterKnife.bind(this, v);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("review")) {
                mReview = (Review) savedInstanceState.getSerializable("review");
            }
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mReview != null) outState.putSerializable("review", mReview);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new AttachPresenterEvent(this));

        if (mReview != null) showReviewInfo(mReview);
        else mPresenter.getReview(getArguments().getInt("id"));
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
    public void bindPresenter(ReviewDetailsContract.Presenter presenter) {
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
    public void setReview(Review review) {
        mReview = review;
    }

    @Override
    public void showReviewInfo(Review review) {
        mTvServiceName.setText(review.getServiceName()+"");
        mTvServiceCity.setText(review.getServiceCityName()+"");
        mTvHospitalName.setText(review.getCompanyName()+"");
        mTvHospitalActivity.setText(review.getCompanyActivityName()+"");

        String phone = review.getCompanyPhone();
        if (phone == null || phone.isEmpty() || phone.equalsIgnoreCase("null")) {
            mTvPhone.setVisibility(View.GONE);
        } else {
            mTvPhone.setText(review.getCompanyPhone() + "");
        }

        String address = review.getCompanyAddress();
        if (address == null || address.isEmpty() || address.equalsIgnoreCase("null")) {
            mTvAddress.setVisibility(View.GONE);
        } else {
            mTvAddress.setText(review.getCompanyAddress()+"");
        }

        mTvReviewTitle.setText("\"" + review.getRatingTitle() + "\"");
        mTvUsername.setText(review.getUserName());
        mTvLocation.setText(review.getUserCityName() + ", " + review.getUserCountryName());
        mTvDate.setText(review.getDays());
        mTvServiceName2.setText(review.getServiceName());
        mTvReview.setText(review.getRatingReview());
        StarUtils.addStars(getActivity(), 5, mLyAvg);
        StarUtils.fillStars(getActivity(), review.getRatingValue(), mLyAvg);

        String imgUrl = HospitalkApi.BASE_URL + "user/image?id_user=" + review.getIdUser();
        mImg.setImageURI(Uri.parse(imgUrl));
    }
}
