package org.taitasciore.android.hospitalk;

/**
 * Created by roberto on 18/04/17.
 */

public interface BaseView<P extends BasePresenter> {

    void bindPresenter(P presenter);
}
