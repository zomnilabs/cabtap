package com.example.alleoindong.cabtap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.alleoindong.cabtap.models.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.first_name) EditText mFirstName;
    @BindView(R.id.last_name) EditText mLastName;
    @BindView(R.id.email) EditText mEmail;
    @BindView(R.id.password) EditText mPassword;
    @BindView(R.id.confirm_password) EditText mConfirmPassword;
    @BindView(R.id.btn_register) Button mRegister;
    @BindView(R.id.btn_register_loading) ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("REGISTRATION");
        setTitle("Register");

        ButterKnife.bind(this);
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

    @OnClick(R.id.btn_register) void registerUser() {
        this.onShowLoader(true);

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();

        if (! password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords does not match", Toast.LENGTH_SHORT).show();
            this.onShowLoader(false);

            return;
        }

        this.createUser(email, password);
    }

    private String createUser(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("CREATE_DRIVER", task.getResult().getUser().getUid());

                        String id = UUID.randomUUID().toString();
                        String uid = task.getResult().getUser().getUid();
                        String firstName = mFirstName.getText().toString();
                        String lastName = mLastName.getText().toString();
                        String role = "passenger";

                        UserProfile user = new UserProfile(id, uid, firstName,
                                lastName, role);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference usersRef = database.getReference("users");
                        usersRef.child(uid).setValue(user);

                        onShowLoader(false);
                    }
                });

        return "";
    }

    public void onShowLoader(boolean isShown) {
        mRegister.setEnabled(!isShown);
        mRegister.setText(isShown ? "" : getString(R.string.add_driver));
        mProgress.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }
}
