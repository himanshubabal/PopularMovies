package com.example.android.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDetails extends AppCompatActivity{
    String jsonStr, title, overview, rating, releaseYear, backdrop, posterPath, tagline;
    String [] genreArray;
    ProgressDialog progress;
    ImageView backdropImage, posterImage;
    TextView titleText, taglineText, overviewText, ratingText, releaseYearText;
    CollapsingToolbarLayout collapsingToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        backdropImage = (ImageView) findViewById(R.id.backdrop_image);
        posterImage = (ImageView) findViewById(R.id.poster_image);
        titleText = (TextView) findViewById(R.id.movie_title);
        overviewText = (TextView) findViewById(R.id.movie_overview);
        taglineText = (TextView) findViewById(R.id.movie_tagline);
        ratingText = (TextView) findViewById(R.id.movie_rating);
        releaseYearText = (TextView) findViewById(R.id.movie_year);

        GetMovie getMovie = new GetMovie();
        getMovie.execute(id);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait while loading...");
        progress.show();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.Mytoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);


    }

    public class GetMovie extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            jsonStr = null;

            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/" + params[0] + "?api_key=4604e49e5c987815097edb08643cbbec");


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
                parseJSON(jsonStr);
            } catch (IOException e) {
                Snackbar.make(findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                restartActivity();
                            }
                        })
                        .setActionTextColor(Color.RED)
                        .show();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }
            return jsonStr;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            showImages();
        }
    }

    public void parseJSON(String jsonString) throws JSONException {

        Log.i("movie-detail", jsonString);

        JSONObject object = new JSONObject(jsonString);
        JSONArray genre = object.getJSONArray("genres");

        genreArray = new String[genre.length()];
        for (int i = 0; i < genre.length(); i++){
            JSONObject genreName = genre.getJSONObject(i);
            String name = genreName.getString("name");
            genreArray[i] = name;
        }

        title = object.getString("title");
        overview = object.getString("overview");
        rating = object.getString("vote_average");
        tagline = object.getString("tagline");
        releaseYear = object.getString("release_date").substring(0,4);
        posterPath = "http://image.tmdb.org/t/p/w185" + object.getString("poster_path");
        backdrop = "http://image.tmdb.org/t/p/w500" + object.getString("backdrop_path");


    }

    public void showImages(){

        //this section gets height and width of device screen in DP
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        //185 x 278 are the dimensions of requested image
        float imageRatioPoster = (float)185/(float)278;   //img ratio is width/height
        float imageRatioBack = (float)500/(float)281;

        //'scale' is used to convert DP to pixels
        final float scale = this.getResources().getDisplayMetrics().density;

        //  padding is the margin/padding kept from screen sides and between the images
        int padding = 7;
        //  (width of final img) + 2 * (padding) = width of device screen.
        //  So,  width of final img = (width of device) - 2 * (padding)
        int widthDpBack = (int) (dpWidth - 2 * padding);
        int widthDpPoster = (int) (dpWidth/2.5);
        //  as to keep the width:height ratio same as original image
        //  height of final img = ratio * width of final img
        int heightDpBack = (int) (widthDpBack / imageRatioBack);
        int heightDpPoster = (int) (widthDpPoster / imageRatioPoster);
        Log.i("width-height", widthDpPoster + " " + heightDpPoster + " " + imageRatioBack + " " + imageRatioPoster);

        //  final image dimensions are converted into pixels from DP
        //  so as to pass in picaso as inputs to resize image according to device.
        int widthPixelsBack = (int) (widthDpBack * scale + 0.5f);
        int widthPixelsPoster = (int) (widthDpPoster * scale + 0.5f);
        int heightPixelsBack = (int) (heightDpBack * scale + 0.5f);
        int heightPixelPoster = (int) (heightDpPoster * scale + 0.5f);

        Picasso.with(this).load(backdrop).resize(widthPixelsBack, heightPixelsBack).into(backdropImage);
        Picasso.with(this).load(posterPath).resize(widthPixelsPoster, heightPixelPoster).into(posterImage);

        collapsingToolbar.setTitle(title);
        titleText.setText(title);
        overviewText.setText(overview);
        taglineText.setText(tagline);
        ratingText.setText(rating);
        releaseYearText.setText(releaseYear);
    }

    public void restartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
