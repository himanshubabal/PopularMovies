package com.example.android.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieMain extends AppCompatActivity implements View.OnClickListener{
    Boolean isPopular;
    String jsonStr;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    ProgressDialog progress;
    RecyclerView movieRecycler;
    MovieAdapter movieAdapter;
    List<Movies> moviesNewList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_main_recycle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait while loading...");
        progress.show();



        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        GetMovies getMovies = new GetMovies();
        //set isPopular = true if you want popular movies
        //set isPopular = false if you want top rated movies

        Intent intent = getIntent();
        Boolean popular = intent.getBooleanExtra("isPopular", true);
        if (popular) {
            isPopular = true;
            toolbar.setTitle("Popular Movies");
        }
        else {
            isPopular = false;
            toolbar.setTitle("Top Rated Movies");
        }
        getMovies.execute();

        movieRecycler = (RecyclerView) findViewById(R.id.movie_recycler);

        layoutManager = new GridLayoutManager(getApplicationContext(), 2);

        movieRecycler.setItemAnimator(new DefaultItemAnimator());
        context = getApplicationContext();
        movieAdapter = new MovieAdapter(moviesNewList, context);
        movieRecycler.setLayoutManager(layoutManager);
        movieRecycler.setAdapter(movieAdapter);

    }


    public void prepareData(){
        Log.i("newMovieList", moviesNewList.size() + " ");
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:
                restartActivity(true);
                break;
            case R.id.fab2:
                restartActivity(false);
                break;
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;

        }
    }

    public class GetMovies extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            jsonStr = null;

            try {
                URL url;
                if (isPopular) {
                    url = new URL("http://api.themoviedb.org/3/movie/" + "popular" + "?api_key=[api_key]");
                }
                else {
                    url = new URL("http://api.themoviedb.org/3/movie/" + "top_rated" + "?api_key=[api_key]");
                }

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
                                restartActivity(true);
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
            prepareData();
        }
    }

    public void parseJSON(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray resultsArray = jsonObject.getJSONArray("results");

        for (int i = 0; i < resultsArray.length(); i ++){
            JSONObject object = resultsArray.getJSONObject(i);
            String title = object.getString("title");
            String rating = object.getString("vote_average");
            String id = object.getString("id");
            String releaseYear = object.getString("release_date").substring(0,4);
            String posterPath = object.getString("poster_path");


            Movies movie = new Movies(title, id, releaseYear, rating, "http://image.tmdb.org/t/p/w185" + posterPath);
            moviesNewList.add(movie);

        }


    }



    public void restartActivity(Boolean popular){
        Intent intent = getIntent();
        intent.putExtra("isPopular", popular);
        finish();
        startActivity(intent);
    }

}
