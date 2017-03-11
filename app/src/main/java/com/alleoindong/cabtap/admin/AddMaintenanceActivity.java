package com.alleoindong.cabtap.admin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.alleoindong.cabtap.models.Maintenance;
import com.alleoindong.cabtap.R;
import com.alleoindong.cabtap.models.MaintenanceMenu;
import com.alleoindong.cabtap.models.Vehicle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddMaintenanceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.maintenance_schedule) EditText mSchedule;
    @BindView(R.id.maintenance_plate_number) AppCompatSpinner mSpinnerPlateNumber;
    @BindView(R.id.select_maintenance) AppCompatSpinner mSpinnerMaintenance;
    @BindView(R.id.btn_add_maintenance) Button mAddMaintenance;

    public ArrayList<Vehicle> mVehicleList;
    public DatabaseReference mVehiclesRef;
    public VehicleSpinnerAdapter mVehicleSpinnerAdapter;

    public ArrayList<MaintenanceMenu> mMaintenanceMenuList;
    public DatabaseReference mMaintenanceMenuRef;
    public MaintenanceMenuSpinnerAdapter mMaintenanceMenuSpinnerAdapter;

    public Vehicle mSelectedVehicle;
    public MaintenanceMenu mSelectedMaintenanceMenu;

    private DialogFragment datePickerDialog;
    private SimpleDateFormat dateFormatter;

    public DatabaseReference mMaintenanceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_maintenance);
        setTitle("Add Maintenance");

        ButterKnife.bind(this);

        this.setDateTimeField();
        init();
    }

    @OnClick(R.id.btn_add_maintenance) void saveMaintenance() {

        String id = UUID.randomUUID().toString();
        String maintenance = mSelectedMaintenanceMenu.name;
        double cost = Double.parseDouble(mSelectedMaintenanceMenu.cost);
        String maintenanceDate = mSchedule.getText().toString();
        String plateNumber = mSelectedVehicle.plateNumber;

        Maintenance maintenanceItem = new Maintenance(id, maintenance, cost,
                maintenanceDate, plateNumber);

        this.saveMaintenance(maintenanceItem);
    }

    private void saveMaintenance(final Maintenance maintenance) {
        mMaintenanceRef = FirebaseDatabase.getInstance().getReference("maintenance");

        Log.i("MAINTENANCE", "ADDING MAINTENANCE");

        mMaintenanceRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.child(maintenance.id).setValue(maintenance);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                finish();
            }
        });
    }

    private void init() {
        initVehiclesData();
        initMaintenanceData();
    }

    private void initVehiclesData() {
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

    private void initMaintenanceData() {
        mMaintenanceMenuList = new ArrayList<MaintenanceMenu>();

        mMaintenanceMenuRef = FirebaseDatabase.getInstance().getReference("maintenance-menu");

        ValueEventListener maintenanceMenuListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot maintenanceMenus : dataSnapshot.getChildren()) {
                    MaintenanceMenu maintenanceMenu = maintenanceMenus.getValue(MaintenanceMenu.class);
                    mMaintenanceMenuList.add(maintenanceMenu);
                }

                initMaintenanceMenuSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mMaintenanceMenuRef.addValueEventListener(maintenanceMenuListener);
    }

    private void initVehicleSpinner() {
        mVehicleSpinnerAdapter = new VehicleSpinnerAdapter(this, mVehicleList);
        mVehicleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPlateNumber.setAdapter(mVehicleSpinnerAdapter);
        mSpinnerPlateNumber.setOnItemSelectedListener(this);
    }

    private void initMaintenanceMenuSpinner() {
        mMaintenanceMenuSpinnerAdapter = new MaintenanceMenuSpinnerAdapter(this, mMaintenanceMenuList);
        mMaintenanceMenuSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMaintenance.setAdapter(mMaintenanceMenuSpinnerAdapter);
        mSpinnerMaintenance.setOnItemSelectedListener(this);
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

        mSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show(getSupportFragmentManager(), "Schedule date for maintenance");
            }
        });

        datePickerDialog = new AddMaintenanceActivity.DatePickerDialogTheme4();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AppCompatSpinner spinner = (AppCompatSpinner) parent;
        if (spinner.getId() == R.id.maintenance_plate_number) {
            mSelectedVehicle = mVehicleList.get(position);
        } else if (spinner.getId() == R.id.select_maintenance) {
            mSelectedMaintenanceMenu = mMaintenanceMenuList.get(position);
        }
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
    public class MaintenanceMenuSpinnerAdapter extends ArrayAdapter<MaintenanceMenu> {

        public MaintenanceMenuSpinnerAdapter(Context context, ArrayList<MaintenanceMenu> objects) {
            super(context, 0,  objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MaintenanceMenu maintenanceMenu = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_spinner_item, parent, false);
            }

            TextView spinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
            spinnerItem.setText(maintenanceMenu.name);
            spinnerItem.setTag(maintenanceMenu.name);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            MaintenanceMenu maintenanceMenu = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_spinner_item, parent, false);
            }

            TextView spinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
            spinnerItem.setText(maintenanceMenu.name);
            spinnerItem.setTag(maintenanceMenu.name);

            return convertView;
        }
    }

}
