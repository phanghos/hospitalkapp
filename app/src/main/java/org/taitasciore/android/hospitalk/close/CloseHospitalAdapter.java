package org.taitasciore.android.hospitalk.close;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private final Activity mContext;
    private final ArrayList<Hospital> mHospitals;

    public CloseHospitalAdapter(Activity context, ArrayList<Hospital> hospitals) {
        mContext = context;
        mHospitals = hospitals;
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
        holder.name.setText(h.getComapnyName());

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
    }

    @Override
    public int getItemCount() {
        return mHospitals.size();
    }

    static class HospitalVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tvNombreHospital) TextView name;
        @BindView(R.id.tvTipo) TextView type;
        @BindView(R.id.tvFecha) TextView date;
        @BindView(R.id.btnVer) Button details;
        @BindView(R.id.lyAvg) LinearLayout avg;

        public HospitalVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
