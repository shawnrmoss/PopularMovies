package com.shawnrmoss.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by smoss on 8/24/2015.
 */
public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> mMovies;

    public MovieAdapter(Context c, ArrayList<Movie> m) {
        mContext = c;
        mMovies = m;
    }

    public int getCount() {
        return mMovies.size() > 0 ? mMovies.size() : 0;
    }

    public Object getItem(int position) {
        return mMovies.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        //Use picasso to load the image into the imageView
        Picasso.with(mContext).load(buildMovie_PosterURL(mMovies.get(position).getMovie_poster(), 3)).into(imageView);
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