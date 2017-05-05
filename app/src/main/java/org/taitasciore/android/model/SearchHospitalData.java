package org.taitasciore.android.model;

import java.util.ArrayList;

/**
 * Created by roberto on 04/05/17.
 */

public class SearchHospitalData {

    private ArrayList<City> cities;
    private SearchResponse searchResponse;

    public SearchHospitalData(ArrayList<City> cities, SearchResponse searchResponse) {
        setCities(cities);
        setSearchResponse(searchResponse);
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    public SearchResponse getSearchResponse() {
        return searchResponse;
    }

    public void setSearchResponse(SearchResponse searchResponse) {
        this.searchResponse = searchResponse;
    }
}
