package org.taitasciore.android.hospitalk.review;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.taitasciore.android.hospitalk.ActivityUtils;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StarUtils;
import org.taitasciore.android.model.Review;
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

    @BindView(R.id.mainContent) LinearLayout mMainContent;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review_details, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new ReviewDetailsPresenter(this);
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

        if (mReview != null) {
            showReviewInfo(mReview);
            showMainContent();
        } else {
            mPresenter.getReview(getArguments().getInt("id"));
        }
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
        mReview = review;
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

        if (review.getRatingTitle() != null && !review.getRatingTitle().isEmpty()) {
            mTvReviewTitle.setText("\"" + review.getRatingTitle() + "\"");
        } else {
            mTvReviewTitle.setText("Título de opinión no disponible");
        }

        mTvUsername.setText(review.getUserName());
        mTvLocation.setText(review.getUserCityName() + ", " + review.getUserCountryName());
        mTvDate.setText(review.getDays());
        mTvServiceName2.setText(review.getServiceName());
        mTvReview.setText(review.getRatingReview());
        StarUtils.resetStars(mLyAvg);
        StarUtils.addStars(getActivity(), 5, mLyAvg);
        StarUtils.fillStars(getActivity(), review.getRatingValue(), mLyAvg);

        String imgUrl = HospitalkApi.BASE_URL + "user/image?id_user=" + review.getIdUser();
        mImg.setImageURI(Uri.parse(imgUrl));

        if (review.getCompanyAnswer() > 0) {
            mBtnRespuesta.setText("SÍ");
        } else {
            mBtnRespuesta.setText("NO");
        }
        setRepliesButtonBackground(mBtnRespuesta, review.getCompanyAnswer());

        mBtnVotos.setText(review.getRatingHelpfulVotes()+"");
    }

    @Override
    public void setRepliesButtonBackground(Button btn, int replies) {
        int bgColor;

        if (replies > 0) {
            bgColor = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        } else {
            bgColor = ContextCompat.getColor(getActivity(), R.color.colorPrimaryText);
        }

        btn.setBackgroundColor(bgColor);
    }

    @Override
    public void showMainContent() {
        mMainContent.setVisibility(View.VISIBLE);
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
    public void showReviewNotApprovedError() {
        Toast.makeText(getActivity(), getString(R.string.review_not_approved_error), Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }
}
