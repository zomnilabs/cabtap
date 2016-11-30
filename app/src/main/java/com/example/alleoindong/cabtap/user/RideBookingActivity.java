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
import com.example.alleoindong.cabtap.models.Booking;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RideBookingActivity extends AppCompatActivity {
    @BindView(R.id.btn_book_now) Button mBookNow;
    @BindView(R.id.btn_book_now_loading) ProgressBar mProgress;
    @BindView(R.id.estimate_fare_rate) EditText mEstimatedFare;
    public SupportPlaceAutocompleteFragment mPlaceAutocomplete;
    public SupportPlaceAutocompleteFragment mPlaceAutocompleteDestination;
    public LatLng mPickup;
    public LatLng mDestination;
    public DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_booking);

        setTitle("RIDE BOOKING");

        ButterKnife.bind(this);

        bookingsRef = FirebaseDatabase.getInstance()
                .getReference("bookings");

        initPlaceAutocomplete();
        initPlaceAutoCompleteDestination();
    }

    @OnClick(R.id.btn_book_now) void bookNow() {
        onShowLoader(true);

        final String id = UUID.randomUUID().toString();
        String status = "finding-driver";
        double fareEstimate = Double.parseDouble(mEstimatedFare.getText().toString());

        final Booking booking = new Booking(id, status, fareEstimate, mPickup, mDestination);

        // Save to firebase
        bookingsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.child(id).setValue(booking);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                finish();
            }
        });
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

}
