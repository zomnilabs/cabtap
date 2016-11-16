package com.example.alleoindong.cabtap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.alleoindong.cabtap.PassengerLoginActivity;
import com.example.alleoindong.cabtap.RegisterActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.btn_login) Button mLogin;
    @BindView(R.id.btn_register) Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_login) void loginClick() {
        Intent intent = new Intent(this, PassengerLoginActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_register) void registerClick() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
