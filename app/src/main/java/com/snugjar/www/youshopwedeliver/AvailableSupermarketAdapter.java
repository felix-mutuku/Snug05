package com.snugjar.www.youshopwedeliver;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AvailableSupermarketAdapter extends RecyclerView.Adapter<AvailableSupermarketAdapter
        .MyViewHolder> {

    private JSONArray dataArray;
    String Image, availability;
    private static LayoutInflater inflater = null;
    private Context context;
    Activity activity;

    public AvailableSupermarketAdapter(JSONArray jsonArray, Activity a) {
        try {
            this.dataArray = jsonArray;
            activity = a;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AvailableSupermarketAdapter(Context context) {
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        try {
            return this.dataArray.length();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_supermarkets_layout, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            final JSONObject jsonObject = this.dataArray.getJSONObject(position);

            Image = jsonObject.getString("image");

            Glide
                    .with(activity.getApplicationContext())
                    .load(Constants.BASE_URL_SUPERMARKET_IMAGE + Image)
                    .thumbnail(0.1f)
                    .error(R.drawable.logo)
                    .crossFade()
                    .into(holder.supermarket_image);

            holder.supermarket_name.setText(jsonObject.getString("name"));
            holder.supermarket_rating.setRating(Float.parseFloat(jsonObject.getString("rating")));
            availability = jsonObject.getString("active");

            if (availability.equals("false")) {
                holder.supermarket_availability.setText("Coming Soon");
                holder.supermarket_availability.setBackground(activity.getResources().getDrawable(R.drawable.bg_button));
            } else {
                holder.supermarket_availability.setText("");
                holder.supermarket_availability.setBackgroundColor(00000000);
            }

            //implement setOnClickListener event on item view.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //open supermarket details to start shopping
                    /*try {
                        Intent intent = new Intent(activity, InfoActivity.class);
                        intent.putExtra("dayID", jsonObject.getInt("id"));
                        activity.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //init the item view's
        TextView supermarket_name;
        TextView supermarket_availability;
        ImageView supermarket_image;
        RatingBar supermarket_rating;

        public MyViewHolder(View itemView) {
            super(itemView);
            supermarket_name = itemView.findViewById(R.id.supermarket_name);
            supermarket_availability = itemView.findViewById(R.id.supermarket_availability);
            supermarket_image = itemView.findViewById(R.id.supermarket_image);
            supermarket_rating = itemView.findViewById(R.id.supermarket_rating);
        }
    }
}
