package com.example.alleoindong.cabtap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.alleoindong.cabtap.admin.AdminActivity;
import com.example.alleoindong.cabtap.models.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class BaseActivity extends AppCompatActivity {
    protected boolean isAuthenticated = false;
    protected String role = null;
    protected Subject<Boolean, Boolean> authenticationObservable = PublishSubject.create();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get auth instance
        mAuth = FirebaseAuth.getInstance();

        // Auth Listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    DatabaseReference userProfileRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                    userProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                            // connected and authenticated
                            isAuthenticated = true;
                            role = userProfile.role;

                            authenticationObservable.onNext(isAuthenticated);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            isAuthenticated = false;
                            role = null;

                            authenticationObservable.onNext(isAuthenticated);
                        }
                    });
                } else {
                    // not connected or signed out
                    role = null;
                    isAuthenticated = false;
                }

                authenticationObservable.onNext(isAuthenticated);
//                Log.i("EmailLogin", String.valueOf(isAuthenticated));
            }
        };
    }

    protected void authenticate(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    protected void logout() {
        mAuth.signOut();

        this.isAuthenticated = false;
        this.goBackToLoginActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    protected void goBackToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
