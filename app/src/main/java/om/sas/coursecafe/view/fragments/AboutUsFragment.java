package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.os.Build;
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
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.AppInfoModel;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_ABOUT_US;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_APP_INFO;


public class AboutUsFragment extends Fragment {

    private static final String TAG = "about_us_info";
    private FirebaseAuth mAuth;
    private Menu menu;// Global Menu Declaration
    private EditText etAbout;
    private TextView tvAbout;
    private OnAboutUsInteractionListener mListener;
    private Context mContext;

    public static AboutUsFragment newInstance(AppInfoModel appInfoAbout) {
        AboutUsFragment fragment = new AboutUsFragment();
        Bundle args = new Bundle();
        args.putSerializable(MyConstants.KEY_ABOUT_OBJECT, appInfoAbout);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            AppInfoModel mAppInfoModel = (AppInfoModel) getArguments().getSerializable(MyConstants.KEY_ABOUT_OBJECT);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
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
            onEditAboutPressed();

            menu.getItem(1).setVisible(true);
            menu.getItem(0).setVisible(false);
            return true;
        } else if (id == R.id.save_info) {
            AppInfoModel appInfoAbout = new AppInfoModel();
            appInfoAbout.setAboutUsInfo(etAbout.getText().toString());
            onSaveAboutPressed(appInfoAbout);

            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.aboutUsTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(false);
        }

        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_about_us, container, false);

        mAuth = FirebaseAuth.getInstance();

        tvAbout = parentView.findViewById(R.id.tv_about_info);
        etAbout = parentView.findViewById(R.id.et_about);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvAbout.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        readAboutUsFromFirebase();

        return parentView;
    }


    private void readAboutUsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference(FIREBASE_KEY_APP_INFO).child(FIREBASE_KEY_ABOUT_US);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AppInfoModel value = dataSnapshot.getValue(AppInfoModel.class);
                if (value != null) {
                    tvAbout.setText(value.getAboutUsInfo());
                    etAbout.setText(value.getAboutUsInfo());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.w(TAG, "Failed to read value.", databaseError.toException());

            }
        });
    }


    private void onEditAboutPressed() {
        tvAbout.setVisibility(View.INVISIBLE);
        etAbout.setVisibility(View.VISIBLE);
    }

    private void onSaveAboutPressed(AppInfoModel appInfoAbout) {
        etAbout.setVisibility(View.INVISIBLE);
        tvAbout.setVisibility(View.VISIBLE);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(FIREBASE_KEY_APP_INFO);
        databaseReference.child(FIREBASE_KEY_ABOUT_US).setValue(appInfoAbout);

        String about = etAbout.getText().toString();
        tvAbout.setText(about);


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnAboutUsInteractionListener) {
            mListener = (OnAboutUsInteractionListener) context;
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
    public interface OnAboutUsInteractionListener {


        void onFragmentInteraction(String title);

        void hideBottomNavBar();
        void showBackButton(boolean show);
    }
}
