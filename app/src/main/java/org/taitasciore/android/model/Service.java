package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by roberto on 25/04/17.
 */

public class Service implements Serializable {

    @SerializedName("id_companies_services")
    private int idCompaniesServices;
    @SerializedName("id_service")
    private String idService;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("service_city")
    private String serviceCity;
    @SerializedName("service_address")
    private String serviceAddress;
    @SerializedName("service_average")
    private int serviceAverage;
    @SerializedName("id_company")
    private String idCompany;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("company_address")
    private String companyAddress;
    @SerializedName("company_website")
    private String companyWebsite;
    @SerializedName("company_phone")
    private String companyPhone;
    @SerializedName("company_country")
    private String companyCountry;
    @SerializedName("company_city")
    private String companyCity;
    @SerializedName("company_latitude")
    private double companyLatitude;
    @SerializedName("company_longitude")
    private double companyLongitude;
    @SerializedName("rating_detail")
    private List<Hospital.RatingDetail> ratingDetail;
    @SerializedName("total_ratings")
    private String totalRatings;

    public int getIdCompaniesServices() {
        return idCompaniesServices;
    }

    public void setIdCompaniesServices(int idCompaniesServices) {
        this.idCompaniesServices = idCompaniesServices;
    }

    public String getIdService() {
        return idService;
    }

    public void setIdService(String idService) {
        this.idService = idService;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceCity() {
        return serviceCity;
    }

    public void setServiceCity(String serviceCity) {
        this.serviceCity = serviceCity;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public int getServiceAverage() {
        return serviceAverage;
    }

    public void setServiceAverage(int serviceAverage) {
        this.serviceAverage = serviceAverage;
    }

    public String getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(String idCompany) {
        this.idCompany = idCompany;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyCountry() {
        return companyCountry;
    }

    public void setCompanyCountry(String companyCountry) {
        this.companyCountry = companyCountry;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public double getCompanyLatitude() {
        return companyLatitude;
    }

    public void setCompanyLatitude(double companyLatitude) {
        this.companyLatitude = companyLatitude;
    }

    public double getCompanyLongitude() {
        return companyLongitude;
    }

    public void setCompanyLongitude(double companyLongitude) {
        this.companyLongitude = companyLongitude;
    }

    public List<Hospital.RatingDetail> getRatingDetail() {
        return ratingDetail;
    }

    public void setRatingDetail(List<Hospital.RatingDetail> ratingDetail) {
        this.ratingDetail = ratingDetail;
    }

    public String getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(String totalRatings) {
        this.totalRatings = totalRatings;
    }

    public static class Rating_detail {
    }
}
