package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by roberto on 24/04/17.
 */

public class SearchResponse implements Serializable {

    private ArrayList<Hospital> companies;
    private ArrayList<ServiceResponse.Service> services;
    private ArrayList<City> cities;
    private ArrayList<Activity> activities;
    @SerializedName("services_filter") private ArrayList<ServiceFilter> servicesFilter;

    public ArrayList<Hospital> getCompanies() {
        return companies;
    }

    public void setCompanies(ArrayList<Hospital> companies) {
        this.companies = companies;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<Activity> activities) {
        this.activities = activities;
    }

    public ArrayList<ServiceResponse.Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<ServiceResponse.Service> services) {
        this.services = services;
    }

    public ArrayList<ServiceFilter> getServicesFilter() {
        return servicesFilter;
    }

    public void setServicesFilter(ArrayList<ServiceFilter> servicesFilter) {
        this.servicesFilter = servicesFilter;
    }
}
