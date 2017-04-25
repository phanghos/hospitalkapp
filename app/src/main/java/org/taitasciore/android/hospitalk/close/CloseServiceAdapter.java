package org.taitasciore.android.hospitalk.close;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.service.ServiceDetailsFragment;
import org.taitasciore.android.model.ServiceResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 19/04/17.
 */

public class CloseServiceAdapter extends RecyclerView.Adapter<CloseServiceAdapter.ServiceVH> {

    private final Activity mContext;
    private final ArrayList<ServiceResponse.Service> mServices;

    public CloseServiceAdapter(Activity context, ArrayList<ServiceResponse.Service> services) {
        mContext = context;
        mServices = services;
    }

    @Override
    public ServiceVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.close_service_row_layout, parent, false);
        return new ServiceVH(v);
    }

    @Override
    public void onBindViewHolder(ServiceVH holder, int position) {
        final ServiceResponse.Service s = mServices.get(position);
        holder.serviceName.setText(s.getServiceName());
        holder.hospitalName.setText(s.getCompanyName());
        holder.reviews.setText(s.getOpinions() + " opiniones");

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

        @BindView(R.id.tvNombreServicio) TextView serviceName;
        @BindView(R.id.tvNombreHospital) TextView hospitalName;
        @BindView(R.id.tvOpiniones) TextView reviews;
        @BindView(R.id.btnVer) Button details;

        public ServiceVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
