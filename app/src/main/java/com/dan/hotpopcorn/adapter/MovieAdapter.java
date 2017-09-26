package com.dan.hotpopcorn.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dan.hotpopcorn.ListActivity;
import com.dan.hotpopcorn.R;
import com.dan.hotpopcorn.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.dan.hotpopcorn.model.Movie.TMDB_IMAGE_PATH;

/**
 * this class helps adapt Movie objetcs with grid layout in ListActivity
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
        if (isNetworkAvailable()) {
        // This is how we use Picasso to load images from the internet.
        Picasso.with(mContext)
                .load(TMDB_IMAGE_PATH + movie.getPoster())
                .placeholder(R.color.colorAccent)
                .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount()
    {
        return (mMovieList == null) ? 0 : mMovieList.size();
    }

    public void setMovieList(List<Movie> movieList) {
        this.mMovieList.clear();
        this.mMovieList.addAll(movieList);
        // The adapter needs to know that the data has changed. If we don't call this, app will crash.
        notifyDataSetChanged();
    }

    //check if the network connection is available
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}