package com.snugjar.www.youshopwedeliver.Activities;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.snugjar.www.youshopwedeliver.R;

public class CheckoutActivity extends AppCompatActivity {
    NestedScrollView nested_summary, nested_delivery, nested_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        nested_delivery = findViewById(R.id.nested_delivery);
        nested_payment = findViewById(R.id.nested_payment);
        nested_summary = findViewById(R.id.nested_summary);

        //show only necessary views
        initialize();
    }

    private void initialize() {
        nested_delivery.setVisibility(View.VISIBLE);
        nested_payment.setVisibility(View.GONE);
        nested_summary.setVisibility(View.GONE);
    }
}
