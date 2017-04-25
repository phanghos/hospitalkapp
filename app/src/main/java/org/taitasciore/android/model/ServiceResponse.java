package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roberto on 19/04/17.
 */

public class ServiceResponse implements Serializable {

    @SerializedName("services")
    private ArrayList<Service> services;

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }

    public static class Service implements Serializable {
        @SerializedName("id_companies_services")
        private int idCompaniesServices;
        @SerializedName("service_name")
        private String serviceName;
        @SerializedName("company_name")
        private String companyName;
        @SerializedName("activity_name")
        private String activityName;
        @SerializedName("opinions")
        private String opinions;
        @SerializedName("average")
        private int average;

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

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getOpinions() {
            return opinions;
        }

        public void setOpinions(String opinions) {
            this.opinions = opinions;
        }

        public int getAverage() {
            return average;
        }

        public void setAverage(int average) {
            this.average = average;
        }
    }
}
