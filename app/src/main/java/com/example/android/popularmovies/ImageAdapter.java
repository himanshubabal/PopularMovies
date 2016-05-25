//package com.example.android.popularmovies;
//
//import android.content.Context;
//import android.support.v7.widget.CardView;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//
//public class ImageAdapter extends BaseAdapter {
//    Context context;
//    ArrayList<HashMap<String, String>> imgUrl;
//    LayoutInflater layoutInflater;
//
//    public ImageAdapter(Context c, ArrayList<HashMap<String, String>> imgUrl){
//        this.context = c;
//        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.imgUrl = imgUrl;
//    }
//
//    @Override
//    public int getCount() {
//        return imgUrl.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return imgUrl.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        holder = new ViewHolder();
//
//        View view = layoutInflater.inflate(R.layout.custom_image, null);
//
//        holder.imageView = (ImageView) view.findViewById(R.id.grid_item_image);
//        holder.titleTextView = (TextView) view.findViewById(R.id.grid_item_title);
//        holder.rating = (TextView) view.findViewById(R.id.grid_item_rating);
//        holder.releaseDate = (TextView) view.findViewById(R.id.grid_item_release_date);
//        holder.cardView = (CardView) view.findViewById(R.id.card_view);
//
//
//        //this section gets height and width of device screen in DP
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//
//        //185 x 278 are the dimensions of requested image
//        float imageRatio = (float)185/(float)278;     //img ratio is width/height
//
//        //'scale' is used to convert DP to pixels
//        final float scale = context.getResources().getDisplayMetrics().density;
//
//        //  padding is the margin/padding kept from screen sides and between the images
//        int padding = 7;
//        //  2 * (width of final img) + 3 * (padding) = width of device screen.
//        //  So,  width of final img = ((width of device) - 3 * (padding)) / 2
//        int widthDp = (int) (dpWidth - 3 * padding) / 2;
//        //  as to keep the width:height ratio same as original image
//        //  height of final img = ratio * width of final img
//        int heightDp = (int) (widthDp / imageRatio);
//        //  final image dimensions are converted into pixels from DP
//        //  so as to pass in picaso as inputs to resize image according to device.
//        int widthPixels = (int) (widthDp * scale + 0.5f);
//        int heightPixels = (int) (heightDp * scale + 0.5f);
//
//
//
//        HashMap<String, String> object = imgUrl.get(position);
//
//        holder.titleTextView.setText(object.get("title"));
//        holder.cardView.setMinimumWidth(widthPixels);
//        holder.rating.setText(object.get("rating"));
//        holder.releaseDate.setText(object.get("releaseYear"));
//        Picasso.with(context).load(object.get("posterUrl")).resize(widthPixels, heightPixels).into(holder.imageView);
//
//        return view;
//    }
//
//    public static class ViewHolder {
//        TextView titleTextView;
//        TextView releaseDate;
//        TextView rating;
//        ImageView imageView;
//        CardView cardView;
//    }
//
//}
