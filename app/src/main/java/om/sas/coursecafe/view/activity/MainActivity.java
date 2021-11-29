package om.sas.coursecafe.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.dialog.UserTypeDialogFragment;
import om.sas.coursecafe.view.fragments.RegisterFragment;
import om.sas.coursecafe.view.model.UserModel;
import om.sas.coursecafe.view.fragments.LoginFragment;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_USERS;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_BLOCK;
import static om.sas.coursecafe.view.MyConstants.GUEST_USER_TYPE;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener,
        RegisterFragment.OnRegisterFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;
    private String mGoogleProfileName;
    private String mGoogleEmail;

    private UserTypeDialogFragment mUserTypeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        ConfigureGoogleSignIn();

        //changeFragment(new SplashFragment(), SplashFragment.class.getSimpleName());

        if (mFirebaseUser == null || mFirebaseUser.getEmail() == null || mFirebaseUser.getEmail().isEmpty()) {
            LoginFragment();
        } else {
            getUserDetails();
        }


    }

    //Method to change fragment
    private void changeFragment(Fragment fragmentToLoad, String fragmentTag) {
        if (getSupportFragmentManager().findFragmentByTag(fragmentTag) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.fragment_container, fragmentToLoad, fragmentTag)
                    .addToBackStack(fragmentTag)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.fragment_container, fragmentToLoad, fragmentTag)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void LoginFragment() {
        changeFragment(new LoginFragment(), LoginFragment.class.getSimpleName());
    }


    // SingIn methods related to Login Fragment
    private void singInWithNormalLogin(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            if (checkIfEmailIsVerified(mAuth.getCurrentUser())) {

                                getUserDetails();


                            } else {
                                Toast.makeText(MainActivity.this, R.string.verify_email,
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Email or Password is failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void getUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference(FIREBASE_KEY_USERS).child(currentUser.getUid());

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel mUser = dataSnapshot.getValue(UserModel.class);
                UserModel currentUser = UserModel.getInstance();
                currentUser.setUsertype(mUser.getUsertype());
                currentUser.setEmail(mUser.getEmail());
                currentUser.setFullName(mUser.getFullName());
                currentUser.setKeyAudience(mUser.getKeyAudience());
                currentUser.setUserStatus(mUser.getUserStatus());
                currentUser.setCourseRegisterDate(mUser.getCourseRegisterDate());
                currentUser.setDescription(mUser.getDescription());
                currentUser.setPhoneNum(mUser.getPhoneNum());
                currentUser.setProfilePic(mUser.getProfilePic());
                currentUser.setMyNotification(mUser.getMyNotification());
                Log.d("TAG", "user :" + mUser);

                //check if use is pending or blocked
                if (currentUser.getUserStatus().equals(FIREBASE_USER_BLOCK)) {
                    Toast.makeText(MainActivity.this, R.string.your_account_is_blocked, Toast.LENGTH_LONG).show();
                } else {
                    if (currentUser.getUserStatus().equals(MyConstants.FIREBASE_USER_PENDING)) {
                        Toast.makeText(MainActivity.this, R.string.need_permeation, Toast.LENGTH_LONG).show();
                    } else {


                        Intent i = getIntent();
                        String userId=i.getStringExtra("userid");

                        if (userId != null) {

                            Intent intentNotification = new Intent(MainActivity.this, MainAppActivity.class);
                            intentNotification.putExtra("userId", userId);
                            Log.d("MainActivity2",userId);
                            startActivity(intentNotification);
                            finish();
                        }
                        else
                                {

                        Intent intent = new Intent(MainActivity.this, MainAppActivity.class);
                        startActivity(intent);
                        finish();}
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.w(TAG, "Failed to read value.", databaseError.toException());

            }
        });

    }

    private boolean checkIfEmailIsVerified(final FirebaseUser firebaseUser) {
        return firebaseUser.isEmailVerified();
    }


    // onLoginClick methods related to Login Fragment
    // check user status
    @Override
    public void onLoginClick(final String username, final String password) {
        singInWithNormalLogin(username, password);
    }


    @Override
    public void onRegisterClick() {
        changeFragment(new RegisterFragment(), RegisterFragment.class.getSimpleName());

    }

    // access guest user (login)
    @Override
    public void onGuestClick() {
        signInAnonymously();
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.e("TAG", "success sign");
                FirebaseUser currentUser = mAuth.getCurrentUser();

                UserModel userModel = UserModel.getInstance();
                userModel.setFirebaseId(currentUser.getUid());
                userModel.setFullName("Guest User");
                userModel.setUsertype(GUEST_USER_TYPE);
                userModel.setEmail("You are Guest");

                startActivity(new Intent(MainActivity.this, MainAppActivity.class));
                finish();
            }

        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("TAG", "failed sign");
                    }
                });
    }

    // Register methods related to Register Fragment
    @Override
    public void onRegisterFragmentInteraction(final UserModel mUser, String password) {
        mAuth.createUserWithEmailAndPassword(mUser.getEmail(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                mUser.setFirebaseId(firebaseUser.getUid());
                                Toast.makeText(MainActivity.this, "ACCOUNT CREATED.",
                                        Toast.LENGTH_SHORT).show();

                                addUserToFirebase(mUser);
                                verifyEmail(firebaseUser);

                                //add by huda
                                /*if (!mUser.getUserStatus().equals(MyConstants.FIREBASE_USER_PENDING)) {
                                    verifyEmail(firebaseUser);
                                }*/
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    // add User to FireBase
    private void addUserToFirebase(final UserModel user) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = database.getReference(FIREBASE_KEY_USERS);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(user.getFirebaseId())) {
                    LoginFragment();
                } else {
                    mRef.child(user.getFirebaseId()).setValue(user);
                    LoginFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //[Email Verification]
    public void verifyEmail(final FirebaseUser firebaseUser) {
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, R.string.verify_email_send,
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.d("sendEmailVerification", "failed: " + task.getException());
                }
            }
        });
    }

    // onSignInClick methods related to Register Fragment
    @Override
    public void onSignInClick() {
        changeFragment(new LoginFragment(), LoginFragment.class.getSimpleName());
    }

    // this is for KEYBOARDS Hide
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // [Google Login]
    @Override
    public void onLoginGoogleClick() {
        signIn();
    }

    private void ConfigureGoogleSignIn() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    // google START sign in
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(MainActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(account);
        } catch (ApiException e) {
            Toast.makeText(MainActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        UserModel userModel = UserModel.getInstance();
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            for (UserInfo profile : firebaseUser.getProviderData()) {
                                // Id of the provider (ex: google.com)
                                //  String providerId = profile.getProviderId();
                                // Name, email address, and profile photo Url
                                mGoogleProfileName = profile.getDisplayName();
                                mGoogleEmail = profile.getEmail();

                            }

                            userModel.setFirebaseId(firebaseUser.getUid());
                            userModel.setEmail(mGoogleEmail);
                            userModel.setFullName(mGoogleProfileName);

                            addUserToFirebaseFromGoogleLogIn(userModel);
                        }

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                }
            });
        } else {
            Log.d(TAG, "acc failed");
        }
    }

    // Write a message to the database
    private void addUserToFirebaseFromGoogleLogIn(final UserModel userModel) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = database.getReference(MyConstants.FIREBASE_KEY_USERS);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userModel.getFirebaseId())){
                    getUserDetails();
                } else {
                    mUserTypeDialog = UserTypeDialogFragment.newInstance(userModel);
                    mUserTypeDialog.show(getSupportFragmentManager(), UserTypeDialogFragment.class.getSimpleName());
                    mRef.child(userModel.getFirebaseId()).setValue(userModel);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}