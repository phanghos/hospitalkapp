package org.taitasciore.android.event;

import android.content.Intent;

/**
 * Created by roberto on 29/04/17.
 */

public class UploadImageEvent {

    public int resultCode;
    public Intent data;

    public UploadImageEvent(int resultCode, Intent data) {
        this.resultCode = resultCode;
        this.data = data;
    }
}
