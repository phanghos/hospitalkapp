package org.taitasciore.android.hospitalk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.taitasciore.android.hospitalk.R;

/**
 * Created by roberto on 30/04/17.
 */

public class FooterDialogFragment extends DialogFragment implements FooterDialogContract.View {

    private FooterDialogContract.Presenter mPresenter;

    private MaterialDialog mDialog;

    public static FooterDialogFragment newInstance(String title, int page) {
        FooterDialogFragment f = new FooterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("page", page);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPresenter == null) mPresenter = new FooterDialogPresenter(this);
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
                .title(getArguments().getString("title"))
                .positiveText("aceptar");

        mDialog = builder.show();

        mPresenter.getFooterContent(getArguments().getInt("page"));

        return mDialog;
    }

    @Override
    public void bindPresenter(FooterDialogContract.Presenter presenter) {
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
    public void showFooterContent(String content) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.setContent(Html.fromHtml(content));
        }
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void showNetworkFailedError() {
        Toast.makeText(getActivity(), getString(R.string.network_failed_error), Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
