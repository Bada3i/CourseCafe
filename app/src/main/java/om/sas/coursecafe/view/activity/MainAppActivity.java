package om.sas.coursecafe.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.SystemClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.adapter.ChatAdapter;
import om.sas.coursecafe.view.adapter.CommentAdapter;
import om.sas.coursecafe.view.adapter.CoursesListAdapter;
import om.sas.coursecafe.view.UtilityClass;
import om.sas.coursecafe.view.adapter.NotificationListAdapter;
import om.sas.coursecafe.view.dialog.AudienceRegisterDialogFragment;
import om.sas.coursecafe.view.dialog.AudienceResigterRequestDialogFragment;
import om.sas.coursecafe.view.dialog.OfferDialogFragment;
import om.sas.coursecafe.view.fragments.AboutUsFragment;

import om.sas.coursecafe.view.fragments.AddCoursesFragment;
import om.sas.coursecafe.view.fragments.AudienceListFragment;
import om.sas.coursecafe.view.fragments.BlockUserFragment;
import om.sas.coursecafe.view.fragments.ChatsListFragment;
import om.sas.coursecafe.view.fragments.CommentsFragment;
import om.sas.coursecafe.view.fragments.ContactUsFragment;
import om.sas.coursecafe.view.fragments.CourseListFragment;
import om.sas.coursecafe.view.fragments.CourseSearchFragment;
import om.sas.coursecafe.view.fragments.DetailsFragment;
import om.sas.coursecafe.view.fragments.DisplayMapFragment;
import om.sas.coursecafe.view.fragments.EditCourseFragment;
import om.sas.coursecafe.view.fragments.EditProfileFragment;

import om.sas.coursecafe.view.fragments.MessageFragment;
import om.sas.coursecafe.view.fragments.NotificationListFragment;
import om.sas.coursecafe.view.fragments.PayPalFragment;
import om.sas.coursecafe.view.fragments.PaymentDetailFragment;
import om.sas.coursecafe.view.fragments.PendingUserFragment;
import om.sas.coursecafe.view.fragments.PickMapFragment;
import om.sas.coursecafe.view.fragments.ProfileFragment;
import om.sas.coursecafe.view.fragments.ReportCourseFragment;
import om.sas.coursecafe.view.fragments.ReportUserFragment;
import om.sas.coursecafe.view.model.CoursesModel;

import om.sas.coursecafe.view.model.MyNotificationPublisherModel;
import om.sas.coursecafe.view.model.NotificationContainer;
import om.sas.coursecafe.view.model.NotificationModel;
import om.sas.coursecafe.view.model.PaymentModel;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_C_START_DATE;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_FIREBASE_ID;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_NOTIFICATION;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_POST_TITLE;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_MY_NOTIFICATION;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_MY_NOTIFICATION_LIST;
import static om.sas.coursecafe.view.MyConstants.GUEST_USER_TYPE;
import static om.sas.coursecafe.view.MyConstants.KEY_ACCEPT;
import static om.sas.coursecafe.view.MyConstants.KEY_ADMIN;
import static om.sas.coursecafe.view.MyConstants.KEY_NORMAL;
import static om.sas.coursecafe.view.MyConstants.KEY_PAID;

