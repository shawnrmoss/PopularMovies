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
import java.util.concurrent.ExecutionException;

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
        String sortBy = getSavedPreferenceOrDefault(getString(R.string.preference_sortBy_key), getString(R.string.preference_sortBy_default));
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity()){
            @Override
            public void onMoviesFetched(ArrayList<Movie> result) {
                mMovieAdapter.addAll(result);
            }
        };
        moviesTask.execute(sortBy);
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
}
