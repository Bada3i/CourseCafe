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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.adapter.ReportUserAdapter;
import om.sas.coursecafe.view.dialog.ConfirmDialogFragment;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_REPORT_USER;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_BLOCK;


public class ReportUserFragment extends Fragment implements ReportUserAdapter.onBlockReportUserClickListener,
        ConfirmDialogFragment.ConfirmDialogFragmentListener {

    private TextView tvNoUserReport;
    private ReportUserAdapter mAdapter;
    private ReportUserFragmentInterface mListener;
    private ArrayList<UserModel> arrayList;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.reportedUsersTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);
        }

        View parentView = inflater.inflate(R.layout.fragment_report_user, container, false);

        fragmentManager = getFragmentManager();

        ListView lvReportUser = parentView.findViewById(R.id.lv_report_user);

        tvNoUserReport = parentView.findViewById(R.id.tv_no_user_report);

        mAdapter = new ReportUserAdapter(getContext(), this);
        readReportUserFromFirebase();
        lvReportUser.setAdapter(mAdapter);


        lvReportUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserModel selectedUser = (UserModel) parent.getAdapter().getItem(position);
                Log.d("user-info", selectedUser.getFullName());
                if (mListener != null) {
                    mListener.onUserItemClick(selectedUser);
                }

            }
        });

        return parentView;
    }

    private void readReportUserFromFirebase() {
        Query query = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_USERS)
                .orderByChild(FIREBASE_REPORT_USER).startAt(1);
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
                    mAdapter.updateReportUserArrayList(arrayList);
                } else {
                    tvNoUserReport.setVisibility(View.VISIBLE);
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
        if (context instanceof ReportUserFragmentInterface) {
            mListener = (ReportUserFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReportUserFragmentInterface");
        }
    }

    @Override
    public void OnBlockReportUserClick(UserModel userModel, int position) {
        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment
                .newInstance(getResources().getString(R.string.d_title_block), getResources().getString(R.string.d_msg_block),
                        userModel,position,null);
        if (fragmentManager != null) {
            confirmDialogFragment.setTargetFragment(ReportUserFragment.this, 300);
            confirmDialogFragment.show(fragmentManager, "block_users");
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
        reference.child(MyConstants.FIREBASE_USER_STATUS).setValue(FIREBASE_USER_BLOCK);
        reference.child(FIREBASE_REPORT_USER).setValue(0);
    }

    public interface ReportUserFragmentInterface {
        void onFragmentInteraction(String title);
        void onUserItemClick(UserModel userModel);
        void hideBottomNavBar();
        void showBackButton(boolean show);
    }
}
