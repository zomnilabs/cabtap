package com.example.alleoindong.cabtap.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.admin.DriverMaintenanceActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassengerHelpActivity extends AppCompatActivity {
    @BindView(R.id.how_to_book) TextView mHowToBook;
    @BindView(R.id.create_paymaya) TextView mCreatePaymaya;
    @BindView(R.id.pay_through_paymaya) TextView mPayThroughPaymaya;
    @BindView(R.id.pay_cash) TextView mPayCash;
    @BindView(R.id.exact_location) TextView mExactLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_help);

        setTitle("HELP");

        ButterKnife.bind(this);
    }

    @OnClick(R.id.how_to_book) void howToBookClick() {
        Intent intent = new Intent(this, HowToBookActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.create_paymaya) void createPaymayaClick() {
        Intent intent = new Intent(this, CreatePayMayaActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.pay_through_paymaya) void payThroughPaymayaClick() {
        Intent intent = new Intent(this, PayThroughPaymayaActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.pay_cash) void payCashClick() {
        Intent intent = new Intent(this, PayCashActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.exact_location) void exactLocationClick() {
        Intent intent = new Intent(this, ExactLocationActivity.class);
        startActivity(intent);
    }
}
