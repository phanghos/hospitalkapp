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
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by roberto on 18/04/17.
 */

public interface HospitalkApi {

    String BASE_URL = "http://dev.hospitalk.org/index.php/api/";

    @GET("location/countries")
    Observable<ArrayList<LocationResponse.Country>> getCountries();

    @GET("location/info")
    Observable<LocationResponse> getLocation(
            @Query("latitude") double lat,
            @Query("longitude") double lon);

    @GET("rating/list")
    Observable<ArrayList<Review>> getReviews(
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang,
            @Query("latitude") double lat,
            @Query("longitude") double lon);

    @GET("rating/detail")
    Observable<ReviewResponse> getReviewDetails(@Query("id_rating") int id);

    @GET("company/best_rated")
    Observable<ArrayList<Hospital>> getBestRatedHospitals(
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang,
            @Query("latitude") double lat,
            @Query("longitude") double lon);

    @GET("company/worst_rated")
    Observable<ArrayList<Hospital>> getWorstRatedHospitals(
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang,
            @Query("latitude") double lat,
            @Query("longitude") double lon);

    @GET("service/list")
    Observable<ServiceResponse> getPopularServices(
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang,
            @Query("latitude") double lat,
            @Query("longitude") double lon);

    @GET("company/detail")
    Observable<Hospital> getHospitalDetails(@Query("id_company") int id);

    @GET("service/detail")
    Observable<Service> getServiceDetails(@Query("id_service") int id);

    @GET("service/list")
    Observable<ServiceResponse> getCompanyServices(
            @Query("id_company") int id,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang);

    @GET("rating/list")
    Observable<ArrayList<Review>> getCompanyReviews(
            @Query("id_company") int id,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang);

    @GET("rating/list")
    Observable<ArrayList<Review>> getServiceReviews(
            @Query("id_service") int id,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang);

    @GET("company/list")
    Observable<SearchResponse> searchHospitals(
            @Query("id_country") String countryId,
            @Query("id_city") String cityId,
            @Query("id_activity") String serviceId,
            @Query("order_reviews") String reviewOrder,
            @Query("rank_reviews") String reviewRank,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang);

    @GET("service/list")
    Observable<SearchResponse> searchServices(
            @Query("id_country") String countryId,
            @Query("id_city") String cityId,
            @Query("id_activity") String serviceId,
            @Query("order_reviews") String reviewOrder,
            @Query("rank_reviews") String reviewRank,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang);
}
