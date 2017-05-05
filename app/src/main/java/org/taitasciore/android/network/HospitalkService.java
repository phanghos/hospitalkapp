package org.taitasciore.android.network;

import org.taitasciore.android.model.Activity;
import org.taitasciore.android.model.City;
import org.taitasciore.android.model.FooterContent;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.NewReview;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.ReviewResponse;
import org.taitasciore.android.model.SearchResponse;
import org.taitasciore.android.model.SendReviewResponse;
import org.taitasciore.android.model.Service;
import org.taitasciore.android.model.ServiceResponse;
import org.taitasciore.android.model.User;
import org.taitasciore.android.model.UserRegistration;

import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roberto on 18/04/17.
 */

public class HospitalkService {

    private HospitalkApi mApi;

    public HospitalkService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HospitalkApi.BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApi = retrofit.create(HospitalkApi.class);
    }

    public HospitalkService() {
        this(new OkHttpClient());
    }

    public Observable<Response<ArrayList<LocationResponse.Country>>> getCountries() {
        return mApi.getCountries();
    }

    public Observable<Response<ArrayList<LocationResponse.State>>> getStates(String countryId) {
        return mApi.getStates(countryId);
    }

    public Observable<Response<ArrayList<City>>> getCompanyCities(String countryId) {
        return mApi.getCompanyCities(countryId);
    }

    public Observable<Response<ArrayList<LocationResponse.City>>> getCities(String countryId, String stateId) {
        return mApi.getCities(countryId, stateId);
    }

    public Observable<Response<LocationResponse>> getLocation(double lat, double lon) {
        return mApi.getLocation(lat, lon);
    }

    public Observable<Response<ArrayList<Review>>> getReviews(
            int offset, int limit, int lang, double lat, double lon) {
        return mApi.getReviews(offset, limit, lang, lat, lon);
    }

    public Observable<Response<ArrayList<Review>>> getUserReviews(int userId, int offset, int limit) {
        return mApi.getUserReviews(userId, offset, limit, 1);
    }

    public Observable<Response<ReviewResponse>> getReviewDetails(int id) {
        return mApi.getReviewDetails(id);
    }

    public Observable<Response<ArrayList<Hospital>>> getBestRatedHospitals(
            int offset, int limit, int lang, double lat, double lon) {
        return mApi.getBestRatedHospitals(offset, limit, lang, lat, lon);
    }

    public Observable<Response<ArrayList<Hospital>>> getWorstRatedHospitals(
            int offset, int limit, int lang, double lat, double lon) {
        return mApi.getWorstRatedHospitals(offset, limit, lang, lat, lon);
    }

    public Observable<Response<ServiceResponse>> getPopularServices(
            int offset, int limit, int lang, double lat, double lon) {
        return mApi.getPopularServices(offset, limit, lang, lat, lon);
    }

    public Observable<Response<Hospital>> getHospitalDetails(int id) {
        return mApi.getCompanyDetails(id);
    }

    public Observable<Response<Service>> getServiceDetails(int id) {
        return mApi.getServiceDetails(id);
    }

    public Observable<Response<ServiceResponse>> getCompanyServices(
            int id, int offset, int limit, int lang) {
        return mApi.getCompanyServices(id, offset, limit, lang);
    }

    public Observable<Response<ArrayList<Review>>> getCompanyReviews(
            int id, int offset, int limit, int lang) {
        return mApi.getCompanyReviews(id, offset, limit, lang);
    }

    public Observable<Response<ArrayList<Review>>> getServiceReviews(
            int id, int offset, int limit, int lang) {
        return mApi.getServiceReviews(id, offset, limit, lang);
    }

    public Observable<Response<SearchResponse>> searchHospitals(
            String countryId, String cityId, String serviceId, String reviewOrder,
            String reviewRank, int offest, int limit, String query) {
        return mApi.searchCompanies(countryId, cityId, serviceId, reviewOrder, reviewRank,
                offest, limit, query, 1);
    }

    public Observable<Response<SearchResponse>> searchServices(
            String countryId, String cityId, String serviceId, String reviewOrder,
            String reviewRank, int offest, int limit, String query) {
        return mApi.searchServices(countryId, cityId, serviceId, reviewOrder, reviewRank,
                offest, limit, query, 1);
    }

    public Observable<Response<User>> login(String email, String psw) {
        return mApi.login(email, psw);
    }

    public Observable<Response<ArrayList<Activity>>> getActivities() {
        return mApi.getActivities();
    }

    public Observable<Response<SearchResponse>> getCompaniesFromCountry(String countryId) {
        return mApi.getCompaniesFromCountry(countryId);
    }

    public Observable<Response<ArrayList<Hospital>>> getCompaniesWithActivity(
            String activityId, String query) {
        return mApi.getCompaniesWithActivity(activityId, query);
    }

    public Observable<Response<SearchResponse>> getCompaniesFromCountry(String countryId, String stateId, String cityId) {
        return mApi.getCompaniesFromCountry(countryId, stateId, cityId);
    }

    public Observable<Response<ServiceResponse>> getServices() {
        return mApi.getServices();
    }

    public Observable<Response<User>> register(UserRegistration ur) {
        if (ur.getId() == 0) {
            return mApi.register(ur.getName(), ur.getFirstLastName(), ur.getSecondLastName(),
                    ur.getCountry() + "", ur.getState() + "", ur.getCity() + "", ur.getPhone(), ur.getBirthday(),
                    ur.getMail(), ur.getPsw(), ur.getImage());
        } else {
            return mApi.register(ur.getId(), ur.getName(), ur.getFirstLastName(), ur.getSecondLastName(),
                    ur.getCountry() + "", ur.getState() + "", ur.getCity() + "", ur.getPhone(), ur.getBirthday(),
                    ur.getMail(), ur.getPsw(), ur.getImage());
        }
    }

    public Observable<Response<User>> registerSocial(UserRegistration ur) {
        return mApi.registerSocial(ur.getName(), ur.getFirstLastName(),
                "", ur.getMail(), ur.getBirthday());
    }

    public Observable<Response<SendReviewResponse>> sendReview(NewReview review) {
        return mApi.sendReview(review.getUser(), review.getActivity(), review.getCompany(),
                review.getService(), review.getTitle(), review.getDescription(), review.getValue(),
                review.getImage(), 1);
    }

    public Observable<Response<User>> getUserInfo(int userId) {
        return mApi.getUserInfo(userId);
    }

    public Observable<Response<FooterContent>> getFooterContent(int pageId) {
        return mApi.getFooterContent(pageId);
    }
}
