package org.taitasciore.android.event;

/**
 * Created by roberto on 03/05/17.
 */

public class SetDateEvent {

    public int year;
    public int month;
    public int day;

    public SetDateEvent(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
