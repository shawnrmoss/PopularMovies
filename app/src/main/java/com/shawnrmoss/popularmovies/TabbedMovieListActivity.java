package com.shawnrmoss.popularmovies;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class TabbedMovieListActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return MovieListPlaceholderFragment.newInstance(position);
        }

        private String getSortBy(int position) {
            switch (position) {
                case 0:
                    return "vote_average.desc";//Highest rated
                case 1:
                    return "popularity.desc";//Most Popular
                case 2:
                    return "vote_average.desc";//Favorites
            }
            return "";
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Highest Rated";
                case 1:
                    return "Most Popular";
                case 2:
                    return "Favorites";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MovieListPlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String LOG_TAG = "MovieListFragment";
        private static final String ARG_SORT_BY = "arg_sort_by";
        private static MovieAdapter mMovieAdapter;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MovieListPlaceholderFragment newInstance(int position) {
            Log.i(LOG_TAG, "newInstance");
            MovieListPlaceholderFragment fragment = new MovieListPlaceholderFragment();
            Bundle args = new Bundle();
            Log.d("Position", Integer.toString(position));
            args.putInt(ARG_SORT_BY, position);
            fragment.setArguments(args);
            return fragment;
        }

        public MovieListPlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(LOG_TAG, "onCreateView");
            View rootView = inflater.inflate(R.layout.fragment_tabbed_movie_list, container, false);

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

        @Override
        public void onStart() {
            Log.i(LOG_TAG, "onStart");
            super.onStart();
            updateMovies();
        }

        private void updateMovies() {

            FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity()){
                @Override
                public void onMoviesFetched(ArrayList<Movie> result) {
                    mMovieAdapter.addAll(result);
                }
            };
            Log.d("UPDATE MOVIES", ARG_SORT_BY);
            moviesTask.execute(ARG_SORT_BY);
        }
    }
}
