package com.example.alleoindong.cabtap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.alleoindong.cabtap.admin.AdminActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassengerLoginActivity extends AppCompatActivity {
    @BindView(R.id.btn_login) Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_login);
        setTitle("Login");
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_login) void loginClick() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
        finish();
    }

}
