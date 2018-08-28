package com.snugjar.www.youshopwedeliver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {
    CarouselView carouselView;
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    private JSONArray jsonArray;
    RecyclerView recyclerView;
    ImageView available;
    String Scountry = "ke";

    int[] sliding_images_array = {R.drawable.naivas_logo_background,
            R.drawable.tuskys_logo_background,
            R.drawable.game_logo_background,
            R.drawable.carrefour_logo_background,
            R.drawable.miniso_logo_background,
            R.drawable.village_logo_background,
            R.drawable.uchumi_logo_background
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        carouselView = findViewById(R.id.sliding_images);
        recyclerView = findViewById(R.id.recycler_view);
        available = findViewById(R.id.available);

        cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert cManager != null;
        nInfo = cManager.getActiveNetworkInfo();

        if (nInfo != null && nInfo.isConnected()) {
            //when connected to the internet, show available supermarkets
            initializeSlidingImages();
            //get all supermarkets from the server in user's country
            new GetAvailableSupermarketsTask().execute(new ApiConnector());
        } else {
            //user not connected to the internet, don't show ads
            Intent intent = new Intent(MainActivity.this, InternetActivity.class);
            startActivity(intent);
        }
    }

    //start the supermarket slide show at the top
    private void initializeSlidingImages() {
        carouselView.setPageCount(sliding_images_array.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Glide
                        .with(MainActivity.this)
                        .load(sliding_images_array[position])
                        .fitCenter()
                        .crossFade()
                        .into(imageView);
            }
        });
    }

    public void setAdapterForDate(JSONArray jsonArray) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        try {
            this.jsonArray = jsonArray;
            this.recyclerView.setAdapter(new AvailableSupermarketAdapter(jsonArray, this));
            if (jsonArray == null) {
                available.setVisibility(View.VISIBLE);
                //swipe_refresh_layout.setVisibility(View.VISIBLE);
            }else {
                available.setVisibility(View.GONE);
            }
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
                //swipe_refresh_layout.setVisibility(View.VISIBLE);
                // it is executed on Background thread
                return params[0].GetAvailableSupermarkets(Scountry);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setAdapterForDate(jsonArray);
        }
    }

}
