package org.taitasciore.android.hospitalk.dialog;

import android.util.Log;

import org.taitasciore.android.model.FooterContent;
import org.taitasciore.android.network.HospitalkService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by roberto on 30/04/17.
 */

public class FooterDialogPresenter implements FooterDialogContract.Presenter {

    private FooterDialogContract.View mView;
    private HospitalkService mService;

    public FooterDialogPresenter(FooterDialogContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(FooterDialogContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void getFooterContent(int pageId) {
        Log.i("debug", "fetching content...");

        mView.showProgress();

        mService.getFooterContent(pageId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<FooterContent>>() {
                    @Override
                    public void onNext(Response<FooterContent> response) {
                        Log.i("result code", response.code()+"");

                        if (mView != null) {
                            mView.hideProgress();

                            if (response.isSuccessful()) {
                                mView.showFooterContent(response.body().getContent());
                            } else {
                                mView.showNetworkFailedError();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showNetworkError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
