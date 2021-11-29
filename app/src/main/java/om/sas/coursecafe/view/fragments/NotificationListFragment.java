package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.adapter.NotificationListAdapter;
import om.sas.coursecafe.view.model.NotificationModel;
import om.sas.coursecafe.view.model.UserModel;


public class NotificationListFragment extends Fragment implements AdapterView.OnItemClickListener {



    private NotificationListAdapter mAdapter;
    private Context mContext;
    private NotificationListFragmentListerner mListener;
    TextView mTvDefaultNotification;

    public NotificationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.notificationListTitle));
            mListener.showBottomNavBar();
            mListener.showBackButton(false);
        }
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_notification_list, container, false);
        ListView lvNotificationListContainer = itemView.findViewById(R.id.lv_notifications);

        mTvDefaultNotification = itemView.findViewById(R.id.tv_default_notification);
        mAdapter = new NotificationListAdapter(mContext);

        readNotificationFromFirebase();
        lvNotificationListContainer.setAdapter(mAdapter);

        lvNotificationListContainer.setOnItemClickListener(this);

        return itemView;
    }


    //Read Notifications From FireBase
    private void readNotificationFromFirebase() {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(currentUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserModel value = dataSnapshot.getValue(UserModel.class);
               // Log.d("user-notify", value.getMyNotification().getMyNotificationList().get(0).getNotification());

                if (value.getMyNotification() != null) {

                    ArrayList<NotificationModel> notificationList = value.getMyNotification().getMyNotificationList();
                    Collections.reverse(notificationList);
                    mAdapter.updateNotificationArrayList(notificationList);

                } else {

                   mTvDefaultNotification.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof NotificationListFragmentListerner) {
            mListener = (NotificationListFragmentListerner) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CourseListFragmentListerner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface NotificationListFragmentListerner {

        void onFragmentInteraction(String pageTitle);
        void showBottomNavBar();
        void showBackButton(boolean show);
    }

}
