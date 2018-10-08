package com.snugjar.www.youshopwedeliver.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snugjar.www.youshopwedeliver.Adapters.AvailableSupermarketAdapter;
import com.snugjar.www.youshopwedeliver.Connectors.ApiConnector;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SupermarketsActivity extends AppCompatActivity {
    CarouselView carouselView;
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    private JSONArray jsonArray;
    RecyclerView recyclerView;
    ImageView available;
    TextView back;
    String Scountry;
    ArrayList<String> SlidingImagesList = new ArrayList<String>();
    Dialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermarkets);

        carouselView = findViewById(R.id.sliding_images);
        recyclerView = findViewById(R.id.recycler_view);
        available = findViewById(R.id.available);
        back = findViewById(R.id.back);

        loading_dialog = new Dialog(SupermarketsActivity.this);
        loading_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.setCancelable(false);
        loading_dialog.setContentView(R.layout.dialog_loading);

        cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert cManager != null;
        nInfo = cManager.getActiveNetworkInfo();

        if (nInfo != null && nInfo.isConnected()) {
            //when connected to the internet, show available supermarkets and start sliding images
            getUserInfoFromPreferences();
            new GetSlidingAds().execute(new ApiConnector());
            loading_dialog.show(); // don't forget to dismiss the dialog when done loading
        } else {
            //user not connected to the internet, don't show ads
            Intent intentInternet = new Intent(SupermarketsActivity.this, InternetActivity.class);
            startActivity(intentInternet);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to previous
                onBackPressed();
            }
        });
    }

    private void getUserInfoFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Scountry = sharedPreferences.getString(Constants.COUNTRY, "N/A");
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSlidingAds extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            String SPlacement = "2";//Ads from Advertisers [1=Home-Page, 2=Supermarkets-Page, 3=Specific-Supermarket]
            return params[0].GetGeneralSlidingAds(Scountry, SPlacement);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    SlidingImagesList.add(Constants.BASE_URL_SUPERMARKET_ADS + object.getString("image"));
                }

                setFlippingImage();
                loading_dialog.dismiss();
                //get all supermarkets from the server in user's country
                loading_dialog.show();
                new GetAvailableSupermarketsTask().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setFlippingImage() {
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide
                        .with(SupermarketsActivity.this)
                        .load(SlidingImagesList.get(position))
                        .fitCenter()
                        .crossFade()
                        .into(imageView);
            }
        });

        carouselView.setPageCount(SlidingImagesList.size());
    }

    public void setSupermarketAdapter(JSONArray jsonArray) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        try {
            this.jsonArray = jsonArray;
            this.recyclerView.setAdapter(new AvailableSupermarketAdapter(jsonArray, this, recyclerView));
            if (jsonArray == null) {
                available.setVisibility(View.VISIBLE);
                loading_dialog.dismiss();
                //swipe_refresh_layout.setVisibility(View.VISIBLE);
            } else {
                available.setVisibility(View.GONE);
            }
            loading_dialog.dismiss();
            //swipe_refresh_layout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetAvailableSupermarketsTask extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            try {
                return params[0].GetAvailableSupermarkets(Scountry);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setSupermarketAdapter(jsonArray);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //get all supermarkets from the server in user's country
        loading_dialog.show();
        new GetAvailableSupermarketsTask().execute(new ApiConnector());
    }
}
