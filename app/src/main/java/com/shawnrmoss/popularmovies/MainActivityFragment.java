package com.shawnrmoss.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter mMovieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new MovieAdapter(getActivity(),
                                         new ArrayList<Movie>());

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridview.setAdapter(mMovieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("Movie", movie);
                startActivity(intent);
            }
        });

        return rootView;

    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }


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
            Log.d(LOG_TAG, "FetchMoviesTask - execute");
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

        @Override
        protected void onPostExecute(ArrayList<Movie> results){
            if(results != null){
                Log.d(LOG_TAG, "The result returned : " + results.size() + " movies");
                mMovieAdapter.addAll(results);
                Log.d(LOG_TAG, "The adapter has : " + mMovieAdapter.getCount() + " movies");
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
                    Log.d(LOG_TAG, "Added movie: " + movie.getTitle());
                }
            } catch (JSONException e) {
                System.err.println(e);
                Log.d(LOG_TAG, "Error parsing JSON. String was: " + stringFromStream);
            }
            return results;
        }
    }
}
