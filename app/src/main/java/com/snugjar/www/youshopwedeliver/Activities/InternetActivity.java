package com.snugjar.www.youshopwedeliver.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.snugjar.www.youshopwedeliver.R;

public class InternetActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_internet);

    Button buttonRetry = findViewById(R.id.buttonRetry);

    buttonRetry.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(InternetActivity.this, SplashScreenActivity.class));
        InternetActivity.this.finish();
      }
    });
  }
}
