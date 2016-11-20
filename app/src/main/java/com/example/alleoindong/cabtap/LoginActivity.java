package com.example.alleoindong.cabtap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.alleoindong.cabtap.admin.AdminActivity;
import com.example.alleoindong.cabtap.driver.DriverMapActivity;
import com.example.alleoindong.cabtap.user.PassengerMapActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.btn_login) Button mLogin;
    @BindView(R.id.btn_register) Button mRegister;

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

    @Override
    protected void onStart() {
        super.onStart();

        this.authenticationObservable.subscribe(this.authenticationSubscriber);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.authenticationSubscriber.unsubscribe();
    }
}
