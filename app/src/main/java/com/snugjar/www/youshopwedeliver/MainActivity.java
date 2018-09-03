package com.snugjar.www.youshopwedeliver;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    CircleImageView profile_image;
    ImageView settings, adImage;
    EditText user_location;
    TextView supermarkets, orders, about, help, feedback, share_app, adQuestion, adHeadline, adOffer;
    String SpersonID, SImage, SName, SEmail, SIMEI, SUserType, SCountry, SPhone;
    Dialog loading_dialog, profile_dialog, play_services_dialog;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 15000;
    private long FASTEST_INTERVAL = 5000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_image = findViewById(R.id.profile_image);
        settings = findViewById(R.id.settings);
        adImage = findViewById(R.id.adImage);
        user_location = findViewById(R.id.user_location);
        supermarkets = findViewById(R.id.supermarkets);
        orders = findViewById(R.id.orders);
        about = findViewById(R.id.about);
        help = findViewById(R.id.help);
        feedback = findViewById(R.id.feedback);
        share_app = findViewById(R.id.share_app);
        adQuestion = findViewById(R.id.adQuestion);
        adHeadline = findViewById(R.id.adHeadline);
        adOffer = findViewById(R.id.adOffer);

        getUserInfoFromPreferences();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show profile dialog

            }
        });

    }

    private void getUserInfoFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SpersonID = sharedPreferences.getString(Constants.PERSON_ID, "N/A");

        if (!SpersonID.equals("N/A")) {
            //get user information from the server
            new GetUserInformation().execute(new ApiConnector());
            loading_dialog = new Dialog(MainActivity.this);
            loading_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loading_dialog.setCancelable(false);
            loading_dialog.setContentView(R.layout.rounded_dialog_loading);
            loading_dialog.show(); // don't forget to dismiss the dialog when done loading
        } else {
            //set login status to false and login again
            SharedPreferences getSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor e = getSharedPreferences.edit();
            e.putBoolean("isLoggedin", false);
            e.apply();
            //go to splash screen
            //Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
            //startActivity(intent);
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
            play_services_dialog = new Dialog(MainActivity.this);
            play_services_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            play_services_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            play_services_dialog.setCancelable(false);
            play_services_dialog.setContentView(R.layout.dialog_google_play_services);
            play_services_dialog.show();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            user_location.setText("Latitude : " + mLocation.getLatitude() + " , Longitude : " + mLocation.getLongitude());
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
        //user has moved from start location
        if (location != null) {
            user_location.setText("Latitude : " + location.getLatitude() + " , Longitude : " + location.getLongitude());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
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

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetUserInformation extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].GetUserInformation(SpersonID);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                JSONObject item = jsonArray.getJSONObject(0);

                SImage = item.getString("photo");
                SName = item.getString("name");
                SEmail = item.getString("email");
                SIMEI = item.getString("device_imei");
                SUserType = item.getString("type");
                SCountry = item.getString("country");
                SPhone = item.getString("phone");

                Glide
                        .with(MainActivity.this)
                        .load(SImage)
                        .error(R.drawable.logo)
                        .crossFade()
                        .into(profile_image);

                updateSharedPreferences();

            } catch (Exception e) {
                e.printStackTrace();
                loading_dialog.dismiss();
                getUserInfoFromPreferences();
            }
        }
    }

    private void updateSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PROFILE_PHOTO, SImage);
        editor.putString(Constants.NAME, SName);
        editor.putString(Constants.EMAIL, SEmail);
        editor.putString(Constants.IMEI, SIMEI);
        editor.putString(Constants.USER_TYPE, SUserType);
        editor.putString(Constants.COUNTRY, SCountry);
        editor.putString(Constants.PHONE, SPhone);
        editor.apply();

        loading_dialog.dismiss();
    }
}
