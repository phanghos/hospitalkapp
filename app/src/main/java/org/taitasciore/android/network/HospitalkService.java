package org.taitasciore.android.network;

import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.ReviewResponse;
import org.taitasciore.android.model.SearchResponse;
import org.taitasciore.android.model.Service;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roberto on 18/04/17.
 */

public class HospitalkService {

    private HospitalkApi mApi;

    public HospitalkService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HospitalkApi.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApi = retrofit.create(HospitalkApi.class);
    }

    public Observable<ArrayList<LocationResponse.Country>> getCountries() {
        return mApi.getCountries();
    }

    public Observable<LocationResponse> getLocation(double lat, double lon) {
        return mApi.getLocation(lat, lon);
    }

    public Observable<ArrayList<Review>> getReviews(
            int offset, int limit, int lang, double lat, double lon) {
        return mApi.getReviews(offset, limit, lang, lat, lon);
    }

    public Observable<ReviewResponse> getReviewDetails(int id) {
        return mApi.getReviewDetails(id);
    }

    public Observable<ArrayList<Hospital>> getBestRatedHospitals(
            int offset, int limit, int lang, double lat, double lon) {
        return mApi.getBestRatedHospitals(offset, limit, lang, lat, lon);
    }

    public Observable<ArrayList<Hospital>> getWorstRatedHospitals(
            int offset, int limit, int lang, double lat, double lon) {
        return mApi.getWorstRatedHospitals(offset, limit, lang, lat, lon);
    }

    public Observable<ServiceResponse> getPopularServices(
            int offset, int limit, int lang, double lat, double lon) {
        return mApi.getPopularServices(offset, limit, lang, lat, lon);
    }

    public Observable<Hospital> getHospitalDetails(int id) {
        return mApi.getHospitalDetails(id);
    }

    public Observable<Service> getServiceDetails(int id) {
        return mApi.getServiceDetails(id);
    }

    public Observable<ServiceResponse> getCompanyServices(
            int id, int offset, int limit, int lang) {
        return mApi.getCompanyServices(id, offset, limit, lang);
    }

    public Observable<ArrayList<Review>> getCompanyReviews(
            int id, int offset, int limit, int lang) {
        return mApi.getCompanyReviews(id, offset, limit, lang);
    }

    public Observable<ArrayList<Review>> getServiceReviews(
            int id, int offset, int limit, int lang) {
        return mApi.getServiceReviews(id, offset, limit, lang);
    }

    public Observable<SearchResponse> searchHospitals(
            String countryId, String cityId, String serviceId, String reviewOrder,
            String reviewRank, int offest, int limit, int lang) {
        return mApi.searchHospitals(countryId, cityId, serviceId, reviewOrder, reviewRank,
                offest, limit, lang);
    }

    public Observable<SearchResponse> searchServices(
            String countryId, String cityId, String serviceId, String reviewOrder,
            String reviewRank, int offest, int limit, int lang) {
        return mApi.searchServices(countryId, cityId, serviceId, reviewOrder, reviewRank,
                offest, limit, lang);
    }
}
