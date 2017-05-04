package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roberto on 02/05/17.
 */

public class SendReviewResponse {

    @SerializedName("id_rating")
    private int idRating;

    public int getIdRating() {
        return idRating;
    }

    public void setIdRating(int idRating) {
        this.idRating = idRating;
    }
}
