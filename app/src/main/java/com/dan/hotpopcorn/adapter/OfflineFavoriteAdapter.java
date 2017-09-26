package com.dan.hotpopcorn.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dan.hotpopcorn.DBBitmapUtility;
import com.dan.hotpopcorn.ListActivity;
import com.dan.hotpopcorn.R;
import com.dan.hotpopcorn.database.MovieContract;
import com.dan.hotpopcorn.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * this class helps adapt favorite movie with Grid View in ListActivity
 */

public class OfflineFavoriteAdapter extends RecyclerView.Adapter<ListActivity.MovieViewHolder> {
    private List<Movie> mMovieList;
    private LayoutInflater mInflater;
    private Context mContext;

    public OfflineFavoriteAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mMovieList = new ArrayList<>();
    }

    @Override
    public ListActivity.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.image, parent, false);
        ListActivity.MovieViewHolder viewHolder = new ListActivity.MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ListActivity.MovieViewHolder holder, int position) {
        final Movie movie = mMovieList.get(position);
        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(Void... voids) {
                String[] projection = {
                        MovieContract.MovieEntry.COLUMN_MOVIE_POSTER};
                Cursor cursor = mContext.getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,   // projection
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", // selection
                        new String[]{movie.getId()},   // selectionArgs
                        null    // sort order
                );
                return cursor;
            }

            @Override
            protected void onPostExecute(Cursor data) {
                int numRows = data.getCount();
                if (data == null || numRows < 1) {
                    return;
                }
                data.moveToFirst();
                int posterColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
                byte[] posterByteArray = data.getBlob(posterColumnIndex);
                holder.imageView.setImageBitmap(DBBitmapUtility.getImage(posterByteArray));
                data.close();
            }
        }.execute();
    }


    @Override
    public int getItemCount() {
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