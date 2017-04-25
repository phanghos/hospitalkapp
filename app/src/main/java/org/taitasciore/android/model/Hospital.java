package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by roberto on 19/04/17.
 */

public class Hospital implements Serializable {

    @SerializedName("id_company")
    private int idCompany;
    @SerializedName("comapny_name")
    private String comapnyName;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("id_city")
    private String idCity;
    @SerializedName("city_name")
    private String cityName;
    @SerializedName("id_activity")
    private String idActivity;
    @SerializedName("company_opinions")
    private String companyOpinions;
    private int average;
    @SerializedName("highlights_services")
    private ArrayList<HighlightService> highlightServices;
    @SerializedName("company_average")
    private int companyAverage;
    @SerializedName("company_description")
    private String companyDescription;
    @SerializedName("activity_name")
    private String activityName;
    @SerializedName("days")
    private String days;
    @SerializedName("type")
    private String type;
    @SerializedName("company_city")
    private String companyCity;
    @SerializedName("rating_detail")
    private ArrayList<RatingDetail> ratingDetail;

    public int getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(int idCompany) {
        this.idCompany = idCompany;
    }

    public String getComapnyName() {
        return comapnyName;
    }

    public void setComapnyName(String comapnyName) {
        this.comapnyName = comapnyName;
    }

    public int getCompanyAverage() {
        return companyAverage;
    }

    public void setCompanyAverage(int companyAverage) {
        this.companyAverage = companyAverage;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public ArrayList<RatingDetail> getRatingDetail() {
        return ratingDetail;
    }

    public void setRatingDetail(ArrayList<RatingDetail> ratingDetail) {
        this.ratingDetail = ratingDetail;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIdCity() {
        return idCity;
    }

    public void setIdCity(String idCity) {
        this.idCity = idCity;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
    }

    public String getCompanyOpinions() {
        return companyOpinions;
    }

    public void setCompanyOpinions(String companyOpinions) {
        this.companyOpinions = companyOpinions;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    public ArrayList<HighlightService> getHighlightServices() {
        return highlightServices;
    }

    public void setHighlightServices(ArrayList<HighlightService> highlightServices) {
        this.highlightServices = highlightServices;
    }

    public static class RatingDetail implements Serializable {

        private int value;
        private int total;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
