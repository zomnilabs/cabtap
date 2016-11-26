package com.example.alleoindong.cabtap.user;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.location.Location;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.admin.AddDriverActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RideBookingActivity extends AppCompatActivity {
    @BindView(R.id.btn_book_now) Button mBookNow;
//    @BindView(R.id.pickup_date) EditText mPickupDate;
    @BindView(R.id.btn_book_now_loading) ProgressBar mProgress;
    @BindView(R.id.estimate_fare_rate) EditText mEstimatedFare;
//    private DialogFragment datePickerDialog;
//    private SimpleDateFormat dateFormatter;
    public SupportPlaceAutocompleteFragment mPlaceAutocomplete;
    public SupportPlaceAutocompleteFragment mPlaceAutocompleteDestination;
//    public AutocompleteFilter mAutoCompleteFilter;
    public LatLng mPickup;
    public LatLng mDestination;


    protected FirebaseApp mApp;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_booking);

        setTitle("RIDE BOOKING");

        ButterKnife.bind(this);

        mApp = FirebaseApp.initializeApp(this);
//        this.setDateTimeField();

//        mAutoCompleteFilter = new AutocompleteFilter.Builder()
//            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
//            .setCountry("ph")
//            .build();

        initPlaceAutocomplete();
        initPlaceAutoCompleteDestination();
    }

    public void initPlaceAutocomplete() {
        mPlaceAutocomplete = (SupportPlaceAutocompleteFragment) getSupportFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        mPlaceAutocomplete.setHint("Pickup Location");
//        mPlaceAutocomplete.setFilter(mAutoCompleteFilter);

        mPlaceAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("PLACE", place.getName().toString() );
                mPickup = place.getLatLng();
                calculateEstimatedFare();
            }

            @Override
            public void onError(Status status) {
                Log.i("PLACE", "ERROR: " + status);
            }
        });
    }
    public void initPlaceAutoCompleteDestination() {
        mPlaceAutocompleteDestination = (SupportPlaceAutocompleteFragment) getSupportFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment_destination);
        mPlaceAutocompleteDestination.setHint("Destination");
//        mPlaceAutocompleteDestination.setFilter(mAutoCompleteFilter);

        mPlaceAutocompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("PLACE", place.getName().toString() );
                mDestination = place.getLatLng();
                calculateEstimatedFare();
            }

            @Override
            public void onError(Status status) {
                Log.i("PLACE", "ERROR: " + status);
            }
        });
    }

    private double getDistanceInMeters() {
        if (mPickup == null) {
            return 0;
        }

        if (mDestination == null) {
            return 0;
        }

        Location a = new Location("a");
        Location b = new Location("b");

        a.setLatitude(mPickup.latitude);
        a.setLongitude(mPickup.longitude);

        b.setLatitude(mDestination.latitude);
        b.setLongitude(mDestination.longitude);

        Log.i("FARE", "DISTANCE: " + String.valueOf(a.distanceTo(b)));

        return Math.abs(a.distanceTo(b));
    }

    private void calculateEstimatedFare() {
        DecimalFormat df = new DecimalFormat("0");
        double total;
        double distance = this.getDistanceInMeters();
        Log.i("FARE", "Distance: " + distance);

        // Flagdown Rate
        distance = distance - 500;
        total = 40;

        // Per 300 meter rate
        total += (distance / 300) * 3.50;

        // Per Minute
        total += (distance / 400) * 3.50;

        Log.i("FARE", "Total: " + total);

        if (total > 0) {
            mEstimatedFare.setText(df.format(total));
        }
    }

    public void onShowLoader(boolean isShown) {
        mBookNow.setEnabled(!isShown);
        mBookNow.setText(isShown ? "" : getString(R.string.book_now));
        mProgress.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }



//    private void setDateTimeField() {
//        mPickupDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    datePickerDialog.show(getSupportFragmentManager(), "Select a date");
//                }
//            }
//        });
//
//        datePickerDialog = new RideBookingActivity.DatePickerDialogTheme4();
//    }

//    public static class DatePickerDialogTheme4 extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar calendar = Calendar.getInstance();
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
//                    R.style.datepicker, this, year, month, day);
//
//            return datepickerdialog;
//        }
//
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//
//            EditText pickupDate = (EditText) getActivity().findViewById(R.id.pickup_date);
//
//            pickupDate.setText(year + "-" + (month + 1) + "-" + day);
//        }
//    }

}
