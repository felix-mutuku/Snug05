package com.snugjar.www.orderviewreview.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
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
import com.snugjar.www.orderviewreview.Connectors.ApiConnector;
import com.snugjar.www.orderviewreview.Connectors.Constants;
import com.snugjar.www.orderviewreview.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {

    private JSONArray dataArray;
    String supermarketID, supermarketName, orderID, personID, deviceIMEI, SCurrency, perUnit;
    private static LayoutInflater inflater = null;
    Activity activity;
    private RecyclerView recycler_View;
    int count, total;

    public ProductsAdapter(JSONArray jsonArray, Activity a, RecyclerView r, String SSupermarketID, String SSupermarketName) {
        try {
            this.dataArray = jsonArray;
            activity = a;
            recycler_View = r;
            supermarketID = SSupermarketID;
            supermarketName = SSupermarketName;
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
                    .skipMemoryCache(false)
                    .error(R.drawable.logo)
                    .crossFade()
                    .into(holder.product_image);

            holder.product_name.setText(jsonObject.getString("name"));

            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String finalPrice = String.format("%s_price", supermarketID);

            final Integer price = Integer.valueOf(jsonObject.getString(finalPrice));
            String formattedString = formatter.format(price);

            holder.product_price.setText(String.format("%s %s", formattedString, SCurrency));

            //implement setOnClickListener event on item view.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //when an item on the list is clicked
                    try {
                        String productName = jsonObject.getString("name");
                        String productImage = jsonObject.getString("image");
                        //pass image, name, price to the function for displaying the dialog
                        showAddToCartDialog(productName, productImage, price);
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

    private void showAddToCartDialog(final String productName, final String productImage, final Integer d_price) {
        final Dialog add_to_cart = new Dialog(activity);
        add_to_cart.requestWindowFeature(Window.FEATURE_NO_TITLE);
        add_to_cart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        add_to_cart.setCancelable(false);
        add_to_cart.setContentView(R.layout.dialog_add_to_cart);

        final ImageView close_dialog = add_to_cart.findViewById(R.id.close_dialog);
        ImageView product_image = add_to_cart.findViewById(R.id.product_image);
        TextView product_name = add_to_cart.findViewById(R.id.product_name);
        ImageView remove_item = add_to_cart.findViewById(R.id.remove_item);
        final TextView item_price = add_to_cart.findViewById(R.id.item_price);
        final TextView item_count = add_to_cart.findViewById(R.id.item_count);
        final TextView item_total_cost = add_to_cart.findViewById(R.id.item_total_cost);
        ImageView add_item = add_to_cart.findViewById(R.id.add_item);
        final Button button_add_to_cart = add_to_cart.findViewById(R.id.add_to_cart);
        final AVLoadingIndicatorView loading = add_to_cart.findViewById(R.id.loading);

        //initialize for the product chosen
        Glide
                .with(activity.getApplicationContext())
                .load(productImage)
                .error(R.drawable.logo)
                .crossFade()
                .skipMemoryCache(false)
                .into(product_image);

        product_name.setText(productName);//set the name
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String formattedString = formatter.format(d_price);
        //for adding current price to the database
        perUnit = formattedString;
        item_price.setText(String.format("%s %s", formattedString, SCurrency));//set the price in human readable format
        item_count.setText("1");//set the count of the items which is "1" by default

        item_total_cost.setText(String.format("%s %s", formattedString, SCurrency));
        loading.setVisibility(View.INVISIBLE);

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close dialog
                add_to_cart.dismiss();
            }
        });

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add item count and calculate the price
                String itemCount = item_count.getText().toString();

                count = Integer.parseInt(itemCount);

                if (count < 50) {
                    //cannot order more than 50 units of an item
                    count = count + 1;

                    item_count.setText(String.valueOf(count));

                    total = d_price * count;

                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String formattedString = formatter.format(total);

                    item_total_cost.setText(String.format("%s %s", formattedString, SCurrency));
                }

            }
        });

        remove_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove item count and calculate the price
                String itemCount = item_count.getText().toString();

                count = Integer.parseInt(itemCount);

                if (count != 1) {
                    //cannot order less than zero items
                    count = count - 1;

                    item_count.setText(String.valueOf(count));

                    total = d_price * count;

                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String formattedString = formatter.format(total);

                    item_total_cost.setText(String.format("%s %s", formattedString, SCurrency));
                }
            }
        });

        button_add_to_cart.setOnClickListener(new View.OnClickListener() {
            //checking if user mobile number already exists in the database
            @SuppressLint("StaticFieldLeak")
            class getOrderID extends AsyncTask<ApiConnector, Long, String> {
                @Override
                protected String doInBackground(ApiConnector... params) {
                    //it is executed on Background thread
                    return params[0].GetOrderID(supermarketName);
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                protected void onPostExecute(String response) {
                    if (!response.equals("Failed")) {
                        //orderID assigned successfully for the user to use
                        //add orderID to shared preferences for continuous use
                        orderID = response;
                        //update sharedPreferences correctly
                        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.ORDER_ID, orderID);
                        editor.apply();
                        //add to cart like normal
                        new addToCart().execute(new ApiConnector());
                    } else {
                        //there was an error while assigning orderID
                        //try and try again until successful
                        Handler myHandler = new Handler();
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //try again after 5 seconds
                                new getOrderID().execute(new ApiConnector());
                            }
                        }, 5000);
                    }
                }
            }

            @SuppressLint("StaticFieldLeak")
            class addToCart extends AsyncTask<ApiConnector, Long, String> {
                @Override
                protected String doInBackground(ApiConnector... params) {
                    String product = productName.replaceAll(" ", "+");
                    String quantity = item_count.getText().toString();

                    total = d_price * Integer.parseInt(quantity);

                    //it is executed on Background thread
                    return params[0].AddToCart(orderID, product, personID, quantity, perUnit, total, deviceIMEI, productImage);
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                protected void onPostExecute(String response) {
                    switch (response) {
                        case "Item added :)":
                            //item successfully added to cart with all details correctly
                            showToast(response);
                            //update UI
                            add_to_cart.dismiss();
                            break;
                        case "Item updated :)":
                            //item was successfully updated on the server with all details needed
                            showToast(response);
                            //update UI
                            add_to_cart.dismiss();
                            break;
                        default:
                            //there was an error while adding item to cart
                            //inform user to try again
                            showToast(response);
                            //update UI
                            button_add_to_cart.setVisibility(View.VISIBLE);
                            close_dialog.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.INVISIBLE);
                            break;
                    }
                }
            }

            @Override
            public void onClick(View v) {
                //add items to database cart for the user and update UI accordingly
                button_add_to_cart.setVisibility(View.INVISIBLE);
                close_dialog.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);

                SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                orderID = sharedPreferences.getString(Constants.ORDER_ID, "N/A");
                personID = sharedPreferences.getString(Constants.PERSON_ID, "N/A");
                deviceIMEI = sharedPreferences.getString(Constants.IMEI, "N/A");

                if (!orderID.equals("N/A")) {
                    //user has a pending cart they need to check out
                    //use the same order number to place more orders
                    new addToCart().execute(new ApiConnector());
                } else {
                    //user has no pending carts assign new orderID for use
                    new getOrderID().execute(new ApiConnector());
                }
            }
        });

        add_to_cart.show();
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
