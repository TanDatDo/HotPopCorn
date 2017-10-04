package com.dan.hotpopcorn;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dan.hotpopcorn.adapter.MovieAdapter;
import com.dan.hotpopcorn.adapter.OfflineFavoriteAdapter;
import com.dan.hotpopcorn.database.MovieContract;
import com.dan.hotpopcorn.model.Movie;
import com.dan.hotpopcorn.retrofit.MovieAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.view.View.GONE;

public class ListActivity extends AppCompatActivity {

    public static final String LOG_TAG = ListActivity.class.getName();
    List<Movie> popularList;
    List<Movie> ratedList;
    List<Movie> favoriteList;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.empty_image_view)
    ImageView emptyImageView;
    String SORT_BY = "POPULAR";
    private MovieAdapter mRatedAdapter;
    private MovieAdapter mPopularAdapter;
    private MovieAdapter mFavoriteAdapter;
    private OfflineFavoriteAdapter mOfflineFavoriteAdapter;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mPopularAdapter = new MovieAdapter(this);
        mRatedAdapter = new MovieAdapter(this);
        mFavoriteAdapter = new MovieAdapter(this);
        mOfflineFavoriteAdapter = new OfflineFavoriteAdapter(this);

        //when the screen is rotated
        if (savedInstanceState != null) {
            //when there is data stored in saveInstanceState
            //get the current SORT_BY ò menu selected
            SORT_BY = savedInstanceState.getString("SORT_BY");
            switch (SORT_BY) {
                case "POPULAR":
                    popularList = savedInstanceState.getParcelableArrayList("POPULAR");
                    if (isNetworkAvailable()) {
                        emptyImageView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mPopularAdapter.setMovieList(popularList);
                        mRecyclerView.setAdapter(mPopularAdapter);
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        emptyImageView.setVisibility(View.VISIBLE);
                    }
                    break;
                case "RATED":
                    ratedList = savedInstanceState.getParcelableArrayList("RATED");
                    if (isNetworkAvailable()) {
                        emptyImageView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mRatedAdapter.setMovieList(ratedList);
                        mRecyclerView.setAdapter(mRatedAdapter);
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        emptyImageView.setVisibility(View.VISIBLE);
                    }
                    break;
                case "FAVORITE":
                    favoriteList = savedInstanceState.getParcelableArrayList("FAVORITE");
                    if (isNetworkAvailable()) {
                        emptyImageView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mFavoriteAdapter.setMovieList(favoriteList);
                        mRecyclerView.setAdapter(mFavoriteAdapter);
                    } else {
                        emptyImageView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mOfflineFavoriteAdapter.setMovieList(favoriteList);
                        mRecyclerView.setAdapter(mOfflineFavoriteAdapter);
                    }
            }

        } else {
            //load new data when there is no stored data in onSaveInstanceState
            setupRecyclerView();
        }


        //set the grid view/ recycle view with either the popular list or rated list

        //add listener to each item in the grid view. navigate to detail activity when item is clicked
        mRecyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (SORT_BY) {
                    case "POPULAR":
                        movie = popularList.get(position);
                        break;
                    case "RATED":
                        movie = ratedList.get(position);
                        break;
                    case "FAVORITE":
                        movie = favoriteList.get(position);
                }
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                Bundle arguments = new Bundle();
                arguments.putParcelable("movie", movie);
                //store the data of that 'movie' into the intent
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        }));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saving data when orientation change trigger
        //saving data before onDestroy happens
        //onSaveInstance is called before going to another activity or orientation change
        outState.putParcelableArrayList("RATED", (ArrayList<? extends Parcelable>) ratedList);
        outState.putParcelableArrayList("POPULAR", (ArrayList<? extends Parcelable>) popularList);
        outState.putParcelableArrayList("FAVORITE", (ArrayList<? extends Parcelable>) favoriteList);
        outState.putString("SORT_BY", SORT_BY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SORT_BY == "FAVORITE") {
            new GetFavorite().execute();
        } else if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            mRecyclerView.setVisibility(View.GONE);
            emptyImageView.setVisibility(View.VISIBLE);
        }
    }


    // options menu allowed user to sort movie by either popularity or rating
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        switch (SORT_BY) {
            case "POPULAR":
                menu.findItem(R.id.action_sort_by_popularity).setChecked(true);
                break;
            case "RATED":
                menu.findItem(R.id.action_sort_by_rating).setChecked(true);
                break;
            case "FAVORITE":
                menu.findItem(R.id.action_sort_by_favorite).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort_by_popularity:
                SORT_BY = "POPULAR";
                setupRecyclerView();
                break;
            case R.id.action_sort_by_rating:
                SORT_BY = "RATED";
                setupRecyclerView();
                break;
            case R.id.action_sort_by_favorite:
                SORT_BY = "FAVORITE";
                setupRecyclerView();
                break;
        }
        item.setChecked(true);
        return super.onOptionsItemSelected(item);
    }

    //check if the network connection is available
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //method to set up RecyclerView
    public void setupRecyclerView() {
        //build the retrofit interface to make http request
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        request.addEncodedQueryParam("api_key", BuildConfig.API_KEY);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        MovieAPI service = restAdapter.create(MovieAPI.class);


        switch (SORT_BY) {
            case "POPULAR":
                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                    mRecyclerView.setVisibility(GONE);
                    emptyImageView.setVisibility(View.VISIBLE);
                    return;
                }
                mRecyclerView.setVisibility(View.VISIBLE);
                emptyImageView.setVisibility(GONE);
                //when sort_by_popularity is selected
                service.getPopularMovies(new Callback<Movie.MovieResult>() {
                    @Override
                    public void success(Movie.MovieResult movieResult, Response response) {
                        popularList = movieResult.getResults();
                        mPopularAdapter.setMovieList(popularList);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
                mRecyclerView.setAdapter(mPopularAdapter);
                break;


            case "RATED":
                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                    mRecyclerView.setVisibility(GONE);
                    emptyImageView.setVisibility(View.VISIBLE);
                    return;
                }
                mRecyclerView.setVisibility(View.VISIBLE);
                emptyImageView.setVisibility(GONE);
                //when sort_by_rate is selected
                service.getRatedMovies(new Callback<Movie.MovieResult>() {
                    @Override
                    public void success(Movie.MovieResult movieResult, Response response) {
                        ratedList = movieResult.getResults();
                        mRatedAdapter.setMovieList(ratedList);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
                mRecyclerView.setAdapter(mRatedAdapter);
                break;

            case "FAVORITE":
                //when sort_by_favorite is selected
                new GetFavorite().execute();
                break;
        }
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    // get favorite movies from databse and add them into favoriteList
    // set Recycler view with Favorite List
    // if connection available, use mFavoriteAdapter
    // if not, use OfflineFavoriteAdapter
    public class GetFavorite extends AsyncTask<Object, Object, Cursor> {
        @Override
        protected Cursor doInBackground(Object... voids) {
            String[] projection = {
                    MovieContract.MovieEntry._ID,
                    MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL,
                    MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_URL,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                    MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                    MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                    MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP,
                    MovieContract.MovieEntry.COLUMN_MOVIE_DATE_RELEASE,
                    MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                    MovieContract.MovieEntry.COLUMN_MOVIE_POSTER};
            Cursor cursor = getApplicationContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    projection,   // projection
                    null, // selection
                    null,   // selectionArgs
                    null    // sort order
            );
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor data) {
            // Bail early if the cursor is null or there is less than 1 row in the cursor
            int numRows = data.getCount();
            if (data == null || numRows < 1) {
                mRecyclerView.setVisibility(GONE);
                emptyImageView.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.no_favorite), Toast.LENGTH_SHORT).show();
                data.close();
                return;
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyImageView.setVisibility(GONE);
            favoriteList = new ArrayList<Movie>();

            // Proceed with moving to the first row of the cursor and reading data from it
            // (This should be the only row in the cursor)
            try {
                while (data.moveToNext()) {
                    // Find the columns that we're interested in
                    int idColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                    int titleColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
                    int backdropUrlColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_URL);
                    int posterUrlColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL);
                    int releaseColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DATE_RELEASE);
                    int overviewColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
                    int ratingColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE);

                    // Extract out the value from the Cursor for the given column index
                    String id = data.getString(idColumnIndex);
                    String title = data.getString(titleColumnIndex);
                    String poster_url = data.getString(posterUrlColumnIndex);
                    String backdrop_url = data.getString(backdropUrlColumnIndex);
                    String release_date = data.getString(releaseColumnIndex);
                    String overview = data.getString(overviewColumnIndex);
                    Double rating = data.getDouble(ratingColumnIndex);

                    // Update the views on the screen with the values from the database
                    Movie newMovie = new Movie();
                    newMovie.setId(id);
                    newMovie.setBackdrop(backdrop_url);
                    newMovie.setTitle(title);
                    newMovie.setPoster(poster_url);
                    newMovie.setRating(rating);
                    newMovie.setOverview(overview);
                    newMovie.setReleaseDate(release_date);
                    favoriteList.add(newMovie);
                }
            } finally {
                data.close();
            }
            if (isNetworkAvailable()) {
                mFavoriteAdapter.setMovieList(favoriteList);
                mRecyclerView.setAdapter(mFavoriteAdapter);
            } else {
                mOfflineFavoriteAdapter.setMovieList(favoriteList);
                Toast.makeText(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                mRecyclerView.setAdapter(mOfflineFavoriteAdapter);
            }
        }
    }
}






