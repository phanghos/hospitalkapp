package org.taitasciore.android.model;

import java.util.ArrayList;

/**
 * Created by roberto on 28/04/17.
 */

public class WriteReviewData {

    private ArrayList<Activity> activities;
    private ArrayList<Hospital> companies;
    private ArrayList<ServiceResponse.Service> services;
    private ArrayList<LocationResponse.Country> countries;
    private ArrayList<LocationResponse.State> states;
    private ArrayList<LocationResponse.City> cities;

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<Activity> activities) {
        this.activities = activities;
    }

    public ArrayList<Hospital> getCompanies() {
        return companies;
    }

    public void setCompanies(ArrayList<Hospital> companies) {
        this.companies = companies;
    }

    public ArrayList<ServiceResponse.Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<ServiceResponse.Service> services) {
        this.services = services;
    }

    public ArrayList<LocationResponse.Country> getCountries() {
        return countries;
    }

    public void setCountries(ArrayList<LocationResponse.Country> countries) {
        this.countries = countries;
    }

    public ArrayList<LocationResponse.State> getStates() {
        return states;
    }

    public void setStates(ArrayList<LocationResponse.State> states) {
        this.states = states;
    }

    public ArrayList<LocationResponse.City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<LocationResponse.City> cities) {
        this.cities = cities;
    }
}
