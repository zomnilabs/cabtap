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
}
