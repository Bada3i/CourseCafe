package om.sas.coursecafe.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.activity.MainActivity;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_USERS;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_NOT_PENDING;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_PENDING;


public class UserTypeDialogFragment extends DialogFragment {

    private UserModel mUser;

    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }

    }

    public static UserTypeDialogFragment newInstance(UserModel user) {
        UserTypeDialogFragment userTypeDialogFragment = new UserTypeDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(MyConstants.KEY_USER, user);
        userTypeDialogFragment.setArguments(args);
        return userTypeDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (UserModel) getArguments().getSerializable(MyConstants.KEY_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_user_type_dialog, container, false);

        RadioButton rbOwner = parentView.findViewById(R.id.radia1_course_owner);
        final RadioButton rbNormal = parentView.findViewById(R.id.radia2_normal_user);

        Button btnAddUserTypeGoogle = parentView.findViewById(R.id.btn_add_user_type_google);
        btnAddUserTypeGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserModel user = mUser;
                //radio button for user type
                String normalUser = MyConstants.USER_TYPE_Normal;
                String courseOwner = MyConstants.USER_TYPE_OWNER;

                if (rbNormal.isChecked()) {
                    user.setUsertype(normalUser);
                    user.setUserStatus(FIREBASE_USER_NOT_PENDING);
                } else {
                    user.setUsertype(courseOwner);
                    user.setUserStatus(FIREBASE_USER_PENDING);
                }

                onAddFromGoogleClick(user);
            }
        });


        return parentView;
    }


    private void onAddFromGoogleClick(final UserModel userModel) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = database.getReference(FIREBASE_KEY_USERS);
        mRef.child(userModel.getFirebaseId()).setValue(userModel);
        Toast.makeText(getContext(), "Signed In Successfully", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}