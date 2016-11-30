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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.alleoindong.cabtap.BaseActivity;
import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.Booking;
import com.example.alleoindong.cabtap.models.BookingRequest;
import com.example.alleoindong.cabtap.models.Vehicle;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
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

    public ArrayList<Vehicle> vehicles;

    public DatabaseReference vehiclesRef;
    public DatabaseReference bookingRequestsRef;
    public DatabaseReference bookingsRef;

    public GeoQuery geoQuery;
    private DatabaseReference mGeofireRef;
    private GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_booking);

        setTitle("RIDE BOOKING");

        ButterKnife.bind(this);

        bookingRequestsRef = FirebaseDatabase.getInstance()
                .getReference("booking-requests");

        // initialize geofire
        mGeofireRef = FirebaseDatabase.getInstance().getReference("geofire");
        geoFire = new GeoFire(mGeofireRef);

        vehicles = new ArrayList<Vehicle>();

        initPlaceAutocomplete();
        initPlaceAutoCompleteDestination();
    }

    public void getNearbyVehicles(LatLng location) {
        geoQuery = geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), 1);

        geoQueryEventListener();
    }

    private void geoQueryEventListener() {
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.i("VEHICLES", String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));

                getVehicle(key);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @OnClick(R.id.btn_book_now) void bookNow() {
        if (vehicles.size() < 0) {
            Toast.makeText(this, "There is no taxi nearby your selected pickup location", Toast.LENGTH_SHORT).show();
            return;
        }

        onShowLoader(true);

        String id = UUID.randomUUID().toString();
        String status = "finding-driver";
        double fareEstimate = Double.parseDouble(mEstimatedFare.getText().toString());
        com.example.alleoindong.cabtap.models.Location pickup = new com.example.alleoindong
                .cabtap.models.Location(mPickup.latitude, mPickup.longitude);

        com.example.alleoindong.cabtap.models.Location destination = new com.example.alleoindong
                .cabtap.models.Location(mDestination.latitude, mDestination.longitude);

        Booking booking = new Booking(id, status, fareEstimate, pickup, destination);

        String requestId = UUID.randomUUID().toString();
        BookingRequest bookingRequest = new BookingRequest(requestId, BaseActivity.uid, "pending", booking);

        for (Vehicle vehicle : vehicles) {
            bookingRequestsRef.child(vehicle.uid).child(requestId).setValue(bookingRequest);
        }
    }


    public void initPlaceAutocomplete() {
        mPlaceAutocomplete = (SupportPlaceAutocompleteFragment) getSupportFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        mPlaceAutocomplete.setHint("Pickup Location");

        mPlaceAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("PLACE", place.getName().toString() );
                mPickup = place.getLatLng();

                calculateEstimatedFare();

                getNearbyVehicles(mPickup);
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

    private void listenForAcceptedBooking(String bookingId) {
        bookingsRef = FirebaseDatabase.getInstance()
                .getReference("bookings")
                .child(BaseActivity.uid)
                .child(bookingId);

        ValueEventListener bookingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        bookingsRef.addValueEventListener(bookingListener);
    }

    private void getVehicle(String plateNumber) {
        vehiclesRef = FirebaseDatabase.getInstance()
                .getReference("vehicles").child(plateNumber);

        ValueEventListener vehiclesDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);

                if (vehicle != null) {
                    addVehicleToList(vehicle);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        vehiclesRef.addListenerForSingleValueEvent(vehiclesDataListener);
    }

    private void addVehicleToList(Vehicle vehicle) {
        Log.i("REQUEST", vehicle.plateNumber);

        if (vehicles.contains(vehicle)) {
            return;
        }

        vehicles.add(vehicle);
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
