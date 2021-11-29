package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.model.CoursesModel;

public class OfferFragment extends Fragment {

    private static final String KEY_COURSES = "offer";
    private CoursesModel mCourses;
    private TextView tvOffer,tvOfferType;

    private OfferInterface mListener;


    public static OfferFragment newInstance(CoursesModel courses) {
        OfferFragment fragment = new OfferFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_COURSES, courses);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourses = (CoursesModel) getArguments().getSerializable(KEY_COURSES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.offerTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);
        }
        View parentView = inflater.inflate(R.layout.fragment_offer, container, false);

        tvOfferType = parentView.findViewById(R.id.tv_offer_title);
        tvOffer = parentView.findViewById(R.id.tv_offer);

        Button btnRegister = parentView.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegOfferPressed(mCourses);
            }
        });

        readOfferFromFirebase();

        return  parentView;
    }

    private void readOfferFromFirebase() {

        tvOfferType.setText(mCourses.getOfferType());
        tvOffer.setText(mCourses.getOfferDetails());
    }

    private void onRegOfferPressed(CoursesModel courses) {
        if (mListener != null) {
            mListener.onRegOfferClick(courses);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OfferFragment.OfferInterface) {
            mListener = (OfferFragment.OfferInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OfferInterface");
        }
    }

    public interface OfferInterface {
        void onRegOfferClick(CoursesModel courses);
        void onFragmentInteraction(String title);
        void hideBottomNavBar();
        void showBackButton(boolean show);
    }
}
