package com.example.alleoindong.cabtap.admin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.user.RideBookingActivity;
import com.google.firebase.FirebaseApp;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddMaintenanceActivity extends AppCompatActivity {

    @BindView(R.id.maintenance_schedule) EditText mSchedule;

    private DialogFragment datePickerDialog;
    private SimpleDateFormat dateFormatter;

    protected FirebaseApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_maintenance);

        setTitle("Add Maintenance");

        ButterKnife.bind(this);

        mApp = FirebaseApp.initializeApp(this);
        this.setDateTimeField();
    }

    private void setDateTimeField() {
        mSchedule.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.show(getSupportFragmentManager(), "Schedule date for maintenance");
                }
            }
        });

        datePickerDialog = new AddMaintenanceActivity.DatePickerDialogTheme4();
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

            EditText pickupDate = (EditText) getActivity().findViewById(R.id.maintenance_schedule);

            pickupDate.setText(year + "-" + (month + 1) + "-" + day);
        }
    }
}
