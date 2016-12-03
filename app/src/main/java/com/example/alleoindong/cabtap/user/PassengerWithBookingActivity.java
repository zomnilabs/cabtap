package com.example.alleoindong.cabtap.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.alleoindong.cabtap.BaseActivity;
import com.example.alleoindong.cabtap.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassengerWithBookingActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    public LatLng mlatlng;
    public Marker mCurrentLocation;
    public Marker mDriverLocation;
    public LocationRequest mLocationRequest;

    @BindView(R.id.btn_cancel_booking) Button mCancelBooking;
    @BindView(R.id.lbl_pickup_location) TextView mPickup;
    @BindView(R.id.lbl_destination_location) TextView mDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_with_booking);
        setTitle("Waiting for taxi");

        ButterKnife.bind(this);

        // initialize
        init();

        mPickup.setText(PassengerMapActivity.mActiveBooking.passengerLocationName);
        mDestination.setText(PassengerMapActivity.mActiveBooking.destinationName);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PassengerMapActivity.MY_PERMISSION_ACCESS_FINE_LOCATION);

            return;
        }

        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        if (mCurrentLocation != null) {
            mCurrentLocation.remove();
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mlatlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mlatlng);
            markerOptions.title("My Current Position");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
            mCurrentLocation = mMap.addMarker(markerOptions);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(mlatlng).zoom(16).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        updatePassengerMarker();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrentLocation != null) {
            mCurrentLocation.remove();
        }

        mlatlng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mlatlng);
        markerOptions.title("My Current Position");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
        mCurrentLocation = mMap.addMarker(markerOptions);
    }

    @OnClick(R.id.btn_cancel_booking) void cancelBookingClick() {
        updateBookingStatus("cancelled");
        launchPassengerMapActivity();
    }

    private void updatePassengerMarker() {
        if (PassengerMapActivity.mActiveBooking.status.equals("completed")) {
            launchPassengerMapActivity();
        }

        if (mDriverLocation != null) {
            mDriverLocation.remove();
        }

        LatLng location = null;

        MarkerOptions markerOptions = new MarkerOptions();

        if (PassengerMapActivity.mActiveBooking.status.equals("accepted")) {
            markerOptions.title("Passenger Location");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cab));

            location = new LatLng(PassengerMapActivity.mActiveBooking.passengerLocation.latitude,
                    PassengerMapActivity.mActiveBooking.passengerLocation.longitude);

            LatLng midPoint = midPoint(mCurrentLocation.getPosition().latitude,
                    mCurrentLocation.getPosition().longitude, PassengerMapActivity.mActiveBooking.passengerLocation.latitude,
                    PassengerMapActivity.mActiveBooking.passengerLocation.longitude);

            float angle = angleBteweenCoordinate(mCurrentLocation.getPosition().latitude,
                    mCurrentLocation.getPosition().longitude, PassengerMapActivity.mActiveBooking.passengerLocation.latitude,
                    PassengerMapActivity.mActiveBooking.passengerLocation.longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(midPoint).zoom(12).bearing(angle).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            markerOptions.title("Destination Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker());

            location = new LatLng(PassengerMapActivity.mActiveBooking.destination.latitude,
                    PassengerMapActivity.mActiveBooking.destination.longitude);

            LatLng midPoint = midPoint(mCurrentLocation.getPosition().latitude,
                    mCurrentLocation.getPosition().longitude, PassengerMapActivity.mActiveBooking.destination.latitude,
                    PassengerMapActivity.mActiveBooking.destination.longitude);

            float angle = angleBteweenCoordinate(mCurrentLocation.getPosition().latitude,
                    mCurrentLocation.getPosition().longitude, PassengerMapActivity.mActiveBooking.destination.latitude,
                    PassengerMapActivity.mActiveBooking.destination.longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(midPoint).zoom(10).bearing(angle).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        markerOptions.position(location);
        mDriverLocation = mMap.addMarker(markerOptions);
    }

    private void updateBookingStatus(String status) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings");

        PassengerMapActivity.mActiveBooking.setStatus(status);

        bookingRef.child(PassengerMapActivity.mActiveBooking.passengerId)
                .child(PassengerMapActivity.mActiveBooking.id)
                .setValue(PassengerMapActivity.mActiveBooking);
    }

    private void init() {
        initMap();
        initGoogleApiClient();
        initButtonVisibility();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private void initGoogleApiClient() {
        // Build Google Api Client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void initButtonVisibility() {
        if (PassengerMapActivity.mActiveBooking.status.equals("started")) {
            setTitle("Going to destination");

            mCancelBooking.setVisibility(View.GONE);
        }
    }

    private LatLng midPoint(double lat1, double long1, double lat2,double long2) {
        return new LatLng((lat1+lat2)/2, (long1+long2)/2);
    }

    private float angleBteweenCoordinate(double lat1, double long1, double lat2,
                                         double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return (float) brng;
    }


    private void launchPassengerMapActivity() {
        PassengerMapActivity.mActiveBooking = null;
        Intent intent = new Intent(this, PassengerMapActivity.class);
        startActivity(intent);
        finish();
    }
}
