package org.taitasciore.android.network;

import org.taitasciore.android.model.Activity;
import org.taitasciore.android.model.City;
import org.taitasciore.android.model.FooterContent;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.model.LocationResponse;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.model.ReviewResponse;
import org.taitasciore.android.model.SearchResponse;
import org.taitasciore.android.model.SendReviewResponse;
import org.taitasciore.android.model.Service;
import org.taitasciore.android.model.ServiceResponse;
import org.taitasciore.android.model.User;

import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by roberto on 18/04/17.
 */

public interface HospitalkApi {

    String BASE_URL = "http://dev.hospitalk.org/index.php/api/";

    @GET("location/countries")
    Observable<Response<ArrayList<LocationResponse.Country>>> getCountries();

    @GET("location/states")
    Observable<Response<ArrayList<LocationResponse.State>>> getStates(
            @Query("countryID") String countryId);

    @GET("company/cities")
    Observable<Response<ArrayList<City>>> getCompanyCities(
            @Query("id_country") String countryId);

    @GET("location/cities")
    Observable<Response<ArrayList<LocationResponse.City>>> getCities(
            @Query("countryID") String countryId,
            @Query("stateID") String stateId);

    @GET("location/info")
    Observable<Response<LocationResponse>> getLocation(
            @Query("latitude") double lat,
            @Query("longitude") double lon);

    @GET("rating/list")
    Observable<Response<ArrayList<Review>>> getReviews(
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang,
            @Query("latitude") double lat,
            @Query("longitude") double lon);

    @GET("rating/list")
    Observable<Response<ArrayList<Review>>> getUserReviews(
            @Query("id_user") int userId,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int language);

    @GET("rating/detail")
    Observable<Response<ReviewResponse>> getReviewDetails(@Query("id_rating") int id);

    @GET("company/best_rated")
    Observable<Response<ArrayList<Hospital>>> getBestRatedHospitals(
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang,
            @Query("latitude") double lat,
            @Query("longitude") double lon);

    @GET("company/worst_rated")
    Observable<Response<ArrayList<Hospital>>> getWorstRatedHospitals(
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang,
            @Query("latitude") double lat,
            @Query("longitude") double lon);

    @GET("service/list")
    Observable<Response<ServiceResponse>> getPopularServices(
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang,
            @Query("latitude") double lat,
            @Query("longitude") double lon);

    @GET("company/detail")
    Observable<Response<Hospital>> getCompanyDetails(@Query("id_company") int id);

    @GET("service/detail")
    Observable<Response<Service>> getServiceDetails(@Query("id_service") int id);

    @GET("service/list")
    Observable<Response<ServiceResponse>> getCompanyServices(
            @Query("id_company") int id,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang);

    @GET("rating/list")
    Observable<Response<ArrayList<Review>>> getCompanyReviews(
            @Query("id_company") int id,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang);

    @GET("rating/list")
    Observable<Response<ArrayList<Review>>> getServiceReviews(
            @Query("id_service") int id,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("language") int lang);

    @GET("activity/list")
    Observable<Response<ArrayList<Activity>>> getActivities();

    @GET("company/list")
    Observable<Response<SearchResponse>> getCompaniesFromCountry(@Query("id_country") String countryId);

    @GET("company/search")
    Observable<Response<ArrayList<Hospital>>> getCompaniesWithActivity(
            @Query("id_activity") String activityId,
            @Query("q") String query);

    @GET("company/list")
    Observable<Response<SearchResponse>> getCompaniesFromCountry(
            @Query("id_country") String countryId,
            @Query("id_state") String stateId,
            @Query("id_city") String cityId);

    @GET("service/list")
    Observable<Response<ServiceResponse>> getServices();

    @GET("company/list")
    Observable<Response<SearchResponse>> searchCompanies(
            @Query("id_country") String countryId,
            @Query("id_city") String cityId,
            @Query("id_activity") String serviceId,
            @Query("order_reviews") String reviewOrder,
            @Query("rank_reviews") String reviewRank,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("search") String query,
            @Query("language") int lang);

    @GET("service/list")
    Observable<Response<SearchResponse>> searchServices(
            @Query("id_country") String countryId,
            @Query("id_city") String cityId,
            @Query("id_service") String serviceId,
            @Query("order_reviews") String reviewOrder,
            @Query("rank_reviews") String reviewRank,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("search") String query,
            @Query("language") int lang);

    @GET("user/login")
    Observable<Response<User>> login(
            @Query("email") String email,
            @Query("password") String psw);

    @FormUrlEncoded
    @POST("user/registration")
    Observable<Response<User>> register(
            @Field("name") String name,
            @Field("first_last_name") String lastName,
            @Field("second_last_name") String secondLastName,
            @Field("id_country") String countryId,
            @Field("id_state") String stateId,
            @Field("id_city") String cityId,
            @Field("phone") String phone,
            @Field("birthday") String birthday,
            @Field("email") String email,
            @Field("password") String psw,
            @Field("image") String image);

    @FormUrlEncoded
    @POST("user/registration")
    Observable<Response<User>> register(
            @Field("id_user") int userId,
            @Field("name") String name,
            @Field("first_last_name") String lastName,
            @Field("second_last_name") String secondLastName,
            @Field("id_country") String countryId,
            @Field("id_state") String stateId,
            @Field("id_city") String cityId,
            @Field("phone") String phone,
            @Field("birthday") String birthday,
            @Field("email") String email,
            @Field("password") String psw,
            @Field("image") String image);

    @FormUrlEncoded
    @POST("user/registration_social")
    Observable<Response<User>> registerSocial(
            @Field("name") String name,
            @Field("first_last_name") String lastName,
            @Field("second_last_name") String secondLastName,
            @Field("email") String email,
            @Field("birthday") String birthday);

    @FormUrlEncoded
    @POST("rating/create")
    Observable<Response<SendReviewResponse>> sendReview(
            @Field("id_user") int userId,
            @Field("id_service") String activityId,
            @Field("id_company") String companyId,
            @Field("id_companies_services") String serviceId,
            @Field("rating_title") String title,
            @Field("rating_review") String description,
            @Field("rating_value") int value,
            @Field("rating_images") String encodedImage,
            @Field("language") int language);

    @GET("user/dashboard")
    Observable<Response<User>> getUserInfo(@Query("id_user") int userId);

    @FormUrlEncoded
    @POST("contact/create")
    Observable<Response<String>> contact(
            @Field("name") String name,
            @Field("email") String email,
            @Field("comment") String comment,
            @Field("phone") String phone,
            @Field("company") String company,
            @Field("type") String type);

    @GET("pages/page")
    Observable<Response<FooterContent>> getFooterContent(@Query("id_page") int pageId);
}
