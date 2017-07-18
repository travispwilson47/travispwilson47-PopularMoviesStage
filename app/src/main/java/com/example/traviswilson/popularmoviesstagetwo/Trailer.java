package com.example.traviswilson.popularmoviesstagetwo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by traviswilson on 7/3/17.
 */

public class Trailer implements Parcelable {
    private String youtubeID;
    private String videoTitle;

    public Trailer(String youtubeID, String videoTitle){
        this.youtubeID = youtubeID;
        this.videoTitle = videoTitle;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getYoutubeID() {
        return youtubeID;
    }

    /*
     Parcelable implementation
     */

    private Trailer(Parcel in) {
        youtubeID = in.readString();
        videoTitle = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(youtubeID);
        dest.writeString(videoTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
}