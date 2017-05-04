package org.taitasciore.android.hospitalk;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import org.greenrobot.eventbus.EventBus;
import org.taitasciore.android.event.SetDateEvent;

import java.util.Calendar;

/**
 * Created by roberto on 03/05/17.
 */

public class DatePickerDialogFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    private int year;
    private int month;
    private int day;

    public static DatePickerDialogFragment newInstance(String date) {
        String[] splitDate = date.split("-");

        DatePickerDialogFragment f = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt("year", Integer.parseInt(splitDate[2]));
        args.putInt("month", Integer.parseInt(splitDate[1]));
        args.putInt("day", Integer.parseInt(splitDate[0]));
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            Bundle args = getArguments();

            year = args.getInt("year");
            month = args.getInt("month") - 1;
            day = args.getInt("day");
        } else {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        EventBus.getDefault().post(new SetDateEvent(year, month + 1, day));
    }
}
