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
import om.sas.coursecafe.view.adapter.ReportCourseAdapter;
import om.sas.coursecafe.view.dialog.ConfirmDialogFragment;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_REPORT_POST;


public class ReportCourseFragment extends Fragment implements ReportCourseAdapter.onDelReportCourseClickListener,
        ConfirmDialogFragment.ConfirmDialogFragmentListener {

    private TextView tvNoCourseReport;
    private ReportCourseAdapter mAdapter;
    private ReportCourseFragmentInterface mListener;
    private ArrayList<CoursesModel> arrayList;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.reportedCoursesTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);
        }

        View parentView = inflater.inflate(R.layout.fragment_report_course, container, false);

        fragmentManager = getFragmentManager();

        ListView lvReportCourse = parentView.findViewById(R.id.lv_report_course);
        tvNoCourseReport = parentView.findViewById(R.id.tv_no_course_report);

        mAdapter = new ReportCourseAdapter(getContext(), this);
        readReportCourseFromFirebase();
        lvReportCourse.setAdapter(mAdapter);

        lvReportCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CoursesModel selectedCourse = (CoursesModel) parent.getAdapter().getItem(position);
                Log.d("post-info", selectedCourse.getTitle());
                if (mListener != null) {
                    mListener.onCourseItemClick(selectedCourse);
                }

            }
        });

        return parentView;
    }

    private void readReportCourseFromFirebase() {
        Query query = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_POSTS)
                .orderByChild(FIREBASE_REPORT_POST).startAt(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList = new ArrayList<>();
                arrayList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CoursesModel coursesModel = snapshot.getValue(CoursesModel.class);
                        arrayList.add(coursesModel);
                    }
                    mAdapter.updateReportCourseArrayList(arrayList);
                } else {
                    tvNoCourseReport.setVisibility(View.VISIBLE);
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
        if (context instanceof ReportCourseFragmentInterface) {
            mListener = (ReportCourseFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReportCourseFragmentInterface");
        }
    }

    @Override
    public void OnDelReportCourseClick(int position, CoursesModel coursesModel) {
        ConfirmDialogFragment confirmDialogFragmentBlock = ConfirmDialogFragment
                .newInstance(getResources().getString(R.string.d_title_del_course), getResources().getString(R.string.d_msg_del_course),
                        null, position, coursesModel);
        if (fragmentManager != null) {
            confirmDialogFragmentBlock.setTargetFragment(ReportCourseFragment.this, 300);
            confirmDialogFragmentBlock.show(fragmentManager, "del_course");
        }
    }

    @Override
    public void onYesButtonClick(UserModel userModel, int position, CoursesModel coursesModel) {
        getUpdatedCourses(coursesModel);
        arrayList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    private void getUpdatedCourses(CoursesModel coursesModel) {
        DatabaseReference deleteCourse = FirebaseDatabase.getInstance()
                .getReference(MyConstants.FIREBASE_KEY_POSTS).child(coursesModel.getPostId());
        deleteCourse.removeValue();
    }

    public interface ReportCourseFragmentInterface {
        void onFragmentInteraction(String title);

        void onCourseItemClick(CoursesModel courseItem);

        void hideBottomNavBar();

        void showBackButton(boolean show);
    }
}
