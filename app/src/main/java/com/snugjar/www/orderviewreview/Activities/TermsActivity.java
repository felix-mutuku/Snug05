package com.snugjar.www.orderviewreview.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snugjar.www.orderviewreview.Connectors.Constants;
import com.snugjar.www.orderviewreview.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TermsActivity extends AppCompatActivity {
    TextView back, terms_txt, please_wait, terms_and_conditions, privacy_policy;
    LinearLayout linear_available;
    Dialog loading_dialog;
    String SUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        back = findViewById(R.id.back);
        terms_txt = findViewById(R.id.terms_txt);
        please_wait = findViewById(R.id.please_wait);
        linear_available = findViewById(R.id.linear_available);
        terms_and_conditions = findViewById(R.id.terms_and_conditions);
        privacy_policy = findViewById(R.id.privacy_policy);

        initialize();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go backwards
                onBackPressed();
            }
        });

        terms_and_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show terms and conditions text
                initializeTermsAndConditions();
            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show privacy policy text
                initializePrivacyPolicy();
            }
        });

    }

    private void initialize() {
        initializeTermsAndConditions();
    }

    private void showLoadingDialog() {
        //show the loading dialog
        loading_dialog = new Dialog(TermsActivity.this);
        loading_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.setCancelable(false);
        loading_dialog.setContentView(R.layout.dialog_loading);
        loading_dialog.show(); //don't forget to dismiss the dialog when done loading
    }

    private void initializeTermsAndConditions() {
        terms_and_conditions.setBackgroundResource(R.drawable.bg_button);
        privacy_policy.setBackgroundResource(R.drawable.bg_button5);
        SUrl = Constants.BASE_URL_TERMS + "terms.txt";
        new GetStringFromUrl().execute();
    }

    private void initializePrivacyPolicy() {
        terms_and_conditions.setBackgroundResource(R.drawable.bg_button5);
        privacy_policy.setBackgroundResource(R.drawable.bg_button);
        SUrl = Constants.BASE_URL_TERMS + "privacy.txt";
        new GetStringFromUrl().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetStringFromUrl extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog();
            // how progress dialog when downloading
            //dialog = ProgressDialog.show(PrivacyActivity.this, null, "Please Wait...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(SUrl);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));

                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line + "\n");
                }
                String result = total.toString();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //TextView textView = (TextView) findViewById(R.id.privacytxt);
            // show result in textView
            if (result == null) {
                please_wait.setText(R.string.check_your_internet);
            } else {
                terms_txt.setText(result);
                linear_available.setVisibility(View.GONE);
            }
            // close progresses dialog
            loading_dialog.dismiss();
        }
    }
}
