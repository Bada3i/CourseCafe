package om.sas.coursecafe.view.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.dialog.ConfirmDialogFragment;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_LOCATION;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_OFFER_TYPE;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_ONLINE;


public class AddCoursesFragment extends Fragment implements ConfirmDialogFragment.ConfirmDialogFragmentListener {

    private static final String DATE_START = "date_start";
    private static final String DATE_END = "date_end";
    private static final int PICK_IMAGE = 10;
    private static final int PICK_FROM_GALLERY = 100;

    private OnAddPostFragmentInteractionListener mListener;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CoursesModel mCourseModel;
    private Context mContext;
    private UserModel mUser;
    private Uri mImageUri;
    private Bitmap mSelectedImage;

    private EditText etInstitutionName;
    private EditText etTitle;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etStartTime;
    private EditText etEndTime;
    private EditText etStartDate;
    private EditText etEndDate;
    private EditText etLocation;
    private EditText etOnline;
    private EditText etPrice;
    private EditText etOfferDetails;
    private ImageView ivViewImg;
    private RadioGroup radioGroup;
    private RadioButton rbLocation, rbOnline;
    private FloatingActionButton fabAddImage;

    private String mFullAddress;
    private String mActiveTextView;
    private String institution;
    private String title;
    private String email;
    private String phone;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String price;
    private String location;
    private String online;
    private int mYear;
    private int mMonth;
    private int mDay;
    private double mLng;
    private double mLat;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.addCourseTitle));
            mListener.showBottomNavBar();
            mListener.showBackButton(false);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        readMyUserFromFirebase();

        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_add_courses, container, false);
        mCourseModel = new CoursesModel();
        etInstitutionName = parentView.findViewById(R.id.et_institution);
        etTitle = parentView.findViewById(R.id.et_title);
        etEmail = parentView.findViewById(R.id.et_email);
        etPhone = parentView.findViewById(R.id.et_phone);
        etStartTime = parentView.findViewById(R.id.et_start_time);
        etEndTime = parentView.findViewById(R.id.et_end_time);
        etStartDate = parentView.findViewById(R.id.et_start_date);
        etEndDate = parentView.findViewById(R.id.et_end_date);
        etLocation = parentView.findViewById(R.id.et_add_location);
        etOnline = parentView.findViewById(R.id.et_add_online);
        etPrice = parentView.findViewById(R.id.et_price);
        rbLocation = parentView.findViewById(R.id.rb_location);
        rbOnline = parentView.findViewById(R.id.rb_online);
        radioGroup = parentView.findViewById(R.id.rg_type_of_location);
        radioGroup.check(R.id.rb_location);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_location){
                    etLocation.setVisibility(View.VISIBLE);
                    etOnline.setVisibility(View.GONE);
                }
                else if(checkedId == R.id.rb_online){
                    etLocation.setVisibility(View.INVISIBLE);
                    etOnline.setVisibility(View.VISIBLE);
                }
            }
        });

        ivViewImg = parentView.findViewById(R.id.iv_img_add);

        if (mSelectedImage != null){
        ivViewImg.setImageBitmap(mSelectedImage);}

        fabAddImage = parentView.findViewById(R.id.ib_add_Img);
        fabAddImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                requestDynamicPermission();
            }
        });

        etStartTime.setInputType(InputType.TYPE_NULL);
        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(etStartTime);
            }
        });

        etEndTime.setInputType(InputType.TYPE_NULL);
        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(etEndTime);
            }
        });

        etStartDate.setInputType(InputType.TYPE_NULL);
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(DATE_START);
            }
        });

        etEndDate.setInputType(InputType.TYPE_NULL);
        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickDate(DATE_END);

            }
        });

        etLocation.setInputType(InputType.TYPE_NULL);
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenPickLocationFragmentPressed();
            }
        });

        currentDate();

        //offer code
        TextView tvPercentage = parentView.findViewById(R.id.tv_percentage);
        TextView tvOfferTitle = parentView.findViewById(R.id.tv_offer_title);
        tvPercentage.setTextColor(getResources().getColor(R.color.buttonTextColorDisabled));
        tvPercentage.setEnabled(false);
        tvOfferTitle.setTextColor(getResources().getColor(R.color.buttonTextColorDisabled));
        tvOfferTitle.setEnabled(false);
        etOfferDetails = parentView.findViewById(R.id.et_offer_details);
        etOfferDetails.setEnabled(false);
        final Switch aSwitch = parentView.findViewById(R.id.switch_offer);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvPercentage.setTextColor(getResources().getColor(R.color.black));
                    tvPercentage.setEnabled(true);
                    tvOfferTitle.setTextColor(getResources().getColor(R.color.black));
                    tvOfferTitle.setEnabled(true);
                    etOfferDetails.setEnabled(true);
                } else {
                    tvPercentage.setTextColor(getResources().getColor(R.color.buttonTextColorDisabled));
                    tvPercentage.setEnabled(false);
                    tvOfferTitle.setTextColor(getResources().getColor(R.color.buttonTextColorDisabled));
                    tvOfferTitle.setEnabled(false);
                    etOfferDetails.setEnabled(false);

                }
            }
        });

        Button btnAddCourses = parentView.findViewById(R.id.btn_add_course);
        btnAddCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidationOfFields()) {

                    mCourseModel.setInstitution(institution);
                    mCourseModel.setTitle(title);
                    mCourseModel.setEmail(email);
                    mCourseModel.setPhone(phone);
                    mCourseModel.setStartDate(startDate);
                    mCourseModel.setEndDate(endDate);
                    mCourseModel.setStartTime(startTime);
                    mCourseModel.setEndTime(endTime);
                    mCourseModel.setPaymentAmount(price);
                    mCourseModel.setPostSubmitTime(getCurrentDate());
                    mCourseModel.setPostProviderId(mUser.getFirebaseId() + "");
                    mCourseModel.setPostProviderImage(mUser.getProfilePic());
                    mCourseModel.setPostProviderName(mUser.getFullName());

                    if (rbLocation.isChecked()) {
                        mCourseModel.setLocationType(FIREBASE_KEY_LOCATION);
                        mCourseModel.setLocation_lat(mLat);
                        mCourseModel.setLocation_lng(mLng);
                        mCourseModel.setLocationAddress(mFullAddress);
                    } else {
                        mCourseModel.setLocationType(FIREBASE_KEY_ONLINE);
                        mCourseModel.setLocationAddress(etOnline.getText().toString());
                        mCourseModel.setLocation_lat(0);
                        mCourseModel.setLocation_lng(0);
                    }


                    //validation of offer
                    if (aSwitch.isChecked()) {
                        mCourseModel.setOfferType(FIREBASE_KEY_OFFER_TYPE);
                        mCourseModel.setOfferDetails(etOfferDetails.getText().toString());
                        courseDiscount();
                    } else {
                        mCourseModel.setOfferType(null);
                        mCourseModel.setOfferDetails(null);
                        mCourseModel.setFinalPayment(null);
                    }


                    //open ConfirmDialogFragment
                    FragmentManager fragmentManager = getFragmentManager();
                    ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance
                            (getResources().getString(R.string.d_title_add_course), getResources().getString(R.string.d_msg_add_course),
                                    null,0,mCourseModel);
                    if (fragmentManager != null) {
                        confirmDialogFragment.setTargetFragment(AddCoursesFragment.this, 300);
                        confirmDialogFragment.show(fragmentManager, "add_course");
                    }



                }

            }
        });
        return parentView;
    }

    @Override
    public void onYesButtonClick(UserModel userModel, int position, CoursesModel coursesModel) {
        uploadToStorage(coursesModel);
    }

    private void courseDiscount(){
        double payment = Double.parseDouble(etPrice.getText().toString());
        double percentageDiscount = Double.parseDouble(etOfferDetails.getText().toString());
        double result = payment - ( payment * percentageDiscount / 100);
        mCourseModel.setFinalPayment(String.valueOf(result));
    }

    private void readMyUserFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(MyConstants.FIREBASE_KEY_USERS)
                .child(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(UserModel.class);
                setUserEmailAndPhone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUserEmailAndPhone() {
        etEmail.setText(mUser.getEmail());
        if (mUser.getPhoneNum() != null) {
            etPhone.setText(mUser.getPhoneNum());
        } else {
            etPhone.setText("");
        }
    }

    private boolean checkValidationOfFields() {
        institution = etInstitutionName.getText().toString();
        title = etTitle.getText().toString();
        email = etEmail.getText().toString();
        phone = etPhone.getText().toString();
        startDate = etStartDate.getText().toString();
        endDate = etEndDate.getText().toString();
        startTime = etStartTime.getText().toString();
        endTime = etEndTime.getText().toString();
        price = etPrice.getText().toString();
        location = etLocation.getText().toString();
        online = etOnline.getText().toString();


        boolean isTitle = title.isEmpty();
        boolean isProvider = institution.isEmpty();
        boolean isStartTime = startTime.isEmpty();
        boolean isEndTime = endTime.isEmpty();
        boolean isStartDate = startDate.isEmpty();
        boolean isEndDate = endDate.isEmpty();
        boolean isEmail = email.isEmpty();
        boolean isPhone = phone.isEmpty();
        boolean isLocation = location.isEmpty();
        boolean isPrice = price.isEmpty();
        boolean isOnline = online.isEmpty();

        boolean allMandatoryFiledFilled = true;

        if (isProvider) {
            etInstitutionName.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        }
        if (isTitle) {
            etTitle.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        }
        if (isEmail) {
            etEmail.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getResources().getString(R.string.error_invalid_email));
            allMandatoryFiledFilled = false;
        }
        if (isPhone) {
            etPhone.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;

        } else if (phone.length() < 8 || phone.length() > 13) {
            etPhone.setError(getResources().getString(R.string.error_invalid_email));
            allMandatoryFiledFilled = false;
        }
        if (isStartTime) {
            etStartTime.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        }
        if (isEndTime) {
            etEndTime.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        }
        if (isStartDate) {
            etStartDate.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        }
        if (isEndDate) {
            etEndDate.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;

        }

        if (rbLocation.isChecked()) {
            if (isLocation) {
                etLocation.setError(getResources().getString(R.string.error_empty_fields));
                allMandatoryFiledFilled = false;
            }
        } else {
            if (isOnline) {
                etLocation.setError(getResources().getString(R.string.error_empty_fields));
                allMandatoryFiledFilled = false;
            }
        }


        if (isPrice) {
            etPrice.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;

        }

        return allMandatoryFiledFilled;

    }

    private void showTimeDialog(final EditText time_in) {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                System.err.println(simpleDateFormat.format(new Date()));
                time_in.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new TimePickerDialog(mContext, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private void pickDate(String activeTextView) {
        mActiveTextView = activeTextView;

// inOrder to display datePicker dialog we can use DatePickerDialog.
        // DatePickerDialog is predefined class, and will help us display dialog
        // DatePickerDialog constructor receive the following things :
        // #1 context
        // #2 DatePickerDialog.OnDateSetListener()
        // #4 Default selected YEAR !
        // #5 Default selected MONTH !
        // #6 Default selected DAY !

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, getListener(), mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener getListener() {

        DatePickerDialog.OnDateSetListener obj = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {



                String month;
                String day;
                if ((selectedMonth + 1) < 10) {
                    month = "0" + (selectedMonth + 1);

                } else {
                    month = (selectedMonth + 1)+"";

                }
                if (selectedDay < 10) {
                    day = "0" + selectedDay;
                } else {
                    day = "" + selectedDay;
                }
                if (mActiveTextView.equals(DATE_START)) {
                    etStartDate.setText(selectedYear + "-" + month + "-" + day);
                } else {
                    etEndDate.setText(selectedYear + "-" + month + "-" + day);
                }

            }
        };

        return obj;
    }


    private void onOpenPickLocationFragmentPressed() {
        if (mListener != null) {
            mListener.onOpenPickLocationFragment();
        }
    }

    private void openGallery() {
        Intent intentObj = new Intent();
        intentObj.setType(MyConstants.IMAGE_PATH);
        intentObj.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentObj, getString(R.string.select_image)), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            try {
                if (data != null) {

                    mImageUri = data.getData();
                    Log.d("img-uri", mImageUri.toString());
                    Log.d("img-uri-path", mImageUri.getPath());

                    InputStream imageStream = mContext.getContentResolver().openInputStream(mImageUri);

                    mSelectedImage = BitmapFactory.decodeStream(imageStream);
                    ivViewImg.setImageBitmap(mSelectedImage);
                }

            } catch (FileNotFoundException e) {
                Toast.makeText(mContext, R.string.image_not_found, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void uploadToStorage(final CoursesModel mCourseModel) {
        if (mImageUri != null) {

            final StorageReference imgRef = mStorageRef.child(MyConstants.FIREBASE_KEY_STORAGE + mCourseModel.getTitle());
            imgRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri imageFirebaseUrl) {
                                    mCourseModel.setImage(imageFirebaseUrl.toString());
                                    onAddPressed(mCourseModel);
                                    Log.d("imageUrl", imageFirebaseUrl.toString());
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Log.d("failToUpload", exception.toString());
                        }
                    });
        }else {
            Toast.makeText(getContext(), getResources().getString(R.string.plz_image), Toast.LENGTH_LONG).show();
            }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestDynamicPermission() {
        try {
            if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
            } else {
                openGallery();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PICK_FROM_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(mContext, "You can't open Images unless, Permission is granted",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnAddPostFragmentInteractionListener) {
            mListener = (OnAddPostFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProfileFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void onAddPressed(CoursesModel mCourseModel) {
        if (mListener != null) {
            mListener.onAddPostClick(mCourseModel);
        }
    }

    public void updateLocation(String fullAddress, double locationLat, double locationLng) {
        mFullAddress = fullAddress;
        mLng = locationLng;
        mLat = locationLat;
        etLocation.setText(mFullAddress);

    }

    //UPLUDE POST
    private String getCurrentDate() {
        return android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date()).toString();
    }

    private void currentDate() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);// get current year
        mMonth = c.get(Calendar.MONTH); // get current month, months count in calender starts from zero!
        mDay = c.get(Calendar.DAY_OF_MONTH); // get current day

    }



    public interface OnAddPostFragmentInteractionListener {

        void onAddPostClick(CoursesModel coursesModel);

        void onOpenPickLocationFragment();

        void onFragmentInteraction(String title);

        void showBottomNavBar();
        void showBackButton(boolean show);
    }

}
