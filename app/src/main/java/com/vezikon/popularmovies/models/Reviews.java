package com.vezikon.popularmovies.models;

import java.util.ArrayList;

/**
 * Created by vezikon on 8/19/15.
 */
public class Reviews {

    ArrayList<Review> results = new ArrayList<>();

    public ArrayList<Review> getResults() {
        return results;
    }

    public void setResults(ArrayList<Review> results) {
        this.results = results;
    }
}
