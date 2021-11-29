package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.creativityapps.gmailbackgroundlibrary.util.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.adapter.PendingOwnerAdapter;
import om.sas.coursecafe.view.dialog.ConfirmDialogFragment;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.UserModel;

public class PendingUserFragment extends Fragment implements PendingOwnerAdapter.onButtonPendingUserClickListener,
        ConfirmDialogFragment.ConfirmDialogFragmentListener {

    private TextView tvNoUserPending;
    private PendingOwnerAdapter mAdapter;
    private Context mContext;
    private PendingUserFragmentInterface mListener;
    private FragmentManager fragmentManager;
    private ArrayList<UserModel> arrayList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mListener != null) {
            mListener.onFragmentInteraction("Pending Users");
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);
        }

        View parentView = inflater.inflate(R.layout.fragment_pending_user, container, false);

        fragmentManager = getFragmentManager();

        ListView lvPendingUser = parentView.findViewById(R.id.lv_pending_user);

        tvNoUserPending = parentView.findViewById(R.id.tv_no_user_pending);

        mAdapter = new PendingOwnerAdapter(mContext,this);
        readPendingUserFromFirebase();
        lvPendingUser.setAdapter(mAdapter);

        return parentView;
    }

    private void readPendingUserFromFirebase() {
        Query query = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_USERS)
                .orderByChild(MyConstants.FIREBASE_USER_STATUS).equalTo(MyConstants.FIREBASE_USER_PENDING);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList = new ArrayList<>();
                arrayList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        arrayList.add(userModel);
                    }
                    mAdapter.updatePendingUserArrayList(arrayList);
                } else {
                    tvNoUserPending.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PendingUserFragmentInterface) {
            mListener = (PendingUserFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PendingUserFragmentInterface");
        }
    }

    @Override
    public void OnButtonPendingUserClick(UserModel userModel, int position) {
        ConfirmDialogFragment confirmDialogFragmentBlock = ConfirmDialogFragment.newInstance
                (getResources().getString(R.string.d_title_accept_user), getResources().getString(R.string.d_msg_accept_user),
                        userModel,position,null);
        if (fragmentManager != null) {
            confirmDialogFragmentBlock.setTargetFragment(PendingUserFragment.this, 300);
            confirmDialogFragmentBlock.show(fragmentManager, "pending_users");
        }
    }

    @Override
    public void onYesButtonClick(UserModel userModel, int position, CoursesModel coursesModel) {
        getUpdatedStatus(userModel);
        arrayList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    private void getUpdatedStatus(UserModel userModel) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(userModel.getFirebaseId());
        reference.child(MyConstants.FIREBASE_USER_STATUS).setValue(MyConstants.FIREBASE_USER_NOT_PENDING);

        BackgroundMail bm = new BackgroundMail(getContext());
        bm.setGmailUserName("courses.om@gmail.com");
        bm.setGmailPassword(Utils.decryptIt("Pass@sas"));
        bm.setMailTo(userModel.getEmail());
        bm.setFormSubject("Acceptance to Access Courses Cafe App");
        bm.setFormBody("Courses Cafe /n Greeting & Congratulations! /n You have been Accepted to access our Application. /n Regards, /n Courses Cafe Team ");
        bm.send();

    }


    public interface PendingUserFragmentInterface {
        void onFragmentInteraction(String title);
        void hideBottomNavBar();
        void showBackButton(boolean show);
    }

}