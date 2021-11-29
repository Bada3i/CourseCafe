package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.UtilityClass;
import om.sas.coursecafe.view.adapter.CoursesListAdapter;
import om.sas.coursecafe.view.adapter.OfferRecyclerViewAdapter;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.MyConstants;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_POSTS;
import static om.sas.coursecafe.view.MyConstants.KEY_OFFER_TYPE;


public class CourseListFragment extends Fragment implements OfferRecyclerViewAdapter.OnItemClickListener {


    private CoursesListAdapter mAdapter;
    private Context mContext;
    private CourseListFragmentListerner mListener;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private ArrayList<CoursesModel> coursesArrayList;
    private String mDate;


    //add by huda
    private OfferRecyclerViewAdapter offerAdapter;
    private RecyclerView rvOffer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.courseListTitle));
            mListener.showBottomNavBar();
            mListener.showBackButton(false);
        }


        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_course_list, container, false);

        RecyclerView recyclerView = parentView.findViewById(R.id.rv_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mAdapter = new CoursesListAdapter(mContext);
        readCoursesFromFirebase();
        recyclerView.setAdapter(mAdapter);


        //add by huda
        rvOffer = parentView.findViewById(R.id.rv_offer);
        //RecyclerView to be Scrolled horizontally with the width
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvOffer.setLayoutManager(mLayoutManager);
        rvOffer.setItemAnimator(new DefaultItemAnimator());
        offerAdapter = new OfferRecyclerViewAdapter(mContext, coursesArrayList, this);
        readOfferFromFirebase();
        rvOffer.setAdapter(offerAdapter);


        return parentView;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof CourseListFragmentListerner) {
            mListener = (CourseListFragmentListerner) context;
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


/*    public void onItemPressed(CoursesModel courseItem) {
        if (mListener != null) {
            mListener.onCourseItemClick(courseItem);
        }
    }*/

    // Read Courses from Firebase DB

    private void readCoursesFromFirebase() {


        getCurrentDate();


        Query query = FirebaseDatabase.getInstance().getReference(FIREBASE_KEY_POSTS)
                .orderByChild(MyConstants.FIREBASE_KEY_START_DATE).startAt(mDate);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coursesArrayList = new ArrayList<>();
                coursesArrayList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CoursesModel value = snapshot.getValue(CoursesModel.class);
                        coursesArrayList.add(value);
                    }
                    sendCoursesArrayList(coursesArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    //Send the Data to the Adapter
    private void sendCoursesArrayList(ArrayList<CoursesModel> coursesArrayList) {
        mAdapter.updateCourseArrayList(coursesArrayList);

    }


    // get current Date to order the courses list
    private void getCurrentDate() {

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        mDate = simpleDateFormat.format(new Date());

    }

    //add by huda read offer
    private void readOfferFromFirebase() {
        Query query = FirebaseDatabase.getInstance()
                .getReference(FIREBASE_KEY_POSTS).orderByChild(KEY_OFFER_TYPE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<CoursesModel> arrayList = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CoursesModel coursesList = snapshot.getValue(CoursesModel.class);
                                if (coursesList != null) {
                                    if (coursesList.getOfferType() != null) {
                                        arrayList.add(coursesList);
                                    }
                                }
                            }
                            offerAdapter.updateOfferArrayList(arrayList);
                        }
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onOfferItemPressed(CoursesModel course) {
        if (mListener != null) {
            mListener.onOfferClick(course);
        }
    }


    @Override
    public void onItemClick(CoursesModel item) {
        onOfferItemPressed(item);
    }

    // Interface Methods
    public interface CourseListFragmentListerner {

    //    void onCourseItemClick(CoursesModel couseItem);

        void onOfferClick(CoursesModel course);

        void onFragmentInteraction(String pageTitle);

        void showBottomNavBar();

        void showBackButton(boolean show);
    }
}
