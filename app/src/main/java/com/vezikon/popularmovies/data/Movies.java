package com.vezikon.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vezikon on 7/11/15.
 */
public class Movies implements Parcelable {
    private Integer page;
    private List<Movie> results = new ArrayList<Movie>();
    private Integer totalPages;
    private Integer totalResults;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.page);
        dest.writeList(this.results);
        dest.writeValue(this.totalPages);
        dest.writeValue(this.totalResults);
    }

    public Movies() {
    }

    protected Movies(Parcel in) {
        this.page = (Integer) in.readValue(Integer.class.getClassLoader());
        this.results = new ArrayList<Movie>();
        in.readList(this.results, List.class.getClassLoader());
        this.totalPages = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalResults = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        public Movies createFromParcel(Parcel source) {
            return new Movies(source);
        }

        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
}
