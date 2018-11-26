package com.snugjar.www.youshopwedeliver.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.snugjar.www.youshopwedeliver.Connectors.ApiConnector;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.Helpers.MyBounceInterpolator;
import com.snugjar.www.youshopwedeliver.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    CircleImageView profile_image;
    ImageView settings;
    TextView user_location, supermarkets, orders, about, help, feedback, share_app, cart, feed;
    String SpersonID, SImage, SName, SEmail, SIMEI, SUserType, SCountry, SPhone, SJoinDate, CSPhone,
            SDeliveryPrice, SPickupPrice, SCurrency, SPricePerKilometre;
    Dialog location_dialog, loading_dialog, profile_dialog, play_services_dialog, cannot_use_dialog, are_you_sure_dialog;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 30000;//30 seconds
    private long FASTEST_INTERVAL = 15000;//15 seconds
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    CarouselView carouselView;
    ArrayList<String> SlidingImagesList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_image = findViewById(R.id.profile_image);
        settings = findViewById(R.id.settings);
        user_location = findViewById(R.id.user_location);
        supermarkets = findViewById(R.id.supermarkets);
        orders = findViewById(R.id.orders);
        about = findViewById(R.id.about);
        help = findViewById(R.id.help);
        feedback = findViewById(R.id.feedback);
        share_app = findViewById(R.id.share_app);
        carouselView = findViewById(R.id.sliding_ads);
        cart = findViewById(R.id.cart);
        feed = findViewById(R.id.feed);

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
        } else {
            //ask user to enable location services
            showLocationDialog();
        }

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open cart page
                goToCart();
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open feed page
                goToFeed();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show profile dialog
                animateProfilePic();
            }
        });

        supermarkets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open supermarkets page
                goToSupermarkets();
            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open orders page
                goToOrders();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open about page
                goToAbout();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open help page
                goToHelp();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open feedback page
                goToFeedback();
            }
        });

        share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //share app with other users on android
                shareApp();
            }
        });

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(MainActivity.this, "Clicked ad: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to settings page
                goToSettings();
            }
        });

    }

    private void goToSettings() {
        //Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        double animationDuration = 500; //time in seconds
        myAnim.setDuration((long) animationDuration);

        //Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(3, 7);

        myAnim.setInterpolator(interpolator);

        settings.startAnimation(myAnim);

        //Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                //when animation starts
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //when animation repeats
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
                //show settings page
            }
        });
    }

    private void animateProfilePic() {
        //Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        double animationDuration = 500; //time in seconds
        myAnim.setDuration((long) animationDuration);

        //Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(3, 7);

        myAnim.setInterpolator(interpolator);

        profile_image.startAnimation(myAnim);

        //Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                //when animation starts
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //when animation repeats
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
                showProfileDialog();
            }
        });
    }

    private void goToSupermarkets() {
        //Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        double animationDuration = 500; //time in seconds
        myAnim.setDuration((long) animationDuration);

        //Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(3, 7);

        myAnim.setInterpolator(interpolator);

        supermarkets.startAnimation(myAnim);

        //Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                //when animation starts
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //when animation repeats
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
                Intent i = new Intent(MainActivity.this, SupermarketsActivity.class);
                startActivity(i);
            }
        });
    }

    private void goToFeed() {
        //Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        double animationDuration = 500; //time in seconds
        myAnim.setDuration((long) animationDuration);

        //Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(3, 7);

        myAnim.setInterpolator(interpolator);

        feed.startAnimation(myAnim);

        //Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                //when animation starts
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //when animation repeats
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
                //Intent i = new Intent(MainActivity.this, SupermarketsActivity.class);
                //startActivity(i);
            }
        });
    }

    private void goToCart() {
        //Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        double animationDuration = 500; //time in seconds
        myAnim.setDuration((long) animationDuration);

        //Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(3, 7);

        myAnim.setInterpolator(interpolator);

        cart.startAnimation(myAnim);

        //Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                //when animation starts
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //when animation repeats
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
                Intent i = new Intent(MainActivity.this, CartActivity.class);
                startActivity(i);
            }
        });
    }

    private void shareApp() {
        //Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        double animationDuration = 500; //time in seconds
        myAnim.setDuration((long) animationDuration);

        //Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(3, 7);

        myAnim.setInterpolator(interpolator);

        share_app.startAnimation(myAnim);

        //Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                //when animation starts
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //when animation repeats
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Shopdrop App");
                    String sAux = "\nThe everyday shopping App you have been waiting for :)" +
                            "\nSelect from a wide range of supermarkets in your area of choice" +
                            "\nYou shop, we deliver it straight into your hands in a coupe of minutes. Happy Shopping :)" +
                            "\n\n";
                    sAux = sAux + Constants.PLAY_STORE_URL;
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                    //tell user to try again
                    e.printStackTrace();
                }
            }
        });

    }

    private void goToFeedback() {
        //Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        double animationDuration = 500; //time in seconds
        myAnim.setDuration((long) animationDuration);

        //Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(3, 7);

        myAnim.setInterpolator(interpolator);

        feedback.startAnimation(myAnim);

        //Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                //when animation starts
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //when animation repeats
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
                //Intent i = new Intent(MainActivity.this, SupermarketsActivity.class);
                //startActivity(i);
            }
        });
    }

    private void goToHelp() {
        //Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        double animationDuration = 500; //time in seconds
        myAnim.setDuration((long) animationDuration);

        //Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(3, 7);

        myAnim.setInterpolator(interpolator);

        help.startAnimation(myAnim);

        //Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                //when animation starts
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //when animation repeats
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
                //Intent i = new Intent(MainActivity.this, SupermarketsActivity.class);
                //startActivity(i);
            }
        });
    }

    private void goToAbout() {
        //Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        double animationDuration = 500; //time in seconds
        myAnim.setDuration((long) animationDuration);

        //Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(3, 7);

        myAnim.setInterpolator(interpolator);

        about.startAnimation(myAnim);

        //Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                //when animation starts
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //when animation repeats
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
                //Intent i = new Intent(MainActivity.this, SupermarketsActivity.class);
                //startActivity(i);
            }
        });
    }

    private void goToOrders() {
        //Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        double animationDuration = 500; //time in seconds
        myAnim.setDuration((long) animationDuration);

        //Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(3, 7);

        myAnim.setInterpolator(interpolator);

        orders.startAnimation(myAnim);

        //Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                //when animation starts
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //when animation repeats
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
                //Intent i = new Intent(MainActivity.this, SupermarketsActivity.class);
                //startActivity(i);
            }
        });
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
        ImageView delete_account = profile_dialog.findViewById(R.id.delete_account);
        Button logout = profile_dialog.findViewById(R.id.logout);
        final RelativeLayout relative_save = profile_dialog.findViewById(R.id.relative_save);

        relative_save.setVisibility(View.GONE);

        //load profile picture and details
        Glide
                .with(MainActivity.this)
                .load(SImage)
                .error(R.drawable.logo)
                .crossFade()
                .into(profile_image);

        personName.setText(SName.replaceAll("_", " "));
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
                    relative_save.setVisibility(View.VISIBLE);
                } else {
                    relative_save.setVisibility(View.GONE);
                }
            }
        });

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete user account from database
                deleteUserAccount();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logout user and destroy current login session
                LogOutAreYouSure();
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

        personName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit profile in google
                showToastEditInGoogle();
            }
        });

        personEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit profile in google
                showToastEditInGoogle();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit profile in google
                showToastEditInGoogle();
            }
        });

        personCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit profile in google
                showToastEditInGoogle();
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

    private void LogOutAreYouSure() {
        //as user whether they are sure they want to logout
        are_you_sure_dialog = new Dialog(MainActivity.this);
        are_you_sure_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        are_you_sure_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        are_you_sure_dialog.setCancelable(false);
        are_you_sure_dialog.setContentView(R.layout.dialog_are_you_sure);

        ImageView dialog_image = are_you_sure_dialog.findViewById(R.id.dialog_image);
        TextView dialog_text = are_you_sure_dialog.findViewById(R.id.dialog_text);
        Button button_no = are_you_sure_dialog.findViewById(R.id.button_no);
        Button button_yes = are_you_sure_dialog.findViewById(R.id.button_yes);

        //load appropriate image
        Glide
                .with(MainActivity.this)
                .load(R.drawable.ic_eject_black_24dp)
                .error(R.drawable.ic_eject_black_24dp)
                .crossFade()
                .into(dialog_image);

        dialog_text.setText(R.string.logout_warning);

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //log current user out
                logOutUser();
            }
        });

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss dialog like nothing happened
                are_you_sure_dialog.dismiss();
            }
        });

        are_you_sure_dialog.show(); //don't forget to dismiss the dialog when done
    }

    private void logOutUser() {
        //destroy current login session from the app
        SharedPreferences getSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getSharedPreferences.edit();
        e.putBoolean("isLoggedin", false);
        e.putBoolean("firstStart", true);
        e.apply();

        //and take user to splash screen
        Intent i = new Intent(MainActivity.this, SplashScreenActivity.class);
        startActivity(i);
        finish();
    }

    private void deleteUserAccount() {
        //ask user whether they are sure they want to delete their account
        are_you_sure_dialog = new Dialog(MainActivity.this);
        are_you_sure_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        are_you_sure_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        are_you_sure_dialog.setCancelable(false);
        are_you_sure_dialog.setContentView(R.layout.dialog_are_you_sure);

        ImageView dialog_image = are_you_sure_dialog.findViewById(R.id.dialog_image);
        TextView dialog_text = are_you_sure_dialog.findViewById(R.id.dialog_text);
        Button button_no = are_you_sure_dialog.findViewById(R.id.button_no);
        Button button_yes = are_you_sure_dialog.findViewById(R.id.button_yes);

        //load appropriate image
        Glide
                .with(MainActivity.this)
                .load(R.drawable.ic_delete_forever_black_24dp)
                .error(R.drawable.ic_delete_forever_black_24dp)
                .crossFade()
                .into(dialog_image);

        dialog_text.setText(R.string.delete_account_warning);

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the account from database
                //dismiss dialog and show loading
                profile_dialog.dismiss();
                loading_dialog.show();
                new deleteUser().execute(new ApiConnector());
            }
        });

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss dialog like nothing happened
                are_you_sure_dialog.dismiss();
            }
        });

        are_you_sure_dialog.show(); //don't forget to dismiss the dialog when done
    }

    @SuppressLint("StaticFieldLeak")
    private class deleteUser extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            //SCountry = SCountry.toUpperCase();//make the country code all caps
            return params[0].DeleteUser(SpersonID);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);

                if (response.equals("Successful")) {
                    Toast toast = Toast.makeText(MainActivity.this, "Your profile has been deleted :(", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); //This'll return the default View of the Toast.
                    TextView toastMessage = toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(12);
                    toastMessage.setTextColor(getResources().getColor(R.color.white));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(10);
                    toastView.setBackground(getResources().getDrawable(R.drawable.bg_button));
                    toast.show();

                    logOutUser();
                } else {
                    builder.setMessage("An error occurred :(\nTry again?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new deleteUser().execute(new ApiConnector());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    loading_dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showToastEditInGoogle() {
        Toast toast = Toast.makeText(MainActivity.this, "Please edit this in your Google Account", Toast.LENGTH_SHORT);
        View toastView = toast.getView(); //This'll return the default View the Toast.
        TextView toastMessage = toastView.findViewById(android.R.id.message);
        toastMessage.setTextSize(12);
        toastMessage.setTextColor(getResources().getColor(R.color.white));
        toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
        toastMessage.setGravity(Gravity.CENTER);
        toastMessage.setCompoundDrawablePadding(10);
        toastView.setBackground(getResources().getDrawable(R.drawable.bg_button));
        toast.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class updateUser extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            //SCountry = SCountry.toUpperCase();//make the country code all caps
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
            loading_dialog.show(); //don't forget to dismiss the dialog when done loading
        } else {
            //set login status to false and login again
            SharedPreferences getSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor e = getSharedPreferences.edit();
            e.putBoolean("isLoggedin", false);
            e.apply();
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
            try {
                //getAddress(MainActivity.this, mLocation.getLatitude(), mLocation.getLongitude());
                String JSONLocation = new GetUserLocation(mLocation.getLatitude(), mLocation.getLongitude()).execute(new ApiConnector()).get();
                user_location.setText(JSONLocation);
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
                //getAddress(MainActivity.this, mLocation.getLatitude(), mLocation.getLongitude());
                String JSONLocation = new GetUserLocation(mLocation.getLatitude(), mLocation.getLongitude()).execute(new ApiConnector()).get();
                user_location.setText(JSONLocation);
                new updateUserLocation(mLocation.getLatitude(), mLocation.getLongitude()).execute(new ApiConnector());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
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

    /*private void checkCountry(String country, double latitude, double longitude) {
        //update user location in database
        new updateUserLocation(latitude, longitude).execute(new ApiConnector());

        if (!country.equals("Kenya")) {
            //show app cannot work in this country dialog
            cannot_use_dialog = new Dialog(MainActivity.this);
            cannot_use_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cannot_use_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cannot_use_dialog.setCancelable(false);
            cannot_use_dialog.setContentView(R.layout.dialog_cannot_use);
            cannot_use_dialog.show(); // don't forget to dismiss the dialog when done loading

            //update new country in database for MIS use
            new newCountryCollect(country).execute(new ApiConnector());
        }
    }*/

    @SuppressLint("StaticFieldLeak")
    private class newCountryCollect extends AsyncTask<ApiConnector, Long, String> {
        String Ncountry;

        newCountryCollect(String country) {
            Ncountry = country;
        }

        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].newCountryCollect(Ncountry);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                if (!response.equals("Successful")) {
                    new newCountryCollect(Ncountry).execute(new ApiConnector());
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        stopLocationUpdates();
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

                new GetPricesNCurrency().execute(new ApiConnector());
                //updateSharedPreferences();

            } catch (Exception e) {
                e.printStackTrace();
                loading_dialog.dismiss();
                getUserInfoFromPreferences();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPricesNCurrency extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].GetPricesNCurrency(SCountry);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                JSONObject item = jsonArray.getJSONObject(0);

                SDeliveryPrice = item.getString("delivery_price");
                SPickupPrice = item.getString("pickup_price");
                SCurrency = item.getString("currency");
                SPricePerKilometre = item.getString("price_per_kilometre");

                /*Toast toast = Toast.makeText(MainActivity.this, SDeliveryPrice + SPickupPrice + SCurrency, Toast.LENGTH_LONG);
                toast.show();*/

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
        editor.putString(Constants.DELIVERY_PRICE, SDeliveryPrice);
        editor.putString(Constants.PICKUP_PRICE, SPickupPrice);
        editor.putString(Constants.CURRENCY, SCurrency);
        editor.putString(Constants.PRICE_PER_KILOMETRE, SPricePerKilometre);

        editor.apply();

        loading_dialog.dismiss();

        new GetSlidingAds().execute(new ApiConnector());
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSlidingAds extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            String Splacement = "1";//Ads from Advertisers [1=Home-Page, 2=Supermarkets-Page, 3=Specific-Supermarket]
            return params[0].GetGeneralSlidingAds(SCountry, Splacement);
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
                        .with(MainActivity.this)
                        .load(SlidingImagesList.get(position))
                        .fitCenter()
                        .crossFade()
                        .into(imageView);
            }
        });

        carouselView.setPageCount(SlidingImagesList.size());
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
                    //confirm that location in database is updated
                    /*Toast toast = Toast.makeText(MainActivity.this, "Location Updated :)", Toast.LENGTH_SHORT);
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

    private void showLocationDialog() {
        //ask user to enable location services
        location_dialog = new Dialog(MainActivity.this);
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
                    Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
                    startActivity(intent);
                    finish();//close current activity
                }
            }
        });
        location_dialog.show();
    }
}
