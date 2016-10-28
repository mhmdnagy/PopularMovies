package com.vezikon.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vezikon on 8/19/15.
 */
public class Trailer implements Parcelable {


    private String id;
    private String source;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.source);
        dest.writeString(this.name);
    }

    public Trailer() {
    }

    protected Trailer(Parcel in) {
        this.id = in.readString();
        this.source = in.readString();
        this.name = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
