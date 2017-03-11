package com.alleoindong.cabtap.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alleoindong.cabtap.BaseActivity;
import com.alleoindong.cabtap.data.remote.RetrofitHelper;
import com.alleoindong.cabtap.models.Booking;
import com.alleoindong.cabtap.models.BookingRequest;
import com.alleoindong.cabtap.R;
import com.alleoindong.cabtap.data.remote.models.Driver;
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
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideBookingActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    @BindView(R.id.btn_book_now) Button mBookNow;
    @BindView(R.id.btn_book_now_loading) ProgressBar mProgress;
    @BindView(R.id.estimate_fare_rate) EditText mEstimatedFare;
    public SupportPlaceAutocompleteFragment mPlaceAutocomplete;
    public SupportPlaceAutocompleteFragment mPlaceAutocompleteDestination;
    public LatLng mPickup;
    public LatLng mDestination;
    public String mPickupName;
    public String mDestinationName;
    public String mFare;

    public ArrayList<Driver> vehicles;

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

        vehicles = new ArrayList<Driver>();

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
        createRemoteBooking();

    }

    private void createRemoteBooking() {
        final double fareEstimate = Double.parseDouble(mFare);

        com.alleoindong.cabtap.data.remote.models.Booking booking =
                new com.alleoindong.cabtap.data.remote.models.Booking();

        booking.setPrice(fareEstimate);
        booking.setPickup(mPickupName);
        booking.setDestination(mDestinationName);
        booking.setPickupLat(mPickup.latitude);
        booking.setPickupLng(mPickup.longitude);
        booking.setDestinationLat(mDestination.latitude);
        booking.setDestinationLng(mDestination.longitude);

        RetrofitHelper.getInstance().getService().createBooking("Bearer " + BaseActivity.currentUser.getApiToken(), booking).enqueue(new Callback<com.alleoindong.cabtap.data.remote.models.Booking>() {
            @Override
            public void onResponse(Call<com.alleoindong.cabtap.data.remote.models.Booking> call, Response<com.alleoindong.cabtap.data.remote.models.Booking> response) {
                int statusCode = response.code();

                if (statusCode != 201) {
                    Toast.makeText(RideBookingActivity.this, "Failed in creating a booking", Toast.LENGTH_SHORT).show();
                    onShowLoader(false);
                    Log.i("Book", String.valueOf(statusCode));
                    return;
                }

                String id = response.body().getId().toString();

                String status = "pending";
                com.alleoindong.cabtap.models.Location pickup = new com.alleoindong.cabtap.models.Location(mPickup.latitude, mPickup.longitude);

                com.alleoindong.cabtap.models.Location destination = new com.alleoindong.cabtap.models.Location(mDestination.latitude, mDestination.longitude);

                Booking booking = new Booking(id, BaseActivity.uid, status,
                        fareEstimate, mPickupName, mDestinationName, pickup, destination);

                String requestId = UUID.randomUUID().toString();
                BookingRequest bookingRequest = new BookingRequest(requestId, BaseActivity.uid,
                        mPickupName, mDestinationName, "pending", booking);

                for (Driver vehicle : vehicles) {
                    bookingRequestsRef.child(vehicle.getUser().getId().toString()).child(requestId).setValue(bookingRequest);
                }

                listenForAcceptedBooking(id);
            }

            @Override
            public void onFailure(Call<com.alleoindong.cabtap.data.remote.models.Booking> call, Throwable t) {
                Toast.makeText(RideBookingActivity.this, "Failed to create booking", Toast.LENGTH_SHORT).show();
                onShowLoader(false);

                t.printStackTrace();
            }
        });
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
                mPickupName = place.getName().toString();

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
                mDestinationName = place.getName().toString();

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
                Booking booking = dataSnapshot.getValue(Booking.class);

                if (booking == null) {
                    return;
                }

                if (! booking.status.equals("accepted")) {
                    return;
                }

                if (booking.status.equals("cancelled")) {
                    finish();
                }

                PassengerMapActivity.mActiveBooking = booking;

                showBookingAcceptedDialog(booking);
                Toast.makeText(RideBookingActivity.this, "A driver has accepted your request", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        bookingsRef.addValueEventListener(bookingListener);
    }

    private void showBookingAcceptedDialog(Booking booking) {
        FragmentManager fm = getSupportFragmentManager();

        BookingAcceptedDialog bookingAcceptedDialog = BookingAcceptedDialog
                .newInstance("Passenger Requesting for taxi", booking.plateNumber);

        bookingAcceptedDialog.show(fm, "fragment_booking_accepted");
    }

    private void getVehicle(String plateNumber) {
//        vehiclesRef = FirebaseDatabase.getInstance()
//                .getReference("vehicles").child(plateNumber);
//
//        ValueEventListener vehiclesDataListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
//
//                if (vehicle != null) {
//                    addVehicleToList(vehicle);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//        vehiclesRef.addListenerForSingleValueEvent(vehiclesDataListener);

        RetrofitHelper.getInstance().getService().getVehicleByPlateNumber(plateNumber).enqueue(new Callback<Driver>() {
            @Override
            public void onResponse(Call<Driver> call, Response<Driver> response) {
                addVehicleToList(response.body());
            }

            @Override
            public void onFailure(Call<Driver> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void addVehicleToList(Driver vehicle) {
        Log.i("REQUEST", vehicle.getVehicle().getPlateNumber());

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
            String formattedTotal = String.format("%s PHP", df.format(total));
            mFare = df.format(total);
            mEstimatedFare.setText(formattedTotal);
        }
    }

    public void onShowLoader(boolean isShown) {
        mBookNow.setEnabled(!isShown);
        mBookNow.setText(isShown ? "" : getString(R.string.book_now));
        mProgress.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (PassengerMapActivity.mActiveBooking != null) {
            Intent intent = new Intent(this, PassengerWithBookingActivity.class);
            startActivity(intent);
        }

        finish();
    }
}
