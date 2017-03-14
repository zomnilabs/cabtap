package com.alleoindong.cabtap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alleoindong.cabtap.admin.AddVehicleActivity;
import com.alleoindong.cabtap.data.remote.models.Profile;
import com.alleoindong.cabtap.data.remote.models.User;
import com.alleoindong.cabtap.models.UserProfile;
import com.alleoindong.cabtap.user.PassengerMapActivity;
import com.alleoindong.cabtap.R;
import com.alleoindong.cabtap.data.remote.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.first_name) EditText mFirstName;
    @BindView(R.id.last_name) EditText mLastName;
    @BindView(R.id.email) EditText mEmail;
    @BindView(R.id.password) EditText mPassword;
    @BindView(R.id.confirm_password) EditText mConfirmPassword;
    @BindView(R.id.btn_register) Button mRegister;
    @BindView(R.id.btn_register_loading) ProgressBar mProgress;
    @BindView(R.id.gender) AppCompatSpinner mGender;

    private ArrayList<String> genders;
    private String selectedGender;

    private GenderAdapter mGenderAdapter;

//    // Observe changes to authentication
//    protected Subscriber<Boolean> authenticationSubscriber = new Subscriber<Boolean>() {
//        @Override
//        public void onCompleted() {
//
//        }
//
//        @Override
//        public void onError(Throwable e) {
//
//        }
//
//        @Override
//        public void onNext(Boolean aBoolean) {
//            Log.i("EmailLogin", aBoolean.toString());
//
//            if (aBoolean) {
//                Log.i("USER_LOGIN", role);
//                Intent intent;
//
//                switch (role) {
//                    case "admin":
//                        intent = new Intent(getApplicationContext(), AdminActivity.class);
//                        startActivity(intent);
//                        break;
//                    case "driver":
//                        intent = new Intent(getApplicationContext(), DriverMapActivity.class);
//                        startActivity(intent);
//                        break;
//                    default:
//                        intent = new Intent(getApplicationContext(), PassengerMapActivity.class);
//                        startActivity(intent);
//                }
//
//                finish();
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("REGISTRATION");

        ButterKnife.bind(this);

        initGenderSpinner();
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

    private void initGenderSpinner() {
        genders = new ArrayList<String>();
        genders.add("Male");
        genders.add("Female");

        mGenderAdapter = new GenderAdapter(this, genders);
        mGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGender.setAdapter(mGenderAdapter);
        mGender.setOnItemSelectedListener(this);
    }

    private void createUser(String email, String password) {
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();

        // Build Data
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        Profile profile = new Profile();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setGender(selectedGender);
        user.setProfile(profile);

        // Register
        RetrofitHelper.getInstance().getService().register(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int statusCode = response.code();
                if (statusCode == 201) {
                    User user = response.body();
                    currentUser = user;
                    uid = user.getId().toString();
                    BaseActivity.firstName = user.getProfile().getFirstName();
                    fullName = user.getProfile().getFirstName() + " " + user.getProfile().getLastName();
                    fcm = user.getFcmToken();

                    // Login the passenger
                    Intent intent = new Intent(getApplicationContext(), PassengerMapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.i("REGISTER", String.valueOf(statusCode));
                    Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                }

                onShowLoader(false);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                onShowLoader(false);
                t.printStackTrace();
            }
        });
    }

//    private String createUser(final String email, String password) {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
//                new OnCompleteListener<AuthResult>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.i("CREATE_DRIVER", task.getResult().getUser().getUid());
//
//                        String id = UUID.randomUUID().toString();
//                        String uid = task.getResult().getUser().getUid();
//                        String firstName = mFirstName.getText().toString();
//                        String lastName = mLastName.getText().toString();
//                        String role = "passenger";
//
//                        UserProfile user = new UserProfile(id, uid, firstName,
//                                lastName, role, email);
//
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference usersRef = database.getReference("users");
//                        usersRef.child(uid).setValue(user);
//
//                        onShowLoader(false);
//                        finish();
//                    }
//                });
//
//        return "";
//    }

    public void onShowLoader(boolean isShown) {
        mRegister.setEnabled(!isShown);
        mRegister.setText(isShown ? "" : getString(R.string.register_now));
        mProgress.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedGender = genders.get(position).toLowerCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class GenderAdapter extends ArrayAdapter<String> {

        public GenderAdapter(Context context, ArrayList<String> objects) {
            super(context, 0,  objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String gender = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_spinner_item, parent, false);
            }

            TextView spinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
            spinnerItem.setText(gender);
            spinnerItem.setTag(gender.toLowerCase());

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            String gender = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_spinner_item, parent, false);
            }

            TextView spinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
            spinnerItem.setText(gender);
            spinnerItem.setTag(gender.toLowerCase());

            return convertView;
        }
    }
}
