package org.taitasciore.android.hospitalk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.model.NewContact;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 30/04/17.
 */

public class ContactDialogFragment extends DialogFragment implements ContactDialogContract.View {

    private ContactDialogContract.Presenter mPresenter;

    private ArrayAdapter<String> mAdapterType;

    @BindView(R.id.spType) Spinner mSpType;
    @BindView(R.id.etName) EditText mEtName;
    @BindView(R.id.etMail) EditText mEtEmail;
    @BindView(R.id.etCompany) EditText mEtCompany;
    @BindView(R.id.etPhone) EditText mEtPhone;
    @BindView(R.id.etComment) EditText mEtComment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new ContactDialogPresenter(this);
        else mPresenter.bindView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPresenter != null) mPresenter.unbindView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .theme(Theme.LIGHT)
                .title(R.string.formulario_contacto)
                .customView(R.layout.dialog_contact, true)
                .positiveText("enviar")
                .negativeText("cancelar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        NewContact contact = new NewContact();
                        contact.setName(mEtName.getText().toString());
                        contact.setEmail(mEtEmail.getText().toString());
                        contact.setComment(mEtCompany.getText().toString());
                        contact.setPhone(mEtPhone.getText().toString());
                        contact.setComment(mEtComment.getText().toString());
                        mPresenter.contact(contact);
                    }
                });

        MaterialDialog d = builder.build();
        View v = d.getCustomView();
        ButterKnife.bind(this, v);

        String[] items = new String[]{getResources().getString(R.string.empresa),
                getResources().getString(R.string.usuario)};
        mAdapterType = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, items);
        mAdapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpType.setAdapter(mAdapterType);

        return d;
    }

    @Override
    public void bindPresenter(ContactDialogContract.Presenter presenter) {
        mPresenter = presenter;
        mPresenter.bindView(this);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showNameError() {
        mEtName.setError(getString(R.string.signup_error));
    }

    @Override
    public void showEmailError() {
        mEtEmail.setError(getString(R.string.signup_error));
    }

    @Override
    public void showEmailFormatError() {
        mEtEmail.setError(getString(R.string.email_format_error));
    }

    @Override
    public void showPhoneError() {
        mEtPhone.setError(getString(R.string.signup_error));
    }

    @Override
    public void showPhoneLengthError() {
        mEtPhone.setError("El campo debe tener al menos 9 caracteres");
    }

    @Override
    public void showCommentError() {
        mEtComment.setError(getString(R.string.signup_error));
    }
}
