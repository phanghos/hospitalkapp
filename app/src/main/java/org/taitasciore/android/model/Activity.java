package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by roberto on 24/04/17.
 */

public class Activity implements Serializable {

    @SerializedName("id_activity")
    private String idActivity;
    @SerializedName("activity_name")
    private String activityName;

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
