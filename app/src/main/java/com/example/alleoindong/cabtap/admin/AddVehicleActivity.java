package com.example.alleoindong.cabtap.admin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.UserProfile;
import com.example.alleoindong.cabtap.models.Vehicle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddVehicleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.add_plate) EditText mPlateNumber;
    @BindView(R.id.add_make) EditText mMake;
    @BindView(R.id.add_model) EditText mModel;
    @BindView(R.id.add_year) EditText mYear;
    @BindView(R.id.assign_driver) AppCompatSpinner mAssignedDriver;
    public String selectedDriverUID = "";
    public ArrayList<UserProfile> mDriverList;
    public DriversAdapter mDriversAdapter;

    @BindView(R.id.btn_add_vehicle) Button mAddVehicle;
    @BindView(R.id.btn_add_vehicle_loading) ProgressBar mProgress;

    public DatabaseReference mVehicleRef;
    public DatabaseReference mDriversRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        setTitle("Add Vehicle");

        Log.i("VEHICLE_MAINTENANCE", "STARTED VEHICLE");

        ButterKnife.bind(this);
        init();
    }

    @OnClick(R.id.btn_add_vehicle) void addVehicle() {
        this.onShowLoader(true);

        if (mDriverList.size() <= 0) {
            Toast.makeText(this, "Please create a driver first", Toast.LENGTH_SHORT).show();
        }

        String plateNumber = mPlateNumber.getText().toString();
        String make = mMake.getText().toString();
        String model = mModel.getText().toString();
        String year = mYear.getText().toString();
        String uid = selectedDriverUID;

        if ("".equals(selectedDriverUID) && mDriverList.size() > 0) {
            uid = mDriverList.get(0).uid;
        }

        Vehicle vehicle = new Vehicle(plateNumber, make, model, year, uid);

        onShowLoader(false);
        this.saveVehicle(vehicle);
    }

    private void init() {
        initDriverList();
    }

    private void initDriverList() {
        mDriverList = new ArrayList<UserProfile>();

        mDriversRef = FirebaseDatabase.getInstance().getReference("users");
        Query mDrivers = mDriversRef.orderByChild("role").equalTo("driver");

        ValueEventListener driversListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userProfiles : dataSnapshot.getChildren()) {
                    UserProfile userProfile = userProfiles.getValue(UserProfile.class);
                    mDriverList.add(userProfile);
                }

                initDriverSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDrivers.addValueEventListener(driversListener);
    }

    private void initDriverSpinner() {
        mDriversAdapter = new DriversAdapter(this, mDriverList);
        mDriversAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAssignedDriver.setAdapter(mDriversAdapter);
        mAssignedDriver.setOnItemSelectedListener(this);
    }

    public void onShowLoader(boolean isShown) {
        mAddVehicle.setEnabled(!isShown);
        mAddVehicle.setText(isShown ? "" : getString(R.string.save));
        mProgress.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }

    private void saveVehicle(final Vehicle vehicle) {
        mVehicleRef = FirebaseDatabase.getInstance().getReference("vehicles");

        Log.i("VEHICLE_MAINTENANCE", "ADDING VEHICLE");

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        UserProfile userProfile = mDriverList.get(position);
        selectedDriverUID = userProfile.uid;


        Log.i("SELECTED_CHANGE", userProfile.uid);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class DriversAdapter extends ArrayAdapter<UserProfile> {

        public DriversAdapter(Context context, ArrayList<UserProfile> objects) {
            super(context, 0,  objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserProfile userProfile = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_spinner_item, parent, false);
            }

            TextView spinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
            spinnerItem.setText(userProfile.firstName + " " + userProfile.lastName);
            spinnerItem.setTag(userProfile.uid);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            UserProfile userProfile = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_spinner_item, parent, false);
            }

            TextView spinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
            spinnerItem.setText(userProfile.firstName + " " + userProfile.lastName);
            spinnerItem.setTag(userProfile.uid);

            return convertView;
        }
    }
}
