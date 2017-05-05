package org.taitasciore.android.hospitalk.dialog;

import android.util.Log;

import org.apache.commons.validator.routines.EmailValidator;
import org.taitasciore.android.model.NewContact;
import org.taitasciore.android.network.HospitalkService;

/**
 * Created by roberto on 30/04/17.
 */

public class ContactDialogPresenter implements ContactDialogContract.Presenter {

    private ContactDialogContract.View mView;
    private HospitalkService mService;

    public ContactDialogPresenter(ContactDialogContract.View view) {
        bindView(view);
        mService = new HospitalkService();
    }

    @Override
    public void bindView(ContactDialogContract.View view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public boolean validate(NewContact contact) {
        boolean valid = true;

        if (contact.getName().isEmpty()) {
            valid = false;
            mView.showNameError();
        }
        if (contact.getEmail().isEmpty()) {
            valid = false;
            mView.showEmailError();
        }
        if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
            EmailValidator validator = EmailValidator.getInstance();

            if (!validator.isValid(contact.getEmail())) {
                valid = false;
                mView.showEmailError();
            }
        }
        if (contact.getPhone().isEmpty()) {
            valid = false;
            mView.showPhoneError();
        }
        if (contact.getPhone() != null && contact.getPhone().length() < 9) {
            valid = false;
            mView.showPhoneLengthError();
        }
        if (contact.getComment().isEmpty()) {
            valid = false;
            mView.showCommentError();
        }

        return valid;
    }

    @Override
    public void contact(NewContact contact) {
        if (!validate(contact)) return;

        Log.i("debug", "sending contact form...");
    }
}
