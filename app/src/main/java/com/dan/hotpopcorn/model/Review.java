package com.dan.hotpopcorn.model;

/**
 * Created by Dat T Do on 9/24/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Review Objects
 */

public class Review implements Parcelable {


    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
    private String id;
    private String content;
    private String author;
    private String url;


    protected Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    public String getId() {
        return id;
    }

    public String getcontent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);


    }

    public static class ReviewResult {
        private List<Review> results;

        public List<Review> getResults() {
            return results;
        }
    }
}