package com.example.alleoindong.cabtap.admin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

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

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.i("CREATE_DRIVER", user.getUid());
//                } else {
//                    // User is signed out
//                }
//            }
//        };
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

    private String createUser(String email, String password) {
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
                        lastName, address, contactNumber, dateBirth, role);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("users");
                usersRef.child(uid).setValue(user);

                onShowLoader(false);
            }
        });

        return "";
    }
}
