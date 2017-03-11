package com.alleoindong.cabtap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alleoindong.cabtap.data.remote.models.User;
import com.google.firebase.auth.FirebaseAuth;

import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class BaseActivity extends AppCompatActivity {
    protected boolean isAuthenticated = false;
    protected String role = null;
    protected Subject<Boolean, Boolean> authenticationObservable = PublishSubject.create();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static String uid = "";
    public static String fullName = "";
    public static String firstName = "";
    public static String email = "";
//    public static UserProfile currentUser;
    public static User currentUser;
    public static String fcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get auth instance
        mAuth = FirebaseAuth.getInstance();

        // Auth Listener
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                if (user != null) {
//                    DatabaseReference userProfileRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
//                    userProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
//
//                            // connected and authenticated
//                            isAuthenticated = true;
//                            role = userProfile.role;
//                            BaseActivity.uid = userProfile.uid;
//                            BaseActivity.fullName = userProfile.firstName + " " + userProfile.lastName;
//                            BaseActivity.firstName = userProfile.firstName;
//                            BaseActivity.email = userProfile.email;
//                            BaseActivity.currentUser = userProfile;
//
//                            BaseActivity.fcm = FirebaseInstanceId.getInstance().getToken();
//                            sendRegistrationToServer(BaseActivity.fcm);
//
//
//                            authenticationObservable.onNext(isAuthenticated);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            isAuthenticated = false;
//                            role = null;
//
//                            authenticationObservable.onNext(isAuthenticated);
//                        }
//                    });
//                } else {
//                    // not connected or signed out
//                    role = null;
//                    isAuthenticated = false;
//                }
//            }
//        };
    }

//    protected void authenticate(String email, String password) {
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), R.string.auth_failed,
//                                    Toast.LENGTH_SHORT).show();
//
//                            isAuthenticated = false;
//                            role = null;
//                            authenticationObservable.onNext(isAuthenticated);
//                        }
//                    }
//                });
//    }

    protected void logout() {
        mAuth.signOut();

        this.isAuthenticated = false;
        this.goBackToLoginActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();

//        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
    }

    protected void goBackToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

//    private void sendRegistrationToServer(String token) {
//        if (BaseActivity.currentUser == null) {
//            return;
//        }
//
//        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
//        UserProfile userProfile = BaseActivity.currentUser;
//        userProfile.setFcmToken(token);
//
//        usersRef.child(userProfile.uid).setValue(userProfile);
//    }
}
