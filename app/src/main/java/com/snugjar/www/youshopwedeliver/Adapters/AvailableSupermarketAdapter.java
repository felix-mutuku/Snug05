package com.snugjar.www.youshopwedeliver.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snugjar.www.youshopwedeliver.Activities.ShoppingActivity;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.R;
import com.transitionseverywhere.Explode;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AvailableSupermarketAdapter extends RecyclerView.Adapter<AvailableSupermarketAdapter.MyViewHolder> {

    private JSONArray dataArray;
    String Image, availability;
    private static LayoutInflater inflater = null;
    Activity activity;
    private RecyclerView recycler_View;

    public AvailableSupermarketAdapter(JSONArray jsonArray, Activity a, RecyclerView r) {
        try {
            this.dataArray = jsonArray;
            activity = a;
            recycler_View = r;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    .load(Constants.BASE_URL_SUPERMARKET_LOGOS + Image)
                    .thumbnail(0.1f)
                    .error(R.drawable.logo)
                    .crossFade()
                    .into(holder.supermarket_image);

            holder.supermarket_name.setText(jsonObject.getString("name"));
            //holder.supermarket_rating.setRating(Float.parseFloat(jsonObject.getString("rating")));
            availability = jsonObject.getString("active");

            if (availability.equals("false")) {
                holder.supermarket_availability.setText(R.string.coming_soon);
                holder.supermarket_availability.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.supermarket_availability.setText("");
                //holder.supermarket_availability.setBackgroundColor(00000000);
            }

            //implement setOnClickListener event on item view.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //exploding recycler view items adapter
                    final Rect viewRect = new Rect();
                    view.getGlobalVisibleRect(viewRect);

                    TransitionSet set = new TransitionSet()
                            .addTransition(new Explode().setEpicenterCallback(new Transition.EpicenterCallback() {
                                @Override
                                public Rect onGetEpicenter(Transition transition) {
                                    return viewRect;
                                }
                            }).excludeTarget(view, true))
                            .addTransition(new Fade().addTarget(view))
                            .setDuration(500)
                            .addListener(new Transition.TransitionListenerAdapter() {
                                @Override
                                public void onTransitionEnd(Transition transition) {
                                    transition.removeListener(this);
                                    //open supermarket details to start shopping
                                    try {
                                        Intent intent = new Intent(activity, ShoppingActivity.class);
                                        intent.putExtra("dayID", jsonObject.getInt("id"));
                                        activity.startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //getActivity().onBackPressed();
                                }
                            });

                    TransitionManager.beginDelayedTransition(recycler_View, set);

                    //remove all views from Recycler View
                    recycler_View.setAdapter(null);
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
        //RatingBar supermarket_rating;

        public MyViewHolder(View itemView) {
            super(itemView);
            supermarket_name = itemView.findViewById(R.id.supermarket_name);
            supermarket_availability = itemView.findViewById(R.id.supermarket_availability);
            supermarket_image = itemView.findViewById(R.id.supermarket_image);
            //supermarket_rating = itemView.findViewById(R.id.supermarket_rating);
        }
    }

}
