package com.alleoindong.cabtap.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.alleoindong.cabtap.BaseActivity;
import com.alleoindong.cabtap.data.remote.models.Profile;
import com.alleoindong.cabtap.R;
import com.paymaya.sdk.android.PayMayaConfig;
import com.paymaya.sdk.android.checkout.PayMayaCheckout;
import com.paymaya.sdk.android.checkout.PayMayaCheckoutCallback;
import com.paymaya.sdk.android.checkout.models.Buyer;
import com.paymaya.sdk.android.checkout.models.Checkout;
import com.paymaya.sdk.android.checkout.models.Item;
import com.paymaya.sdk.android.checkout.models.RedirectUrl;
import com.paymaya.sdk.android.checkout.models.TotalAmount;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingCompletedActivity extends AppCompatActivity implements PayMayaCheckoutCallback {
    private static final String PUBLIC_FACING_API_KEY = "pk-Lks2bOCVTPho0GMq69xpucndRw0iNGjvNMNmLlY7IKL";
    private PayMayaCheckout mPayMayaCheckout;

    @BindView(R.id.txt_destination) TextView mDestination;
    @BindView(R.id.txt_pickup) TextView mPickup;
    @BindView(R.id.txt_fare) TextView mFare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_completed);

        ButterKnife.bind(this);

        mDestination.setText(PassengerMapActivity.mActiveBooking.destinationName);
        mPickup.setText(PassengerMapActivity.mActiveBooking.passengerLocationName);
        String fare = String.valueOf(PassengerMapActivity.mActiveBooking.fareEstimate) + " PHP";
        mFare.setText(fare);

        PayMayaConfig.setEnvironment(PayMayaConfig.ENVIRONMENT_SANDBOX);
        mPayMayaCheckout = new PayMayaCheckout(PUBLIC_FACING_API_KEY, this);
    }

    @OnClick(R.id.btn_paymaya)
    void payByPaymaya() {
        Profile profile = BaseActivity.currentUser.getProfile();
        Buyer buyer = new Buyer(profile.getFirstName(), "", profile.getLastName());

        BigDecimal summaryTotal = BigDecimal.valueOf(0);
        List itemsList = new ArrayList();
        String currency = "PHP";

        BigDecimal itemAmount = BigDecimal.valueOf(PassengerMapActivity.mActiveBooking.fareEstimate);
        itemAmount = itemAmount.setScale(2, BigDecimal.ROUND_UP);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);
        String result = df.format(itemAmount);

        summaryTotal.add(itemAmount);
        TotalAmount totalAmount = new TotalAmount(itemAmount, currency);
        int quantity = 1;
        Item item1 = new Item("Fare", quantity, totalAmount);
        itemsList.add(item1);
        Log.i("PAYMAYA", totalAmount.toString());
        // URLs
        String successURL = "http://cabtap.zomnilabs.com/bookings/" + PassengerMapActivity.mActiveBooking.id + "/payments/success";
        String failedURL = "http://cabtap.zomnilabs.com/bookings/" + PassengerMapActivity.mActiveBooking.id + "/payments/failed";
        String canceledURL = "http://cabtap.zomnilabs.com/bookings/" + PassengerMapActivity.mActiveBooking.id + "/payments/canceled";

        RedirectUrl redirectUrl = new RedirectUrl(successURL, failedURL, canceledURL);

        // Request
        String requestReference = "YourRequestReferenceCode";
        Checkout checkout = new Checkout(totalAmount, buyer, itemsList, requestReference, redirectUrl);

        // execute
        executeCheckout(checkout);
    }

    @OnClick(R.id.btn_cash)
    void payByCash() {
        moveToHome();
    }

    private void executeCheckout(Checkout payload) {
        mPayMayaCheckout.execute(BookingCompletedActivity.this, payload);
    }

    private void moveToHome() {
        Intent intent = new Intent(this, PassengerMapActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCheckoutSuccess() {
        PassengerMapActivity.mActiveBooking = null;
        moveToHome();
    }

    @Override
    public void onCheckoutCanceled() {
        moveToHome();
    }

    @Override
    public void onCheckoutFailure(String message) {
        moveToHome();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPayMayaCheckout.onActivityResult(requestCode, resultCode, data);
    }
}
