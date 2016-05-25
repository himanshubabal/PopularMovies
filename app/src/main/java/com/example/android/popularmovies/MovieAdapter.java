package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder>{

    private List<Movies> popularMovies;
    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, year, rating;
        public ImageView posterImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.grid_item_title);
            year = (TextView) itemView.findViewById(R.id.grid_item_release_date);
            rating = (TextView) itemView.findViewById(R.id.grid_item_rating);
            posterImage = (ImageView) itemView.findViewById(R.id.grid_item_image);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "You Clicked" , Toast.LENGTH_SHORT).show();
            Intent i = new Intent(context, MovieDetails.class);
            int itemPosition = getAdapterPosition();
            i.putExtra("id", popularMovies.get(itemPosition).getDb_id());
            itemView.getContext().startActivity(i);
        }
    }

    public MovieAdapter(List<Movies> movies, Context context){
        this.context = context;
        this.popularMovies = movies;
    }

    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MyViewHolder holder, int position) {

        //this section gets height and width of device screen in DP
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        //185 x 278 are the dimensions of requested image
        float imageRatio = (float)185/(float)278;     //img ratio is width/height

        //'scale' is used to convert DP to pixels
        final float scale = context.getResources().getDisplayMetrics().density;

        //  padding is the margin/padding kept from screen sides and between the images
        int padding = 7;
        //  2 * (width of final img) + 3 * (padding) = width of device screen.
        //  So,  width of final img = ((width of device) - 3 * (padding)) / 2
        int widthDp = (int) (dpWidth - 3 * padding) / 2;
        //  as to keep the width:height ratio same as original image
        //  height of final img = ratio * width of final img
        int heightDp = (int) (widthDp / imageRatio);
        //  final image dimensions are converted into pixels from DP
        //  so as to pass in picaso as inputs to resize image according to device.
        int widthPixels = (int) (widthDp * scale + 0.5f);
        int heightPixels = (int) (heightDp * scale + 0.5f);


        Movies movie = popularMovies.get(position);
        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear());
        holder.rating.setText(movie.getYear());

        Picasso.with(context).load(movie.getPosterUrl()).resize(widthPixels, heightPixels).into(holder.posterImage);
    }

    @Override
    public int getItemCount() {
        return popularMovies.size();
    }
}
