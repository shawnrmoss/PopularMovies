package com.shawnrmoss.popularmovies;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Movie movie = getActivity().getIntent().getParcelableExtra("Movie");

        ((TextView) rootView.findViewById(R.id.title))
                .setText(movie.getTitle());

        ((TextView) rootView.findViewById(R.id.release_date))
                .setText(movie.getRelease_date().substring(0,4));

        ((TextView) rootView.findViewById(R.id.vote_average))
                .setText(Double.toString(movie.getVote_average()) + "/10");

        ((TextView) rootView.findViewById(R.id.plot_synopsis))
                .setText(movie.getPlot_synopsis());

        ImageView imageView = ((ImageView) rootView.findViewById(R.id.movie_poster));
        Picasso.with(rootView.getContext()).load(buildMovie_PosterURL(movie.getMovie_poster(), 3)).into(imageView);

        getActivity().setTitle("Movie Details");


        return rootView;
    }

    public String buildMovie_PosterURL(String url, int imageSize) {
        //http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        //"w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using "w185"
        String size;
        switch (imageSize) {
            case 0:
                size = "w92";
                break;
            case 1:
                size = "w154";
                break;
            case 2:
                size = "w185";
                break;
            case 3:
                size = "w342";
                break;
            case 4:
                size = "w500";
                break;
            case 5:
                size = "w780";
                break;
            case 6:
                size = "original";
                break;
            default:
                size = "w185";
                break;
        }


        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath(size)
                .appendEncodedPath(url);

        return builder.build().toString();
    }
}
