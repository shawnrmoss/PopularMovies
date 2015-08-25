package com.shawnrmoss.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by smoss on 8/24/2015.
 */
public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> mMovieData;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        this.mContext = context;
        this.mMovieData = movies;
    }

    public void add(Movie object) {
        mMovieData.add(object);
        this.notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Movie> collection) {
        mMovieData.addAll(collection);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMovieData.size() > 0 ? mMovieData.size() : 0;
    }

    @Override
    public Movie getItem(int position) {
        return mMovieData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            imageView.setLayoutParams(new GridView.LayoutParams(550, 800) );
        } else {
            imageView = (ImageView) convertView;
        }

        //Use picasso to load the image into the imageView
        Picasso.with(mContext).load(buildMovie_PosterURL(mMovieData.get(position).getMovie_poster(), 3)).into(imageView);
        return imageView;
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