package om.sas.coursecafe.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.PaymentModel;
import om.sas.coursecafe.view.model.UserModel;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PayPalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PayPalFragment extends Fragment {

    private PaymentFragmentListerner mListener;
    //Paypal  request code
    private static final int PAYPAL_REQUEST_CODE = 123;

    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration() // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(MyConstants.PAYPAL_CLIENT_ID)
            .merchantName(String.valueOf(R.string.app_name))
            .merchantPrivacyPolicyUri(
                    Uri.parse(String.valueOf(R.string.PaypalPrivacyPolicyUri)))
            .merchantUserAgreementUri(
                    Uri.parse(String.valueOf(R.string.PaypalUserAgreementUri)))
            ;  // or live (ENVIRONMENT_PRODUCTION)


    private String paymentAmount;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String KEY_CourseId = "param1";



    private CoursesModel mCourseModel;
    private String mCourseId;
    private UserModel mAudience;
    private FirebaseUser mCurrentUser;
    private int mPositionAudience;
    private FirebaseDatabase firebaseDatabase;
    private PaymentModel mPayment;
    private TextView mTvPaymentAmount;
    TextView courseRegistrationFeeText;

    public PayPalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param courseId Parameter 1.
     * @return A new instance of fragment PayPalFragment.
     */
    // TODO: It should recive course Model and Payment Amount
    public static PayPalFragment newInstance(String courseId) {
        PayPalFragment fragment = new PayPalFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_CourseId, courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourseId =  getArguments().getString(KEY_CourseId);
            mPayment=new PaymentModel();

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.paymentProcessTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);
        }
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_payment_paypal, container, false);


        Button btnPayNow = itemView.findViewById(R.id.btn_make_payment_paypal);

        mTvPaymentAmount = itemView.findViewById(R.id.tv_payment_amount_paypal);

        courseRegistrationFeeText = itemView.findViewById(R.id.tv_text_reg_fee);
        getCourseModelFromFirebase();





        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPayment();
            }
        });

        return itemView;
    }

    private void getCourseModelFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_POSTS).child(mCourseId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCourseModel = dataSnapshot.getValue(CoursesModel.class);
                //getCurrencyChanged();
                getAmountText();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



    }

    private void getAmountText() {
        String courseFee = mCourseModel.getPaymentAmount();
        String afterOfferFee = mCourseModel.getFinalPayment();

        if (afterOfferFee != null) {
            courseRegistrationFeeText.setText("Course Registration Fee (After Discount!)");
            mTvPaymentAmount.setText(afterOfferFee);
        } else {
            mTvPaymentAmount.setText(courseFee);
        }
    }



    private void getPayment() {

       String p = mTvPaymentAmount.getText().toString();

        double payments = Double.parseDouble(p);
        double result = payments / .385;
        paymentAmount = String.valueOf(result);

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new
                BigDecimal(paymentAmount),"USD","Registration Total Fee",PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        // check how to do invoice number to be dynamic and keep changing
        //invoice number
        //payment.invoiceNumber("123");

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PAYPAL_REQUEST_CODE) {
            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                Log.d("CONFIRM", String.valueOf(confirm));
                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.d("paymentExample",paymentDetails);
                        Log.i("paymentExample", paymentDetails);
                        Log.d("Pay Confirm : ", String.valueOf(confirm.getPayment().toJSONObject()));
//                        Starting a new activity for the payment details and status to show

                        // check this you need to move from fragment to other
//                        startActivity(new Intent(mContext, SuccessActivity.class)
//                                .putExtra("PaymentDetails", paymentDetails);

                        //you can save this details in Firebase, for future reference
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mCurrentUser = mAuth.getCurrentUser();

                        mPayment.setPaymentDetails(paymentDetails);
                        mPayment.setPaymentCourseId(mCourseId);
                        mPayment.setPaymentCourseTitle(mCourseModel.getTitle());
                        mPayment.setPaymentOwnerId(mCurrentUser.getUid());
                        mPayment.setPaymentAmount(mCourseModel.getPaymentAmount());
                        mPayment.setOfferPayment(mCourseModel.getFinalPayment());
                        //Log.d("afterPayment", String.valueOf(mCourseModel.getPaymentAmount()));

                        // get Audience Position and then update the KeyAudience
                        getAudiencePosition();
                        savePaymentToFirebase();

                        if (mListener != null){
                       mListener.onPaymentSuccessful(mPayment);}

                    } catch (JSONException e) {
                        Log.e("paymentExample","an extremely unlikely failure occurred : ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    private void savePaymentToFirebase() {


            // Write payment detail to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_PAYMENT);
            String key = myRef.push().getKey();
            if(key != null) {
                mPayment.setPaymentId(key);
                myRef.child(key).setValue(mPayment);
            }



    }

    private void getAudiencePosition() {



        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference firebaseReference = firebaseDatabase.getReference(MyConstants.FIREBASE_KEY_POSTS).child(mCourseId);

        firebaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CoursesModel coursesModel1 = dataSnapshot.getValue(CoursesModel.class);
                if(coursesModel1!=null) {
                    int position = 0;
                    for (UserModel registerUsers : coursesModel1.getMyAudiance().getMyAudianceList()) {

                        if (registerUsers.getFirebaseId().equals(mCurrentUser.getUid())) {
                            //registerUsers.setKeyAudience(MyConstants.KEY_PAID);
                            mPositionAudience =position;
                            //String position=firebaseDatabase.child("MyAudiance").child("MyAudianceList").getValue();
                            //coursesModel1.getMyAudiance().getMyAudianceList().set(registerUsers)
                            Log.d("position is ", String.valueOf(position));
                            Log.d("positionAud is ", String.valueOf(mPositionAudience));
                            updateAudenceKey();
                        }

                        position++;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void updateAudenceKey() {


        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference firebaseReference = firebaseDatabase.getReference(MyConstants.FIREBASE_KEY_POSTS).child(mCourseId).child(MyConstants.FIREBASE_KEY_AUDIENCE).child(MyConstants.FIREBASE_KEY_AUDIENCE_LIST).child(mPositionAudience+"");

        firebaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().child(MyConstants.FIREBASE_KEY_AUDIENCE_KEY).setValue(MyConstants.KEY_PAID);
                           }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PaymentFragmentListerner) {
            mListener = (PaymentFragmentListerner) context;
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

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }
    public interface PaymentFragmentListerner {


        void onFragmentInteraction(String pageTitle);
        void hideBottomNavBar();
        void showBackButton(boolean show);
        void onPaymentSuccessful(PaymentModel paymentModel);
    }
}
