package om.sas.coursecafe.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;


import java.util.Objects;

import om.sas.coursecafe.R;

import om.sas.coursecafe.view.dialog.ForgotPasswordDialogFragment;
import om.sas.coursecafe.view.model.UserModel;

public class LoginFragment extends Fragment {

    private LoginListener mListener;
    private ForgotPasswordDialogFragment mDialog;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View parentView = inflater.inflate(R.layout.fragment_login, container, false);

        progressDialog = new ProgressDialog(getContext());

        final EditText etEmail = parentView.findViewById(R.id.et_email);
        final EditText etPassword = parentView.findViewById(R.id.et_password);
        final TextView tvSignUp = parentView.findViewById(R.id.tv_sign_up_login);
        TextView tvForgotPassword = parentView.findViewById(R.id.tv_forget_Pass);
        Button btnLogin = parentView.findViewById(R.id.btn_sign_in);
        Button btnGuest = parentView.findViewById(R.id.btn_guest);
        SignInButton btnGoogleLogin = parentView.findViewById(R.id.btn_google);

        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginGooglePressed();
            }
        });


        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGuestPressed();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                //check validation
                boolean isEmail = email.isEmpty();
                boolean isPass = password.isEmpty();

                if (isEmail || isPass) {
                    if (isEmail) {
                        etEmail.setError(getResources().getString(R.string.email_empty));
                    }
                    if (isPass) {
                        etPassword.setError(getResources().getString(R.string.pass_empty));
                    }
                } else {

                    progressDialog.setMessage("Please Wait Loading...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    new Thread()
                    {
                        public void run()
                        {
                            onLoginPressed(email,password);
                            progressDialog.dismiss();
                        }

                    }.start();
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterPressed();
            }
        });


        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new ForgotPasswordDialogFragment();
                mDialog.show(getChildFragmentManager(), ForgotPasswordDialogFragment.class.getSimpleName());

            }
        });

        return parentView;
    }

    private void onLoginPressed(String username,String password) {
        if (mListener != null) {
            mListener.onLoginClick(username,password);
        }
    }


    private void onRegisterPressed() {
        if (mListener != null) {
            mListener.onRegisterClick();
        }
    }

    private void onGuestPressed() {
        if (mListener != null) {
            mListener.onGuestClick();
        }
    }

    private void onLoginGooglePressed() {
        if (mListener != null) {
            mListener.onLoginGoogleClick();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginListener) {
            mListener = (LoginListener) context;
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
    public interface LoginListener {
        void onLoginClick(String username,String password);

        void onRegisterClick();

        void onGuestClick();

        void onLoginGoogleClick();

    }
}
