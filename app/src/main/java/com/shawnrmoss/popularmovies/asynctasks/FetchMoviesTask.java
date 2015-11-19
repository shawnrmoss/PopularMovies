package com.shawnrmoss.popularmovies.asynctasks;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.shawnrmoss.popularmovies.activities.MainActivity;
import com.shawnrmoss.popularmovies.data.Movie;

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
 * Created by shawn on 11/12/2015.
 */
public abstract class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> implements iMoviesTask {

    private final String API_KEY = "840ec5fe6ac05a0d3b8cd264361a5543";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    Activity activity;

    public FetchMoviesTask(Activity activity) {
        this.activity = activity;
    }
    public abstract void onMoviesFetched(ArrayList<Movie> result);

    @Override
    //http://stackoverflow.com/questions/17382587/what-does-string-params-mean-if-passed-as-a-parameter -- vargars
    protected ArrayList<Movie> doInBackground(String... params) {
        //Build our URL to make our request
        //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
        Uri.Builder builder = new Uri.Builder();

        String sortBy = params[0];
        //Log.d(LOG_TAG, "Shared Pref SortBy : " + mSortBy);
        if(sortBy == "vote_average.desc")
        {
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("vote_count.gte", "1000")
                    .appendQueryParameter("sort_by", sortBy)
                    .appendQueryParameter("api_key", API_KEY);

        }
        else {
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("sort_by", sortBy)
                    .appendQueryParameter("api_key", API_KEY);
        }

        InputStream stream = null;
        try {
            //Get our url to make our network request
            URL url = new URL(builder.build().toString());
            // Establish a connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.addRequestProperty("Accept", "application/json");
            conn.setDoInput(true);
            conn.connect();

            //int responseCode = conn.getResponseCode();
            //Log.d(LOG_TAG, "The response code is: " + responseCode + " " + conn.getResponseMessage());
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

    @Override
    //http://developer.android.com/reference/android/os/AsyncTask.html#onPostExecute(Result)
    protected void onPostExecute(ArrayList<Movie> results){
        if(results != null){
            onMoviesFetched(results);
        }
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
            }
        } catch (JSONException e) {
            System.err.println(e);
            Log.d(LOG_TAG, "Error parsing JSON. String was: " + stringFromStream);
        }
        return results;
    }
}


/**
 * Created by shawn on 11/13/2015.
 * Callbacks to return fetched movies.
 */
interface iMoviesTask {

    void onMoviesFetched(ArrayList<Movie> result);
}