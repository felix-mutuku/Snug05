package com.snugjar.www.youshopwedeliver.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.snugjar.www.youshopwedeliver.Connectors.ApiConnector;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class BranchesActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    Dialog loading_dialog, location_dialog, play_services_dialog, branches_info;
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    String SSupermarketID, SSupermarketName, SSupermarketImage, SSupermarketSlogan, SSupermarketDescription,
            SSupermarketRating, SpersonID, SCountry, SServerTime, duration, distance, OLatitude, OLongitude, SLocation,
            SBranchSelected, BLatitude, BLongitude, SDeliveryPrice, SPickupPrice, SCurrency, SPricePerKilometre;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 30000;//30 seconds
    private long FASTEST_INTERVAL = 15000;//15 seconds
    RelativeLayout relative_confirm_branch, relative_branches, relative_current_location, relative_loading;
    TextView user_location, dialog_branch_name, dialog_branch_distance, dialog_delivery_duration, dialog_delivery_cost,
            dialog_essentials_cost, back;
    Button retry_location, confirm_location, confirm_branch;
    RecyclerView branch_recycler_view;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    int finalDeliveryCost1, finalEssentialCost1;
    ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);

        relative_confirm_branch = findViewById(R.id.relative_confirm_branch);
        relative_branches = findViewById(R.id.relative_branches);
        relative_current_location = findViewById(R.id.relative_current_location);
        retry_location = findViewById(R.id.retry_location);
        confirm_location = findViewById(R.id.confirm_location);
        user_location = findViewById(R.id.user_location);
        relative_loading = findViewById(R.id.relative_loading);
        branch_recycler_view = findViewById(R.id.branch_recycler_view);
        dialog_branch_name = findViewById(R.id.dialog_branch_name);
        dialog_branch_distance = findViewById(R.id.dialog_branch_distance);
        dialog_delivery_duration = findViewById(R.id.dialog_delivery_duration);
        dialog_delivery_cost = findViewById(R.id.dialog_delivery_cost);
        confirm_branch = findViewById(R.id.confirm_branch);
        dialog_essentials_cost = findViewById(R.id.dialog_essentials_cost);
        info = findViewById(R.id.info);
        back = findViewById(R.id.back);

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

        cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert cManager != null;
        nInfo = cManager.getActiveNetworkInfo();

        if (nInfo == null && !nInfo.isConnected()) {
            //when user not connected to the internet
            Intent intent = new Intent(BranchesActivity.this, InternetActivity.class);
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
            loading_dialog = new Dialog(BranchesActivity.this);
            loading_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loading_dialog.setCancelable(false);
            loading_dialog.setContentView(R.layout.dialog_loading);
            loading_dialog.show();

            initialize();
        } else {
            onBackPressed();
        }

        retry_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retry looking for location

            }
        });

        confirm_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayBranches();//when user confirms location
            }
        });

        confirm_branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeShoppingDetails(duration, finalDeliveryCost1, finalEssentialCost1);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show information about selecting branches
                showInfoDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go backwards
                onBackPressed();
            }
        });

    }

    private void showInfoDialog() {
        //ask user to enable location services
        branches_info = new Dialog(BranchesActivity.this);
        branches_info.requestWindowFeature(Window.FEATURE_NO_TITLE);
        branches_info.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        branches_info.setCancelable(false);
        branches_info.setContentView(R.layout.dialog_branches_info);

        Button done = branches_info.findViewById(R.id.dialog_done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take user to settings to enable location manually
                branches_info.dismiss();
            }
        });

        branches_info.show();
    }

    private void initialize() {
        new CheckTimeFromServer().execute(new ApiConnector());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            try {
                OLatitude = String.valueOf(mLocation.getLatitude());
                OLongitude = String.valueOf(mLocation.getLongitude());

                SLocation = new GetUserLocation(mLocation.getLatitude(), mLocation.getLongitude()).execute(new ApiConnector()).get();
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

    @Override
    public void onLocationChanged(Location location) {

    }

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetUserLocation extends AsyncTask<ApiConnector, Long, String> {
        double Latitude, Longitude;

        GetUserLocation(double latitude, double longitude) {
            Latitude = latitude;
            Longitude = longitude;
        }

        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].GetUserLocation(Latitude, Longitude);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPostExecute(String args) {
            //pass arguments to display on branches
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

    private void getUserInfoFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SCountry = sharedPreferences.getString(Constants.COUNTRY, "N/A");
        SpersonID = sharedPreferences.getString(Constants.PERSON_ID, "N/A");
        SDeliveryPrice = sharedPreferences.getString(Constants.DELIVERY_PRICE, "N/A");
        SPickupPrice = sharedPreferences.getString(Constants.PICKUP_PRICE, "N/A");
        SCurrency = sharedPreferences.getString(Constants.CURRENCY, "N/A");
        SPricePerKilometre = sharedPreferences.getString(Constants.PRICE_PER_KILOMETRE, "N/A");
    }

    private void showLocationDialog() {
        //ask user to enable location services
        location_dialog = new Dialog(BranchesActivity.this);
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
                    Intent intent = new Intent(BranchesActivity.this, SplashScreenActivity.class);
                    startActivity(intent);
                    finish();//close current activity
                }
            }
        });
        location_dialog.show();
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
                displayConfirmLocation();
            } else {
                //time was not returned
                //try getting time again from the server
                new CheckTimeFromServer().execute(new ApiConnector());
            }
        }
    }

    private void displayConfirmLocation() {
        relative_current_location.setVisibility(View.VISIBLE);
        relative_branches.setVisibility(View.INVISIBLE);
        relative_confirm_branch.setVisibility(View.INVISIBLE);
        user_location.setText(SLocation);
    }

    private void displayBranches() {
        relative_current_location.setVisibility(View.INVISIBLE);
        relative_branches.setVisibility(View.VISIBLE);
        relative_confirm_branch.setVisibility(View.INVISIBLE);

        relative_loading.setVisibility(View.VISIBLE);
        new GetAllSupermarketBranches().execute(new ApiConnector());
    }

    private void showSelectedBranch() {
        try {
            relative_current_location.setVisibility(View.INVISIBLE);
            relative_branches.setVisibility(View.INVISIBLE);
            relative_confirm_branch.setVisibility(View.VISIBLE);

            //show user the branch, distance and time it will take to deliver the items by road
            String[] combined;
            combined = new GetBranchDistance(BLatitude, BLongitude).execute(new ApiConnector()).get();

            distance = combined[0];
            duration = combined[1];

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
                double_distance = double_distance * Integer.parseInt(SPricePerKilometre);//get price of distance over threshold
                Long L = Math.round(double_distance);
                int i = L.intValue();
                //int ei = L.intValue()/2;
                //calculated price of normal deliveries
                dialog_delivery_cost.setText(String.format("%s %s %s", getString(R.string.delivery_cost),
                        i, SCurrency));

                finalDeliveryCost = i;

                dialog_essentials_cost.setVisibility(View.GONE);//make it disappear since we don't need it

            } else {
                int i = 200;
                int ei = 100;
                //calculated price of normal deliveries
                dialog_delivery_cost.setText(String.format("%s %s %s", getString(R.string.delivery_cost),
                        i, SCurrency));
                //calculated price of essentials deliveries
                dialog_essentials_cost.setText(String.format("%s %s %s", getString(R.string.e_delivery_cost),
                        ei, SCurrency));

                finalDeliveryCost = i;
                finalEssentialCost = ei;
            }

            finalDeliveryCost1 = finalDeliveryCost;
            finalEssentialCost1 = finalEssentialCost;

            loading_dialog.dismiss();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void setBranchesAdapter(JSONArray jsonArray) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
        branch_recycler_view.setLayoutManager(staggeredGridLayoutManager);
        try {
            branch_recycler_view.setAdapter(new AllBranchesAdapter(jsonArray));
            //branch_r_view.smoothScrollToPosition(0);
            relative_loading.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class GetAllSupermarketBranches extends AsyncTask<ApiConnector, Long, JSONArray> {

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
            setBranchesAdapter(jsonArray);
        }
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
        public AllBranchesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.supermarket_branches_layout, parent, false);
            return new AllBranchesAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final AllBranchesAdapter.MyViewHolder holder, final int position) {
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
                                loading_dialog.show();
                                showSelectedBranch();
                            } else {
                                //show toast that supermarket is closed
                                Toast toast = Toast.makeText(BranchesActivity.this, "This branch is closed!!" +
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

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView supermarket_name;
            TextView supermarket_availability;

            MyViewHolder(View itemView) {
                super(itemView);
                supermarket_name = itemView.findViewById(R.id.supermarket_name);
                supermarket_availability = itemView.findViewById(R.id.supermarket_availability);
            }
        }
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
            loading_dialog.dismiss();
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
            play_services_dialog = new Dialog(BranchesActivity.this);
            play_services_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            play_services_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            play_services_dialog.setCancelable(false);
            play_services_dialog.setContentView(R.layout.dialog_google_play_services);
            play_services_dialog.show();
        }
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
    private class updateUserLocation extends AsyncTask<ApiConnector, Long, String> {
        Double DLatitude, DLongitude;

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
                    goToShoppingActivity();
                } else {
                    goToShoppingActivity();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void goToShoppingActivity() {
        //go to next activity
        //pass data needed to next activity
        Intent intent = new Intent(BranchesActivity.this, ShoppingActivity.class);
        intent.putExtra("id", SSupermarketID);
        intent.putExtra("name", SSupermarketName);
        intent.putExtra("image", SSupermarketImage);
        intent.putExtra("slogan", SSupermarketSlogan);
        intent.putExtra("description", SSupermarketDescription);
        intent.putExtra("rating", SSupermarketRating);
        startActivity(intent);
    }


}
