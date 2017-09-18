package com.dan.hotpopcorn.retrofit;

import com.dan.hotpopcorn.BuildConfig;
import com.dan.hotpopcorn.model.Movie;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Dat T Do on 9/15/2017.
 */

public interface MovieAPI {

    @GET("/movie/popular")
    void getPopularMovies(Callback<Movie.MovieResult> cb);
    @GET("/movie/top_rated")
    void  getRatedMovies(Callback<Movie.MovieResult> cb);


    class MovieClient
    {
        private MovieAPI movieAPI;
        public MovieAPI getMovieAPI()
        {
            return movieAPI;
        }
    }



}
