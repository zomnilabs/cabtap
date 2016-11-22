package com.example.alleoindong.cabtap.user;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.admin.AddDriverActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RideBookingActivity extends AppCompatActivity {
    @BindView(R.id.btn_book_now) Button mBookNow;
    @BindView(R.id.pickup_date) EditText mPickupDate;
    @BindView(R.id.btn_book_now_loading) ProgressBar mProgress;
    private DialogFragment datePickerDialog;
    private SimpleDateFormat dateFormatter;

    protected FirebaseApp mApp;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_booking);

        setTitle("RIDE BOOKING");

        ButterKnife.bind(this);

        mApp = FirebaseApp.initializeApp(this);
        this.setDateTimeField();
    }

    public void onShowLoader(boolean isShown) {
        mBookNow.setEnabled(!isShown);
        mBookNow.setText(isShown ? "" : getString(R.string.book_now));
        mProgress.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }

    private void setDateTimeField() {
        mPickupDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.show(getSupportFragmentManager(), "Select a date");
                }
            }
        });

        datePickerDialog = new RideBookingActivity.DatePickerDialogTheme4();
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

            EditText pickupDate = (EditText) getActivity().findViewById(R.id.pickup_date);

            pickupDate.setText(year + "-" + (month + 1) + "-" + day);
        }
    }

}
