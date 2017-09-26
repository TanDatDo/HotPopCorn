package com.dan.hotpopcorn.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Trailer Objects
 */
public class Trailer implements Parcelable {


    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
    private String id;
    private String key;

    protected Trailer(Parcel in) {
        id = in.readString();
        key = in.readString();
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(key);
    }

    public static class TrailerResult {
        private List<Trailer> results;

        public List<Trailer> getResults() {
            return results;
        }
    }
}
