package com.dan.hotpopcorn;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dan.hotpopcorn.adapter.ReviewAdapter;
import com.dan.hotpopcorn.adapter.TrailerAdapter;
import com.dan.hotpopcorn.database.MovieContract;
import com.dan.hotpopcorn.model.Movie;
import com.dan.hotpopcorn.model.Review;
import com.dan.hotpopcorn.model.Trailer;
import com.dan.hotpopcorn.retrofit.MovieAPI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.dan.hotpopcorn.ListActivity.LOG_TAG;
import static com.dan.hotpopcorn.model.Movie.TMDB_IMAGE_PATH;

/**
 * detail activity displays details of the Movie objects
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_MOVIE_LOADER = 0;
    Movie movie;
    @Bind(R.id.toolImage)
    ImageView toolImage;
    @Bind(R.id.detail_image_view)
    ImageView posterImage;
    @Bind(R.id.titleView)
    TextView titleView;
    @Bind(R.id.rating)
    TextView ratingTextView;
    @Bind(R.id.overview)
    TextView overviewTextView;
    @Bind(R.id.releaseText)
    TextView releaseTextView;
    @Bind(R.id.add_favorite_fab)
    FloatingActionButton favoriteButton;
    @Bind(R.id.share_fab)
    FloatingActionButton shareButton;
    ArrayList<Trailer> trailerList;
    ArrayList<Review> reviewList;
    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    @Bind(R.id.trailersRecyclerView)
    RecyclerView trailersRecyclerView;
    @Bind(R.id.reviewsRecyclerView)
    RecyclerView reviewsRecyclerView;
    private boolean isFavorite;
    private Bitmap posterBitmap;
    private Bitmap backdropBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        //get data from intent
        movie = getIntent().getParcelableExtra("movie");

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable("movie", movie);
        }

        if (isNetworkAvailable()) {
            //the internet connection available
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(movie.getTitle());
            //set each view with specific text or images
            Picasso.with(this).load(TMDB_IMAGE_PATH + movie.getBackdrop()).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(toolImage);
            Picasso.with(this).load(TMDB_IMAGE_PATH + movie.getPoster()).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(posterImage);
            posterBitmap = ((BitmapDrawable) posterImage.getDrawable()).getBitmap();
            backdropBitmap = ((BitmapDrawable) toolImage.getDrawable()).getBitmap();
            titleView.setText(movie.getTitle());
            ratingTextView.setText(getString(R.string.Rating) + " " + Double.toString(movie.getRating()) + "/10");
            releaseTextView.setText(getString(R.string.release_on) + " " + movie.getReleaseDate());
            overviewTextView.setText(movie.getOverview());
            new FetchTrailers().execute();
            new FetchReviews().execute();

            //add click listener to open the clicked reviews or trailers
            trailersRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getApplicationContext(), new RecyclerClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String url = "https://www.youtube.com/watch?v=".concat(trailerList.get(position).getKey());
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

            }));

            //add click listener to open the clicked reviews or trailers
            reviewsRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getApplicationContext(), new RecyclerClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(reviewList.get(position).getUrl()));
                    startActivity(i);
                }
            }));

        } else {
            // if no internet connection, load the information from the database
            getLoaderManager().initLoader(EXISTING_MOVIE_LOADER, null, this);
        }


        //AsyncTask class to check whether the current movie is favorite (already exists in the database)
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                Cursor cursor = getApplicationContext().getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,   // projection
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", // selection
                        new String[]{movie.getId()},   // selectionArgs
                        null    // sort order
                );
                int numRows = cursor.getCount();
                Log.i(LOG_TAG, "abc " + " " + Integer.toString(numRows));
                cursor.close();
                return numRows;
            }

            @Override
            protected void onPostExecute(Integer numRows) {
                if (numRows == 1) {
                    //the movie already exists in the database
                    isFavorite = true;
                    favoriteButton.setImageResource(R.drawable.delete_fav);
                } else {
                    //the movie not exist in the database
                    isFavorite = false;
                    favoriteButton.setImageResource(R.drawable.add_fav);
                }
            }
        }.execute();


        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movie != null) {
                    if (isFavorite) {
                        //if the movies is favorite, the click will delete the movie from databse
                        new DeleteTheFavMovie().execute();
                    } else {
                        if (isNetworkAvailable()) { //only allow to add the movie when the internet connection available,
                            // or else the app will crash
                            //if the movie is not favorite, the click will add the movie into database
                            new AddTheFavMovie().execute();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        //share information of the current movies through different apps
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, movie.getTitle());
                share.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=".concat(trailerList.get(0).getKey()));
                startActivity(Intent.createChooser(share, "Share Trailer!"));
            }
        });
    }

    /**
     * Create the loader to load the information of the movie from database using the movie id
     * use the movie.getId to get the correct movie
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//         Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP,
                MovieContract.MovieEntry.COLUMN_MOVIE_DATE_RELEASE,
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                MovieContract.MovieEntry.CONTENT_URI,
                projection,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",// argument = movie_id
                new String[]{movie.getId()},// COLUMN_MOVIE_ID = movie.getId
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data.moveToFirst()) {
            // Find the columns that we're interested in
            int titleColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
            int backdropColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP);
            int posterColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
            int releaseColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DATE_RELEASE);
            int overviewColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
            int ratingColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE);

            // Extract out the value from the Cursor for the given column index

            String title = data.getString(titleColumnIndex);
            byte[] backdropByteArray = data.getBlob(backdropColumnIndex);
            byte[] posterByteArray = data.getBlob(posterColumnIndex);
            String release_date = data.getString(releaseColumnIndex);
            String overview = data.getString(overviewColumnIndex);
            Double rating = data.getDouble(ratingColumnIndex);

            // Update the views on the screen with the values from the database
            titleView.setText(title);
            toolImage.setImageBitmap(DBBitmapUtility.getImage(backdropByteArray));
            posterImage.setImageBitmap(DBBitmapUtility.getImage(posterByteArray));
            releaseTextView.setText(release_date);
            overviewTextView.setText(overview);
            ratingTextView.setText(Double.toString(rating));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //check if the network connection is available
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Aysnc task class to delete a favorite movie from the sqlite
    private class DeleteTheFavMovie extends AsyncTask<Object, Object, Integer> {
        @Override
        protected Integer doInBackground(Object... params) {
            return getApplicationContext().getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{movie.getId()});
        }

        @Override
        protected void onPostExecute(Integer numDeleted) {
            favoriteButton.setImageResource(R.drawable.add_fav);
            Toast.makeText(getApplicationContext(), getString(R.string.delete_toast), Toast.LENGTH_SHORT).show();
            isFavorite = false;

        }
    }

    //Aysnc task class to add a favorite movie from the sqlite
    private class AddTheFavMovie extends AsyncTask<Object, Object, Uri> {
        @Override
        protected Uri doInBackground(Object... params) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, DBBitmapUtility.getBytes(posterBitmap));
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP, DBBitmapUtility.getBytes(backdropBitmap));
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getRating());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_DATE_RELEASE, movie.getReleaseDate());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_URL, movie.getBackdrop());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, movie.getPoster());
            return getApplicationContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                    values);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            favoriteButton.setImageResource(R.drawable.delete_fav);
            Toast.makeText(getApplicationContext(), getString(R.string.add_fav_toast), Toast.LENGTH_SHORT).show();
            isFavorite = true;
        }
    }

    // Async task class to fetch the trailers
    private class FetchTrailers extends AsyncTask<Object, Object, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Object... params) {
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

            service.getTrailers(movie.getId(), new Callback<Trailer.TrailerResult>() {
                @Override
                public void success(Trailer.TrailerResult trailerResult, Response response) {
                    trailerList = (ArrayList<Trailer>) trailerResult.getResults();
                    trailerAdapter = new TrailerAdapter(getApplicationContext(), trailerList);
                    LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    trailersRecyclerView.setLayoutManager(trailerLayoutManager);
                    trailersRecyclerView.setAdapter(trailerAdapter);
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
            return null;
        }
    }

    //Async task class to fetch the reviews
    private class FetchReviews extends AsyncTask<Object, Object, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Object... params) {
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

            service.getReviews(movie.getId(), new Callback<Review.ReviewResult>() {
                @Override
                public void success(Review.ReviewResult reviewResult, Response response) {
                    reviewList = (ArrayList<Review>) reviewResult.getResults();
                    reviewAdapter = new ReviewAdapter(getApplicationContext(), reviewList);
                    LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getApplicationContext());
                    reviewsRecyclerView.setLayoutManager(reviewLayoutManager);
                    reviewsRecyclerView.setAdapter(reviewAdapter);

                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
            return null;
        }
    }
}

