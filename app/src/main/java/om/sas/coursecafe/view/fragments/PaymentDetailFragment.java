package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.model.PaymentModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentDetailFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String KEY_PAYMENT_DETAIL = "param1";


    private PaymentModel mPaymentDetail;
    private PaymentDetailFragmentInterfaceListener mListener;

    public PaymentDetailFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PaymentDetailFragment newInstance(PaymentModel paymentModel) {
        PaymentDetailFragment fragment = new PaymentDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_PAYMENT_DETAIL, paymentModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPaymentDetail = (PaymentModel) getArguments().getSerializable(KEY_PAYMENT_DETAIL);

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.paymentDetailTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(false);
        }

        // Inflate the layout for this fragment
        View parentView= inflater.inflate(R.layout.fragment_payment_detail, container, false);


        TextView tvPaymentDetails= parentView.findViewById(R.id.tv_payment_detail);
        tvPaymentDetails.setText("Course Title: "+mPaymentDetail.getPaymentCourseTitle()+"\n"+" Reference ID: "+mPaymentDetail.getPaymentId());

        String paymentText = String.valueOf(tvPaymentDetails.getText());

        Button btnBackHome=parentView.findViewById(R.id.btn_back_home);

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.backToHome();
            }
        });

        ImageView btnShare=parentView.findViewById(R.id.img_share_payment);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.sharePaymentDetail(paymentText);
            }
        });

        return parentView;
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PaymentDetailFragmentInterfaceListener) {
            mListener = (PaymentDetailFragmentInterfaceListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PaymentDetailFragmentInterfaceListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface PaymentDetailFragmentInterfaceListener {


        void onFragmentInteraction(String pageTitle);
        void hideBottomNavBar();
        void showBackButton(boolean show);
        void backToHome();
        void sharePaymentDetail(String paymentText);
    }
}
