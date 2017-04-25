package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by roberto on 19/04/17.
 */

public class Review implements Serializable {

    @SerializedName("id_rating")
    private int idRating;
    @SerializedName("id_user")
    private int idUser;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("city_name")
    private String cityName;
    @SerializedName("country_name")
    private String countryName;
    @SerializedName("rating_review")
    private String ratingReview;
    @SerializedName("rating_title")
    private String ratingTitle;
    @SerializedName("id_company")
    private int idCompany;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("id_service")
    private int idService;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("days")
    private String days;
    @SerializedName("rating_value")
    private int ratingValue;
    @SerializedName("rating_helpful_votes")
    private String ratingHelpfulVotes;
    @SerializedName("company_answer")
    private String companyAnswer;
    @SerializedName("service_city_name")
    private String serviceCityName;
    @SerializedName("company_activity_name")
    private String companyActivityName;
    @SerializedName("company_phone")
    private String companyPhone;
    @SerializedName("company_address")
    private String companyAddress;
    @SerializedName("user_city_name")
    private String userCityName;
    @SerializedName("user_country_name")
    private String userCountryName;

    public int getIdRating() {
        return idRating;
    }

    public void setIdRating(int idRating) {
        this.idRating = idRating;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getRatingReview() {
        return ratingReview;
    }

    public void setRatingReview(String ratingReview) {
        this.ratingReview = ratingReview;
    }

    public String getRatingTitle() {
        return ratingTitle;
    }

    public void setRatingTitle(String ratingTitle) {
        this.ratingTitle = ratingTitle;
    }

    public int getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(int idCompany) {
        this.idCompany = idCompany;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getRatingHelpfulVotes() {
        return ratingHelpfulVotes;
    }

    public void setRatingHelpfulVotes(String ratingHelpfulVotes) {
        this.ratingHelpfulVotes = ratingHelpfulVotes;
    }

    public String getCompanyAnswer() {
        return companyAnswer;
    }

    public void setCompanyAnswer(String companyAnswer) {
        this.companyAnswer = companyAnswer;
    }

    public String getServiceCityName() {
        return serviceCityName;
    }

    public void setServiceCityName(String serviceCityName) {
        this.serviceCityName = serviceCityName;
    }

    public String getCompanyActivityName() {
        return companyActivityName;
    }

    public void setCompanyActivityName(String companyActivityName) {
        this.companyActivityName = companyActivityName;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getUserCityName() {
        return userCityName;
    }

    public void setUserCityName(String userCityName) {
        this.userCityName = userCityName;
    }

    public String getUserCountryName() {
        return userCountryName;
    }

    public void setUserCountryName(String userCountryName) {
        this.userCountryName = userCountryName;
    }
}
