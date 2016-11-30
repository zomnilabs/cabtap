package com.example.alleoindong.cabtap.driver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.alleoindong.cabtap.BaseActivity;
import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.Booking;
import com.example.alleoindong.cabtap.models.BookingRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alleoindong on 11/30/16.
 */

public class BookingRequestDialogFragment extends DialogFragment {
    @BindView(R.id.btn_accept_booking) Button mAccept;
    @BindView(R.id.btn_reject_booking) Button mReject;

    public static BookingRequest mBookingRequest;

    public BookingRequestDialogFragment() {

    }

    public static BookingRequestDialogFragment newInstance(String title) {
        BookingRequestDialogFragment frag = new BookingRequestDialogFragment();
        Bundle args = new Bundle();

        args.putString("title", title);
        frag.setArguments(args);

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.booking_request, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(view);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

    }

    @OnClick(R.id.btn_accept_booking) void acceptBooking() {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance()
                .getReference("bookings");

        Booking booking = BookingRequestDialogFragment.mBookingRequest.getBooking();
        booking.setPlateNumber(DriverMapActivity.assignedPlateNumber);
        booking.setStatus("accepted");

        // Save to firebase
        bookingsRef.child(BookingRequestDialogFragment.mBookingRequest.uid).child(booking.id).setValue(booking);

        dismiss();
    }

    @OnClick(R.id.btn_reject_booking) void rejectBooking() {
        DatabaseReference mBookingNotificationsRef = FirebaseDatabase.getInstance()
                .getReference("booking-requests").child(BaseActivity.uid);

        mBookingNotificationsRef.child(BookingRequestDialogFragment.mBookingRequest.id).setValue(null);

        dismiss();
    }
}
