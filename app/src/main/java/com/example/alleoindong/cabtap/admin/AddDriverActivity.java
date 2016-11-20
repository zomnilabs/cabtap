package com.example.alleoindong.cabtap.admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.User;
import com.example.alleoindong.cabtap.models.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDriverActivity extends AppCompatActivity {
    @BindView(R.id.btn_save_driver) Button mSaveDriver;
    @BindView(R.id.driver_first_name) EditText mFirstName;
    @BindView(R.id.driver_last_name) EditText mLastName;
    @BindView(R.id.driver_address) EditText mAddress;
    @BindView(R.id.driver_contact_number) EditText mContactNumber;
    @BindView(R.id.driver_date_birth) EditText mDateBirth;
    @BindView(R.id.driver_email) EditText mEmail;
    @BindView(R.id.driver_password) EditText mPassword;
    @BindView(R.id.btn_save_driver_loading) ProgressBar mProgress;
    private DialogFragment datePickerDialog;
    private SimpleDateFormat dateFormatter;

    protected FirebaseApp mApp;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);
        setTitle("Create User");
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mApp = FirebaseApp.initializeApp(this);
        this.setDateTimeField();
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

    @OnClick(R.id.btn_save_driver) void saveDriver() {
        this.onShowLoader(true);

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        this.createUser(email, password);
    }

    public void onShowLoader(boolean isShown) {
        mSaveDriver.setEnabled(!isShown);
        mSaveDriver.setText(isShown ? "" : getString(R.string.add_driver));
        mProgress.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }

    private void setDateTimeField() {
        mDateBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.show(getSupportFragmentManager(), "Select a date");
                }
            }
        });

        datePickerDialog = new DatePickerDialogTheme4();
    }

    private String createUser(final String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance(mApp);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i("CREATE_DRIVER", task.getResult().getUser().getUid());

                String id = UUID.randomUUID().toString();
                String uid = task.getResult().getUser().getUid();
                String firstName = mFirstName.getText().toString();
                String lastName = mLastName.getText().toString();
                String address = mAddress.getText().toString();
                String contactNumber = mContactNumber.getText().toString();
                String dateBirth = mDateBirth.getText().toString();
                String role = "driver";

                UserProfile user = new UserProfile(id, uid, firstName,
                        lastName, address, contactNumber, dateBirth, role, email);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("users");
                usersRef.child(uid).setValue(user);

                onShowLoader(false);
            }
        });

        return "";
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

            EditText birthDate = (EditText) getActivity().findViewById(R.id.driver_date_birth);

            birthDate.setText(year + "-" + (month + 1) + "-" + day);
        }
    }
}
