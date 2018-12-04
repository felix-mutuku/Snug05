package com.snugjar.www.orderviewreview.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snugjar.www.orderviewreview.Adapters.CartAdapter;
import com.snugjar.www.orderviewreview.Connectors.ApiConnector;
import com.snugjar.www.orderviewreview.Connectors.Constants;
import com.snugjar.www.orderviewreview.R;

import org.json.JSONArray;

import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity {
    TextView back, total_sub_price;
    ImageView info, checkout;
    LinearLayout linear_available;
    SwipeRefreshLayout swipe_refresh_layout;
    String SIMEI, SOrderID, SPersonID, SCurrency;
    RecyclerView recycler_view;
    Dialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        back = findViewById(R.id.back);
        info = findViewById(R.id.info);
        linear_available = findViewById(R.id.linear_available);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        recycler_view = findViewById(R.id.recycler_view);
        checkout = findViewById(R.id.checkout);
        total_sub_price = findViewById(R.id.total_sub_price);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SIMEI = sharedPreferences.getString(Constants.IMEI, "N/A");
        SOrderID = sharedPreferences.getString(Constants.ORDER_ID, "N/A");
        SPersonID = sharedPreferences.getString(Constants.PERSON_ID, "N/A");

        swipe_refresh_layout.setRefreshing(true);

        initilize();

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                      @TargetApi(Build.VERSION_CODES.KITKAT)
                                                      @Override
                                                      public void onRefresh() {
                                                          initilize();
                                                          swipe_refresh_layout.setRefreshing(true);
                                                      }
                                                  }
        );

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go backwards
                onBackPressed();
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open checkout page
                goToCheckout();
            }
        });

    }

    private void initilize() {
        loading_dialog = new Dialog(CartActivity.this);
        loading_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.setCancelable(false);
        loading_dialog.setContentView(R.layout.dialog_loading);
        loading_dialog.show(); //don't forget to dismiss the dialog when done loading

        //get current cart and total amount for the cart
        getCurrencyNPrices();
        new GetCartItems().execute(new ApiConnector());
        new GetSubTotal().execute(new ApiConnector());
    }

    private void getCurrencyNPrices() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //SDeliveryPrice = sharedPreferences.getString(Constants.DELIVERY_PRICE, "N/A");
        //SPickupPrice = sharedPreferences.getString(Constants.PICKUP_PRICE, "N/A");
        SCurrency = sharedPreferences.getString(Constants.CURRENCY, "N/A");
        //SPricePerKilometre = sharedPreferences.getString(Constants.PRICE_PER_KILOMETRE, "N/A");
    }

    private void goToCheckout() {
        //open checkout page
        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
        startActivity(intent);
    }

    public void setCartAdapter(JSONArray jsonArray) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(staggeredGridLayoutManager);
        try {
            recycler_view.setAdapter(new CartAdapter(jsonArray, this));
            if (jsonArray == null) {
                linear_available.setVisibility(View.VISIBLE);
                //swipe_refresh_layout.setVisibility(View.VISIBLE);
            } else {
                linear_available.setVisibility(View.INVISIBLE);
            }
            swipe_refresh_layout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetCartItems extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            try {
                swipe_refresh_layout.setVisibility(View.VISIBLE);
                //it is executed on Background thread
                return params[0].GetCartItems(SOrderID, SPersonID, SIMEI);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setCartAdapter(jsonArray);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSubTotal extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].getSubTotal(SIMEI, SPersonID, SOrderID);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            /*Toast toast = Toast.makeText(CartActivity.this, response, Toast.LENGTH_SHORT);
            toast.show();*/

            try {
                int subTotal = Integer.parseInt(response);
                DecimalFormat formatter = new DecimalFormat("#,###,###");
                String formattedString = formatter.format(subTotal);

                total_sub_price.setText(String.format("Subtotal : %s %s", formattedString, SCurrency));//set the price in human readable format
                //loading_dialog.dismiss();

                new GetItemsCount().execute(new ApiConnector());

                /*try {

                if (Objects.equals(response, "Exist")) {
                    //user already in database, proceed to main
                    goToMain();
                } else {
                    //user absent in database, confirm details first
                    displayConfirmDialog();
                }
                } catch (Exception e) {
                e.printStackTrace();
                }*/
            } catch (Exception e) {
                total_sub_price.setText(getString(R.string.cart_empty));
                loading_dialog.dismiss();
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetItemsCount extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].getCartItemsNumber(SIMEI, SPersonID, SOrderID);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            //insert number into cart items shared preference
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.CART_ITEMS_NUMBER, response);
            editor.apply();

            /*Toast toast = Toast.makeText(CartActivity.this, response, Toast.LENGTH_SHORT);
            toast.show();*/

            loading_dialog.dismiss();
        }
    }
}
