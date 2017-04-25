package org.taitasciore.android.hospitalk;

/**
 * Created by roberto on 18/04/17.
 */

public interface BasePresenter<V extends BaseView> {

    void bindView(V view);
    void unbindView();
}
