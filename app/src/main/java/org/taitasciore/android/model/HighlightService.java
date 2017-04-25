package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by roberto on 24/04/17.
 */

public class HighlightService implements Serializable {

    @SerializedName("id_companies_services")
    private int idCompaniesServices;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("opinions")
    private String opinions;

    public int getIdCompaniesServices() {
        return idCompaniesServices;
    }

    public void setIdCompaniesServices(int idCompaniesServices) {
        this.idCompaniesServices = idCompaniesServices;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOpinions() {
        return opinions;
    }

    public void setOpinions(String opinions) {
        this.opinions = opinions;
    }
}
