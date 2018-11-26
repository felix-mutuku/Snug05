package com.snugjar.www.youshopwedeliver.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.snugjar.www.youshopwedeliver.Connectors.ApiConnector;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LoginSignUpActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    RelativeLayout signInButton;
    AVLoadingIndicatorView loading;
    String phone_IMEI, Scountry, Sname, Semail, SpersonID, Sphone, Stype;
    Uri Sphoto;
    Dialog dialog;
    public boolean isLoggedin;
    TextView terms_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        signInButton = findViewById(R.id.sign_in_button);
        loading = findViewById(R.id.loading);
        terms_txt = findViewById(R.id.terms_txt);

        checkLoginStatus();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                //.requestIdToken (String serverClientId)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //get user country
        getCountry(this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something when sign in button clicked
                //make button disappear and show loading
                loading.setVisibility(View.VISIBLE);
                signInButton.setVisibility(View.INVISIBLE);
                //proceed to signing in
                signIn();
            }
        });

        terms_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to activity to show terms and conditions
                goToTerms();
            }
        });

    }

    private void goToTerms() {
        Intent intent = new Intent(LoginSignUpActivity.this, TermsActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Check for existing Google Sign In account, if the user is already signed in the
            //GoogleSignInAccount will be non-null.
            //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account == null) {
                //this is a new user on google request them to get a gmail account
                showGoogleAccountDialog();
            } else {
                //Signed in successfully, show authenticated UI.
                updateUI(account);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
            Toast toast = Toast.makeText(LoginSignUpActivity.this, "Sorry, an error occurred :" +
                    "(\nPlease try again later.", Toast.LENGTH_LONG);
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

            loading.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    private void showGoogleAccountDialog() {
        signInButton.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);

        Dialog dialog = new Dialog(LoginSignUpActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_google_account);

        ImageView google_image = dialog.findViewById(R.id.google_image);

        google_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("https://www.google.com/");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
        dialog.show();
    }

    private void updateUI(GoogleSignInAccount account) {
        signInButton.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        //this is a return user, do the appropriate for the UI
        //this is a return user on google ask to confirm google details
        Sname = account.getDisplayName();
        Semail = account.getEmail();
        Sphoto = account.getPhotoUrl();
        SpersonID = account.getId();

        try {
            Sphone = getPhoneNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new CheckExistingUser().execute(new ApiConnector());
    }

    private String getPhoneNumber() {
        String phone_number = "null";

        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                        != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                        PackageManager.PERMISSION_GRANTED) {

            Toast toast = Toast.makeText(LoginSignUpActivity.this, "Please enable all permissions" +
                    " requested by the application", Toast.LENGTH_LONG);
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

        if (phoneMgr != null) {
            phone_number = phoneMgr.getLine1Number();
            phone_IMEI = phoneMgr.getDeviceId();
        }

        return phone_number;
    }

    private void showToastEditInGoogle() {
        Toast toast = Toast.makeText(LoginSignUpActivity.this, "Please edit this in your Google " +
                "Account", Toast.LENGTH_SHORT);
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

    public void getCountry(Context context) {
        String country;
        LocationManager locationManager = (LocationManager) getSystemService(Context
                .LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission
                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest
                            .permission.ACCESS_COARSE_LOCATION) != PackageManager
                            .PERMISSION_GRANTED) {
                Toast toast = Toast.makeText(context, "Please enable all permissions requested by" +
                        " the application", Toast.LENGTH_LONG);
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

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else {
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude
                            (), 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        country = addresses.get(0).getCountryName();
                        if (country != null) {
                            Scountry = country;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        country = getCountryBasedOnSimCardOrNetwork(context);
        if (country != null) {
            Scountry = country;
        }
    }

    private static String getCountryBasedOnSimCardOrNetwork(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context
                    .TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not
                // 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country
                    // code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    private class registerUser extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            Stype = "end_user";

            if (Sname != null &&
                    Semail != null &&
                    Sphone != null &&
                    Scountry != null &&
                    Sphoto != null &&
                    SpersonID != null &&
                    phone_IMEI != null) {
                //all automatic entries picked well without errors
                Sname = Sname.replaceAll(" ", "_");//detecting spaces in the name to avoid HTTP errors
                //SCountry = SCountry.toUpperCase();//make the country code all caps
                return params[0].RegisterUser(Sname, Semail, Sphone, Stype, Scountry, Sphoto, SpersonID, phone_IMEI);
            }else if (Scountry == null &&
                    phone_IMEI == null){
                //automatic entries failed to pick automatically
                Scountry = "ke";
                phone_IMEI = "0123456789";

                return params[0].RegisterUser(Sname, Semail, Sphone, Stype, Scountry, Sphoto, SpersonID, phone_IMEI);
            }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app
                        .AlertDialog.Builder(LoginSignUpActivity.this);

                if (response.equals("Successful")) {
                    loading.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(LoginSignUpActivity.this, "You are Now " +
                            "Registered :)", Toast.LENGTH_LONG);
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

                    goToMain();
                } else {
                    loading.setVisibility(View.INVISIBLE);
                    signInButton.setVisibility(View.VISIBLE);

                    builder.setMessage(response + "\n\nTry again?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new registerUser().execute(new ApiConnector());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkLoginStatus() {
        SharedPreferences getSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        isLoggedin = getSharedPreferences.getBoolean("isLoggedin", false);

        if (isLoggedin) {
            //user has already logged in
            Intent i = new Intent(LoginSignUpActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void goToMain() {
        //save login session for user
        SharedPreferences getSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getSharedPreferences.edit();
        e.putBoolean("isLoggedin", true);
        e.apply();

        addInfoToSharedPreference();

        //go to main activity after user has finished successfully
        Intent i = new Intent(LoginSignUpActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void addInfoToSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PERSON_ID, SpersonID);
        editor.apply();
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckExistingUser extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckExistingUser(SpersonID);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                if (Objects.equals(response, "Exist")) {
                    //user already in database, proceed to main
                    goToMain();
                } else {
                    //user absent in database, confirm details first
                    displayConfirmDialog();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void displayConfirmDialog() {
        //declaring the dialog items displayed
        dialog = new Dialog(LoginSignUpActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_login);

        ImageView profile_image = dialog.findViewById(R.id.profile_image);
        final ImageView close_dialog = dialog.findViewById(R.id.close_dialog);
        EditText personName = dialog.findViewById(R.id.personName);
        EditText personEmail = dialog.findViewById(R.id.personEmail);
        EditText personCountry = dialog.findViewById(R.id.personCountry);
        final EditText personPhone = dialog.findViewById(R.id.personPhone);
        final Button confirm_details = dialog.findViewById(R.id.confirm_details);
        final AVLoadingIndicatorView loading_dialog = dialog.findViewById(R.id.loading_dialog);
        final CheckBox terms_checkbox = dialog.findViewById(R.id.terms_checkbox);
        TextView terms_text = dialog.findViewById(R.id.terms_text);

        //make confirm button invisible until user confirms the terms and conditions
        confirm_details.setVisibility(View.GONE);

        //making sure numbers entered have "254" in them
        personPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredString = s.toString();
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
            }
        });

        //setting all the data for the UI
        Glide
                .with(LoginSignUpActivity.this)
                .load(Sphoto)
                .error(R.drawable.logo)
                .crossFade()
                .into(profile_image);

        personName.setText(Sname);
        personEmail.setText(Semail);
        personCountry.setText(Scountry);

        if (!Sphone.equals("null")) {
            Sphone = Sphone.replaceAll("\\+","");
            personPhone.setText(Sphone);
        }

        terms_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when user accepts the terms and conditions
                if(terms_checkbox.isChecked()){
                    //user has accepted the terms and conditions
                    confirm_details.setVisibility(View.VISIBLE);
                }else{
                    //user hasn't accepted the terms and conditions
                    confirm_details.setVisibility(View.GONE);
                }
            }
        });

        terms_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to terms and conditions activity
                Intent intent = new Intent(LoginSignUpActivity.this, TermsActivity.class);
                startActivity(intent);
            }
        });

        confirm_details.setOnClickListener(new View.OnClickListener() {
            //checking if user mobile number already exists in the database
            @SuppressLint("StaticFieldLeak")
            class CheckUserMobile extends AsyncTask<ApiConnector, Long, String> {
                @Override
                protected String doInBackground(ApiConnector... params) {
                    //it is executed on Background thread
                    //Log.e("NUMBER >>>>>>>>>>", Sphone);
                    return params[0].CheckUserMobile(Sphone);
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                protected void onPostExecute(String response) {
                    try {
                        if (Objects.equals(response, "Exist")) {
                            //entered phone number is already in the database
                            Toast toast = Toast.makeText(LoginSignUpActivity.this, "Phone number already registered !!\nType a different phone " +
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
                            confirm_details.setVisibility(View.VISIBLE);
                            close_dialog.setVisibility(View.VISIBLE);
                            terms_checkbox.setEnabled(true);
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
                                    Sphone = personPhone.getText().toString();
                                    if (PhoneNumberUtils.isGlobalPhoneNumber(Sphone)) {
                                        //hide UI elements to avoid HTTP collusion
                                        loading_dialog.setVisibility(View.VISIBLE);
                                        confirm_details.setVisibility(View.INVISIBLE);
                                        close_dialog.setVisibility(View.INVISIBLE);
                                        //send confirmed data to database
                                        new registerUser().execute(new ApiConnector());
                                    } else {
                                        //number is not a valid phone number
                                        personPhone.setError("Phone number is invalid");
                                        //return UI to normal state
                                        loading_dialog.setVisibility(View.GONE);
                                        confirm_details.setVisibility(View.VISIBLE);
                                        close_dialog.setVisibility(View.VISIBLE);
                                        terms_checkbox.setEnabled(true);
                                    }
                                } else {
                                    //number too short
                                    personPhone.setError("Phone number too short");
                                    //return UI to normal state
                                    loading_dialog.setVisibility(View.GONE);
                                    confirm_details.setVisibility(View.VISIBLE);
                                    close_dialog.setVisibility(View.VISIBLE);
                                    terms_checkbox.setEnabled(true);
                                }
                            } else {
                                //number is empty
                                personPhone.setError("Please fill your phone number");
                                //return UI to normal state
                                loading_dialog.setVisibility(View.GONE);
                                confirm_details.setVisibility(View.VISIBLE);
                                close_dialog.setVisibility(View.VISIBLE);
                                terms_checkbox.setEnabled(true);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onClick(View v) {
                //check whether entered number is already in database
                loading_dialog.setVisibility(View.VISIBLE);
                confirm_details.setVisibility(View.INVISIBLE);
                close_dialog.setVisibility(View.INVISIBLE);
                terms_checkbox.setEnabled(false);
                new CheckUserMobile().execute(new ApiConnector());
            }
        });

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close dialog
                dialog.dismiss();
            }
        });

        personName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tell user to change personal information on Google
                showToastEditInGoogle();
            }
        });

        personEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tell user to change personal information on Google
                showToastEditInGoogle();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tell user to change personal information on Google
                showToastEditInGoogle();
            }
        });

        dialog.show();
    }
}
