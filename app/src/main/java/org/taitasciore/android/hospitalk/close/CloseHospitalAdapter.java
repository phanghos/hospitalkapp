package org.taitasciore.android.hospitalk.close;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.taitasciore.android.event.LoadMoreBestRatedHospitalsEvent;
import org.taitasciore.android.event.LoadMoreWorstRatedHospitalsEvent;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StarUtils;
import org.taitasciore.android.hospitalk.hospital.HospitalDetailsFragment;
import org.taitasciore.android.model.Hospital;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 19/04/17.
 */

public class CloseHospitalAdapter extends RecyclerView.Adapter<CloseHospitalAdapter.HospitalVH> {

    public static final int TYPE_BEST_RATED = 1;
    public static final int TYPE_WORST_RATED = 2;

    private final Activity mContext;
    private final ArrayList<Hospital> mHospitals;
    private final int mType;

    public CloseHospitalAdapter(Activity context, ArrayList<Hospital> hospitals, int type) {
        mContext = context;
        mHospitals = hospitals;
        mType = type;
    }

    @Override
    public HospitalVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.close_hospital_row_layout, parent, false);
        return new HospitalVH(v);
    }

    @Override
    public void onBindViewHolder(HospitalVH holder, int position) {
        final Hospital h = mHospitals.get(position);
        holder.name.setText(h.getCompanyName());

        int textColor = -1;
        String type = h.getType();
        if (type.equalsIgnoreCase("excelente")) {
            textColor = ContextCompat.getColor(mContext, R.color.colorPrimary);
        } else if (type.equalsIgnoreCase("muy bueno")) {
            textColor = ContextCompat.getColor(mContext, R.color.colorPrimaryDark);
        }
        if (textColor != -1) holder.type.setTextColor(textColor);
        holder.type.setText(type.toUpperCase());

        holder.date.setText("Hace " + h.getDays() + " días");
        StarUtils.addStars(mContext, 10, holder.avg);
        StarUtils.fillStars(mContext, h.getCompanyAverage(), holder.avg);

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HospitalDetailsFragment f = HospitalDetailsFragment.newInstance(h.getIdCompany());
                ((AppCompatActivity) mContext).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.container, f)
                        .addToBackStack(null).commit();
            }
        });

        if (position == getItemCount() - 1) {
            holder.fab.setVisibility(View.VISIBLE);
            holder.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mType == TYPE_BEST_RATED) {
                        EventBus.getDefault().post(new LoadMoreBestRatedHospitalsEvent());
                    } else if (mType == TYPE_WORST_RATED) {
                        EventBus.getDefault().post(new LoadMoreWorstRatedHospitalsEvent());
                    }
                }
            });
        } else {
            holder.fab.setVisibility(View.GONE);
            holder.fab.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mHospitals.size();
    }

    public void add(Hospital h) {
        mHospitals.add(h);
        notifyItemInserted(getItemCount());
        notifyItemRangeChanged(0, getItemCount());
    }

    public ArrayList<Hospital> getList() {
        return mHospitals;
    }

    static class HospitalVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tvNombreHospital) TextView name;
        @BindView(R.id.tvTipo) TextView type;
        @BindView(R.id.tvFecha) TextView date;
        @BindView(R.id.btnVer) Button details;
        @BindView(R.id.lyAvg) LinearLayout avg;
        @BindView(R.id.fab) FloatingActionButton fab;

        public HospitalVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
