package com.shawnrmoss.popularmovies;

import java.util.ArrayList;
/**
 * Created by smoss on 8/24/2015.
 */
public class Movie {

    private int id;
    private String title;
    private String release_date;
    private String movie_poster;
    private double vote_average;
    private String plot_synopsis;

    public Movie(int id) {
        this.id = id;
    }

    public Movie(int id, String title, String release_date, String movie_poster, double vote_average, String plot_synopsis) {
        this.id = id;
        this.title = title;
        this.release_date = release_date;
        this.movie_poster = movie_poster;
        this.vote_average = vote_average;
        this.plot_synopsis = plot_synopsis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getMovie_poster() {
        return movie_poster;
    }

    public void setMovie_poster(String movie_poster) {
        this.movie_poster = movie_poster;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getPlot_synopsis() {
        return plot_synopsis;
    }

    public void setPlot_synopsis(String plot_synopsis) {
        this.plot_synopsis = plot_synopsis;
    }
}
