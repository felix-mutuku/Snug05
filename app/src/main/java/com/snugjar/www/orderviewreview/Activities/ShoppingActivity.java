package com.snugjar.www.orderviewreview.Activities;

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
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.snugjar.www.orderviewreview.Adapters.ProductsAdapter;
import com.snugjar.www.orderviewreview.Connectors.ApiConnector;
import com.snugjar.www.orderviewreview.Connectors.Constants;
import com.snugjar.www.orderviewreview.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShoppingActivity extends AppCompatActivity {
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    TextView back, supermarket_slogan, offers, baby_kids, beauty_cosmetics, canned_goods, cleaning, cooking_oil,
            dairy, drinks, food_cupboard, fresh_food, frozen, health_wellness, household, kitchen_dining, office_supplies,
            sauces, snacks, toiletries;
    Dialog loading_dialog, description_dialog, location_dialog, play_services_dialog;
    String SSupermarketID, SSupermarketName, SSupermarketImage, SSupermarketSlogan, SSupermarketDescription,
            SSupermarketRating, SCountry, SpersonID;
    ImageView supermarket_logo, supermarket_info, available, search, cart;
    ArrayList<String> SlidingImagesList = new ArrayList<String>();
    CarouselView carouselView;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        back = findViewById(R.id.back);
        supermarket_slogan = findViewById(R.id.supermarket_slogan);
        supermarket_logo = findViewById(R.id.supermarket_logo);
        supermarket_info = findViewById(R.id.supermarket_info);
        carouselView = findViewById(R.id.sliding_images);
        available = findViewById(R.id.available);
        recycler_view = findViewById(R.id.recycler_view);
        search = findViewById(R.id.search);
        offers = findViewById(R.id.offers);
        baby_kids = findViewById(R.id.baby_kids);
        beauty_cosmetics = findViewById(R.id.beauty_cosmetics);
        canned_goods = findViewById(R.id.canned_goods);
        cleaning = findViewById(R.id.cleaning);
        cooking_oil = findViewById(R.id.cooking_oil);
        dairy = findViewById(R.id.dairy);
        drinks = findViewById(R.id.drinks);
        food_cupboard = findViewById(R.id.food_cupboard);
        fresh_food = findViewById(R.id.fresh_food);
        frozen = findViewById(R.id.frozen);
        health_wellness = findViewById(R.id.health_wellness);
        household = findViewById(R.id.household);
        kitchen_dining = findViewById(R.id.kitchen_dining);
        office_supplies = findViewById(R.id.office_supplies);
        sauces = findViewById(R.id.sauces);
        snacks = findViewById(R.id.snacks);
        toiletries = findViewById(R.id.toiletries);
        cart = findViewById(R.id.cart);

        cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert cManager != null;
        nInfo = cManager.getActiveNetworkInfo();

        if (nInfo == null && !nInfo.isConnected()) {
            //when user not connected to the internet
            Intent intent = new Intent(ShoppingActivity.this, InternetActivity.class);
            startActivity(intent);
        }

        //get supermarket details
        SSupermarketID = getIntent().getStringExtra("id");
        SSupermarketName = getIntent().getStringExtra("name");
        SSupermarketImage = getIntent().getStringExtra("image");
        SSupermarketSlogan = getIntent().getStringExtra("slogan");
        SSupermarketDescription = getIntent().getStringExtra("description");
        SSupermarketRating = getIntent().getStringExtra("rating");

        if (!SSupermarketID.equals("") &&
                !SSupermarketName.equals("") &&
                !SSupermarketImage.equals("") &&
                !SSupermarketSlogan.equals("") &&
                !SSupermarketDescription.equals("") &&
                !SSupermarketRating.equals("")) {
            //all information from previous activity is passed successfully
            loading_dialog = new Dialog(ShoppingActivity.this);
            loading_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loading_dialog.setCancelable(false);
            loading_dialog.setContentView(R.layout.dialog_loading);
            loading_dialog.show();

            initialize();
        } else {
            onBackPressed();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to previous activity
                onBackPressed();
            }
        });

        supermarket_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open dialog for supermarket description
                showDescriptionDialog();
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to cart page
                goToCart();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearch();
            }
        });

        offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("offers");
            }
        });

        baby_kids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("baby_kids");
            }
        });

        beauty_cosmetics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("beauty_cosmetics");
            }
        });

        canned_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("canned_goods");
            }
        });

        cleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("cleaning");
            }
        });

        cooking_oil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("cooking_oil");
            }
        });

        dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("dairy");
            }
        });

        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("drinks");
            }
        });

        food_cupboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("food_cupboard");
            }
        });

        fresh_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("fresh_food");
            }
        });

        frozen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("frozen");
            }
        });

        health_wellness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("health_wellness");
            }
        });

        household.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("household");
            }
        });

        kitchen_dining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("kitchen_dining");
            }
        });

        office_supplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("office_supplies");
            }
        });

        sauces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("sauces");
            }
        });

        snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("snacks");
            }
        });

        toiletries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategories("toiletries");
            }
        });
    }

    private void goToCart() {
        //go to cart page
        Intent intent = new Intent(ShoppingActivity.this, CartActivity.class);
        startActivity(intent);
    }

    private void goToCategories(String category) {
        //start categories activity pass category to the intent
        Intent intent = new Intent(ShoppingActivity.this, CategoriesActivity.class);
        intent.putExtra("category_selected", category);
        intent.putExtra("supermarketID", SSupermarketID);
        intent.putExtra("supermarketName", SSupermarketName);
        intent.putExtra("country", SCountry);
        startActivity(intent);
    }

    private void goToSearch() {
        //open search activity
        Intent intent = new Intent(ShoppingActivity.this, SearchActivity.class);
        intent.putExtra("id", SSupermarketID);
        intent.putExtra("name", SSupermarketName);
        startActivity(intent);
    }

    private void showDescriptionDialog() {
        //show dialog
        description_dialog = new Dialog(ShoppingActivity.this);
        description_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        description_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        description_dialog.setCancelable(false);
        description_dialog.setContentView(R.layout.dialog_description);

        ImageView close_dialog = description_dialog.findViewById(R.id.close_dialog);
        TextView dialog_text = description_dialog.findViewById(R.id.dialog_text);
        ImageView dialog_image = description_dialog.findViewById(R.id.dialog_image);
        RatingBar dialog_supermarket_rating = description_dialog.findViewById(R.id.dialog_supermarket_rating);

        dialog_supermarket_rating.setRating(Float.parseFloat(SSupermarketRating));

        Glide
                .with(ShoppingActivity.this)
                .load(Constants.BASE_URL_SUPERMARKET_LOGOS + SSupermarketImage)
                .error(R.drawable.logo)
                .crossFade()
                .into(dialog_image);

        dialog_text.setText(SSupermarketDescription);

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close dialog
                description_dialog.dismiss();
            }
        });

        description_dialog.show();
    }

    private void initialize() {
        getUserInfoFromPreferences();

        Glide
                .with(ShoppingActivity.this)
                .load(Constants.BASE_URL_SUPERMARKET_LOGOS + SSupermarketImage)
                .error(R.drawable.logo)
                .crossFade()
                .into(supermarket_logo);

        supermarket_slogan.setText(SSupermarketSlogan);

        new GetSlidingAds().execute(new ApiConnector());

        new GetEssentialProducts().execute(new ApiConnector());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            //show install google services dialog
            play_services_dialog = new Dialog(ShoppingActivity.this);
            play_services_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            play_services_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            play_services_dialog.setCancelable(false);
            play_services_dialog.setContentView(R.layout.dialog_google_play_services);
            play_services_dialog.show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSlidingAds extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            return params[0].GetSupermarketSlidingAds(SCountry, SSupermarketName);
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
                //get all supermarkets from the server in user's country
                //new GetAvailableSupermarketsTask().execute(new ApiConnector());

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
                        .with(ShoppingActivity.this)
                        .load(SlidingImagesList.get(position))
                        .fitCenter()
                        .crossFade()
                        .into(imageView);
            }
        });

        carouselView.setPageCount(SlidingImagesList.size());
    }

    private void getUserInfoFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SCountry = sharedPreferences.getString(Constants.COUNTRY, "N/A");
        SpersonID = sharedPreferences.getString(Constants.PERSON_ID, "N/A");
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else
                finish();

            return false;
        }
        return true;
    }

    public void setProductsAdapter(JSONArray jsonArray) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(staggeredGridLayoutManager);
        try {
            recycler_view.setAdapter(new ProductsAdapter(jsonArray, this, recycler_view, SSupermarketID, SSupermarketName));
            loading_dialog.dismiss();
            if (jsonArray == null) {
                available.setVisibility(View.VISIBLE);
                //swipe_refresh_layout.setVisibility(View.VISIBLE);
            } else {
                available.setVisibility(View.GONE);
            }
            //swipe_refresh_layout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetEssentialProducts extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            try {
                return params[0].GetEssentialProducts(SCountry);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setProductsAdapter(jsonArray);
        }
    }

}
