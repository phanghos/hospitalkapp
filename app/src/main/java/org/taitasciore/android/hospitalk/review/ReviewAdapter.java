package org.taitasciore.android.hospitalk.review;

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

import org.taitasciore.android.hospitalk.StarUtils;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.network.HospitalkApi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 19/04/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewVH> {

    private final Activity mContext;
    private final List<Review> mReviews;

    public ReviewAdapter(Activity context, List<Review> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    @Override
    public ReviewVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_row_layout, parent, false);
        return new ReviewVH(v);
    }

    @Override
    public void onBindViewHolder(ReviewVH holder, int position) {
        final Review r = mReviews.get(position);

        holder.title.setText(r.getRatingTitle());
        holder.username.setText(r.getUserName());
        holder.location.setText(r.getCityName() + ", " + r.getCountryName());
        holder.date.setText(r.getDays());
        holder.hospital.setText(r.getCompanyName());
        holder.service.setText(r.getServiceName());
        holder.review.setText(r.getRatingReview());
        StarUtils.resetStars(holder.avg);
        StarUtils.addStars(mContext, 5, holder.avg);
        StarUtils.fillStars(mContext, r.getRatingValue(), holder.avg);

        String imgUrl = HospitalkApi.BASE_URL + "user/image?id_user=" + r.getIdUser();
        holder.img.setImageURI(Uri.parse(imgUrl));

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewDetailsFragment f = ReviewDetailsFragment.newInstance(r.getIdRating());
                ((AppCompatActivity) mContext).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.container, f)
                        .addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    static class ReviewVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle) TextView title;
        @BindView(R.id.tvUsername) TextView username;
        @BindView(R.id.tvLocation) TextView location;
        @BindView(R.id.tvDate) TextView date;
        @BindView(R.id.tvHospital) TextView hospital;
        @BindView(R.id.tvService) TextView service;
        @BindView(R.id.tvReview) TextView review;
        @BindView(R.id.btnDetails) Button details;
        @BindView(R.id.btnRespuesta) Button respuesta;
        @BindView(R.id.btnVotos) Button votos;
        @BindView(R.id.img) SimpleDraweeView img;
        @BindView(R.id.lyAvg) LinearLayout avg;

        public ReviewVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
