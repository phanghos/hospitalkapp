package org.taitasciore.android.hospitalk.service;

import android.app.Activity;
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
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 20/04/17.
 */

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceVH> {

    private final Activity mContext;
    private final ArrayList<ServiceResponse.Service> mServices;

    public ServiceAdapter(Activity context, ArrayList<ServiceResponse.Service> services) {
        mContext = context;
        mServices = services;
    }

    @Override
    public ServiceVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_row_layout, parent, false);
        return new ServiceVH(v);
    }

    @Override
    public void onBindViewHolder(ServiceVH holder, int position) {
        final ServiceResponse.Service s = mServices.get(position);
        holder.serviceName.setText(s.getServiceName());
        holder.activityName.setText(s.getActivityName());
        holder.reviews.setText(s.getOpinions() + " opiniones");
        StarUtils.resetStars(holder.avg);
        StarUtils.addStars(mContext, 10, holder.avg);
        StarUtils.fillStars(mContext, s.getAverage(), holder.avg);

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceDetailsFragment f = ServiceDetailsFragment.newInstance(s.getIdCompaniesServices());

                ((AppCompatActivity) mContext).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.container, f)
                        .addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mServices.size();
    }

    static class ServiceVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tvServiceName) TextView serviceName;
        @BindView(R.id.tvActivityName) TextView activityName;
        @BindView(R.id.tvOpiniones) TextView reviews;
        @BindView(R.id.btnVerMas) Button details;
        @BindView(R.id.lyAvg) LinearLayout avg;

        public ServiceVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
