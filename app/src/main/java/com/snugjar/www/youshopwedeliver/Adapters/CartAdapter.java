package com.snugjar.www.youshopwedeliver.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private JSONArray dataArray;
    String Image, availability, Sname;
    private static LayoutInflater inflater = null;
    Activity activity;

    public CartAdapter(JSONArray jsonArray, Activity a) {
        try {
            this.dataArray = jsonArray;
            activity = a;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            JSONObject jsonObject = this.dataArray.getJSONObject(position);

            Glide
                    .with(activity.getApplicationContext())
                    .load(jsonObject.getString("image"))
                    .thumbnail(0.1f)
                    .error(R.drawable.logo)
                    .crossFade()
                    .into(holder.product_image);

            holder.product_name.setText(jsonObject.getString("item_name"));
            holder.product_quantity.setText(String.format("quantity: %s", jsonObject.getString("quantity")));
            holder.product_price.setText(String.format("total: %s", jsonObject.getString("price")));

            //implement setOnClickListener event on item view.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final JSONObject json2Object;

                        json2Object = dataArray.getJSONObject(position);
                        final String active = json2Object.getString("quantity");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        //init the item view's
        TextView product_quantity;
        TextView product_name;
        ImageView product_image;
        TextView product_price;

        MyViewHolder(View itemView) {
            super(itemView);
            product_quantity = itemView.findViewById(R.id.product_quantity);
            product_name = itemView.findViewById(R.id.product_name);
            product_image = itemView.findViewById(R.id.product_image);
            product_price = itemView.findViewById(R.id.product_price);
        }
    }

}