public class MainAppActivity extends AppCompatActivity implements CourseSearchFragment.CourseSearchFragmentListerner,
        EditProfileFragment.EditProfileFragmentListener, AudienceListFragment.OnAudienceFragmentListener,
        ProfileFragment.ProfileFragmentListener, AddCoursesFragment.OnAddPostFragmentInteractionListener,
        DetailsFragment.DetailsInterface, EditCourseFragment.EditInterface,
        CoursesListAdapter.CourseListAdapterListerner, CourseListFragment.CourseListFragmentListerner,
        NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener,
        PickMapFragment.OnPickLocationFragmentInteractionListener, NotificationListFragment.NotificationListFragmentListerner,
        AudienceRegisterDialogFragment.AudienceRegisterConfirmationInterface,
        DisplayMapFragment.OnDisplayMapFragmentInteractionListener, ReportCourseFragment.ReportCourseFragmentInterface,
        ReportUserFragment.ReportUserFragmentInterface, BlockUserFragment.BlockUserFragmentInterface,
        PendingUserFragment.PendingUserFragmentInterface, PayPalFragment.PaymentFragmentListerner,
        ContactUsFragment.OnContactUsInteractionListener, AboutUsFragment.OnAboutUsInteractionListener,
        AudienceResigterRequestDialogFragment.OnRegistrationRequestDialogInterfaceListener,
        NotificationListAdapter.NotificationAdapterInterfaceListener, PaymentDetailFragment.PaymentDetailFragmentInterfaceListener,
        ChatsListFragment.ChatsListFragmentInterface, CommentsFragment.CommentInterface, MessageFragment.MessageFragmentInterface,
        CommentAdapter.CommentListAdapterListerner, ChatAdapter.ChatListAdapterListerner {

    private UserModel mUser;
    private FirebaseAuth mAuth;
    private NotificationModel mNotification;
    private AudienceRegisterDialogFragment mDialogC;
    private AudienceResigterRequestDialogFragment mDialogR;
    private OfferDialogFragment offerDialogFragment;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavView;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private boolean mToolBarNavigationListenerIsRegistered = false;
    private FirebaseUser currentUser;
    private boolean mEnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        mUser = UserModel.getInstance();
        mNotification = new NotificationModel();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDrawerLayout = findViewById(R.id.drawer);

        navigationView = findViewById(R.id.navigationView);
        bottomNavView = findViewById(R.id.nav_bottom_bar);
        //bottomNavView.setVisibility(View.GONE);

        if (mUser.getUsertype().equals(GUEST_USER_TYPE)) {
            View header = navigationView.getHeaderView(0);
            TextView tvSideMenuTitle = header.findViewById(R.id.tv_title);
            TextView tvSideMenuEmail = header.findViewById(R.id.tv_email);
            tvSideMenuTitle.setText(mUser.getFullName());
            tvSideMenuEmail.setText(mUser.getEmail());

        } else {
            // Global Variables

            DatabaseReference mRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(currentUser.getUid());

            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final UserModel value = dataSnapshot.getValue(UserModel.class);
                    mUser = value;
                    View header = navigationView.getHeaderView(0);

                    TextView tvSideMenuTitle = header.findViewById(R.id.tv_title);
                    TextView tvSideMenuEmail = header.findViewById(R.id.tv_email);

                    CircleImageView civSideMenuImage = header.findViewById(R.id.profilePic);

                    civSideMenuImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToProfileNewInstance(value);
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }
                    });

                    if (value.getFullName() != null) {
                        tvSideMenuTitle.setText(value.getFullName());
                    } else {
                        tvSideMenuTitle.setText("New User");
                    }

                    tvSideMenuEmail.setText(value.getEmail());

                    if (!MainAppActivity.this.isFinishing()) {
                        Glide.with(MainAppActivity.this).load(value.getProfilePic()).placeholder(R.drawable.ic_person).into(civSideMenuImage);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Log.w(TAG, "Failed to read value.", databaseError.toException());

                }
            });
        }


        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Available Courses");
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawerOpen, R.string.drawerClose);

        mDrawerLayout.addDrawerListener(mToggle);

        mToggle.syncState();
        //mToggle.setDrawerIndicatorEnabled(false);
        navigationView.setNavigationItemSelectedListener(MainAppActivity.this);
        bottomNavView.setOnNavigationItemSelectedListener(MainAppActivity.this);


        Intent i = getIntent();
        String userId=i.getStringExtra("userId");

        if (userId != null) {

            Log.d("MainApp1",userId);
            //Toast.makeText(MainAppActivity.this, "From main1 userID: "+userId, Toast.LENGTH_SHORT).show();
            goToMessage(userId);
        } else {
        //Open Home Page "List of Courses"
        goToHome();}

        hideItem();

        checkNotFromPostFirebase();





    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String userId=intent.getStringExtra("userid");
       // Toast.makeText(this, "From MAin APP2 userID: "+userId, Toast.LENGTH_SHORT).show();
        //Bundle extras = intent.getExtras();
        if (userId != null) {
            Log.d("Main1",userId);
              goToMessage(userId);
        }
    }

    @Override
    public void showBackButton(boolean show) {

        mToolbar.setVisibility(View.VISIBLE);

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (show) {
            //You may not want to open the drawer on swipe from the left in this case
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            // Remove hamburger
            mToggle.setDrawerIndicatorEnabled(false);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mEnable=false;
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't have to be onBackPressed
                        onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            //You must regain the power of swipe for the drawer.
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            // Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            mToggle.setDrawerIndicatorEnabled(true);
            mEnable=true;
            // Remove the/any drawer toggle listener
            mToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }

//    @Override
//    public void startChattingWith(String firebaseId) {
//
//    }


    private void checkNotFromPostFirebase() {

        Query query = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_USERS).child(currentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel value = dataSnapshot.getValue(UserModel.class);
                if (value != null && value.getMyNotification() != null) {

                    DataSnapshot needRef = dataSnapshot.child(FIREBASE_MY_NOTIFICATION).child(FIREBASE_MY_NOTIFICATION_LIST);
                    for (DataSnapshot snap : needRef.getChildren()) {
                        String title = snap.child(FIREBASE_KEY_POST_TITLE).getValue().toString();
                        String notifyDate = snap.child(FIREBASE_KEY_C_START_DATE).getValue().toString();
                        int keyAccept = Integer.parseInt(snap.child(FIREBASE_KEY_NOTIFICATION).getValue().toString());
                        if (keyAccept == KEY_ACCEPT) {
                            Date newDate = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String formattedDate = df.format(newDate);
                            if ((notifyDate).equals(formattedDate)) {
                               /* Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.MINUTE, 45);
                                calendar.set(Calendar.HOUR, 12);
                                calendar.set(Calendar.AM_PM, Calendar.PM);*/
                                scheduleNotification(getNotification("Don't miss your course", title),
                                       /* (int) calendar.getTimeInMillis()*/3000);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // Change Fragment Method
    private void changeFragment(Fragment fragmentToDisplay, String tag) {

//        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                    .replace(R.id.fragment_main_app_container, fragmentToDisplay, tag)
//                    .addToBackStack(tag)
//                    .commit();
//
//
//        } else {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                    .replace(R.id.fragment_main_app_container, fragmentToDisplay, tag)
//                    .commit();
//        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(R.id.fragment_main_app_container, fragmentToDisplay, tag);
        ft.addToBackStack(tag);
        ft.commit();
    }

    @Override
    public void onBackPressed() {

        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1 && mEnable) {

                //getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().popBackStackImmediate(1,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                bottomNavView.getMenu().getItem(0).setChecked(true);
            }else {
                super.onBackPressed();
            }
        }
    }

    // Selection Options for Side Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            MenuItem item = navigationView.getMenu().getItem(i);
            boolean isChecked = item.getItemId() == menuItem.getItemId();
            item.setChecked(isChecked);
        }


        switch (menuItem.getItemId()) {

            case R.id.navigation_home:
                goToHome();
                UtilityClass.hideKeyBoard(MainAppActivity.this);
                break;

            case R.id.navigation_profile:
                //goToProfile();
                if (mUser.getUsertype().equals(GUEST_USER_TYPE)) {
                    startActivity(new Intent(MainAppActivity.this, MainActivity.class));
                } else {
                    goToProfileNewInstance(mUser);
                    UtilityClass.hideKeyBoard(MainAppActivity.this);
                }
                break;

            case R.id.navigation_notifications:
                if (mUser.getUsertype().equals(GUEST_USER_TYPE)) {
                    startActivity(new Intent(MainAppActivity.this, MainActivity.class));
                } else {
                    goToNotification();
                    UtilityClass.hideKeyBoard(MainAppActivity.this);
                }
                break;

            case R.id.navigation_add_post:
                goToAddCourse();
                UtilityClass.hideKeyBoard(MainAppActivity.this);
                break;
            case R.id.navigation_search:
                goToSearchPage();
                UtilityClass.hideKeyBoard(MainAppActivity.this);

                break;
            case R.id.contact:
                changeFragment(new ContactUsFragment(), ContactUsFragment.class.getSimpleName());
                UtilityClass.hideKeyBoard(MainAppActivity.this);
                //bottomNavView.setVisibility(View.GONE);
                break;
            case R.id.about:
                changeFragment(new AboutUsFragment(), AboutUsFragment.class.getSimpleName());
                UtilityClass.hideKeyBoard(MainAppActivity.this);
                //bottomNavView.setVisibility(View.GONE);
                break;
            case R.id.chat:
                if (mUser.getUsertype().equals(GUEST_USER_TYPE)) {
                    startActivity(new Intent(MainAppActivity.this, MainActivity.class));
                } else {
                    goTochatlist(mUser);
                    UtilityClass.hideKeyBoard(MainAppActivity.this);
                }
                break;
            case R.id.logout:
                mAuth.signOut();
                UserModel.getInstance().clearUserDetails();
                startActivity(new Intent(MainAppActivity.this, MainActivity.class));
                finish();
                break;
            default:
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void hideItem() {
        Menu nav_Menu = bottomNavView.getMenu();
        if (mUser.getUsertype().equals(GUEST_USER_TYPE) || mUser.getUsertype().equals(KEY_ADMIN) || mUser.getUsertype().equals(KEY_NORMAL)) {
            nav_Menu.findItem(R.id.navigation_add_post).setVisible(false);
        } else {
            nav_Menu.findItem(R.id.navigation_add_post).setVisible(true);
        }

    }

    @Override
    public void hideBottomNavBar() {
        bottomNavView.setVisibility(View.GONE);

    }


    @Override
    public void showBottomNavBar() {
        bottomNavView.setVisibility(View.VISIBLE);

    }


    // --------------------------- Methods to be used when moving between fragments ------------------//

    // Search Page
    private void goToSearchPage() {
        changeFragment(new CourseSearchFragment(), CourseSearchFragment.class.getSimpleName());
        bottomNavView.getMenu().getItem(3).setChecked(true);
    }

    // Add Course Page
    private void goToAddCourse() {
        changeFragment(new AddCoursesFragment(), AddCoursesFragment.class.getSimpleName());
        bottomNavView.getMenu().getItem(2).setChecked(true);
    }

    // Notification Page
    private void goToNotification() {
        changeFragment(new NotificationListFragment(), NotificationListFragment.class.getSimpleName());
        bottomNavView.getMenu().getItem(1).setChecked(true);
    }

    // Profile Page
    private void goToProfileNewInstance(UserModel userModel) {
        changeFragment(ProfileFragment.newInstance(userModel), ProfileFragment.class.getSimpleName());
        bottomNavView.getMenu().getItem(4).setChecked(true);


    }

    //Course List Page
    private void goToHome() {
        changeFragment(new CourseListFragment(), CourseListFragment.class.getSimpleName());
        bottomNavView.getMenu().getItem(0).setChecked(true);

    }


    // Audience List Page
    private void goToAudienceList(CoursesModel coursesModel) {
        changeFragment(AudienceListFragment.newInstance(coursesModel), AudienceListFragment.class.getSimpleName());

    }


    // Edit Course
    private void goToEditPost(CoursesModel coursesModel) {
        changeFragment(EditCourseFragment.newInstance(coursesModel), EditCourseFragment.class.getSimpleName());

    }

    // Course Detail
    private void goToCourseDetailPage(CoursesModel coursesModel) {
        changeFragment(DetailsFragment.newInstance(coursesModel), DetailsFragment.class.getSimpleName());
    }
    //CHATlst
    private void goTochatlist(UserModel userModel) {
        changeFragment(ChatsListFragment.newInstance(userModel), ChatsListFragment.class.getSimpleName());
        navigationView.getMenu().getItem(0).setChecked(true);


    }

    // Chat with (Message Screen)

    private void goToMessage(String firebaseId){
        changeFragment(MessageFragment.newInstance(firebaseId), MessageFragment.class.getSimpleName());
    }

    //Comment Screen
    private void goToComments(String postId, String postProviderId){
        changeFragment(CommentsFragment.newInstance(postId,postProviderId), CommentsFragment.class.getSimpleName());
    }

    //-------------------------------- Interface Method Implementations -----------------------------------//

    // Add Course Interface Method Implementation
    //1. onAddPostClick
    //2. onOpenPickLocationFragment
    @Override
    public void onAddPostClick(CoursesModel coursesModel) {
        addPostToFirebase(coursesModel);
    }

    @Override
    public void onOpenPickLocationFragment() {
        changeFragment(new PickMapFragment(), PickMapFragment.class.getSimpleName());
        UtilityClass.hideKeyBoard(MainAppActivity.this);
    }


    @Override
    public void OnPostClick() {

    }

    @Override
    public void onOfferClick(CoursesModel course) {
        goToCourseDetailPage(course);
    }

    // This to Implement the Method in all interfaces to change the Title of each page
    // still not added in all Fragments
    @Override
    public void onFragmentInteraction(String title) {
        mToolbar.setTitle(title);

    }

    //Report User Interface
    //1.onUserItemClick
    @Override
    public void onUserItemClick(UserModel userModel) {
        goToProfileNewInstance(userModel);
    }


    // Edit Profile Interface Method Implementations
    // 1. onEditClick
    @Override
    public void onEditClick(UserModel userModel) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_USERS);
        myRef.child(userModel.getFirebaseId()).setValue(userModel);
        UtilityClass.hideKeyBoard(MainAppActivity.this);

        updateCoursesForProfile(userModel);

    }

    private void updateCoursesForProfile(UserModel userModel) {
        FirebaseDatabase database;
        database = FirebaseDatabase.getInstance();
        Query query = database.getReference(MyConstants.FIREBASE_KEY_POSTS)
                .orderByChild(FIREBASE_KEY_FIREBASE_ID).equalTo(userModel.getFirebaseId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //CoursesModel coursesModel = snapshot.getValue(CoursesModel.class);
                        snapshot.getRef().child(MyConstants.FIREBASE_KEY_PROVIDER_NAME).setValue(userModel.getFullName());
                        snapshot.getRef().child(MyConstants.FIREBASE_KEY_PROVIDER_PIC).setValue(userModel.getProfilePic());


                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        goToProfileNewInstance(userModel);
    }


    // Audience List Fragment Function to Open "Audience Register Dialog Fragment"
    //1. onAudienceRegistrationClick

    @Override
    public void onAudienceRegistrationClick(UserModel objUser, CoursesModel objCourse, int i) {
        mDialogC = AudienceRegisterDialogFragment.newInstance(objUser, objCourse, i);
        mDialogC.show(getSupportFragmentManager(), AudienceRegisterDialogFragment.class.getSimpleName());
    }

    // Pick Location MAP Interface Function to update location data
    // It will bring the location Name, Latitude and Longitude
    // 1. onAddLocation
    @Override
    public void onAddLocation(String fullAddress, double locationLat, double locationLng) {

        //Toast.makeText(this, "Address is :"+fullAddress, Toast.LENGTH_SHORT).show();

        onBackPressed();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main_app_container);

        if (fragment instanceof AddCoursesFragment) {
            ((AddCoursesFragment) fragment).updateLocation(fullAddress, locationLat, locationLng);
        } else if (fragment instanceof EditCourseFragment) {
            ((EditCourseFragment) fragment).updateLocation(fullAddress, locationLat, locationLng);
        }
    }

    // Search Interface Method Implementation
    // & CoursesListAdapter Interface Method Implementation
    @Override
    public void onCourseItemClick(CoursesModel courseItem) {
        goToCourseDetailPage(courseItem);

    }

    @Override
    public void onProfileImageClick(String userModelId) {

        //read User from FireBase

        DatabaseReference userRef;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(userModelId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel value = dataSnapshot.getValue(UserModel.class);

                goToProfileNewInstance(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void goToDetailScreen(CoursesModel courseItem) {
        goToCourseDetailPage(courseItem);

    }

    @Override
    public void shareCourseWithFriends(CoursesModel courseItem) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");

        share.putExtra(Intent.EXTRA_TEXT, "Hi Friends, have a look on new course called: " + courseItem.getTitle() + ", Start from " + courseItem.getStartDate()
                + " at " + courseItem.getStartTime() + " , Provide By : " + courseItem.getInstitution() + ".\n"+"Download Courses Cafe App, for more interesting course.\n https://play.google.com/store/apps/details?id=om.sas.coursecafe");

        MainAppActivity.this.startActivity(Intent.createChooser(share, "Share"));

    }

    @Override
    public void goToCommentScreen(String postId, String postProviderId) {
        goToComments(postId, postProviderId);
    }


    // Course Detail Interface Method Implementation
    //1.onSeeAudienceList
    //2.onEditClick
    //3.onRegisterAudienceDialogOpen
    //4.onCheckLocation
    //5.onDeleteClick
    //6.onOfferItemClick

    @Override
    public void onSeeAudienceList(CoursesModel coursesModel) {

        goToAudienceList(coursesModel);

    }

    @Override
    public void onEditClick(CoursesModel coursesModel) {
        goToEditPost(coursesModel);
    }

    @Override
    public void onRegisterAudienceDialogOpen(CoursesModel coursesModel) {
        if (mUser.getUsertype().equals(GUEST_USER_TYPE)) {
            startActivity(new Intent(MainAppActivity.this, MainActivity.class));
        } else {
            mDialogR = AudienceResigterRequestDialogFragment.newInstance(coursesModel);
            mDialogR.show(getSupportFragmentManager(), AudienceResigterRequestDialogFragment.class.getSimpleName());
        }
    }

    @Override
    public void onCheckLocation(CoursesModel coursesModel) {
        changeFragment(DisplayMapFragment.newInstance(coursesModel), DisplayMapFragment.class.getSimpleName());

    }

    @Override
    public void onDeleteClick() {
        goToHome();
    }

    @Override
    public void onOfferItemClick(CoursesModel course) {
        offerDialogFragment = OfferDialogFragment.newInstance(course);
        offerDialogFragment.show(getSupportFragmentManager(), OfferDialogFragment.class.getSimpleName());
    }

    // Edit Course Fragment Interface Methods Implementation
    //1. onEditClickFormEditFragment
    //2. onMap...
    @Override
    public void onEditClickFormEditFragment(CoursesModel courseModel) {
        UtilityClass.hideKeyBoard(MainAppActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_POSTS);
        myRef.child(courseModel.getPostId()).setValue(courseModel);
        goToCourseDetailPage(courseModel);


    }


    // Profile Fragment Interface Methods Implementation
    // 1.onEmailClick
    //2.onPhoneClick
    //3.onEditProfileClick
    //4.onListBlockUserClick
    //5.onListPendingUserClick
    //6.onCourseProfileItemClick
    //7.onReportCourseClick
    //8.onReportUserClick
    @Override
    public void onEmailClick(String email) {
        Intent sendEmail = new Intent(Intent.ACTION_SEND);
        sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        sendEmail.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.courses_apps));
        sendEmail.setType(MyConstants.MESSAGE_PATH);
        startActivity(Intent.createChooser(sendEmail, getResources().getString(R.string.choose_email_client)));
    }

    @Override
    public void onPhoneClick(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "" + 968 + phone, null));
        startActivity(intent);
    }

    @Override
    public void onEditProfileClick(UserModel userModel) {

        changeFragment(EditProfileFragment.newInstance(userModel), EditProfileFragment.class.getSimpleName());
    }

    @Override
    public void onListBlockUserClick() {
        changeFragment(new BlockUserFragment(), BlockUserFragment.class.getSimpleName());
    }

    @Override
    public void onListPendingUserClick() {
        changeFragment(new PendingUserFragment(), PendingUserFragment.class.getSimpleName());
    }

    @Override
    public void onCourseProfileItemClick(CoursesModel coursesModel) {
        goToCourseDetailPage(coursesModel);
    }

    @Override
    public void onReportCourseClick() {
        changeFragment(new ReportCourseFragment(), ReportCourseFragment.class.getSimpleName());
    }

    @Override
    public void onReportUserClick() {
        changeFragment(new ReportUserFragment(), ReportUserFragment.class.getSimpleName());
    }


    // AudienceRegisterDialogFragment interface method implementation
    //1. onNotificationSubmitted
    @Override
    public void onNotificationSubmitted(UserModel sender, UserModel receiver, CoursesModel
            coursesModel, String message, int position) {


        writeNotificationToFireBase(sender, receiver, coursesModel, message);
        getUpdatedAudienceStatus(coursesModel, receiver, position);
        mDialogC.dismiss();
    }


    private void getUpdatedAudienceStatus(CoursesModel mCourse, UserModel mAudiance , int mAudianceKeyPosition) {
        FirebaseDatabase db2 = FirebaseDatabase.getInstance();
        DatabaseReference myRefPost = db2.getReference(MyConstants.FIREBASE_KEY_POSTS).child(mCourse.getPostId()).child("myAudiance").child("myAudianceList");
        myRefPost.child(mAudianceKeyPosition+"").setValue(mAudiance);

        mDialogC.dismiss();
    }


    // AudienceRegisterRequestDialogFragment interface method implementation
    // 1.onAudienceSendRegistrationRequest
    //2.onAlreadyRegisterAudience

    @Override
    public void onAudienceSendRegistrationRequest(CoursesModel coursesModel) {
        goToCourseDetailPage(coursesModel);
        mDialogR.dismiss();
    }

    @Override
    public void onAlreadyRegisterAudience() {
        Toast.makeText(this, R.string.already_reg_text, Toast.LENGTH_SHORT).show();
        mDialogR.dismiss();

    }


    // Notification Adapter interface method implementation

    @Override
    public void OpenPaypalPaymentFragment(String courseId) {

        changeFragment(PayPalFragment.newInstance(courseId), PayPalFragment.class.getSimpleName());

    }

    @Override
    public void onClickNotificationImage(String notificationFromPostId) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef1 = database.getReference(MyConstants.FIREBASE_KEY_POSTS).child(notificationFromPostId);
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CoursesModel value = dataSnapshot.getValue(CoursesModel.class);
                goToCourseDetailPage(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    // PaymentFragment Interface Method Implementation


    @Override
    public void onPaymentSuccessful(PaymentModel paymentModel) {
        changeFragment(PaymentDetailFragment.newInstance(paymentModel), PaymentDetailFragment.class.getSimpleName());

    }

    // Payment Details Fragmnet Method Implementation

    @Override
    public void backToHome() {
        goToHome();
    }

    @Override
    public void sharePaymentDetail(String paymentText) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");

        share.putExtra(Intent.EXTRA_TEXT, paymentText);

        MainAppActivity.this.startActivity(Intent.createChooser(share, "Share"));



    }

    // Message Fragment Interface Method Implementation

        @Override
        public void goToChatListScreenBack() {
            //goTochatlist(mUser);
            onBackPressed();

        }

    @Override
    public void hideBackButton() {

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // Remove hamburger
        mToggle.setDrawerIndicatorEnabled(false);

        // Remove back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mEnable=false;

        mToolbar.setVisibility(View.GONE);


    }

    //Chat Adapter Interface Method Implementation

    @Override
    public void onChatListClick(String firebaseId) {
        goToMessage(firebaseId);
    }

    //--------------------------------------------------End Interface Implementation Section-----------------//


    // ----------------- Other Methods Used --------//
    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(this, MyNotificationPublisherModel.class);
        notificationIntent.putExtra(MyNotificationPublisherModel.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisherModel.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content, String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.logo);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        return builder.build();
    }


    // Write Notification to Firebase
    private void writeNotificationToFireBase(UserModel sender, UserModel receiver, CoursesModel
            coursesModel, String message) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef1 = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(sender.getFirebaseId());
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel valueSender = dataSnapshot.getValue(UserModel.class);
                mNotification.setNotificationSender(valueSender.getFullName());
                mNotification.setNotificationSenderId(valueSender.getFirebaseId());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d("receiver", receiver.getPhoneNum() + "/" + receiver.getProfilePic());

        mNotification.setNotificationDate(UtilityClass.getCurrentDate());
        mNotification.setNotificationImage(coursesModel.getImage());
        mNotification.setSenderPostTitle(coursesModel.getTitle());
        mNotification.setNotificationFromPostId(coursesModel.getPostId());
        mNotification.setNotification(message + coursesModel.getTitle());
        mNotification.setKeyNotification(receiver.getKeyAudience());
        mNotification.setCourseStartDate(coursesModel.getStartDate());

        DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(receiver.getFirebaseId());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel value = dataSnapshot.getValue(UserModel.class);

                if (value.getMyNotification() != null) {
                    value.getMyNotification().getMyNotificationList().add(mNotification);
                } else {

                    ArrayList<NotificationModel> myNotificationArrayList = new ArrayList<>();
                    myNotificationArrayList.add(mNotification);
                    NotificationContainer noteContainer = new NotificationContainer();
                    noteContainer.setMyNotificationList(myNotificationArrayList);
                    value.setMyNotification(noteContainer);
                }
                FirebaseDatabase db2 = FirebaseDatabase.getInstance();
                DatabaseReference myRefUser = db2.getReference(MyConstants.FIREBASE_KEY_USERS);
                myRefUser.child(value.getFirebaseId()).setValue(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    // Writing the new Courses in Firebase
    private void addPostToFirebase(CoursesModel post) {
        // Write a message to the database
        UtilityClass.hideKeyBoard(MainAppActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_POSTS);
        String key = myRef.push().getKey();
        post.setPostId(key);
        myRef.child(key).setValue(post);
        Toast.makeText(MainAppActivity.this, "Congratulations! The course was added successfully", Toast.LENGTH_SHORT).show();
        goToHome();

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



}
