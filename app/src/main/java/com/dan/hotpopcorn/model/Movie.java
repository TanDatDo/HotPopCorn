package com.dan.hotpopcorn.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dat T Do on 9/15/2017.
 */

public class Movie implements Parcelable {
    public static final String TMDB_IMAGE_PATH = "http://image.tmdb.org/t/p/w500";

    private String title;

    @SerializedName("poster_path")
    private String poster;

    @SerializedName("overview")
    private String overview;

    @SerializedName("backdrop_path")
    private String backdrop;

    private String release_date;

    private double vote_average;





    public Movie() {
    }

    public Movie(Movie movie) {
        title = movie.title;
        poster = movie.poster;
        overview = movie.overview;
        backdrop = movie.backdrop;
        release_date= movie.release_date;
        vote_average=movie.vote_average;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(overview);
        dest.writeString(backdrop);
        dest.writeString(release_date);
        dest.writeDouble(vote_average);
        ;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        poster = in.readString();
        overview = in.readString();
        backdrop = in.readString();
        release_date=in.readString();
        vote_average=in.readDouble();
    }

    public static class MovieResult {
        private List<Movie> results;

        public List<Movie> getResults() {
            return results;
        }
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return TMDB_IMAGE_PATH + poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String description) {
        this.overview = overview;
    }

    public String getBackdrop() {
        return TMDB_IMAGE_PATH + backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public void setReleaseDate(String release_date){this.release_date=release_date;}

    public String getReleaseDate(){return release_date;}

    public void setRating(Double vote_average){this.vote_average=vote_average;}

    public double getRating(){return vote_average;}

}