package org.taitasciore.android.hospitalk;

import org.taitasciore.android.hospitalk.close.CloseContract;
import org.taitasciore.android.hospitalk.hospital.HospitalDetailsContract;
import org.taitasciore.android.hospitalk.hospital.SearchHospitalsContract;
import org.taitasciore.android.hospitalk.login.LoginContract;
import org.taitasciore.android.hospitalk.main.LocationContract;
import org.taitasciore.android.hospitalk.mainmenu.MainMenuContract;
import org.taitasciore.android.hospitalk.review.ReviewDetailsContract;
import org.taitasciore.android.hospitalk.review.ReviewsContract;
import org.taitasciore.android.hospitalk.service.SearchServicesContract;
import org.taitasciore.android.hospitalk.service.ServiceDetailsContract;
import org.taitasciore.android.hospitalk.signup.SignupContract;

/**
 * Created by roberto on 19/04/17.
 */

public class PresenterState {

    private LocationContract.Presenter mLocationPresenter;
    private MainMenuContract.Presenter mMenuPresenter;
    private ReviewsContract.Presenter mReviewsPresenter;
    private ReviewDetailsContract.Presenter mReviewDetailsPresenter;
    private CloseContract.Presenter mClosePresenter;
    private HospitalDetailsContract.Presenter mHospitalDetailsPresenter;
    private SearchHospitalsContract.Presenter mSearchHospitalsPresenter;
    private SearchServicesContract.Presenter mSearchServicesPresenter;
    private ServiceDetailsContract.Presenter mServiceDetailsPresenter;
    private SignupContract.Presenter mSignUpPresenter;
    private LoginContract.Presenter mLoginPresenter;

    public LocationContract.Presenter getLocationPresenter() {
        return mLocationPresenter;
    }

    public void setLocationPresenter(LocationContract.Presenter presenter) {
        mLocationPresenter = presenter;
    }

    public MainMenuContract.Presenter getMenuPresenter() {
        return mMenuPresenter;
    }

    public void setMenuPresenter(MainMenuContract.Presenter presenter) {
        mMenuPresenter = presenter;
    }

    public ReviewsContract.Presenter getReviewsPresenter() {
        return mReviewsPresenter;
    }

    public void setReviewsPresenter(ReviewsContract.Presenter presenter) {
        this.mReviewsPresenter = presenter;
    }

    public ReviewDetailsContract.Presenter getReviewDetailsPresenter() {
        return mReviewDetailsPresenter;
    }

    public void setReviewDetailsPresenter(ReviewDetailsContract.Presenter presenter) {
        this.mReviewDetailsPresenter = presenter;
    }

    public CloseContract.Presenter getClosePresenter() {
        return mClosePresenter;
    }

    public void setClosePresenter(CloseContract.Presenter presenter) {
        this.mClosePresenter = presenter;
    }

    public HospitalDetailsContract.Presenter getHospitalDetailsPresenter() {
        return mHospitalDetailsPresenter;
    }

    public void setHospitalDetailsPresenter(HospitalDetailsContract.Presenter presenter) {
        this.mHospitalDetailsPresenter = presenter;
    }

    public SearchHospitalsContract.Presenter getSearchHospitalsPresenter() {
        return mSearchHospitalsPresenter;
    }

    public void setSearchHospitalsPresenter(SearchHospitalsContract.Presenter presenter) {
        this.mSearchHospitalsPresenter = presenter;
    }

    public SearchServicesContract.Presenter getSearchServicesPresenter() {
        return mSearchServicesPresenter;
    }

    public void setSearchServicesPresenter(SearchServicesContract.Presenter presenter) {
        this.mSearchServicesPresenter = presenter;
    }

    public ServiceDetailsContract.Presenter getServiceDetailsPresenter() {
        return mServiceDetailsPresenter;
    }

    public void setServiceDetailsPresenter(ServiceDetailsContract.Presenter presenter) {
        this.mServiceDetailsPresenter = presenter;
    }

    public SignupContract.Presenter getSignupPresenter() {
        return mSignUpPresenter;
    }

    public void setSignupPresenter(SignupContract.Presenter presenter) {
        this.mSignUpPresenter = presenter;
    }

    public LoginContract.Presenter getLoginPresenter() {
        return mLoginPresenter;
    }

    public void setLoginPresenter(LoginContract.Presenter presenter) {
        this.mLoginPresenter = presenter;
    }
}
