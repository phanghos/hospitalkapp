package org.taitasciore.android.hospitalk.dialog;

import org.taitasciore.android.hospitalk.BasePresenter;
import org.taitasciore.android.hospitalk.BaseView;
import org.taitasciore.android.model.NewContact;

/**
 * Created by roberto on 30/04/17.
 */

public interface ContactDialogContract {

    interface View extends BaseView<Presenter> {

        void showProgress();
        void hideProgress();
        void showNameError();
        void showEmailError();
        void showPhoneError();
        void showCommentError();
    }

    interface Presenter extends BasePresenter<View> {

        boolean validate(NewContact contact);
        void contact(NewContact contact);
    }
}
