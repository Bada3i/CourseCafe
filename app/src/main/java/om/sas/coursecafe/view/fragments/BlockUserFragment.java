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
import om.sas.coursecafe.view.adapter.BlockUserAdapter;
import om.sas.coursecafe.view.dialog.ConfirmDialogFragment;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_NOT_PENDING;

public class BlockUserFragment extends Fragment implements BlockUserAdapter.onUnBlockClickListener, ConfirmDialogFragment.ConfirmDialogFragmentListener{

    private TextView tvNoUserBlocked;
    private BlockUserAdapter mAdapter;
    private Context mContext;
    private BlockUserFragmentInterface mListener;
    private ArrayList<UserModel> arrayList;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.blockUsersTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);
        }

        View parentView = inflater.inflate(R.layout.fragment_block_user, container, false);

        fragmentManager = getFragmentManager();

        ListView lvBlockUser = parentView.findViewById(R.id.lv_block_user);

        tvNoUserBlocked = parentView.findViewById(R.id.tv_no_user_blocked);

        mAdapter = new BlockUserAdapter(mContext,this);
        readBlockedUserFromFirebase();
        lvBlockUser.setAdapter(mAdapter);

        lvBlockUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    private void readBlockedUserFromFirebase() {
        Query query = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_USERS)
                .orderByChild(MyConstants.FIREBASE_USER_STATUS).equalTo(MyConstants.FIREBASE_USER_BLOCK);
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
                    mAdapter.updateBlockUserArrayList(arrayList);
                } else {
                    tvNoUserBlocked.setVisibility(View.VISIBLE);
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
        if (context instanceof BlockUserFragmentInterface) {
            mListener = (BlockUserFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BlockUserFragmentInterface");
        }
    }

    @Override
    public void OnUnBlockClick(UserModel userModel, int position) {
        ConfirmDialogFragment confirmDialogFragmentBlock = ConfirmDialogFragment.newInstance
                (getResources().getString(R.string.d_title_unblock), getResources().getString(R.string.d_msg_unblock),
                        userModel,position,null);
        if (fragmentManager != null) {
            confirmDialogFragmentBlock.setTargetFragment(BlockUserFragment.this, 300);
            confirmDialogFragmentBlock.show(fragmentManager, "unblock_users");
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
        reference.child(MyConstants.FIREBASE_USER_STATUS).setValue(FIREBASE_USER_NOT_PENDING);
    }

    public interface BlockUserFragmentInterface {
        void onFragmentInteraction(String title);
        void onUserItemClick(UserModel userModel);
        void hideBottomNavBar();
        void showBackButton(boolean show);
    }

}
