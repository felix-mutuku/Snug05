package com.snugjar.www.youshopwedeliver.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snugjar.www.youshopwedeliver.Adapters.CartAdapter;
import com.snugjar.www.youshopwedeliver.Adapters.ProductsAdapter;
import com.snugjar.www.youshopwedeliver.Connectors.ApiConnector;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.R;

import org.json.JSONArray;

public class CartActivity extends AppCompatActivity {
    TextView back;
    ImageView info, checkout;
    LinearLayout linear_available;
    SwipeRefreshLayout swipe_refresh_layout;
    String SIMEI, SOrderID, SPersonID;
    RecyclerView recycler_view;

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

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SIMEI = sharedPreferences.getString(Constants.IMEI, "N/A");
        SOrderID = sharedPreferences.getString(Constants.ORDER_ID, "N/A");
        SPersonID = sharedPreferences.getString(Constants.PERSON_ID, "N/A");

        swipe_refresh_layout.setRefreshing(true);
        new GetCartItems().execute(new ApiConnector());

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                      @TargetApi(Build.VERSION_CODES.KITKAT)
                                                      @Override
                                                      public void onRefresh() {
                                                          new GetCartItems().execute(new ApiConnector());
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

    private void goToCheckout() {
        //open checkout page

    }

    public void setCartAdapter(JSONArray jsonArray) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(staggeredGridLayoutManager);
        try {
            recycler_view.setAdapter(new CartAdapter(jsonArray, this));
            if (jsonArray == null) {
                linear_available.setVisibility(View.VISIBLE);
                //swipe_refresh_layout.setVisibility(View.VISIBLE);
            }else{
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
}
