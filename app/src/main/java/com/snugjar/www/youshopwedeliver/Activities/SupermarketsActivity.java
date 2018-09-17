package com.snugjar.www.youshopwedeliver.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
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
import com.transitionseverywhere.Explode;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import org.json.JSONArray;
import org.json.JSONException;
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
    String Image, availability;
    String Scountry = "ke";
    ArrayList<String> SlidingImagesList = new ArrayList<String>();
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    View rootLayout;
    private int revealX;
    private int revealY;
    private JSONArray dataArray;
    private static LayoutInflater inflater = null;
    Dialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermarkets);

        carouselView = findViewById(R.id.sliding_images);
        recyclerView = findViewById(R.id.recycler_view);
        available = findViewById(R.id.available);
        back = findViewById(R.id.back);
        rootLayout = findViewById(R.id.root_layout);
        final Intent intent = getIntent();

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
            new GetSlidingImages().execute(new ApiConnector());
            loading_dialog.show(); // don't forget to dismiss the dialog when done loading
        } else {
            //user not connected to the internet, don't show ads
            Intent intentInternet = new Intent(SupermarketsActivity.this, InternetActivity.class);
            startActivity(intentInternet);
        }

        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to previous
                onBackPressed();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSlidingImages extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].GetSlidingImages(Scountry);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    SlidingImagesList.add(Constants.BASE_URL_SUPERMARKET_IMAGE + object.getString("image"));
                }

                setFlippingImage();
                //get all supermarkets from the server in user's country
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
            loading_dialog.show();
            setSupermarketAdapter(jsonArray);
        }
    }

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            //create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(1000);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            //make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();

        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unRevealActivity();
    }

    protected void unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, revealX, revealY, finalRadius, 0);

            circularReveal.setDuration(1000);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    finish();
                }
            });

            circularReveal.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //get all supermarkets from the server in user's country
        new GetAvailableSupermarketsTask().execute(new ApiConnector());
    }
}
