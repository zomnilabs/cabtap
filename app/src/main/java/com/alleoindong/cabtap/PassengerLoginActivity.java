package com.alleoindong.cabtap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alleoindong.cabtap.data.remote.models.User;
import com.alleoindong.cabtap.user.PassengerMapActivity;
import com.alleoindong.cabtap.R;
import com.alleoindong.cabtap.admin.AdminActivity;
import com.alleoindong.cabtap.data.remote.RetrofitHelper;
import com.alleoindong.cabtap.data.remote.models.Vehicle;
import com.alleoindong.cabtap.driver.DriverMapActivity;
//import Vehicle;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import rx.Observer;
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
                        finish();
                        break;
                    case "driver":
                        getAssignedVehicle();
                        break;
                    default:
                        intent = new Intent(getApplicationContext(), PassengerMapActivity.class);
                        startActivity(intent);
                        finish();
                }
            }

            loginProgress.dismiss();
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
        Call<User> auth = RetrofitHelper.getInstance().getService().login(email, password);
        auth.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                loginProgress.dismiss();
                int statusCode = response.code();

                if (statusCode == 200) {
                    User user = response.body();
                    currentUser = user;
                    uid = user.getId().toString();
                    firstName = user.getProfile().getFirstName();
                    fullName = user.getProfile().getFirstName() + " " + user.getProfile().getLastName();
                    fcm = user.getFcmToken();


                    Intent intent = null;
                    switch (user.getUserGroup()) {
                        case "driver":
                            getAssignedVehicle();
                            break;
                        case "passenger":
                            intent = new Intent(getApplicationContext(), PassengerMapActivity.class);
                            break;
                        default:
                            intent = new Intent(getApplicationContext(), PassengerMapActivity.class);
                    }

                    if (intent != null) {
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Log.e("LOGIN", "FAILED TO LOGIN");
                }

                mLogin.setEnabled(true);
                mLogin.setText(R.string.log_in);
                loginProgress.dismiss();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();

                mLogin.setEnabled(true);
                mLogin.setText(R.string.log_in);
                loginProgress.dismiss();
            }
        });
    }

    private void getAssignedVehicle() {
        Call<Vehicle> vehicleCall = RetrofitHelper.getInstance().getService().getAssignedVehicle("Bearer " + currentUser.getApiToken());
        vehicleCall.enqueue(new Callback<Vehicle>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                int statusCode = response.code();

                if (statusCode != 200) {
                    Toast.makeText(PassengerLoginActivity.this, "You dont have a vehicle assigned to you", Toast.LENGTH_SHORT).show();
                    return;
                }

                Vehicle vehicle = response.body();
                Log.i("LOGIN", vehicle.getPlateNumber());
                DriverMapActivity.assignedPlateNumber = vehicle.getPlateNumber();

                Intent intent = new Intent(getApplicationContext(), DriverMapActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                Toast.makeText(PassengerLoginActivity.this, "You dont have a vehicle assigned to you", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

//    private void getAssignedVehicle() {
//        DatabaseReference vehiclesRef = FirebaseDatabase.getInstance().getReference("vehicles");
//        Query assignedVehicleQuery = vehiclesRef.orderByChild("uid").equalTo(BaseActivity.uid);
//        Log.i("LOGIN", BaseActivity.uid);
//
//        assignedVehicleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Vehicle vehicle = null;
//
//                for (DataSnapshot vehicleSnapshop : dataSnapshot.getChildren()) {
//                    vehicle = vehicleSnapshop.getValue(Vehicle.class);
//                }
//
//                if (vehicle == null) {
//                    Toast.makeText(PassengerLoginActivity.this, "You are not assigned into a vehicle", Toast.LENGTH_SHORT).show();
//                    loginProgress.dismiss();
//                    mLogin.setEnabled(true);
//                    mLogin.setText(R.string.log_in);
//                    logout();
//                    return;
//                }
//
//                if (vehicle.plateNumber == null) {
//                    Toast.makeText(PassengerLoginActivity.this, "You are not assigned into a vehicle", Toast.LENGTH_SHORT).show();
//                    loginProgress.dismiss();
//                    mLogin.setEnabled(true);
//                    mLogin.setText(R.string.log_in);
//                    logout();
//                    return;
//                }
//
//                DriverMapActivity.assignedPlateNumber = vehicle.plateNumber;
//
//                Intent intent = new Intent(getApplicationContext(), DriverMapActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(PassengerLoginActivity.this, "You are not assigned into a vehicle", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
