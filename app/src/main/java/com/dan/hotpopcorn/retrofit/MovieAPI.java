package com.dan.hotpopcorn.retrofit;

import com.dan.hotpopcorn.model.Movie;
import com.dan.hotpopcorn.model.Review;
import com.dan.hotpopcorn.model.Trailer;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Dat T Do on 9/15/2017.
 */

public interface MovieAPI {

    @GET("/movie/popular")
    void getPopularMovies(Callback<Movie.MovieResult> cb);

    @GET("/movie/top_rated")
    void  getRatedMovies(Callback<Movie.MovieResult> cb);

    @GET("/movie/{id}/videos")
    void getTrailers(@Path("id") String id, Callback<Trailer.TrailerResult> cb);

    @GET("/movie/{id}/reviews")
    void getReviews(@Path("id") String id, Callback<Review.ReviewResult> cb);

    class MovieClient
    {
        private MovieAPI movieAPI;
        public MovieAPI getMovieAPI()
        {
            return movieAPI;
        }
    }
}
