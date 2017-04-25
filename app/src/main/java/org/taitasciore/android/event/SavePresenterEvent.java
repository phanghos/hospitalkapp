package org.taitasciore.android.event;

import org.taitasciore.android.hospitalk.BasePresenter;

/**
 * Created by roberto on 19/04/17.
 */

public class SavePresenterEvent {

    public BasePresenter presenter;

    public SavePresenterEvent(BasePresenter presenter) {
        this.presenter = presenter;
    }
}
