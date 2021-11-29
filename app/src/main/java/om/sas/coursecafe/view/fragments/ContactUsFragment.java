package om.sas.coursecafe.view.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.model.AppInfoModel;
import om.sas.coursecafe.view.MyConstants;


public class ContactUsFragment extends Fragment {

    private static final String TAG = "contact_us_info";
    private Menu menu;// Global Menu Declaration
    private TextView tvContactInfo,tvAdminName,tvAdminEmail;
    private EditText etContact, etAdmin, etAdminEmail;
    private OnContactUsInteractionListener mListener;
    private Context mContext;

    public static ContactUsFragment newInstance(AppInfoModel appInfoContact) {
        ContactUsFragment fragment = new ContactUsFragment();
        Bundle args = new Bundle();
        args.putSerializable(MyConstants.KEY_CONTACT_OBJECT, appInfoContact);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            AppInfoModel mAppInfoModel = (AppInfoModel) getArguments().getSerializable(MyConstants.KEY_CONTACT_OBJECT);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.contactUsTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(false);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();


        if (firebaseUser != null) {
            if (Objects.requireNonNull(firebaseUser.getEmail()).equals(MyConstants.KEY_ADMIN_EMAIL)) {
                inflater.inflate(R.menu.edit_save_menu, menu);
                this.menu = menu;
                super.onCreateOptionsMenu(menu, inflater);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();
        if (id == R.id.edit_info) {
            onEditContactPressed();
            menu.getItem(1).setVisible(true);
            menu.getItem(0).setVisible(false);
            return true;
        } else if (id == R.id.save_info) {
            AppInfoModel appInfoContact = new AppInfoModel();
            appInfoContact.setContactUsInfo(etContact.getText().toString());
            appInfoContact.setAdminName(etAdmin.getText().toString());
            appInfoContact.setAdminEmail(etAdminEmail.getText().toString());
            onSaveContactPressed(appInfoContact);
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_contact_us, container, false);

        tvContactInfo = parentView.findViewById(R.id.tv_contact_info);
        tvAdminName = parentView.findViewById(R.id.tv_admin_name);
        tvAdminEmail = parentView.findViewById(R.id.tv_admin_email);

        etContact = parentView.findViewById(R.id.et_contact);
        etAdmin = parentView.findViewById(R.id.et_admin);
        etAdminEmail = parentView.findViewById(R.id.et_admin_email);

        tvAdminEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendEmail = new Intent(Intent.ACTION_SEND);
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{etAdminEmail.getText().toString()});
                sendEmail.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.courses_apps));
                sendEmail.setType(MyConstants.MESSAGE_PATH);
                startActivity(Intent.createChooser(sendEmail, getResources().getString(R.string.choose_email_client)));
            }
        });

        readContactUsFromFirebase();

        return parentView;
    }

    private void readContactUsFromFirebase() {
        Query query = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_APP_INFO)
                .orderByChild(MyConstants.FIREBASE_KEY_CONTACT_US);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AppInfoModel value = snapshot.getValue(AppInfoModel.class);
                        if (value != null) {
                            tvContactInfo.setText(value.getContactUsInfo());
                            tvAdminName.setText(value.getAdminName());
                            tvAdminEmail.setText(value.getAdminEmail());

                            etContact.setText(value.getContactUsInfo());
                            etAdmin.setText(value.getAdminName());
                            etAdminEmail.setText(value.getAdminEmail());

                        }
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

    private void onEditContactPressed() {
        tvContactInfo.setVisibility(View.INVISIBLE);
        tvAdminName.setVisibility(View.INVISIBLE);
        tvAdminEmail.setVisibility(View.INVISIBLE);

        etContact.setVisibility(View.VISIBLE);
        etAdmin.setVisibility(View.VISIBLE);
        etAdminEmail.setVisibility(View.VISIBLE);
    }

    private void onSaveContactPressed(AppInfoModel appInfoContact) {
        etContact.setVisibility(View.INVISIBLE);
        etAdmin.setVisibility(View.INVISIBLE);
        etAdminEmail.setVisibility(View.INVISIBLE);

        tvContactInfo.setVisibility(View.VISIBLE);
        tvAdminName.setVisibility(View.VISIBLE);
        tvAdminEmail.setVisibility(View.VISIBLE);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(MyConstants.FIREBASE_KEY_APP_INFO);
        databaseReference.child(MyConstants.FIREBASE_KEY_CONTACT_US).setValue(appInfoContact);

        String contact = etContact.getText().toString();
        String admin = etAdmin.getText().toString();
        String adminEmail = etAdminEmail.getText().toString();

        tvContactInfo.setText(contact);
        tvAdminName.setText(admin);
        tvAdminEmail.setText(adminEmail);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnContactUsInteractionListener) {
            mListener = (OnContactUsInteractionListener) context;
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
    public interface OnContactUsInteractionListener {


        void onFragmentInteraction(String title);

        void hideBottomNavBar();
        void showBackButton(boolean show);
    }
}
