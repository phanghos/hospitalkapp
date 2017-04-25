package org.taitasciore.android.event;

import android.support.v4.app.Fragment;

import org.taitasciore.android.hospitalk.BaseView;

/**
 * Created by roberto on 19/04/17.
 */

public class AttachPresenterEvent {

    public BaseView fragment;

    public AttachPresenterEvent(BaseView fragment) {
        this.fragment = fragment;
    }
}
