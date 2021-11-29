package om.sas.coursecafe.view.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.model.CoursesModel;

public class OfferDialogFragment extends DialogFragment {

    private static final String KEY_COURSES = "offer";
    private CoursesModel mCourses;
    private TextView tvOffer,tvOfferType,tvFinalPrice;


    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }

    }


    public static OfferDialogFragment newInstance(CoursesModel courses) {
        OfferDialogFragment offerDialogFragment = new OfferDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_COURSES, courses);
        offerDialogFragment.setArguments(args);
        return offerDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourses = (CoursesModel) getArguments().getSerializable(KEY_COURSES);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View parentView = inflater.inflate(R.layout.fragment_offer_dialog, container, false);

        tvOfferType = parentView.findViewById(R.id.tv_offer_title);
        tvOffer = parentView.findViewById(R.id.tv_offer);
        tvFinalPrice = parentView.findViewById(R.id.tv_final_price);

        Button btnOkay = parentView.findViewById(R.id.btn_okay);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        readOfferFromFirebase();

        return  parentView;
    }

    private void readOfferFromFirebase() {

        tvOfferType.setText(mCourses.getOfferType());
        tvOffer.setText(mCourses.getOfferDetails());
        tvFinalPrice.setText(mCourses.getFinalPayment());
    }

}
