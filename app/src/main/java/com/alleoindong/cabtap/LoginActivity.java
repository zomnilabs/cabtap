package com.alleoindong.cabtap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.alleoindong.cabtap.models.Vehicle;
import com.alleoindong.cabtap.R;
import com.alleoindong.cabtap.driver.DriverMapActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
//            Log.i("EmailLogin", aBoolean.toString());
//
//            if (! aBoolean || role == null) {
//                mLogin.setEnabled(true);
//                mLogin.setText(R.string.log_in);
//            }
//
//            if (aBoolean) {
//                Log.i("USER_LOGIN", role);
//                Intent intent;
//
//                switch (role) {
//                    case "admin":
//                        intent = new Intent(getApplicationContext(), AdminActivity.class);
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case "driver":
//                        getAssignedVehicle();
//                        break;
//                    default:
//                        intent = new Intent(getApplicationContext(), PassengerMapActivity.class);
//                        startActivity(intent);
//                        finish();
//                }
//            }
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

    private void getAssignedVehicle() {
        Log.i("VEHICLES", "uid: " + BaseActivity.uid);

        DatabaseReference vehiclesRef = FirebaseDatabase.getInstance().getReference("vehicles");
        Query assignedVehicleQuery = vehiclesRef.orderByChild("uid").equalTo(BaseActivity.uid);

        assignedVehicleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vehicle vehicle = null;

                for (DataSnapshot vehicleSnapshop : dataSnapshot.getChildren()) {
                    vehicle = vehicleSnapshop.getValue(Vehicle.class);
                }

                if (vehicle == null) {
                    Toast.makeText(LoginActivity.this, "You are not assigned into a vehicle", Toast.LENGTH_SHORT).show();

                    return;
                }

                if (vehicle.plateNumber == null) {
                    Toast.makeText(LoginActivity.this, "You are not assigned into a vehicle", Toast.LENGTH_SHORT).show();

                    return;
                }

                Log.i("VEHICLE", "plate: " + vehicle.plateNumber);
                DriverMapActivity.assignedPlateNumber = vehicle.plateNumber;

                Intent intent = new Intent(getApplicationContext(), DriverMapActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "You are not assigned into a vehicle", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
