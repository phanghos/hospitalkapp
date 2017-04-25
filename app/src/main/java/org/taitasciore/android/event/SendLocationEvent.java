package org.taitasciore.android.event;

/**
 * Created by roberto on 19/04/17.
 */

public class SendLocationEvent {

    public double lat;
    public double lon;

    public SendLocationEvent(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
