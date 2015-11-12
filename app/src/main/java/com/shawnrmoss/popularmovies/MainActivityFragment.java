package com.shawnrmoss.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    private String mSortBy = "popularity.desc";

    public MainActivityFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
    }

    public void savePreferences(String prefName, String preValue){
        Log.d(LOG_TAG, "savePreferences - " + prefName + " : " + preValue);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(prefName, preValue);
        editor.commit();
    }

    public String getSavedPreferenceOrDefault(String prefName, String prefDefault){
        SharedPreferences sharedPref = getActivity()
                .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String prefValue = sharedPref.getString(prefName, prefDefault);
        Log.d(LOG_TAG, "getSavedPreferenceOrDefault - " + prefName + " : " + prefValue);
        return prefValue;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mainactivityfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_most_popular) {
            //mSortBy = "popularity.desc";
            savePreferences(getString(R.string.preference_sortBy_key), mSortBy = "popularity.desc");
            updateMovies();
        }

        if (id == R.id.action_highest_rated) {
            //mSortBy = "vote_average.desc";
            savePreferences(getString(R.string.preference_sortBy_key), mSortBy = "vote_average.desc");
            updateMovies();
        }

        return super.onOptionsItemSelected(item);
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






    /**
     * Created by smoss on 8/24/2015.
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String API_KEY = "SEEREADMEFORDETAILS";
        private final String LOG_TAG = MainActivity.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            //Build our URL to make our request
            //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
            Uri.Builder builder = new Uri.Builder();

            mSortBy = getSavedPreferenceOrDefault(getString(R.string.preference_sortBy_key), getString(R.string.preference_sortBy_default));
            //Log.d(LOG_TAG, "Shared Pref SortBy : " + mSortBy);
            if(mSortBy == "vote_average.desc")
            {
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("vote_count.gte", "1000")
                        .appendQueryParameter("sort_by", mSortBy)
                        .appendQueryParameter("api_key", API_KEY);

            }
            else {
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by", mSortBy)
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
        protected void onPostExecute(ArrayList<Movie> results){
            if(results != null){
                mMovieAdapter.addAll(results);
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
}
