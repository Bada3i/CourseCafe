package om.sas.coursecafe.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.AudienceContainer;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.UtilityClass.getCurrentDate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudienceResigterRequestDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudienceResigterRequestDialogFragment extends DialogFragment {

    private OnRegistrationRequestDialogInterfaceListener mListener;
    private static final String Key_Course_Model = "param1";
    private CoursesModel mCourseModel;



    public static AudienceResigterRequestDialogFragment newInstance(CoursesModel coursesModel) {
        AudienceResigterRequestDialogFragment fragment = new AudienceResigterRequestDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Key_Course_Model, coursesModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourseModel = (CoursesModel) getArguments().getSerializable(Key_Course_Model);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_audience_resigter_request_dialog, container, false);


        Button btnSendRequest = parentView.findViewById(R.id.btn_send_request);
        Button btnCancelRequest = parentView.findViewById(R.id.btn_cancle_request);


        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Add Audience under mCourse and view it in List View of Audience

                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser currentUser = mAuth.getCurrentUser();

                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(currentUser.getUid());
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        UserModel value = dataSnapshot.getValue(UserModel.class);

                        UserModel audience = new UserModel();
                        if (value != null) {

                            audience.setFirebaseId(value.getFirebaseId());
                            audience.setFullName(value.getFullName());
                            audience.setEmail(value.getEmail());
                            audience.setProfilePic(value.getProfilePic());
                            audience.setCourseRegisterDate(getCurrentDate());
                            audience.setKeyAudience(0);
                        }

                        if (mCourseModel.getMyAudiance() != null) {

                            // TODO: I need to check if this user already registered or not

                            if (isUserNotRegister(mCourseModel, audience)) {
                                mCourseModel.getMyAudiance().getMyAudianceList().add(audience);
                            } else {
                                if (mListener !=null) {
                                    mListener.onAlreadyRegisterAudience();
                                }
                            }
                        } else {
                            ArrayList<UserModel> myAudienceArrayList = new ArrayList<>();
                            myAudienceArrayList.add(audience);
                            AudienceContainer audContainer = new AudienceContainer();
                            audContainer.setMyAudianceList(myAudienceArrayList);
                            mCourseModel.setMyAudiance(audContainer);

                        }


                        FirebaseDatabase db2 = FirebaseDatabase.getInstance();
                        DatabaseReference myRefPost = db2.getReference(MyConstants.FIREBASE_KEY_POSTS);
                        myRefPost.child(mCourseModel.getPostId()).setValue(mCourseModel);

                        if (mListener != null) {
                            mListener.onAudienceSendRegistrationRequest(mCourseModel);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

        });


        btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });
        return parentView;
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
    private boolean isUserNotRegister(CoursesModel coursesModel, UserModel audience) {

        for (UserModel registerUsers : coursesModel.getMyAudiance().getMyAudianceList()) {
            if (registerUsers.getFirebaseId().equals(audience.getFirebaseId())) {
                return false;
            }
        }


        return true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnRegistrationRequestDialogInterfaceListener) {
            mListener = (OnRegistrationRequestDialogInterfaceListener) context;
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

    public interface OnRegistrationRequestDialogInterfaceListener {

        void onAudienceSendRegistrationRequest(CoursesModel coursesModel);
        void onAlreadyRegisterAudience();
    }


}