package org.taitasciore.android.model;

/**
 * Created by roberto on 26/04/17.
 */

public class NewReview {

    private int user;
    private String activity;
    private String company;
    private String service;
    private String country;
    private String state;
    private String city;
    private String title;
    private String description;
    private String image;
    private int value;
    private boolean acceptedConditions;


    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean getAcceptedConditions() {
        return acceptedConditions;
    }

    public void setAcceptedConditions(boolean acceptedConditions) {
        this.acceptedConditions = acceptedConditions;
    }
}
