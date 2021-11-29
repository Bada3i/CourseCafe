package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.adapter.AudienceAdapter;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.UserModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudienceListFragment#newInstance} factory method to
 * create an instance of this fragment.*/


public class AudienceListFragment extends Fragment implements ListView.OnItemClickListener{

    private static final String KEY_Course = "param1";
    private TextView tvDefaultAudienceList;

    private Context mContext;
    private AudienceAdapter mAdapter;
    private CoursesModel mCourse;
    private OnAudienceFragmentListener mListener;




    public static AudienceListFragment newInstance(CoursesModel coursesModel) {
        AudienceListFragment fragment = new AudienceListFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_Course, coursesModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourse = (CoursesModel) getArguments().getSerializable(KEY_Course);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.audianceListTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);
        }
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_audience_list, container, false);
        ListView lvAudeinceListContainer = itemView.findViewById(R.id.lv_audiences);

        tvDefaultAudienceList = itemView.findViewById(R.id.tv_default_audience);

        mAdapter = new AudienceAdapter(mContext);
        //Toast.makeText(mContext, "the course post id is" + mCourse.getPostId(), Toast.LENGTH_SHORT).show();
        readAudienceFromFirebase();
        lvAudeinceListContainer.setAdapter(mAdapter);

        lvAudeinceListContainer.setOnItemClickListener(this);

        return itemView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnAudienceFragmentListener) {
            mListener = (OnAudienceFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddFeedbackListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void readAudienceFromFirebase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_POSTS)
                .child(mCourse.getPostId());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CoursesModel value = dataSnapshot.getValue(CoursesModel.class);

                assert value != null;
                if (value.getMyAudiance() != null) {
                    ArrayList<UserModel> temp = value.getMyAudiance().getMyAudianceList();
                    Collections.reverse(temp);
                    mAdapter.updateAudienceArrayList(temp);

                }

                else {
                    tvDefaultAudienceList.setVisibility(View.VISIBLE);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     *
     * @param adapterView
     * @param view
     * @param i
     * @param l
     * Position Manipulation is manage to get exact raw after getting updated the status of the Audience from Confirmation  Dialog
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        UserModel objUser =(UserModel) adapterView.getAdapter().getItem(i);
        int size =(int) adapterView.getAdapter().getCount();
        int position= (size-1)-i;
        onItemPressed(objUser, mCourse, position);

    }


    private void onItemPressed(UserModel objUser, CoursesModel objCourse, int i) {
        if (mListener != null) {
            mListener.onAudienceRegistrationClick(objUser, objCourse, i);
        }
    }


    public interface OnAudienceFragmentListener {

        void onAudienceRegistrationClick(UserModel objUser, CoursesModel objCourse, int i);

        void onFragmentInteraction(String pageTitle);
        void hideBottomNavBar();
        void showBackButton(boolean show);
    }
}
