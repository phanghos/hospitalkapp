package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by roberto on 24/04/17.
 */

public class City implements Serializable {

    @SerializedName("id_city")
    private String idCity;
    @SerializedName("city_name")
    private String cityName;

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
}
