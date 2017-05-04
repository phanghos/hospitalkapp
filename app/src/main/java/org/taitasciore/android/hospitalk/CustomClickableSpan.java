package org.taitasciore.android.hospitalk;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by roberto on 30/04/17.
 */

public class CustomClickableSpan extends ClickableSpan {

    private Context mContext;
    private View.OnClickListener mClickListener;

    public CustomClickableSpan(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View view) {
        if (mClickListener != null) mClickListener.onClick(view);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(ContextCompat.getColor(mContext, R.color.colorPrimaryText));
        ds.setUnderlineText(false);
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }
}
