package com.vezikon.popularmovies.models;

import java.util.ArrayList;

/**
 * Created by vezikon on 8/19/15.
 */
public class Trailers {

    private ArrayList<Trailer> youtube = new ArrayList<>();

    public ArrayList<Trailer> getYoutube() {
        return youtube;
    }

    public void setYoutube(ArrayList<Trailer> youtube) {
        this.youtube = youtube;
    }
}
