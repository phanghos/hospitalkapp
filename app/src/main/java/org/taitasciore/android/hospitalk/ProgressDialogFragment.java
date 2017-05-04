package org.taitasciore.android.hospitalk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by roberto on 29/04/17.
 */

public class ProgressDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Espere un momento...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        return pd;
    }
}
