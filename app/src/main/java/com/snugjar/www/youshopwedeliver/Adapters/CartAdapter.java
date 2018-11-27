package com.snugjar.www.youshopwedeliver.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.snugjar.www.youshopwedeliver.Connectors.ApiConnector;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private JSONArray dataArray;
    String Image, availability, Sname, SCurrency;
    private static LayoutInflater inflater = null;
    Activity activity;
    int perUnitNum, quantityNum, itemTotalCostNum;

    public CartAdapter(JSONArray jsonArray, Activity a) {
        try {
            this.dataArray = jsonArray;
            activity = a;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            getCurrencyNPrices();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCurrencyNPrices() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //SDeliveryPrice = sharedPreferences.getString(Constants.DELIVERY_PRICE, "N/A");
        //SPickupPrice = sharedPreferences.getString(Constants.PICKUP_PRICE, "N/A");
        SCurrency = sharedPreferences.getString(Constants.CURRENCY, "N/A");
        //SPricePerKilometre = sharedPreferences.getString(Constants.PRICE_PER_KILOMETRE, "N/A");
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
            final JSONObject jsonObject = this.dataArray.getJSONObject(position);

            Glide
                    .with(activity.getApplicationContext())
                    .load(jsonObject.getString("image"))
                    .thumbnail(0.1f)
                    .error(R.drawable.logo)
                    .crossFade()
                    .into(holder.product_image);

            holder.product_name.setText(jsonObject.getString("item_name"));
            holder.product_quantity.setText(String.format("quantity: %s", jsonObject.getString("quantity")));

            int itemTotal = Integer.parseInt(jsonObject.getString("price"));
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String formattedString = formatter.format(itemTotal);

            holder.product_price.setText(String.format("total: %s %s", formattedString, SCurrency));

            //implement setOnClickListener event on item view.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        //get required fields to update or delete cart items
                        String personID = jsonObject.getString("person_id");
                        String deviceIMEI = jsonObject.getString("device_imei");
                        String orderID = jsonObject.getString("order_id");
                        String itemName = jsonObject.getString("item_name");
                        String quantity = jsonObject.getString("quantity");
                        String perUnit = jsonObject.getString("per_unit");
                        String image = jsonObject.getString("image");

                        //pass items to the function for displaying the dialog
                        showUpdateCartDialog(personID, deviceIMEI, orderID, itemName, quantity, perUnit, image);

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

    private void showUpdateCartDialog(final String personID, final String deviceIMEI, final String orderID, final String itemName,
                                      final String quantity, final String perUnit, final String image) {
        final Dialog update_cart_dialog = new Dialog(activity);
        update_cart_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        update_cart_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        update_cart_dialog.setCancelable(false);
        update_cart_dialog.setContentView(R.layout.dialog_update_cart);

        final ImageView close_dialog = update_cart_dialog.findViewById(R.id.close_dialog);
        ImageView delete_item = update_cart_dialog.findViewById(R.id.delete_item);
        ImageView product_image = update_cart_dialog.findViewById(R.id.product_image);
        TextView product_name = update_cart_dialog.findViewById(R.id.product_name);
        ImageView remove_item = update_cart_dialog.findViewById(R.id.remove_item);
        TextView item_price = update_cart_dialog.findViewById(R.id.item_price);
        final TextView item_count = update_cart_dialog.findViewById(R.id.item_count);
        final TextView item_total_cost = update_cart_dialog.findViewById(R.id.item_total_cost);
        ImageView add_item = update_cart_dialog.findViewById(R.id.add_item);
        final Button update_cart = update_cart_dialog.findViewById(R.id.update_cart);
        final AVLoadingIndicatorView loading = update_cart_dialog.findViewById(R.id.loading);

        //initialize dialog details
        perUnitNum = Integer.parseInt(perUnit);
        quantityNum = Integer.parseInt(quantity);

        Glide
                .with(activity.getApplicationContext())
                .load(image)
                .error(R.drawable.logo)
                .crossFade()
                .skipMemoryCache(false)
                .into(product_image);

        product_name.setText(itemName);
        item_price.setText(String.format("%s %s", perUnit, SCurrency));
        item_count.setText(quantity);

        itemTotalCostNum = quantityNum * perUnitNum;
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String formattedString = formatter.format(itemTotalCostNum);
        item_total_cost.setText(String.format("%s %s", formattedString, SCurrency));

        remove_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove items from the cart
                //when an item is added, perform the necessary calculations
                if (quantityNum != 1) {
                    //cannot order less than zero items
                    quantityNum = quantityNum - 1;

                    item_count.setText(String.valueOf(quantityNum));

                    itemTotalCostNum = perUnitNum * quantityNum;

                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String formattedString = formatter.format(itemTotalCostNum);

                    item_total_cost.setText(String.format("%s %s", formattedString, SCurrency));
                }
            }
        });

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add items to the cart
                //when an item is added, perform the necessary calculations
                if (quantityNum < 50) {
                    //cannot order more than 50 units of an item
                    quantityNum = quantityNum + 1;

                    item_count.setText(String.valueOf(quantityNum));

                    itemTotalCostNum = perUnitNum * quantityNum;

                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String formattedString = formatter.format(itemTotalCostNum);

                    item_total_cost.setText(String.format("%s %s", formattedString, SCurrency));
                }
            }
        });

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close dialog
                update_cart_dialog.dismiss();
            }
        });

        update_cart.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            class updateCart extends AsyncTask<ApiConnector, Long, String> {
                @Override
                protected String doInBackground(ApiConnector... params) {
                    String productName = itemName.replaceAll(" ", "+");
                    String quantity = String.valueOf(quantityNum);
                    String total = String.valueOf(itemTotalCostNum);

                    //it is executed on Background thread
                    return params[0].UpdateCart(personID, deviceIMEI, orderID, productName, quantity, total);
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                protected void onPostExecute(String response) {
                    switch (response) {
                        case "Item added :)":
                            //item successfully added to cart with all details correctly
                            showToast(response);
                            //update UI
                            update_cart_dialog.dismiss();
                            break;
                        case "Item updated :)":
                            //item was successfully updated on the server with all details needed
                            showToast(response);
                            //update UI
                            update_cart_dialog.dismiss();
                            break;
                        default:
                            //there was an error while adding item to cart
                            //inform user to try again
                            showToast(response);
                            //update UI
                            update_cart.setVisibility(View.VISIBLE);
                            close_dialog.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.INVISIBLE);
                            break;
                    }
                }
            }

            @Override
            public void onClick(View v) {
                //update cart in database
                update_cart.setVisibility(View.INVISIBLE);
                close_dialog.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                //call API to update values in database
                new updateCart().execute(new ApiConnector());
            }
        });

        update_cart_dialog.show();
    }

    private void showToast(String response) {
        Toast toast = Toast.makeText(activity, response, Toast.LENGTH_LONG);
        View toastView = toast.getView(); //This'll return the default View of
        //the Toast.
        TextView toastMessage = toastView.findViewById(android.R.id.message);
        toastMessage.setTextSize(12);
        toastMessage.setTextColor(activity.getResources().getColor(R.color.white));
        toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
        toastMessage.setGravity(Gravity.CENTER);
        toastMessage.setCompoundDrawablePadding(10);
        toastView.setBackground(activity.getResources().getDrawable(R.drawable.bg_button));
        toast.show();
    }

}
