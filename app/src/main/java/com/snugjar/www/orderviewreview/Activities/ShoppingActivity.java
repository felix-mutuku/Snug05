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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.snugjar.www.orderviewreview.Connectors.ApiConnector;
import com.snugjar.www.orderviewreview.Connectors.Constants;
import com.snugjar.www.orderviewreview.Fragments.CategoriesFragment;
import com.snugjar.www.orderviewreview.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShoppingActivity extends AppCompatActivity {
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    TextView back, supermarket_slogan;
    Dialog loading_dialog, description_dialog, location_dialog, play_services_dialog;
    String SSupermarketID, SSupermarketName, SSupermarketImage, SSupermarketSlogan, SSupermarketDescription,
            SSupermarketRating, SCountry, SpersonID;
    ImageView supermarket_logo, supermarket_info, available, search, cart;
    ArrayList<String> SlidingImagesList = new ArrayList<String>();
    CarouselView carouselView;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    RecyclerView recycler_view;
    TabLayout tabs_layout;
    ViewPager viewPager;

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
        cart = findViewById(R.id.cart);
        viewPager = findViewById(R.id.viewpager);
        tabs_layout = findViewById(R.id.tabs_layout);

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
    }

    private void goToCart() {
        //go to cart page
        Intent intent = new Intent(ShoppingActivity.this, CartActivity.class);
        startActivity(intent);
    }

    /*private void goToCategories(String category) {
        //start categories activity pass category to the intent
        Intent intent = new Intent(ShoppingActivity.this, CategoriesActivity.class);
        intent.putExtra("category_selected", category);
        intent.putExtra("supermarketID", SSupermarketID);
        intent.putExtra("supermarketName", SSupermarketName);
        intent.putExtra("country", SCountry);
        startActivity(intent);
    }*/

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

        new GetSlidingImages().execute(new ApiConnector());

        categoriesViewPager(viewPager);
        tabs_layout.setupWithViewPager(viewPager);
    }

    private void categoriesViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, "popular", loading_dialog), "Popular");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, "breakfast", loading_dialog), "Breakfast");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, "meals", loading_dialog), "Meals");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, "drinks", loading_dialog), "Drinks");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, "extras", loading_dialog), "Extras");
        viewPager.setAdapter(adapter);
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
    private class GetSlidingImages extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            SSupermarketName = SSupermarketName.replaceAll(" ", "+");
            return params[0].GetFoodJointSlidingImages(SCountry, SSupermarketName);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    SlidingImagesList.add(object.getString("image"));
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /*public void setProductsAdapter(JSONArray jsonArray) {
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
    }*/

    /*@SuppressLint("StaticFieldLeak")
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
    }*/

}
