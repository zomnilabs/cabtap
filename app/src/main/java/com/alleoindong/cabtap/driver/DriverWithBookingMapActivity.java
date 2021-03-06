package com.alleoindong.cabtap.driver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alleoindong.cabtap.BaseActivity;
import com.alleoindong.cabtap.data.remote.RetrofitHelper;
import com.alleoindong.cabtap.R;
import com.alleoindong.cabtap.data.remote.models.Booking;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverWithBookingMapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    public LatLng mlatlng;
    public Marker mCurrentLocation;
    public Marker mPassengerLocation;
    public LocationRequest mLocationRequest;

    @BindView(R.id.btn_cancel_booking) Button mCancelBooking;
    @BindView(R.id.btn_start_booking) Button mStartBooking;
    @BindView(R.id.btn_complete_booking) Button mCompleteBooking;
    @BindView(R.id.lbl_pickup_location) TextView mPickup;
    @BindView(R.id.lbl_destination_location) TextView mDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_with_booking_map);
        setTitle("Picking up passenger");

        ButterKnife.bind(this);

        // initialize
        init();

        mPickup.setText(DriverMapActivity.mActiveBooking.passengerLocationName);
        mDestination.setText(DriverMapActivity.mActiveBooking.destinationName);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, DriverMapActivity.MY_PERMISSION_ACCESS_FINE_LOCATION);

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
        launchDriverMapActivity();
    }

    @OnClick(R.id.btn_start_booking) void startBookingClick() {
        updateBookingStatus("on-trip");

        LatLng destinationLatLng = new LatLng(DriverMapActivity.mActiveBooking.destination.latitude,
                DriverMapActivity.mActiveBooking.destination.longitude);

        mPassengerLocation.setPosition(destinationLatLng);

        mCancelBooking.setVisibility(View.GONE);
        mStartBooking.setVisibility(View.GONE);
        mCompleteBooking.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_complete_booking) void completeBookingClick() {
        updateBookingStatus("completed");

        launchDriverMapActivity();
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

    private void updatePassengerMarker() {
        if (DriverMapActivity.mActiveBooking.status.equals("completed")) {
            launchDriverMapActivity();
            return;
        }

        if (mPassengerLocation != null) {
            mPassengerLocation.remove();
        }

        LatLng location = null;

        MarkerOptions markerOptions = new MarkerOptions();

        if (DriverMapActivity.mActiveBooking.status.equals("accepted")) {
            markerOptions.title("Passenger Location");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_passenger));

            location = new LatLng(DriverMapActivity.mActiveBooking.passengerLocation.latitude,
                    DriverMapActivity.mActiveBooking.passengerLocation.longitude);

            LatLng midPoint = midPoint(mCurrentLocation.getPosition().latitude,
                    mCurrentLocation.getPosition().longitude, DriverMapActivity.mActiveBooking.passengerLocation.latitude,
                    DriverMapActivity.mActiveBooking.passengerLocation.longitude);

            float angle = angleBteweenCoordinate(mCurrentLocation.getPosition().latitude,
                    mCurrentLocation.getPosition().longitude, DriverMapActivity.mActiveBooking.passengerLocation.latitude,
                    DriverMapActivity.mActiveBooking.passengerLocation.longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(midPoint).zoom(12).bearing(angle).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            markerOptions.title("Destination Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker());

            location = new LatLng(DriverMapActivity.mActiveBooking.destination.latitude,
                    DriverMapActivity.mActiveBooking.destination.longitude);

            LatLng midPoint = midPoint(mCurrentLocation.getPosition().latitude,
                    mCurrentLocation.getPosition().longitude, DriverMapActivity.mActiveBooking.destination.latitude,
                    DriverMapActivity.mActiveBooking.destination.longitude);

            float angle = angleBteweenCoordinate(mCurrentLocation.getPosition().latitude,
                    mCurrentLocation.getPosition().longitude, DriverMapActivity.mActiveBooking.destination.latitude,
                    DriverMapActivity.mActiveBooking.destination.longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(midPoint).zoom(10).bearing(angle).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        markerOptions.position(location);
        mPassengerLocation = mMap.addMarker(markerOptions);
    }

    private void initButtonVisibility() {
        if (DriverMapActivity.mActiveBooking.status.equals("started")) {
            setTitle("Going to destination");

            mCancelBooking.setVisibility(View.GONE);
            mStartBooking.setVisibility(View.GONE);
            mCompleteBooking.setVisibility(View.VISIBLE);
        }
    }

    private void updateBookingStatus(final String status) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings");

        DriverMapActivity.mActiveBooking.setStatus(status);

        bookingRef.child(DriverMapActivity.mActiveBooking.passengerId)
                .child(DriverMapActivity.mActiveBooking.id)
                .setValue(DriverMapActivity.mActiveBooking);

        // Update Remote booking status
        RetrofitHelper.getInstance().getService()
                .changeStatus("Bearer " + BaseActivity.currentUser.getApiToken(),
                        Integer.parseInt(DriverMapActivity.mActiveBooking.id), status)
                .enqueue(new Callback<Booking>() {
                    @Override
                    public void onResponse(Call<Booking> call, Response<Booking> response) {
                        int statusCode = response.code();
                        Log.i("Booking", String.valueOf(statusCode));
                    }

                    @Override
                    public void onFailure(Call<Booking> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
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


    private void launchDriverMapActivity() {
        DriverMapActivity.mActiveBooking = null;
        Intent intent = new Intent(this, DriverMapActivity.class);
        startActivity(intent);
        finish();
    }
}
