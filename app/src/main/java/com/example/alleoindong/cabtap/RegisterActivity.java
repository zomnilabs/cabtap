package com.example.alleoindong.cabtap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.alleoindong.cabtap.admin.AdminActivity;
import com.example.alleoindong.cabtap.data.remote.RetrofitHelper;
import com.example.alleoindong.cabtap.data.remote.models.Profile;
import com.example.alleoindong.cabtap.data.remote.models.User;
import com.example.alleoindong.cabtap.driver.DriverMapActivity;
import com.example.alleoindong.cabtap.models.UserProfile;
import com.example.alleoindong.cabtap.user.PassengerMapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.first_name) EditText mFirstName;
    @BindView(R.id.last_name) EditText mLastName;
    @BindView(R.id.email) EditText mEmail;
    @BindView(R.id.password) EditText mPassword;
    @BindView(R.id.confirm_password) EditText mConfirmPassword;
    @BindView(R.id.btn_register) Button mRegister;
    @BindView(R.id.btn_register_loading) ProgressBar mProgress;

//    // Observe changes to authentication
//    protected Subscriber<Boolean> authenticationSubscriber = new Subscriber<Boolean>() {
//        @Override
//        public void onCompleted() {
//
//        }
//
//        @Override
//        public void onError(Throwable e) {
//
//        }
//
//        @Override
//        public void onNext(Boolean aBoolean) {
//            Log.i("EmailLogin", aBoolean.toString());
//
//            if (aBoolean) {
//                Log.i("USER_LOGIN", role);
//                Intent intent;
//
//                switch (role) {
//                    case "admin":
//                        intent = new Intent(getApplicationContext(), AdminActivity.class);
//                        startActivity(intent);
//                        break;
//                    case "driver":
//                        intent = new Intent(getApplicationContext(), DriverMapActivity.class);
//                        startActivity(intent);
//                        break;
//                    default:
//                        intent = new Intent(getApplicationContext(), PassengerMapActivity.class);
//                        startActivity(intent);
//                }
//
//                finish();
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("REGISTRATION");

        ButterKnife.bind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.btn_register) void registerUser() {
        this.onShowLoader(true);

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();

        if (! password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords does not match", Toast.LENGTH_SHORT).show();
            this.onShowLoader(false);

            return;
        }

        this.createUser(email, password);
    }

    private void createUser(String email, String password) {
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();

        // Build Data
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        Profile profile = new Profile();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        user.setProfile(profile);

        // Register
        RetrofitHelper.getInstance().getService().register(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int statusCode = response.code();
                if (statusCode == 201) {
                    User user = response.body();
                    BaseActivity.currentUser = user;
                    BaseActivity.uid = user.getId().toString();
                    BaseActivity.firstName = user.getProfile().getFirstName();
                    BaseActivity.fullName = user.getProfile().getFirstName() + " " + user.getProfile().getLastName();
                    BaseActivity.fcm = user.getFcmToken();

                    // Login the passenger
                    Intent intent = new Intent(getApplicationContext(), PassengerMapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.i("REGISTER", String.valueOf(statusCode));
                    Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                }

                onShowLoader(false);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                onShowLoader(false);
                t.printStackTrace();
            }
        });
    }

//    private String createUser(final String email, String password) {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
//                new OnCompleteListener<AuthResult>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.i("CREATE_DRIVER", task.getResult().getUser().getUid());
//
//                        String id = UUID.randomUUID().toString();
//                        String uid = task.getResult().getUser().getUid();
//                        String firstName = mFirstName.getText().toString();
//                        String lastName = mLastName.getText().toString();
//                        String role = "passenger";
//
//                        UserProfile user = new UserProfile(id, uid, firstName,
//                                lastName, role, email);
//
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference usersRef = database.getReference("users");
//                        usersRef.child(uid).setValue(user);
//
//                        onShowLoader(false);
//                        finish();
//                    }
//                });
//
//        return "";
//    }

    public void onShowLoader(boolean isShown) {
        mRegister.setEnabled(!isShown);
        mRegister.setText(isShown ? "" : getString(R.string.register_now));
        mProgress.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }
}
