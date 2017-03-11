package com.alleoindong.cabtap.driver;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alleoindong.cabtap.data.remote.models.Booking;
import com.alleoindong.cabtap.models.BookingRequest;
import com.alleoindong.cabtap.BaseActivity;
import com.alleoindong.cabtap.R;
import com.alleoindong.cabtap.data.remote.RetrofitHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alleoindong on 11/30/16.
 */

public class BookingRequestDialogFragment extends DialogFragment {
    @BindView(R.id.btn_accept_booking) Button mAccept;
    @BindView(R.id.btn_reject_booking) Button mReject;

    public static BookingRequest mBookingRequest;

    public BookingRequestDialogFragment() {

    }

    public static BookingRequestDialogFragment newInstance(String title, String pickup, String destination) {
        BookingRequestDialogFragment frag = new BookingRequestDialogFragment();
        Bundle args = new Bundle();

        args.putString("title", title);
        args.putString("pickup", pickup);
        args.putString("destination", destination);
        frag.setArguments(args);

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booking_request, container);

        ((Button) view.findViewById(R.id.btn_accept_booking)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptBooking();
            }
        });

        ((Button) view.findViewById(R.id.btn_reject_booking)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectBooking();
            }
        });

        String pickup = getArguments().getString("pickup", "No selected");
        String destination = getArguments().getString("destination", "No selected");

        ((TextView) view.findViewById(R.id.lbl_pickup_location)).setText(pickup);
        ((TextView) view.findViewById(R.id.lbl_destination_location)).setText(destination);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    public void acceptBooking() {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance()
                .getReference("bookings");

        DatabaseReference requestRef = FirebaseDatabase.getInstance()
                .getReference("booking-requests")
                .child(BaseActivity.uid);

        requestRef.setValue(null);

        com.alleoindong.cabtap.models.Booking booking = BookingRequestDialogFragment.mBookingRequest.getBooking();
        booking.setPlateNumber(DriverMapActivity.assignedPlateNumber);
        booking.setStatus("accepted");
        booking.passengerLocationName = BookingRequestDialogFragment.mBookingRequest.pickup;
        booking.destinationName = BookingRequestDialogFragment.mBookingRequest.destination;

        // Save to firebase
        bookingsRef.child(BookingRequestDialogFragment.mBookingRequest.uid).child(booking.id).setValue(booking);
        RetrofitHelper.getInstance().getService()
                .changeStatus("Bearer " + BaseActivity.currentUser.getApiToken()
                        , Integer.parseInt(booking.id), "accepted")
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

        // Set as currently active booking
        DriverMapActivity.mActiveBooking = booking;

        dismissAllowingStateLoss();
    }

    public void rejectBooking() {
        DatabaseReference mBookingNotificationsRef = FirebaseDatabase.getInstance()
                .getReference("booking-requests").child(BaseActivity.uid);

        mBookingNotificationsRef.child(BookingRequestDialogFragment.mBookingRequest.id).setValue(null);

        // Set null to active booking
        DriverMapActivity.mActiveBooking = null;

        dismissAllowingStateLoss();
    }
}
