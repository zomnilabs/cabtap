package com.example.alleoindong.cabtap.admin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.UserProfile;
import com.example.alleoindong.cabtap.models.Vehicle;
import com.example.alleoindong.cabtap.user.RideBookingActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddMaintenanceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.maintenance_schedule) EditText mSchedule;
    @BindView(R.id.maintenance_plate_number) AppCompatSpinner mSpinnerPlateNumber;
    @BindView(R.id.select_maintenance) AppCompatSpinner mSpinnerMaintenance;

    public ArrayList<Vehicle> mVehicleList;
    public DatabaseReference mVehiclesRef;
    public VehicleSpinnerAdapter mVehicleSpinnerAdapter;

    private DialogFragment datePickerDialog;
    private SimpleDateFormat dateFormatter;

    protected FirebaseApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_maintenance);

        setTitle("Add Maintenance");

        ButterKnife.bind(this);

        mApp = FirebaseApp.initializeApp(this);
        this.setDateTimeField();
        init();
    }

    private void init() {
        initVehiclesSpinner();
    }

    private void initVehiclesSpinner() {
        mVehicleList = new ArrayList<Vehicle>();

        mVehiclesRef = FirebaseDatabase.getInstance().getReference("vehicles");

        ValueEventListener vehiclesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot vehicles : dataSnapshot.getChildren()) {
                    Vehicle vehicle = vehicles.getValue(Vehicle.class);
                    mVehicleList.add(vehicle);
                }

                initVehicleSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mVehiclesRef.addValueEventListener(vehiclesListener);
    }

    private void initVehicleSpinner() {
        mVehicleSpinnerAdapter = new VehicleSpinnerAdapter(this, mVehicleList);
        mVehicleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPlateNumber.setAdapter(mVehicleSpinnerAdapter);
        mSpinnerPlateNumber.setOnItemSelectedListener(this);
    }

    private void setDateTimeField() {
        mSchedule.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.show(getSupportFragmentManager(), "Schedule date for maintenance");
                }
            }
        });

        datePickerDialog = new AddMaintenanceActivity.DatePickerDialogTheme4();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class DatePickerDialogTheme4 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    R.style.datepicker, this, year, month, day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            EditText pickupDate = (EditText) getActivity().findViewById(R.id.maintenance_schedule);

            pickupDate.setText(year + "-" + (month + 1) + "-" + day);
        }
    }

    public class VehicleSpinnerAdapter extends ArrayAdapter<Vehicle> {

        public VehicleSpinnerAdapter(Context context, ArrayList<Vehicle> objects) {
            super(context, 0,  objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Vehicle vehicle = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_spinner_item, parent, false);
            }

            TextView spinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
            spinnerItem.setText(vehicle.plateNumber);
            spinnerItem.setTag(vehicle.plateNumber);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            Vehicle vehicle = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_spinner_item, parent, false);
            }

            TextView spinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
            spinnerItem.setText(vehicle.plateNumber);
            spinnerItem.setTag(vehicle.plateNumber);

            return convertView;
        }
    }

}
