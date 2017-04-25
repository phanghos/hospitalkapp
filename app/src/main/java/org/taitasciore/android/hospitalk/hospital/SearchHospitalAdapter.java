package org.taitasciore.android.hospitalk.hospital;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.hospitalk.StarUtils;
import org.taitasciore.android.model.HighlightService;
import org.taitasciore.android.model.Hospital;
import org.taitasciore.android.network.HospitalkApi;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 24/04/17.
 */

public class SearchHospitalAdapter extends RecyclerView.Adapter<SearchHospitalAdapter.SearchHospitalVH> {

    private final Activity mContext;
    private final ArrayList<Hospital> mHospitals;

    public SearchHospitalAdapter(Activity context, ArrayList<Hospital> hospitals) {
        mContext = context;
        mHospitals = hospitals;
    }

    public SearchHospitalAdapter(Activity context) {
        this(context, new ArrayList<Hospital>());
    }

    @Override
    public SearchHospitalVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_hospital_row_layout, parent, false);
        return new SearchHospitalVH(v);
    }

    @Override
    public void onBindViewHolder(SearchHospitalVH holder, int position) {
        final Hospital h = mHospitals.get(position);
        holder.nameAndCity.setText(h.getCompanyName() + " - " + h.getCityName());
        holder.activityName.setText(h.getActivityName());
        holder.reviews.setText(h.getCompanyOpinions() + " opiniones");

        String imgUrl = HospitalkApi.BASE_URL + "company/logo?id_company=" + h.getIdCompany();
        holder.img.setImageURI(Uri.parse(imgUrl));

        StarUtils.resetStars(holder.avg);
        StarUtils.addStars(mContext, 10, holder.avg);
        StarUtils.fillStars(mContext, h.getAverage(), holder.avg);

        holder.highlightServices.removeAllViews();
        for (HighlightService service : h.getHighlightServices()) {
            addHighlightService(holder.highlightServices, service);
        }

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

    public void add(Hospital h) {
        mHospitals.add(h);
        notifyItemInserted(getItemCount());
    }

    public ArrayList<Hospital> getList() {
        return mHospitals;
    }

    private void addHighlightService(LinearLayout ly, HighlightService service) {
        TextView tv = new TextView(mContext);
        tv.setText("-> " + service.getServiceName() + " - " + service.getOpinions());
        ly.addView(tv);
    }

    static class SearchHospitalVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tvNameAndCity) TextView nameAndCity;
        @BindView(R.id.tvActivityName) TextView activityName;
        @BindView(R.id.tvReviews) TextView reviews;
        @BindView(R.id.btnDetails) Button details;
        @BindView(R.id.lyAvg) LinearLayout avg;
        @BindView(R.id.lyHighlightServices) LinearLayout highlightServices;
        @BindView(R.id.img) SimpleDraweeView img;

        public SearchHospitalVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
