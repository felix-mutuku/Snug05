package com.snugjar.www.youshopwedeliver;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    CircleImageView profile_image;
    ImageView settings, adImage;
    EditText user_location;
    TextView supermarkets, orders, about, help, feedback, share_app, adQuestion, adHeadline, adOffer;
    String SpersonID, SImage, SName, SEmail, SIMEI, SUserType, SCountry, SPhone, SJoinDate, CSPhone;
    Dialog loading_dialog, profile_dialog, play_services_dialog;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 30000;//30 seconds
    private long FASTEST_INTERVAL = 15000;//15 seconds
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
                showProfileDialog();
            }
        });

    }

    private void showProfileDialog() {
        //customise profile dialog
        profile_dialog = new Dialog(MainActivity.this);
        profile_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        profile_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profile_dialog.setCancelable(false);
        profile_dialog.setContentView(R.layout.dialog_profile);

        final ImageView close_dialog = profile_dialog.findViewById(R.id.close_dialog);
        TextView personCountry = profile_dialog.findViewById(R.id.personCountry);
        CircleImageView profile_image = profile_dialog.findViewById(R.id.profile_image);
        EditText personName = profile_dialog.findViewById(R.id.personName);
        EditText personEmail = profile_dialog.findViewById(R.id.personEmail);
        EditText personJoinDate = profile_dialog.findViewById(R.id.personJoinDate);
        final EditText personPhone = profile_dialog.findViewById(R.id.personPhone);
        final AVLoadingIndicatorView loading_dialog = profile_dialog.findViewById(R.id.loading_dialog);
        final Button save_details = profile_dialog.findViewById(R.id.save_details);

        save_details.setVisibility(View.GONE);

        //load profile picture and details
        Glide
                .with(MainActivity.this)
                .load(SImage)
                .error(R.drawable.logo)
                .crossFade()
                .into(profile_image);

        personName.setText(SName);
        personEmail.setText(SEmail);
        personCountry.setText(SCountry);
        personPhone.setText(SPhone);
        personJoinDate.setText(String.format("From %s to Present", SJoinDate));

        //checking whether user has changed original stored phone number
        personPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredString = s.toString();
                //making number start with 254
                if (enteredString.startsWith("0")) {
                    personPhone.setText("254" + enteredString.substring(1));
                    personPhone.setSelection(personPhone.getText().length());
                } else if (enteredString.startsWith("+")) {
                    personPhone.setText("254" + enteredString.substring(4));
                    personPhone.setSelection(personPhone.getText().length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //before typing
            }

            @Override
            public void afterTextChanged(Editable s) {
                //after typing
                //check for changes in phone number
                CSPhone = personPhone.getText().toString();
                if (!CSPhone.equals(SPhone)) {
                    save_details.setVisibility(View.VISIBLE);
                } else {
                    save_details.setVisibility(View.GONE);
                }
            }
        });

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close dialog without saving
                profile_dialog.dismiss();
                getUserInfoFromPreferences();
            }
        });

        save_details.setOnClickListener(new View.OnClickListener() {
            //checking if user mobile number already exists in the database
            @SuppressLint("StaticFieldLeak")
            class CheckUserMobile extends AsyncTask<ApiConnector, Long, String> {
                @Override
                protected String doInBackground(ApiConnector... params) {
                    //it is executed on Background thread
                    return params[0].CheckUserMobile(SPhone);
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                protected void onPostExecute(String response) {
                    try {
                        if (Objects.equals(response, "Exist")) {
                            //entered phone number is already in the database
                            Toast toast = Toast.makeText(MainActivity.this, "Phone number already registered !!\nType a different phone " +
                                    "number.", Toast.LENGTH_LONG);
                            View toastView = toast.getView(); //This'll return the default View of the Toast.
                            TextView toastMessage = toastView.findViewById(android.R.id.message);
                            toastMessage.setTextSize(12);
                            toastMessage.setTextColor(getResources().getColor(R.color.white));
                            toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                            toastMessage.setGravity(Gravity.CENTER);
                            toastMessage.setCompoundDrawablePadding(10);
                            toastView.setBackground(getResources().getDrawable(R.drawable.bg_button));
                            toast.show();
                            //return UI to normal state
                            loading_dialog.setVisibility(View.GONE);
                            save_details.setVisibility(View.VISIBLE);
                            close_dialog.setVisibility(View.VISIBLE);
                        } else {
                            //number entered isn't in the database, proceed to adding user to Database
                            //confirm all the details entered by the user and send data to database
                            try {
                                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (personPhone.length() > 0) {
                                //name not empty
                                if (personPhone.length() > 11) {
                                    SPhone = personPhone.getText().toString();
                                    if (PhoneNumberUtils.isGlobalPhoneNumber(SPhone)) {
                                        //hide UI elements to avoid HTTP collusion
                                        loading_dialog.setVisibility(View.VISIBLE);
                                        save_details.setVisibility(View.INVISIBLE);
                                        close_dialog.setVisibility(View.INVISIBLE);
                                        //send confirmed data to database
                                        new updateUser().execute(new ApiConnector());
                                    } else {
                                        //number is not a valid phone number
                                        personPhone.setError("Phone number is invalid");
                                        //return UI to normal state
                                        loading_dialog.setVisibility(View.GONE);
                                        save_details.setVisibility(View.VISIBLE);
                                        close_dialog.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    //number too short
                                    personPhone.setError("Phone number too short");
                                    //return UI to normal state
                                    loading_dialog.setVisibility(View.GONE);
                                    save_details.setVisibility(View.VISIBLE);
                                    close_dialog.setVisibility(View.VISIBLE);
                                }
                            } else {
                                //number is empty
                                personPhone.setError("Please fill your phone number");
                                //return UI to normal state
                                loading_dialog.setVisibility(View.GONE);
                                save_details.setVisibility(View.VISIBLE);
                                close_dialog.setVisibility(View.VISIBLE);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onClick(View v) {
                //save if user has made any changes
                loading_dialog.setVisibility(View.VISIBLE);
                save_details.setVisibility(View.INVISIBLE);
                close_dialog.setVisibility(View.INVISIBLE);
                //update phone number string
                SPhone = personPhone.getText().toString();
                new CheckUserMobile().execute(new ApiConnector());
            }
        });

        profile_dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class updateUser extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            //Scountry = Scountry.toUpperCase();//make the country code all caps
            return params[0].UpdateUser(SpersonID, SPhone);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);

                if (response.equals("Successful")) {
                    Toast toast = Toast.makeText(MainActivity.this, "Your profile has been updated :)", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); //This'll return the default View of the
                    // Toast.
                    TextView toastMessage = toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(12);
                    toastMessage.setTextColor(getResources().getColor(R.color.white));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(10);
                    toastView.setBackground(getResources().getDrawable(R.drawable.bg_button));
                    toast.show();

                    //dismiss dialog after finishing and update shared preferences
                    profile_dialog.dismiss();
                    getUserInfoFromPreferences();
                } else {
                    builder.setMessage("An error occurred :(\nTry again?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new updateUser().execute(new ApiConnector());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    profile_dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            loading_dialog.setContentView(R.layout.dialog_loading);
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
                SJoinDate = item.getString("date_joined");

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
        editor.putString(Constants.DATE_JOINED, SJoinDate);
        editor.apply();

        loading_dialog.dismiss();
    }
}
