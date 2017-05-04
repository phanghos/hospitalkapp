package org.taitasciore.android.model;

/**
 * Created by roberto on 26/04/17.
 */

public class NewReview {

    private int user;
    private int activity;
    private int company;
    private int service;
    private String country;
    private int state;
    private int city;
    private String title;
    private String description;
    private int value;
    private String image;
    private boolean acceptedConditions;

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getCompany() {
        return company;
    }

    public void setCompany(int company) {
        this.company = company;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getAcceptedConditions() {
        return acceptedConditions;
    }

    public void setAcceptedConditions(boolean acceptedConditions) {
        this.acceptedConditions = acceptedConditions;
    }
}
