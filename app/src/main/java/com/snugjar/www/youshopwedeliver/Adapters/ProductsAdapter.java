package com.snugjar.www.youshopwedeliver.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {

    private JSONArray dataArray;
    String supermarketID;
    private static LayoutInflater inflater = null;
    Activity activity;
    private RecyclerView recycler_View;

    public ProductsAdapter(JSONArray jsonArray, Activity a, RecyclerView r, String SSupermarketID) {
        try {
            this.dataArray = jsonArray;
            activity = a;
            recycler_View = r;
            supermarketID = SSupermarketID;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            final JSONObject jsonObject = this.dataArray.getJSONObject(position);

            Glide
                    .with(activity.getApplicationContext())
                    .load(jsonObject.getString("image"))
                    .thumbnail(0.1f)
                    .error(R.drawable.logo)
                    .crossFade()
                    .into(holder.product_image);

            holder.product_name.setText(jsonObject.getString("name"));

            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String finalPrice = String.format("%s_price", supermarketID);

            Integer price = Integer.valueOf(jsonObject.getString(finalPrice));
            String formattedString = formatter.format(price);

            holder.product_price.setText(String.format("KSh %s", formattedString));

            //implement setOnClickListener event on item view.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //when an item on the list is clicked
                    try {
                        Toast toast = Toast.makeText(activity, jsonObject.getString("name"), Toast.LENGTH_SHORT);
                        toast.show();
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
        TextView product_name;
        TextView product_price;
        ImageView product_image;

        MyViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_image = itemView.findViewById(R.id.product_image);
            //supermarket_rating = itemView.findViewById(R.id.supermarket_rating);
        }
    }

}
