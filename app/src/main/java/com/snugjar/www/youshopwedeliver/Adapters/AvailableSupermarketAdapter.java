package com.snugjar.www.youshopwedeliver.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
    String Image, availability, Sname;
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
            JSONObject jsonObject = this.dataArray.getJSONObject(position);

            Glide
                    .with(activity.getApplicationContext())
                    .load(Constants.BASE_URL_SUPERMARKET_LOGOS + jsonObject.getString("image"))
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
                    final JSONObject json2Object;

                    try {
                        json2Object = dataArray.getJSONObject(position);
                        final String active = json2Object.getString("active");

                        if (active.equals("true")) {
                            //proceed to animate and go to next activity
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
                                                //open supermarket clicked by the user
                                                Intent intent = new Intent(activity, ShoppingActivity.class);
                                                intent.putExtra("id", json2Object.getString("id"));
                                                intent.putExtra("name", json2Object.getString("name"));
                                                intent.putExtra("image", json2Object.getString("image"));
                                                intent.putExtra("slogan", json2Object.getString("slogan"));
                                                intent.putExtra("description", json2Object.getString("description"));
                                                intent.putExtra("rating", json2Object.getString("rating"));
                                                activity.startActivity(intent);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            TransitionManager.beginDelayedTransition(recycler_View, set);

                            //remove all views from Recycler View
                            recycler_View.setAdapter(null);
                        } else {
                            //show dialog of coming soon
                            Image = json2Object.getString("image");
                            Sname = json2Object.getString("name");
                            showComingSoonDialog(Image, Sname);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showComingSoonDialog(String image, String sname) {
        final Dialog coming_soon_dialog = new Dialog(activity);
        coming_soon_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        coming_soon_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        coming_soon_dialog.setCancelable(false);
        coming_soon_dialog.setContentView(R.layout.dialog_coming_soon);

        ImageView close_dialog = coming_soon_dialog.findViewById(R.id.close_dialog);
        TextView dialog_text = coming_soon_dialog.findViewById(R.id.dialog_text);
        ImageView dialog_image = coming_soon_dialog.findViewById(R.id.dialog_image);

        Glide
                .with(activity.getApplicationContext())
                .load(Constants.BASE_URL_SUPERMARKET_LOGOS + image)
                .error(R.drawable.logo)
                .crossFade()
                .into(dialog_image);

        dialog_text.setText(String.format("%s %s", sname, activity.getString(R.string.soon_available)));

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close dialog
                coming_soon_dialog.dismiss();
            }
        });

        coming_soon_dialog.show();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        //init the item view's
        TextView supermarket_name;
        TextView supermarket_availability;
        ImageView supermarket_image;
        //RatingBar supermarket_rating;

        MyViewHolder(View itemView) {
            super(itemView);
            supermarket_name = itemView.findViewById(R.id.supermarket_name);
            supermarket_availability = itemView.findViewById(R.id.supermarket_availability);
            supermarket_image = itemView.findViewById(R.id.supermarket_image);
            //supermarket_rating = itemView.findViewById(R.id.supermarket_rating);
        }
    }

}
