package org.taitasciore.android.hospitalk.menu;

import android.util.Log;

import org.taitasciore.android.network.HospitalkService;

/**
 * Created by roberto on 18/04/17.
 */

public class MenuPresenter implements MenuContract.Presenter {

    private MenuContract.View mView;
    private HospitalkService mService;

    public MenuPresenter(MenuContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(MenuContract.View view) {
        Log.i("binding view", (view == null)+"");
        this.mView = view;
        mView.bindPresenter(this);
    }

    @Override
    public void unbindView() {
        mView = null;
    }
}
