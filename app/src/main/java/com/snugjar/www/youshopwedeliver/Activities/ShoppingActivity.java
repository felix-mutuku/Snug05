package com.snugjar.www.youshopwedeliver.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.snugjar.www.youshopwedeliver.Adapters.ProductsAdapter;
import com.snugjar.www.youshopwedeliver.Connectors.ApiConnector;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ShoppingActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    TextView back, supermarket_slogan, branch_text, offers, baby_kids, beauty_cosmetics, canned_goods, cleaning, cooking_oil,
            dairy, drinks, food_cupboard, fresh_food, frozen, health_wellness, household, kitchen_dining, office_supplies,
            sauces, snacks, toiletries;
    Dialog loading_dialog, description_dialog, branches_dialog, location_dialog, play_services_dialog, confirm_location_dialog,
            selected_branch_dialog;
    String SSupermarketID, SSupermarketName, SSupermarketImage, SSupermarketSlogan, SSupermarketDescription,
            SSupermarketRating, SCountry, SLocation, OLatitude, OLongitude, SServerTime, SBranchSelected, BLatitude,
            BLongitude, SpersonID;
    ImageView supermarket_logo, supermarket_info, available, search;
    ArrayList<String> SlidingImagesList = new ArrayList<String>();
    CarouselView carouselView;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 30000;//30 seconds
    private long FASTEST_INTERVAL = 15000;//15 seconds
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
        branch_text = findViewById(R.id.branch_text);
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

        cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert cManager != null;
        nInfo = cManager.getActiveNetworkInfo();

        if (nInfo == null && !nInfo.isConnected()) {
            //when user not connected to the internet
            Intent intent = new Intent(ShoppingActivity.this, InternetActivity.class);
            startActivity(intent);
        }

        //check to see whether user has enabled location services
        if (isLocationServiceEnabled()) {
            //location services are enabled
            //get profile information of the user
            getUserInfoFromPreferences();
            //look for user precise location
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        } else {
            //prompt user to turn on location services
            showLocationDialog();
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
    }

    private void displayConfirmLocationDialog() {
        //ask user to confirm the auto selected location
        //show dialog
        confirm_location_dialog = new Dialog(ShoppingActivity.this);
        confirm_location_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirm_location_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirm_location_dialog.setCancelable(false);
        confirm_location_dialog.setContentView(R.layout.dialog_user_location);

        TextView user_location = confirm_location_dialog.findViewById(R.id.user_location);
        Button retry_location = confirm_location_dialog.findViewById(R.id.retry_location);
        Button confirm_location = confirm_location_dialog.findViewById(R.id.confirm_location);

        //show user current location
        user_location.setText(SLocation);

        retry_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retry looking for location
                mGoogleApiClient.connect();
                confirm_location_dialog.dismiss();
                confirm_location_dialog.show();
            }
        });

        confirm_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_location_dialog.dismiss();//close current dialog
                displayBranchDialog();//when user confirms location
            }
        });

        confirm_location_dialog.show();
    }

    private void displayBranchDialog() {
        //get the distance of all available branches and prompt user to select a branch
        //show dialog
        branches_dialog = new Dialog(ShoppingActivity.this);
        branches_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        branches_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        branches_dialog.setCancelable(false);
        branches_dialog.setContentView(R.layout.dialog_branches);

        RecyclerView branch_recycler_view = branches_dialog.findViewById(R.id.branch_recycler_view);
        RelativeLayout relative_loading = branches_dialog.findViewById(R.id.relative_loading);
        ImageView close_dialog = branches_dialog.findViewById(R.id.close_dialog);

        relative_loading.setVisibility(View.VISIBLE);
        new GetAllSupermarketBranches(branch_recycler_view, relative_loading).execute(new ApiConnector());

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close dialog and close activity
                branches_dialog.dismiss();
                finish();
            }
        });

        branches_dialog.show();
    }

    public void setBranchesAdapter(JSONArray jsonArray, RecyclerView branch_r_view, RelativeLayout relative_loading) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
        branch_r_view.setLayoutManager(staggeredGridLayoutManager);
        try {
            branch_r_view.setAdapter(new AllBranchesAdapter(jsonArray));
            branch_r_view.smoothScrollToPosition(0);
            relative_loading.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class GetAllSupermarketBranches extends AsyncTask<ApiConnector, Long, JSONArray> {
        RecyclerView branch_r_view;
        RelativeLayout relative_loading;

        GetAllSupermarketBranches(RecyclerView branch_recycler_view, RelativeLayout r_loading) {
            branch_r_view = branch_recycler_view;
            relative_loading = r_loading;
        }

        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            try {
                return params[0].GetAllBranches(SSupermarketID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            relative_loading.setVisibility(View.VISIBLE);
            setBranchesAdapter(jsonArray, branch_r_view, relative_loading);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            try {
                OLatitude = String.valueOf(mLocation.getLatitude());
                OLongitude = String.valueOf(mLocation.getLongitude());

                getAddress(ShoppingActivity.this, mLocation.getLatitude(), mLocation.getLongitude());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        /*mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //user has moved from start location
        if (location != null) {
            try {
                getAddress(ShoppingActivity.this, mLocation.getLatitude(), mLocation.getLongitude());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    protected void onDestroy() {
        //when app is destroyed
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    protected void onUserLeaveHint() {
        //when user presses home or a phone call comes in
        super.onUserLeaveHint();
        try {
            stopLocationUpdates();
            branches_dialog.dismiss();
            loading_dialog.dismiss();
            confirm_location_dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLocationUpdates() {
        try {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void getAddress(Context context, double LATITUDE, double LONGITUDE) {
        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {

                String address = addresses.get(0).getAddressLine(0);
                //getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); //Only if available else return NULL

                //Log.d("D", "getAddress:  address" + address);
                //Log.d("D", "getAddress:  city" + city);
                //Log.d("D", "getAddress:  state" + state);
                //Log.d("D", "getAddress:  postalCode" + postalCode);//mostly returns null
                //Log.d("D", "getAddress:  knownName" + knownName);
                //Log.d("D", "getAddress:  country" + country);

                SLocation = address;

                new CheckTimeFromServer().execute(new ApiConnector());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class CheckTimeFromServer extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckTimeFromServer();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            if (!response.equals("error")) {
                //returned time successfully
                SServerTime = response.replaceAll(":", "");
                loading_dialog.dismiss();

                //when finished checking time
                displayConfirmLocationDialog();
            } else {
                //time was not returned
                //try getting time again from the server
                new CheckTimeFromServer().execute(new ApiConnector());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
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

    public boolean isLocationServiceEnabled() {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
            ex.printStackTrace();
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
            ex.printStackTrace();
        }

        return gps_enabled || network_enabled;
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

    private void showLocationDialog() {
        //ask user to enable location services
        location_dialog = new Dialog(ShoppingActivity.this);
        location_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        location_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        location_dialog.setCancelable(false);
        location_dialog.setContentView(R.layout.dialog_location);

        Button enable_location = location_dialog.findViewById(R.id.enable_location);
        Button location_retry = location_dialog.findViewById(R.id.location_retry);

        enable_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take user to settings to enable location manually
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });

        location_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check location services again
                if (isLocationServiceEnabled()) {
                    //location services are available
                    location_dialog.dismiss();
                    //restart the app for good measure
                    Intent intent = new Intent(ShoppingActivity.this, SplashScreenActivity.class);
                    startActivity(intent);
                    finish();//close current activity
                }
            }
        });
        location_dialog.show();
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

    private class AllBranchesAdapter extends RecyclerView.Adapter<AllBranchesAdapter.MyViewHolder> {
        private JSONArray dataArray;
        private LayoutInflater inflater = null;

        AllBranchesAdapter(JSONArray jsonArray) {
            try {
                dataArray = jsonArray;
                inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.supermarket_branches_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            try {
                JSONObject jsonObject = this.dataArray.getJSONObject(position);

                String branch = String.format("%s %s", jsonObject.getString("supermarket"),
                        jsonObject.getString("supermarket_branch"));
                String openingTime = jsonObject.getString("opening_time");
                String closingTime = jsonObject.getString("closing_time");
                openingTime = openingTime.replaceAll(":", "");
                closingTime = closingTime.replaceAll(":", "");

                holder.supermarket_name.setText(branch);

                if (Integer.valueOf(openingTime) > Integer.valueOf(SServerTime) ||
                        Integer.valueOf(closingTime) < Integer.valueOf(SServerTime)) {
                    //show that the supermarket is closed
                    holder.supermarket_availability.setText(R.string.closed);
                    holder.supermarket_availability.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    //show that supermarket is open
                    holder.supermarket_availability.setText(R.string.open);
                    holder.supermarket_availability.setTextColor(getResources().getColor(R.color.dark_gray));
                }

                //implement setOnClickListener event on item view.
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final JSONObject json2Object;

                        try {
                            json2Object = dataArray.getJSONObject(position);
                            String available = holder.supermarket_availability.getText().toString();
                            BLatitude = json2Object.getString("latitude");
                            BLongitude = json2Object.getString("longitude");
                            SBranchSelected = String.format("%s %s", json2Object.getString("supermarket"),
                                    json2Object.getString("supermarket_branch"));

                            if (available.equals("Open")) {
                                //open supermarket clicked by the user
                                branches_dialog.dismiss();
                                loading_dialog.show();
                                showSelectedBranchDialog();
                            } else {
                                //show toast that supermarket is closed
                                Toast toast = Toast.makeText(ShoppingActivity.this, "This branch is closed!!" +
                                                "\nPlease try again when it opens.",
                                        Toast.LENGTH_SHORT);
                                View toastView = toast.getView(); //This'll return the default View of
                                //the Toast.
                                TextView toastMessage = toastView.findViewById(android.R.id.message);
                                toastMessage.setTextSize(12);
                                toastMessage.setTextColor(getResources().getColor(R.color.white));
                                toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                                toastMessage.setGravity(Gravity.CENTER);
                                toastMessage.setCompoundDrawablePadding(10);
                                toastView.setBackground(getResources().getDrawable(R.drawable.bg_button));
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
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
                return dataArray.length();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView supermarket_name;
            TextView supermarket_availability;

            MyViewHolder(View itemView) {
                super(itemView);
                supermarket_name = itemView.findViewById(R.id.supermarket_name);
                supermarket_availability = itemView.findViewById(R.id.supermarket_availability);
            }
        }
    }

    private void showSelectedBranchDialog() {
        try {
            selected_branch_dialog = new Dialog(ShoppingActivity.this);
            selected_branch_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            selected_branch_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            selected_branch_dialog.setCancelable(false);
            selected_branch_dialog.setContentView(R.layout.dialog_selected_branch);

            TextView back = selected_branch_dialog.findViewById(R.id.back);
            TextView dialog_branch_name = selected_branch_dialog.findViewById(R.id.dialog_branch_name);
            TextView dialog_branch_distance = selected_branch_dialog.findViewById(R.id.dialog_branch_distance);
            TextView dialog_delivery_duration = selected_branch_dialog.findViewById(R.id.dialog_delivery_duration);
            TextView dialog_delivery_cost = selected_branch_dialog.findViewById(R.id.dialog_delivery_cost);
            Button confirm_branch = selected_branch_dialog.findViewById(R.id.confirm_branch);
            TextView dialog_essentials_cost = selected_branch_dialog.findViewById(R.id.dialog_essentials_cost);

            //show user the branch, distance and time it will take to deliver the items by road
            String[] combined = new GetBranchDistance(BLatitude, BLongitude).execute(new ApiConnector()).get();
            String distance = combined[0];
            final String duration = combined[1];

            dialog_branch_distance.setText(String.format("%s %s", getString(R.string.distance), distance));

            //split integer from string
            String[] part = distance.split("(?<=\\d)( )(?=[a-zA-Z])");
            distance = part[0];

            dialog_branch_name.setText(SBranchSelected);
            dialog_delivery_duration.setText(String.format("%s %s", getString(R.string.duration), duration));

            double double_distance = Double.parseDouble(distance);

            int finalDeliveryCost = 0;
            int finalEssentialCost = 0;

            if (double_distance > 10) {
                //user has passed the threshold KMs of 10KMs
                //charge 20KES per KM after the 10KMs
                double_distance = double_distance * 20;//get price of distance over threshold
                Long L = Math.round(double_distance);
                int i = L.intValue();
                //int ei = L.intValue()/2;
                //calculated price of normal deliveries
                dialog_delivery_cost.setText(String.format("%s %s %s", getString(R.string.delivery_cost),
                        i, getString(R.string.kenya_shillings)));

                finalDeliveryCost = i;

                dialog_essentials_cost.setVisibility(View.GONE);//make it disappear since we don't need it

            } else {
                int i = 200;
                int ei = 100;
                //calculated price of normal deliveries
                dialog_delivery_cost.setText(String.format("%s %s %s", getString(R.string.delivery_cost),
                        i, getString(R.string.kenya_shillings)));
                //calculated price of essentials deliveries
                dialog_essentials_cost.setText(String.format("%s %s %s", getString(R.string.e_delivery_cost),
                        ei, getString(R.string.kenya_shillings)));

                finalDeliveryCost = i;
                finalEssentialCost = ei;
            }

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //back pressed, go to previous branches dialog
                    selected_branch_dialog.dismiss();
                    branches_dialog.show();
                }
            });

            final int finalDeliveryCost1 = finalDeliveryCost;
            final int finalEssentialCost1 = finalEssentialCost;
            confirm_branch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //when user has confirmed the branch they want to shop in
                    selected_branch_dialog.dismiss();
                    //loading_dialog.show();
                    branch_text.setText(String.format("Shopping from %s", SBranchSelected));//show user branch they are shopping from

                    new GetEssentialProducts().execute(new ApiConnector());

                    storeShoppingDetails(duration, finalDeliveryCost1, finalEssentialCost1);
                }
            });

            selected_branch_dialog.show();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void storeShoppingDetails(String finalDuration, int finalDeliveryCost, int finalEssentialCost) {
        //stores shopping details and location of the user
        //store supermarket, branch, delivery duration, delivery cost, essentials delivery, set prompting to false
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SUPERMARKET_NAME, SSupermarketName);
        editor.putString(Constants.SUPERMARKET_BRANCH, SBranchSelected);
        editor.putString(Constants.DELIVERY_LATITUDE, OLatitude);
        editor.putString(Constants.DELIVERY_LONGITUDE, OLongitude);
        editor.putString(Constants.DELIVERY_DURATION, finalDuration);
        editor.putString(Constants.DELIVERY_COST, String.valueOf(finalDeliveryCost));
        editor.putString(Constants.ESSENTIALS_DELIVERY_COST, String.valueOf(finalEssentialCost));
        editor.apply();

        new updateUserLocation(Double.parseDouble(OLatitude), Double.parseDouble(OLongitude)).execute(new ApiConnector());
    }

    @SuppressLint("StaticFieldLeak")
    private class GetBranchDistance extends AsyncTask<ApiConnector, Long, String[]> {
        String DLatitude, DLongitude;

        GetBranchDistance(String latitude, String longitude) {
            DLatitude = latitude;
            DLongitude = longitude;
        }

        @Override
        protected String[] doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].GetBranchDistance(OLatitude, OLongitude, DLatitude, DLongitude);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPostExecute(String[] args) {
            //pass arguments to display on branches
        }
    }

    public void setProductsAdapter(JSONArray jsonArray) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(staggeredGridLayoutManager);
        try {
            recycler_view.setAdapter(new ProductsAdapter(jsonArray, this, recycler_view, SSupermarketID));
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

    @SuppressLint("StaticFieldLeak")
    private class updateUserLocation extends AsyncTask<ApiConnector, Long, String> {
        Double DLatitude, DLongitude;
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        updateUserLocation(double latitude, double longitude) {
            DLatitude = latitude;
            DLongitude = longitude;
        }

        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].UpdateUserLocation(SpersonID, DLatitude, DLongitude);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                if (response.equals("Successful")) {
                    //confirm that location in database is updated
                    /*Toast toast = Toast.makeText(ShoppingActivity.this, "Location Updated :)", Toast.LENGTH_SHORT);
                    View toastView = toast.getView(); //This'll return the default View of the toast
                    TextView toastMessage = toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(12);
                    toastMessage.setTextColor(getResources().getColor(R.color.white));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(10);
                    toastView.setBackground(getResources().getDrawable(R.drawable.bg_button));
                    toast.show();*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
