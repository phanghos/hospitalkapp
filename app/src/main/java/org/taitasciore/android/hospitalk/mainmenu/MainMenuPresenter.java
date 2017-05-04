package org.taitasciore.android.hospitalk.mainmenu;

import android.util.Log;

import org.taitasciore.android.network.HospitalkService;

/**
 * Created by roberto on 18/04/17.
 */

public class MainMenuPresenter implements MainMenuContract.Presenter {

    private MainMenuContract.View mView;
    private HospitalkService mService;

    public MainMenuPresenter(MainMenuContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(MainMenuContract.View view) {
        this.mView = view;
        mView.bindPresenter(this);
    }

    @Override
    public void unbindView() {
        mView = null;
    }
}
