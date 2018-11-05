package com.snugjar.www.youshopwedeliver.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.snugjar.www.youshopwedeliver.Fragments.IntroSlider;
import com.snugjar.www.youshopwedeliver.R;

public class IntroActivity extends AppIntro {

  @Override
  public void init(Bundle savedInstanceState) {

    //adding the three slides for introduction app you can ad as many you needed
    addSlide(IntroSlider.newInstance(R.layout.app_intro1));
    addSlide(IntroSlider.newInstance(R.layout.app_intro2));
    addSlide(IntroSlider.newInstance(R.layout.app_intro3));

    // Show and Hide Skip and Done buttons
    showStatusBar(false);
    showSkipButton(false);

    // Turn vibration on and set intensity
    // You will need to add VIBRATE permission in Manifest file
    setVibrate(true);
    setVibrateIntensity(30);

    //Add animation to the intro slider
    setDepthAnimation();
  }

  @Override
  public void onSkipPressed() {
    Intent i = new Intent(getApplicationContext(), LoginSignUpActivity.class);
    startActivity(i);
    finish();
  }

  @Override
  public void onNextPressed() {
    //Do something here when users click or tap on Next button.
  }

  @Override
  public void onDonePressed() {
    //Do something here when users click or tap tap on Done button.
    Intent i = new Intent(getApplicationContext(), LoginSignUpActivity.class);
    startActivity(i);
    finish();
  }

  @Override
  public void onSlideChanged() {
    //Do something here when slide is changed
  }
}
