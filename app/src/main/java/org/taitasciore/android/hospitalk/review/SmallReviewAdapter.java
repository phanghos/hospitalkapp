package org.taitasciore.android.hospitalk.review;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.taitasciore.android.hospitalk.R;
import org.taitasciore.android.model.Review;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 19/04/17.
 */

public class SmallReviewAdapter extends RecyclerView.Adapter<SmallReviewAdapter.SmallReviewVH> {

    private final Activity mContext;
    private final ArrayList<Review> mReviews;

    public SmallReviewAdapter(Activity context, ArrayList<Review> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    public SmallReviewAdapter(Activity context) {
        this(context, new ArrayList<Review>());
    }

    @Override
    public SmallReviewVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.small_review_row_layout, parent, false);
        return new SmallReviewVH(v);
    }

    @Override
    public void onBindViewHolder(SmallReviewVH holder, int position) {
        final Review r = mReviews.get(position);
        holder.user.setText(r.getUserName());
        holder.review.setText(r.getRatingReview());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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

    public void add(Review r) {
        mReviews.add(r);
        notifyItemInserted(getItemCount());
    }

    static class SmallReviewVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tvUsr) TextView user;
        @BindView(R.id.tvOpinion) TextView review;

        public SmallReviewVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
