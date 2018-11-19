package com.snugjar.www.youshopwedeliver.Activities;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.snugjar.www.youshopwedeliver.R;

public class CheckoutActivity extends AppCompatActivity {
    NestedScrollView nested_summary, nested_delivery, nested_payment;
    TextView delivery_text, payment_text, summary_text;
    Button buttonPayments, buttonSummary, buttonConfirm;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        nested_delivery = findViewById(R.id.nested_delivery);
        nested_payment = findViewById(R.id.nested_payment);
        nested_summary = findViewById(R.id.nested_summary);
        delivery_text = findViewById(R.id.delivery_text);
        payment_text = findViewById(R.id.payment_text);
        summary_text = findViewById(R.id.summary_text);
        buttonPayments = findViewById(R.id.buttonPayments);
        buttonSummary = findViewById(R.id.buttonSummary);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        back = findViewById(R.id.back);

        //show only necessary views
        initialize();

        delivery_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make delivery section visible
                initializeDelivery();
            }
        });

        payment_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make payment section visible
                initializePayment();
            }
        });

        summary_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make summary section visible
                initializeSummary();
            }
        });

        buttonPayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to payments tab
                initializePayment();
            }
        });

        buttonSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to summary tab
                initializeSummary();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send data to orders for processing
                //and shoppers to shop

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go backwards
                onBackPressed();
            }
        });
    }

    private void initialize() {
        //set up everything the user needs to see from previous screen
        initializeDelivery();

    }

    private void initializeSummary() {
        nested_delivery.setVisibility(View.GONE);
        nested_payment.setVisibility(View.GONE);
        nested_summary.setVisibility(View.VISIBLE);

        delivery_text.setBackgroundResource(R.drawable.bg_button5);
        payment_text.setBackgroundResource(R.drawable.bg_button5);
        summary_text.setBackgroundResource(R.drawable.bg_button4);
    }

    private void initializePayment() {
        nested_delivery.setVisibility(View.GONE);
        nested_payment.setVisibility(View.VISIBLE);
        nested_summary.setVisibility(View.GONE);

        delivery_text.setBackgroundResource(R.drawable.bg_button5);
        payment_text.setBackgroundResource(R.drawable.bg_button4);
        summary_text.setBackgroundResource(R.drawable.bg_button5);
    }

    private void initializeDelivery() {
        nested_delivery.setVisibility(View.VISIBLE);
        nested_payment.setVisibility(View.GONE);
        nested_summary.setVisibility(View.GONE);

        delivery_text.setBackgroundResource(R.drawable.bg_button4);
        payment_text.setBackgroundResource(R.drawable.bg_button5);
        summary_text.setBackgroundResource(R.drawable.bg_button5);
    }
}
