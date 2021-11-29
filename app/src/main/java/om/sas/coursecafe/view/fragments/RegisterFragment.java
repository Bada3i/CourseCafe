package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.UserModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRegisterFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterActivity";

    private boolean isEmailValid, isPasswordValid;
    private OnRegisterFragmentInteractionListener mListener;
    private Context mContext;
    private RadioButton rbNormal;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View parentView = inflater.inflate(R.layout.fragment_register, container, false);

        final EditText etUsername = parentView.findViewById(R.id.et_username_register);
        final Button btnRegister = parentView.findViewById(R.id.btn_signup);
        final EditText etEmail = parentView.findViewById(R.id.et_email_register);
        final EditText etPass = parentView.findViewById(R.id.et_pass_register);
        final TextView tvSignIn = parentView.findViewById(R.id.tv_sign_in);
        RadioButton rbOwner = parentView.findViewById(R.id.radia1_course_owner);
        rbNormal = parentView.findViewById(R.id.radia2_normal_user);
        final ProgressBar simpleProgressBar = parentView.findViewById(R.id.progressBar);

       //On SignIn TEXTView Click
      tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInPressed();
            }
        });

        //On Register Button  Click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etEmail.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPass.getText().toString();
                //check if  fields empty
                boolean isEmail = email.isEmpty();
                boolean isUser = username.isEmpty();
                boolean isPass = password.isEmpty();

                if (isEmail || isUser || isPass) {
                if (isUser) {
                    etUsername.setError(getResources().getString(R.string.username_empty));
                }

                if (isEmail) {
                    etEmail.setError(getResources().getString(R.string.email_empty));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                    etEmail.setError(getResources().getString(R.string.error_invalid_email));
                    isEmailValid = false;
                } else {
                    isEmailValid = true;
                }

                if (isPass) {
                    etPass.setError(getResources().getString(R.string.pass_empty));
                } else if (etPass.getText().length() < 6) {
                    etPass.setError(getResources().getString(R.string.error_invalid_password));
                    isPasswordValid = false;
                } else {
                    isPasswordValid = true;
                }
                }
                else{
                UserModel user = UserModel.getInstance();
                //radio button for user type
                String normalUser = MyConstants.USER_TYPE_Normal;
                String courseOwner =MyConstants.USER_TYPE_OWNER;

                if (rbNormal.isChecked()) {
                    user.setUsertype(normalUser);
                    user.setUserStatus(getString(R.string.notPending));
                } else {
                    user.setUsertype(courseOwner);
                    user.setUserStatus(getString(R.string.pending));
                }

                user.setFullName(etUsername.getText().toString());
                user.setEmail(etEmail.getText().toString());

                    simpleProgressBar.setVisibility(View.VISIBLE);

                onRegisterButtonPressed(user , password);
            }
            }
        });


        return parentView;

    }

    private void onRegisterButtonPressed(UserModel user, String userPassword) {
        if (mListener != null) {
            mListener.onRegisterFragmentInteraction(user , userPassword);
        }
    }

    private void onSignInPressed() {
        if (mListener != null) {
            mListener.onSignInClick();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRegisterFragmentInteractionListener {
        void onRegisterFragmentInteraction(UserModel user , String password);
        void onSignInClick();
    }


}
