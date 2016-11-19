package com.example.alleoindong.cabtap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alleoindong.cabtap.admin.AdminActivity;
import com.example.alleoindong.cabtap.driver.DriverMapActivity;
import com.example.alleoindong.cabtap.user.PassengerMapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscriber;

public class PassengerLoginActivity extends BaseActivity {
    @BindView(R.id.btn_login) Button mLogin;
    @BindView(R.id.email_address) EditText mEmailAddress;
    @BindView(R.id.password) EditText mPassword;
    private ProgressDialog loginProgress;

    // Observe changes to authentication
    protected Subscriber<Boolean> authenticationSubscriber = new Subscriber<Boolean>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Boolean aBoolean) {
            Log.i("EmailLogin", aBoolean.toString());

            if (! aBoolean || role == null) {
                mLogin.setEnabled(true);
                mLogin.setText(R.string.log_in);
                loginProgress.dismiss();
            }

            if (aBoolean) {
                Log.i("USER_LOGIN", role);
                Intent intent;

                switch (role) {
                    case "admin":
                        intent = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(intent);
                        break;
                    case "driver":
                        intent = new Intent(getApplicationContext(), DriverMapActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        intent = new Intent(getApplicationContext(), PassengerMapActivity.class);
                        startActivity(intent);
                }

                finish();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_login);
        setTitle("Login");
        ButterKnife.bind(this);

        loginProgress = new ProgressDialog(this, R.style.ProgressDialog);
        loginProgress.setTitle("Please wait...");
        loginProgress.setCancelable(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Subscribe to changes on authentication
        this.authenticationObservable.subscribe(this.authenticationSubscriber);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        authenticationSubscriber.unsubscribe();
    }

    @OnClick(R.id.btn_login) void loginClick() {
        String email = mEmailAddress.getText().toString();
        String password = mPassword.getText().toString();

        // disable the button
        mLogin.setEnabled(false);
        mLogin.setText(R.string.loading_button);
        loginProgress.show();

        // authenticate user
        this.authenticate(email, password);
    }
}
