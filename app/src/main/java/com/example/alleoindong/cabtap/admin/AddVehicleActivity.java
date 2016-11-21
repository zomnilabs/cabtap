package com.example.alleoindong.cabtap.admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.Vehicle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddVehicleActivity extends AppCompatActivity {

    @BindView(R.id.add_plate) EditText mPlateNumber;
    @BindView(R.id.add_make) EditText mMake;
    @BindView(R.id.add_model) EditText mModel;
    @BindView(R.id.add_year) EditText mYear;

    public DatabaseReference mVehicleRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        setTitle("Add Vehicle");

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_add_vehicle) void addVehicle() {
        String plateNumber = mPlateNumber.getText().toString();
        String make = mMake.getText().toString();
        String model = mModel.getText().toString();
        String year = mYear.getText().toString();

        Vehicle vehicle = new Vehicle(plateNumber, make, model, year);
        this.saveVehicle(vehicle);
    }

    private void saveVehicle(final Vehicle vehicle) {
        mVehicleRef = FirebaseDatabase.getInstance().getReference("vehicles");

        mVehicleRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.child(vehicle.plateNumber).setValue(vehicle);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                finish();
            }
        });
    }
}
