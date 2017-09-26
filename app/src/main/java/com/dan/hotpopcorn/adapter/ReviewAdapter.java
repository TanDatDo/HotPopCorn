package com.dan.hotpopcorn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dan.hotpopcorn.R;
import com.dan.hotpopcorn.model.Review;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * this class helps adapt Review objects in DetailAcitivity
 */
public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Review> data = new ArrayList<>();


    public ReviewAdapter(Context context, ArrayList<Review> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.review_list_item, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ((MyItemHolder) holder).authorText.setText(data.get(position).getAuthor());
        ((MyItemHolder) holder).reviewText.setText(data.get(position).getcontent());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.authorText)
        TextView authorText;
        @Bind(R.id.reviewText)
        TextView reviewText;


        public MyItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }


}