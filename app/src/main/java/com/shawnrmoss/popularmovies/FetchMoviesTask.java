package com.shawnrmoss.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by smoss on 8/24/2015.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private final String API_KEY = "840ec5fe6ac05a0d3b8cd264361a5543";
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        //Build our URL to make our request
        //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]

        String sort = params[0];
        Log.d(LOG_TAG, "The sort param is : " + sort);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("sort_by", "popularity.desc")
                .appendQueryParameter("api_key", API_KEY);

        InputStream stream = null;
        try {
            //Get our url to make our network request
            URL url = new URL(builder.build().toString());

            Log.d(LOG_TAG, "The URL is: " + url.toString());

            // Establish a connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.addRequestProperty("Accept", "application/json");
            conn.setDoInput(true);
            conn.connect();

            int responseCode = conn.getResponseCode();
            Log.d(LOG_TAG, "The response code is: " + responseCode + " " + conn.getResponseMessage());

            stream = conn.getInputStream();

            //Read the stream into a String of JSON
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String json = bufferedReader.readLine();

            //Return our list of movies to use in our GridAdapter
            return(parseJson(json));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private ArrayList<Movie> parseJson(String stream) {

        String stringFromStream = stream;
        ArrayList<Movie> results = new ArrayList<Movie>();
        try {
            JSONObject jsonObject = new JSONObject(stringFromStream);
            JSONArray array = (JSONArray) jsonObject.get("results");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonMovieObject = array.getJSONObject(i);
                Movie movie = new Movie(
                        Integer.parseInt(jsonMovieObject.getString("id")),              //id
                        jsonMovieObject.getString("original_title"),                    //title
                        jsonMovieObject.getString("release_date"),                      //release_date
                        jsonMovieObject.getString("poster_path"),                       //mover_poster
                        Double.parseDouble(jsonMovieObject.getString("vote_average")),  //vote_average
                        jsonMovieObject.getString("overview")                           //plot_synopsis
                );

                results.add(movie);
                Log.d(LOG_TAG, "Added movie: " + movie.getTitle());
            }
        } catch (JSONException e) {
            System.err.println(e);
            Log.d(LOG_TAG, "Error parsing JSON. String was: " + stringFromStream);
        }
        return results;
    }
}