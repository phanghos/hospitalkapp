package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by roberto on 18/04/17.
 */

public class LocationResponse implements Serializable {

    @SerializedName("country")
    private Country country;
    @SerializedName("state")
    private State state;
    @SerializedName("city")
    private City city;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public static class Country implements Serializable {
        @SerializedName("countryID")
        private String countryid;
        @SerializedName("countryName")
        private String countryname;
        @SerializedName("localName")
        private String localname;
        @SerializedName("webCode")
        private String webcode;
        @SerializedName("region")
        private String region;
        @SerializedName("continent")
        private String continent;
        @SerializedName("latitude")
        private String latitude;
        @SerializedName("longitude")
        private String longitude;
        @SerializedName("surfaceArea")
        private String surfacearea;
        @SerializedName("population")
        private String population;

        public String getCountryid() {
            return countryid;
        }

        public void setCountryid(String countryid) {
            this.countryid = countryid;
        }

        public String getCountryname() {
            return countryname;
        }

        public void setCountryname(String countryname) {
            this.countryname = countryname;
        }

        public String getLocalname() {
            return localname;
        }

        public void setLocalname(String localname) {
            this.localname = localname;
        }

        public String getWebcode() {
            return webcode;
        }

        public void setWebcode(String webcode) {
            this.webcode = webcode;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getContinent() {
            return continent;
        }

        public void setContinent(String continent) {
            this.continent = continent;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getSurfacearea() {
            return surfacearea;
        }

        public void setSurfacearea(String surfacearea) {
            this.surfacearea = surfacearea;
        }

        public String getPopulation() {
            return population;
        }

        public void setPopulation(String population) {
            this.population = population;
        }
    }

    public static class State implements Serializable {
        @SerializedName("stateID")
        private String stateid;
        @SerializedName("stateName")
        private String statename;
        @SerializedName("countryID")
        private String countryid;
        @SerializedName("latitude")
        private String latitude;
        @SerializedName("longitude")
        private String longitude;

        public String getStateid() {
            return stateid;
        }

        public void setStateid(String stateid) {
            this.stateid = stateid;
        }

        public String getStatename() {
            return statename;
        }

        public void setStatename(String statename) {
            this.statename = statename;
        }

        public String getCountryid() {
            return countryid;
        }

        public void setCountryid(String countryid) {
            this.countryid = countryid;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }

    public static class City implements Serializable {
        @SerializedName("cityID")
        private String cityid;
        @SerializedName("cityName")
        private String cityname;
        @SerializedName("stateID")
        private String stateid;
        @SerializedName("countryID")
        private String countryid;
        @SerializedName("latitude")
        private String latitude;
        @SerializedName("longitude")
        private String longitude;

        public String getCityid() {
            return cityid;
        }

        public void setCityid(String cityid) {
            this.cityid = cityid;
        }

        public String getCityname() {
            return cityname;
        }

        public void setCityname(String cityname) {
            this.cityname = cityname;
        }

        public String getStateid() {
            return stateid;
        }

        public void setStateid(String stateid) {
            this.stateid = stateid;
        }

        public String getCountryid() {
            return countryid;
        }

        public void setCountryid(String countryid) {
            this.countryid = countryid;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }
}
