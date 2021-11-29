package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.adapter.NormalProfileRecyclerViewAdapter;
import om.sas.coursecafe.view.adapter.ProfileRecyclerViewAdapter;
import om.sas.coursecafe.view.dialog.ConfirmDialogFragment;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.NotificationModel;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_FIREBASE_ID;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_NOTIFICATION;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_MY_NOTIFICATION;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_MY_NOTIFICATION_LIST;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_REPORT_USER;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_BLOCK;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_STATUS;
import static om.sas.coursecafe.view.MyConstants.KEY_ACCEPT;
import static om.sas.coursecafe.view.MyConstants.KEY_ADMIN;
import static om.sas.coursecafe.view.MyConstants.KEY_PAID;


public class ProfileFragment extends Fragment implements ProfileRecyclerViewAdapter.OnItemProfileClickListener,
        NormalProfileRecyclerViewAdapter.OnItemProfileClickListener, ConfirmDialogFragment.ConfirmDialogFragmentListener {

    private static final String TAG = "profile_fragment";

    private Context mContext;
    private DatabaseReference mRef;
    private FirebaseDatabase database;
    private ProfileRecyclerViewAdapter mAdapter;
    private NormalProfileRecyclerViewAdapter mNormalAdapter;
    private ProfileFragmentListener mListener;
    private UserModel mUserModel;
    private ArrayList<CoursesModel> coursesArrayList;
    private ArrayList<NotificationModel> regCoursesArrayList;
    private TextView tvUserName, tvInfo, mTvDefaultInfo, tvAdminProfile;
    private ImageButton ibEmail, ibPhone;
    private CircleImageView ivProfilePic;
    private ImageView ivBgProfilePic;
    private RecyclerView rvCourses;
    private Menu menu;
    private int currentAction = 0;
    private FragmentManager fragmentManager;


    public static ProfileFragment newInstance(UserModel user) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(MyConstants.KEY_USER, user);
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mUserModel = (UserModel) getArguments().getSerializable(MyConstants.KEY_USER);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            if (!mUserModel.getFirebaseId().equals(firebaseUser.getUid())) {
                this.menu = menu;
                inflater.inflate(R.menu.other_user_profile_menu, menu);

                //hide item from menu
                if (!Objects.requireNonNull(firebaseUser.getEmail()).equals(MyConstants.KEY_ADMIN_EMAIL)) {
                    MenuItem block = menu.findItem(R.id.user_block);
                    block.setVisible(false);
                }

            } else if (Objects.requireNonNull(firebaseUser.getEmail()).equals(MyConstants.KEY_ADMIN_EMAIL)) {
                inflater.inflate(R.menu.admin_profile_menu, menu);
            } else {
                inflater.inflate(R.menu.profile_menu, menu);
            }
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // menu show for profile owner
        if (item.getItemId() == R.id.edit_profile) {
            onEditPressed(mUserModel);
            return true;
        }

        // menu show only for admin
        switch (item.getItemId()) {
            case R.id.edit_profile:
                onEditPressed(mUserModel);
                return true;

            case R.id.block_user:
                if (mListener != null) {
                    mListener.onListBlockUserClick();
                }
                return true;

            case R.id.user_pending:
                if (mListener != null) {
                    mListener.onListPendingUserClick();
                }
                return true;

            case R.id.report_user:
                if (mListener != null) {
                    mListener.onReportUserClick();
                }
                return true;

            case R.id.report_course:
                if (mListener != null) {
                    mListener.onReportCourseClick();
                }
                return true;
        }

        // menu show for other user
        switch (item.getItemId()) {
            case R.id.send_message:
//                String firebaseID = mUserModel.getFirebaseId();
//                String firebaseID1 = UserModel.getInstance().getFirebaseId();
                if (mListener != null){
                mListener.onChatListClick(mUserModel.getFirebaseId());}

                return true;

            case R.id.report_profile:
                currentAction = 1;
                ConfirmDialogFragment confirmDialogFragmentReport = ConfirmDialogFragment.newInstance
                        (getResources().getString(R.string.d_title_rep_user), getResources().getString(R.string.d_msg_rep_user),
                                mUserModel, 0, null);
                if (fragmentManager != null) {
                    confirmDialogFragmentReport.setTargetFragment(ProfileFragment.this, 300);
                    confirmDialogFragmentReport.show(fragmentManager, "report_user");
                }

                return true;

            case R.id.user_block:
                currentAction = 2;
                ConfirmDialogFragment confirmDialogFragmentBlock = ConfirmDialogFragment.newInstance
                        (getResources().getString(R.string.d_title_block), getResources().getString(R.string.d_msg_block),
                                mUserModel, 0, null);
                if (fragmentManager != null) {
                    confirmDialogFragmentBlock.setTargetFragment(ProfileFragment.this, 300);
                    confirmDialogFragmentBlock.show(fragmentManager, "block_users");
                }


                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.profileTitle));
            mListener.showBottomNavBar();
            mListener.showBackButton(false);
        }
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_profile, container, false);

        database = FirebaseDatabase.getInstance();
        fragmentManager = getFragmentManager();

        ivProfilePic = parentView.findViewById(R.id.iv_profile_pic);
        tvUserName = parentView.findViewById(R.id.tv_user_name);
        tvInfo = parentView.findViewById(R.id.tv_info);
        ibEmail = parentView.findViewById(R.id.ib_email);
        ibPhone = parentView.findViewById(R.id.ib_phone);
        ivBgProfilePic = parentView.findViewById(R.id.iv_bg_profile_pic);
        tvAdminProfile = parentView.findViewById(R.id.tv_admin_profile);

        rvCourses = parentView.findViewById(R.id.rv_courses);
        mTvDefaultInfo = parentView.findViewById(R.id.tv_profile);

        mAdapter = new ProfileRecyclerViewAdapter(mContext, coursesArrayList, this);

        mNormalAdapter = new NormalProfileRecyclerViewAdapter(mContext, regCoursesArrayList, this);

        readUsersFromFireBase();

        checkingUserType();

        return parentView;
    }

    private void checkingUserType() {
        mRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(mUserModel.getFirebaseId());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel value = dataSnapshot.getValue(UserModel.class);
                if (value != null) {
                    String userType = value.getUsertype();
                    if (userType.equals(KEY_ADMIN)) {

                        tvAdminProfile.setVisibility(View.VISIBLE);
                        tvInfo.setText(getResources().getString(R.string.courses_admin));
                        tvAdminProfile.setText(value.getDescription());

                    } else if (userType.equals(MyConstants.KEY_NORMAL)) {
                        readCourseUserRegisterFromFirebase();
                        rvCourses.setAdapter(mNormalAdapter);

                    } else {
                        readCoursesFromFirebase();
                        rvCourses.setAdapter(mAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void readUsersFromFireBase() {
        mRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(mUserModel.getFirebaseId());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel value = dataSnapshot.getValue(UserModel.class);
                if (value != null) {
                    tvUserName.setText(value.getFullName());
                    tvInfo.setText(value.getDescription());
                    if (getContext() != null) {
                        Glide.with(getContext()).load(value.getProfilePic()).placeholder(R.drawable.ic_person).into(ivProfilePic);
                        Glide.with(getContext()).load(value.getProfilePic()).placeholder(R.color.bubble_gum_pink).into(ivBgProfilePic);
                    }
                    if (value.getEmail() == null) {
                        ibEmail.setVisibility(View.INVISIBLE);
                    } else {
                        ibEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onEmailPressed(value.getEmail());
                            }
                        });
                    }
                    if (value.getPhoneNum() == null) {
                        ibPhone.setVisibility(View.INVISIBLE);
                    } else {
                        ibPhone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onPhonePressed(value.getPhoneNum());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void readCourseUserRegisterFromFirebase() {
        Query query = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_USERS).child(mUserModel.getFirebaseId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel value = dataSnapshot.getValue(UserModel.class);
                if (value != null) {
                    if (getContext() != null) {
                        if (value.getMyNotification() == null) {
                            mTvDefaultInfo.setVisibility(View.VISIBLE);
                            mTvDefaultInfo.setText(getResources().getString(R.string.normal_user));
                        } else {
                            ArrayList<NotificationModel> registerCourseList = new ArrayList<>();
                            if (dataSnapshot.exists()) {
                                DataSnapshot needRef = dataSnapshot.child(FIREBASE_MY_NOTIFICATION).child(FIREBASE_MY_NOTIFICATION_LIST);
                                for (DataSnapshot snap : needRef.getChildren()) {
                                    int keyAccept = Integer.parseInt(snap.child(FIREBASE_KEY_NOTIFICATION).getValue().toString());
                                    if (keyAccept == KEY_ACCEPT) {
                                        NotificationModel notificationModel = snap.getValue(NotificationModel.class);
                                        registerCourseList.add(notificationModel);
                                        mNormalAdapter.updateNormalCourseArrayList(registerCourseList);
                                    }
                                }
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


    private void readCoursesFromFirebase() {
        Query query = database.getReference(MyConstants.FIREBASE_KEY_POSTS)
                .orderByChild(FIREBASE_KEY_FIREBASE_ID).equalTo(mUserModel.getFirebaseId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<CoursesModel> arrayList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CoursesModel coursesModel = snapshot.getValue(CoursesModel.class);
                        arrayList.add(coursesModel);
                    }
                    mAdapter.updateCourseArrayList(arrayList);
                } else {
                    mTvDefaultInfo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void onEmailPressed(String email) {
        if (mListener != null) {
            mListener.onEmailClick(email);
        }
    }

    private void onPhonePressed(String phone) {
        if (mListener != null) {
            mListener.onPhoneClick(phone);
        }
    }

    private void onEditPressed(UserModel userModel) {
        if (mListener != null) {
            mListener.onEditProfileClick(userModel);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof ProfileFragmentListener) {
            mListener = (ProfileFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProfileFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(CoursesModel item) {
        if (mListener != null) {
            mListener.onCourseProfileItemClick(item);
        }
    }

    @Override
    public void onItemClick(NotificationModel item) {
        mRef = database.getReference(MyConstants.FIREBASE_KEY_POSTS).child(item.getNotificationFromPostId());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CoursesModel valuePost = dataSnapshot.getValue(CoursesModel.class);
                if (mListener != null) {
                    mListener.onCourseProfileItemClick(valuePost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onYesButtonClick(UserModel userModel, int position, CoursesModel coursesModel) {
        if (currentAction == 1) {

            mRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(userModel.getFirebaseId());
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(FIREBASE_REPORT_USER)) {
                        UserModel value = snapshot.getValue(UserModel.class);
                        int score = 0;
                        if (value != null) {
                            score = value.getReportUser();
                        }
                        mRef.child(FIREBASE_REPORT_USER).setValue(score + 1);
                    } else {
                        mRef.child(FIREBASE_REPORT_USER).setValue(1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (currentAction == 2) {

            mRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(userModel.getFirebaseId());
            mRef.child(FIREBASE_USER_STATUS).setValue(FIREBASE_USER_BLOCK);

        }

    }


    public interface ProfileFragmentListener {
        void onEmailClick(String email);
        void onFragmentInteraction(String pageTitle);
        void onPhoneClick(String phone);
        void onEditProfileClick(UserModel userModel);
        void onListBlockUserClick();
        void onListPendingUserClick();
        void onCourseProfileItemClick(CoursesModel coursesModel);
        void onReportCourseClick();
        void onReportUserClick();

        void showBottomNavBar();

        void showBackButton(boolean show);

        void onChatListClick(String firebaseId);

    }
}
