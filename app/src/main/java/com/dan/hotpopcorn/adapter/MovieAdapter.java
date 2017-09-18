package com.dan.hotpopcorn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dan.hotpopcorn.ListActivity;
import com.dan.hotpopcorn.model.Movie;
import com.dan.hotpopcorn.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dat T Do on 9/15/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<ListActivity.MovieViewHolder>
{
    private List<Movie> mMovieList;
    private LayoutInflater mInflater;
    private Context mContext;

    public MovieAdapter(Context context)
    {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mMovieList = new ArrayList<>();
    }

    @Override
    public ListActivity.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.image, parent, false);
        ListActivity.MovieViewHolder viewHolder = new ListActivity.MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListActivity.MovieViewHolder holder, int position)
    {
        Movie movie = mMovieList.get(position);

        // This is how we use Picasso to load images from the internet.
        Picasso.with(mContext)
                .load(movie.getPoster())
                .placeholder(R.color.colorAccent)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount()
    {
        return (mMovieList == null) ? 0 : mMovieList.size();
    }

    public void setMovieList(List<Movie> movieList)
    {
        this.mMovieList.clear();
        this.mMovieList.addAll(movieList);
        // The adapter needs to know that the data has changed. If we don't call this, app will crash.
        notifyDataSetChanged();
    }


}