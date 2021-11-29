package om.sas.coursecafe.view.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Objects;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.dialog.ConfirmDialogFragment;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_LOCATION;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_ONLINE;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_REPORT_POST;
import static om.sas.coursecafe.view.MyConstants.KEY_ADMIN_EMAIL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailsInterface} interface
 * to handle interaction events.
 */
public class DetailsFragment extends Fragment implements ConfirmDialogFragment.ConfirmDialogFragmentListener {
    private static final String KEY_POST = "param1";
    private CoursesModel mCourseModel;
    private UserModel mUserModel;
    private Context mContext;
    private static final String TAG = "details";
    private DetailsInterface mListener;
    private ImageView ivImg;
    private TextView tvTitle;
    private TextView tvProvider,tvViewTime,tvViewDate,tvPrice;
    private Button btnDisplayPalace;
    private DatabaseReference mRef;

    private Menu menu;
    private FirebaseUser firebaseUser;
    private FragmentManager fragmentManager;
    private int currentAction = 0;


    public static Fragment newInstance(CoursesModel coursesModel) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_POST, coursesModel);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mCourseModel = (CoursesModel) getArguments().getSerializable(KEY_POST);



        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            if (mCourseModel.getPostProviderId().equals(firebaseUser.getUid())) {
                this.menu = menu;
                inflater.inflate(R.menu.details_menu, menu);

                //hide item from menu when owner have audience
               if(mCourseModel.getMyAudiance() != null){
                    MenuItem editPost = menu.findItem(R.id.edit_post);
                    editPost.setVisible(false);
                    MenuItem DeletePost = menu.findItem(R.id.Delete_post);
                    DeletePost.setVisible(false);
                }

            } else if (Objects.requireNonNull(firebaseUser.getEmail()).equals(KEY_ADMIN_EMAIL)) {
                inflater.inflate(R.menu.details_menu, menu);
            } else {
                inflater.inflate(R.menu.other_user_details_menu, menu);
            }
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.edit_post:
                onEditPressed(mCourseModel);
                return true;

            case R.id.Delete_post:
                currentAction = 1;
                ConfirmDialogFragment confirmDialogFragmentDelete = ConfirmDialogFragment.newInstance
                        (getResources().getString(R.string.d_title_del_course),getResources().getString(R.string.d_msg_del_course),
                                null,0,mCourseModel);
                if (fragmentManager != null) {
                    confirmDialogFragmentDelete.setTargetFragment(DetailsFragment.this, 300);
                    confirmDialogFragmentDelete.show(fragmentManager, "delete_course");
                }

                return true;

            case R.id.show_adiance:
                onSeeAudianceButtonPressed(mCourseModel);
                return true;
        }

        if(item.getItemId() == R.id.report_post){
            currentAction = 2;
            ConfirmDialogFragment confirmDialogFragmentReport = ConfirmDialogFragment.newInstance
                    (getResources().getString(R.string.d_title_rep_course), getResources().getString(R.string.d_msg_rep_course),
                            null,0,mCourseModel);
            if (fragmentManager != null) {
                confirmDialogFragmentReport.setTargetFragment(DetailsFragment.this, 300);
                confirmDialogFragmentReport.show(fragmentManager, "report_course");
            }

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.courseDetailTitle));
            mListener.showBottomNavBar();
            mListener.showBackButton(true);
        }

        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_course_details, container, false);

        fragmentManager = getFragmentManager();

        ivImg = itemView.findViewById(R.id.iv_img);
        tvTitle = itemView.findViewById(R.id.tv_title);
        tvProvider = itemView.findViewById(R.id.tv_institution_name);
        tvViewTime = itemView.findViewById(R.id.tv_view_time);
        tvViewDate = itemView.findViewById(R.id.tv_view_date);
        tvPrice = itemView.findViewById(R.id.tv_price);

        Button btnEmail = itemView.findViewById(R.id.btn_view_email);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmailPressed(mCourseModel.getEmail());
            }
        });

        Button btnPhone = itemView.findViewById(R.id.btn_view_phone);
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhonePressed(mCourseModel.getPhone());
            }
        });

        btnDisplayPalace = itemView.findViewById(R.id.btn_view_location);
        if(mCourseModel.getLocationType().equals(FIREBASE_KEY_ONLINE)){
            btnDisplayPalace.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_airplay, 0, 0);
            btnDisplayPalace.setCompoundDrawablePadding(8);
        }

        btnDisplayPalace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCourseModel.getLocationType().equals(FIREBASE_KEY_LOCATION)){
                    onCheckLocationPressed(mCourseModel);
                }else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Online Course In : " + mCourseModel.getLocationAddress())
                            .setMessage("For more Information the Owner will contact you after Successful Registration!!")
                            .setPositiveButton(android.R.string.ok, null).create().show();
                }
            }
        });

        Button btnOffer = itemView.findViewById(R.id.btn_view_offer);
        btnOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCourseModel.getOfferType() != null){
                    if (mListener != null) {
                        mListener.onOfferItemClick(mCourseModel);
                    }
                }else{
                    Toast.makeText(getContext(), getResources().getString(R.string.no_offer), Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnRegisterInCourse = itemView.findViewById(R.id.btn_join);
        btnRegisterInCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onRegisterAudienceDialogOpen(mCourseModel);
            }
        });

        readPostFromFirebase();


        return itemView;

    }

    @SuppressLint("SetTextI18n")
    private void readPostFromFirebase() {

        tvTitle.setText(mCourseModel.getTitle());
        tvProvider.setText("by " + mCourseModel.getInstitution());
        tvViewTime.setText(mCourseModel.getStartTime() + " - " + mCourseModel.getEndTime());
        tvViewDate.setText(mCourseModel.getStartDate() + " TO " + mCourseModel.getEndDate());
        tvPrice.setText(mCourseModel.getPaymentAmount());
        if(mCourseModel.getLocationType().equals(FIREBASE_KEY_LOCATION)){
            btnDisplayPalace.setText(mCourseModel.getLocationAddress());
        }else{
            btnDisplayPalace.setText(FIREBASE_KEY_ONLINE);
        }

        Glide.with(mContext).load(mCourseModel.getImage()).placeholder(R.mipmap.ic_launcher).into(ivImg);


    }

    private void deletePost(String postId) {
        DatabaseReference Post = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_POSTS).child(postId);
        Post.removeValue();
    }

    private void onEditPressed(CoursesModel coursesModel) {
        if (mListener != null) {
            mListener.onEditClick(coursesModel);
        }
    }


    private void onSeeAudianceButtonPressed(CoursesModel coursesModel) {
        if (mListener != null) {
            mListener.onSeeAudienceList(coursesModel);
        }
    }

    private void onCheckLocationPressed(CoursesModel coursesModel) {
        if (mListener != null) {
            mListener.onCheckLocation(coursesModel);
        }
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


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

        if (context instanceof DetailsInterface) {
            mListener = (DetailsInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DetailsInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onYesButtonClick(UserModel userModel, int position, CoursesModel coursesModel) {
        if(currentAction == 1){
            //block course
            deletePost(coursesModel.getPostId());
            mListener.onDeleteClick();

        } else if(currentAction == 2){
            //report course
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            mRef = database.getReference(MyConstants.FIREBASE_KEY_POSTS).child(coursesModel.getPostId());
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(FIREBASE_REPORT_POST)){
                        CoursesModel value = snapshot.getValue(CoursesModel.class);
                        int score = 0;
                        if (value != null) {
                            score = value.getReport();
                        }
                        mRef.child(FIREBASE_REPORT_POST).setValue(score+1);
                    } else {
                        mRef.child(FIREBASE_REPORT_POST).setValue(1);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    public interface DetailsInterface {

        void onSeeAudienceList(CoursesModel coursesModel);

        void onRegisterAudienceDialogOpen(CoursesModel coursesModel);

        void onCheckLocation(CoursesModel coursesModel);

        void onDeleteClick();

        void onEditClick(CoursesModel coursesModel);

        void onFragmentInteraction(String title);

        void onEmailClick(String email);

        void onPhoneClick(String phone);

        void showBottomNavBar();
        void showBackButton(boolean show);

        void onOfferItemClick(CoursesModel course);

    }
}
