package org.taitasciore.android.hospitalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by roberto on 19/04/17.
 */

public final class StarUtils {

    public static void addStars(Context context, int number, LinearLayout layout) {
        for (int i = 0; i < number; i++) {
            addStar(context, layout);
        }
    }

    private static void addStar(Context context, LinearLayout layout) {
        ImageView star = (ImageView)
                LayoutInflater.from(context).inflate(R.layout.star, layout, false);
        star.setImageResource(R.drawable.star_grey);
        layout.addView(star);
    }

    public static void fillStars(Context context, int number, LinearLayout layout) {
        for (int i = 0; i < number; i++) {
            ImageView star = (ImageView) layout.getChildAt(i);
            star.setImageResource(R.drawable.star_blue);
        }
    }

    public static void resetStars(LinearLayout layout) {
        layout.removeAllViews();
    }
}
