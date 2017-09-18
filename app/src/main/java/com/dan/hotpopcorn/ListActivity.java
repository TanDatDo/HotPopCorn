package com.dan.hotpopcorn;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dan.hotpopcorn.adapter.MovieAdapter;
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

public class ListActivity extends AppCompatActivity {

    public static final String LOG_TAG = ListActivity.class.getName();
    private MovieAdapter mRatedAdapter;
    private MovieAdapter mPopularAdapter;
    List<Movie> popularList;
    List<Movie> ratedList;
    private Movie movie;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    String SORT_BY = "POPULAR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mPopularAdapter = new MovieAdapter(this);
        mRatedAdapter = new MovieAdapter(this);
        final List<Movie> movies = new ArrayList<>();

        //set the grid view/ recycle view with either the popular list or rated list
        setupRecyclerView();

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

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
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
        }
        item.setChecked(true);
        return super.onOptionsItemSelected(item);
    }

    //check if the network connection is available
    private boolean isNetworkAvailable() {
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
        }
    }
}
