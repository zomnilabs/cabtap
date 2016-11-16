package com.example.alleoindong.cabtap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alleoindong.cabtap.admin.AdminActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassengerLoginActivity extends BaseActivity {
    @BindView(R.id.btn_login) Button mLogin;
    @BindView(R.id.email_address) EditText mEmailAddress;
    @BindView(R.id.password) EditText mPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_login);
        setTitle("Login");
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login) void loginClick() {
        String email = mEmailAddress.getText().toString();
        String password = mPassword.getText().toString();

        // disable the button
        mLogin.setEnabled(false);
        mLogin.setText(R.string.loading_button);

        // authenticate user
        this.authenticate(email, password);
    }
}
