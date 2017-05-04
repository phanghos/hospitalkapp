package org.taitasciore.android.hospitalk.review;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.taitasciore.android.event.LoadMoreReviewsEvent;
import org.taitasciore.android.hospitalk.StarUtils;
import org.taitasciore.android.model.Review;
import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.network.HospitalkApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by roberto on 19/04/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_REVIEW = 1;

    private final Activity mContext;
    private final ArrayList<Review> mReviews;

    public ReviewAdapter(Activity context) {
        mContext = context;
        mReviews = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        switch (viewType) {
            case TYPE_REVIEW:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.review_row_layout, parent, false);
                return new ReviewVH(v);
            case TYPE_HEADER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.load_more_row_layout, parent, false);
                return new LoadMoreVH(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        if (vh.getItemViewType() == TYPE_REVIEW) {
            ReviewVH holder = (ReviewVH) vh;
            final Review r = mReviews.get(position);

            if (r.getRatingTitle() != null && !r.getRatingTitle().isEmpty()) {
                holder.title.setText(r.getRatingTitle());
            } else {
                holder.title.setText("Título de opinión no disponible");
            }

            holder.username.setText(r.getUserName());
            holder.location.setText(r.getCityName() + ", " + r.getCountryName());
            holder.date.setText("Hace " + r.getDays() + " días");
            holder.hospital.setText(r.getCompanyName());
            holder.service.setText(r.getServiceName());
            holder.review.setText(r.getRatingReview());
            StarUtils.resetStars(holder.avg);
            StarUtils.addStars(mContext, 5, holder.avg);
            StarUtils.fillStars(mContext, r.getRatingValue(), holder.avg);

            String imgUrl = HospitalkApi.BASE_URL + "user/image?id_user=" + r.getIdUser();
            holder.img.setImageURI(Uri.parse(imgUrl));

            if (r.getCompanyAnswer() > 0) {
                holder.respuesta.setText("SÍ");
            } else {
                holder.respuesta.setText("NO");
            }
            setRepliesButtonBackground(holder.respuesta, r.getCompanyAnswer());

            holder.votos.setText(r.getRatingHelpfulVotes() + "");

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
    }

    @Override
    public int getItemCount() {
        return mReviews.size() > 0 ? mReviews.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position < mReviews.size() ? TYPE_REVIEW : TYPE_HEADER;
    }

    public void add(Review r) {
        mReviews.add(r);
        notifyItemInserted(getItemCount());
    }

    public ArrayList<Review> getList() {
        return mReviews;
    }

    private void setRepliesButtonBackground(Button btn, int replies) {
        int bgColor;

        if (replies > 0) {
            bgColor = ContextCompat.getColor(mContext, R.color.colorPrimary);
        } else {
            bgColor = ContextCompat.getColor(mContext, R.color.colorPrimaryText);
        }

        btn.setBackgroundColor(bgColor);
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

    static class LoadMoreVH extends RecyclerView.ViewHolder {

        @OnClick(R.id.tvCargarMas) void onLoadMoreClicked() {
            EventBus.getDefault().post(new LoadMoreReviewsEvent());
        }

        public LoadMoreVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
