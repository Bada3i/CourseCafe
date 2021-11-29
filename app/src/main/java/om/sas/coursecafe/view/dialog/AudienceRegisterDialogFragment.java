package om.sas.coursecafe.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.UserModel;

public class AudienceRegisterDialogFragment extends DialogFragment {

    private static final String KEY_AUDIANCE = "param1";
    private static final String KEY_Course = "param2";
    private static final String KEY_AUDIANCE_LIST = "param3";

    private UserModel mAudiance;
    private CoursesModel mCourse;
    private int mAudianceKeyPosition;

    private AudienceRegisterConfirmationInterface mListener;

    private int mCount;

    public static AudienceRegisterDialogFragment newInstance(UserModel objUser, CoursesModel objCourse, int position) {
        AudienceRegisterDialogFragment fragment = new AudienceRegisterDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_AUDIANCE, objUser);
        args.putSerializable(KEY_Course, objCourse);
        args.putSerializable(KEY_AUDIANCE_LIST, position);
        fragment.setArguments(args);
        return fragment;
    }
    public AudienceRegisterDialogFragment() {
        // Required empty public constructor

    }

    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }

    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAudiance = (UserModel) getArguments().getSerializable(KEY_AUDIANCE);
            mCourse = (CoursesModel) getArguments().getSerializable(KEY_Course);
            mAudianceKeyPosition = (int) getArguments().getSerializable(KEY_AUDIANCE_LIST);

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView= inflater.inflate(R.layout.fragment_audience_rgister_dialog, container, false);

        ImageView imgAudPic =parentView.findViewById(R.id.iv_audience_image_dialog);
        TextView tvAudName =parentView.findViewById(R.id.tv_audience_name_dialog);
        TextView tvAudEmail =parentView.findViewById(R.id.tv_audience_email_dialog);
        tvAudName.setText(mAudiance.getFullName());
        tvAudEmail.setText(mAudiance.getEmail());

        Glide.with(getContext()).load(mAudiance.getProfilePic()).placeholder(R.mipmap.ic_launcher).into(imgAudPic);

        Button btnAccept =parentView.findViewById(R.id.btn_accept);
        Button btnReject =parentView.findViewById(R.id.btn_reject);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCount= MyConstants.KEY_ACCEPT;
                mAudiance.setKeyAudience(MyConstants.KEY_ACCEPT);
                getRegistrationConfirmation();
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCount= MyConstants.KEY_REGECT;
                mAudiance.setKeyAudience(MyConstants.KEY_REGECT);
                getRegistrationConfirmation();
            }
        });
        return parentView;
    }



    public void getRegistrationConfirmation(){

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(currentUser.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserModel mCurrentUser = dataSnapshot.getValue(UserModel.class);

                if (mCount==MyConstants.KEY_ACCEPT) {
                    //getUpdatedAudienceStatus();

                    onConfirmationAudienceClick(mCurrentUser, mAudiance, mCourse, getString(R.string.audience_accepted), mAudianceKeyPosition);


                }else if (mCount==MyConstants.KEY_REGECT){
                    //getUpdatedAudienceStatus();
                    onConfirmationAudienceClick(mCurrentUser,mAudiance,mCourse,getString(R.string.audience_rejected), mAudianceKeyPosition);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }



//    private void getUpdatedAudienceStatus() {
//        FirebaseDatabase db2 = FirebaseDatabase.getInstance();
//        DatabaseReference myRefPost = db2.getReference(MyConstants.FIREBASE_KEY_POSTS).child(mCourse.getPostId()).child("myAudiance").child("myAudianceList");
//        myRefPost.child(mAudianceKeyPosition+"").setValue(mAudiance);
//
//        //dismiss();
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AudienceRegisterConfirmationInterface) {
            mListener = (AudienceRegisterConfirmationInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }


    private void onConfirmationAudienceClick(UserModel sender, UserModel receiver, CoursesModel coursesModel, String message, int position) {
        if (mListener != null) {
            mListener.onNotificationSubmitted(sender,receiver,coursesModel,message, position);
        }
    }

    public interface AudienceRegisterConfirmationInterface {
        // TODO: Update argument type and name
        void onNotificationSubmitted(UserModel sender, UserModel receiver, CoursesModel coursesModel, String message, int position);
    }

}
