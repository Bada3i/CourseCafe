package om.sas.coursecafe.view.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.MyConstants;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_LOCATION;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_OFFER_TYPE;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_KEY_ONLINE;


public class EditCourseFragment extends Fragment {

    private static final String KEY_COURSE = "course";
    private static final String DATE_START = "date_start";
    private static final String DATE_END = "date_end";
    private static final int PICK_IMAGE = 100;

    private EditInterface mListener;
    private StorageReference mStorageRef;
    private Context mContext;
    private Uri mImageUri;
    private CoursesModel mCoursesModel;

    private ImageView ibImg;
    private ImageView ivImg;
    private RadioGroup radioGroup;
    private RadioButton rbLocation, rbOnline;

    private EditText etSelectedStartDate;
    private EditText etSelectedEndDate;
    private EditText etTitle;
    private EditText etProviderName;
    private EditText etStartTime;
    private EditText etEndTime;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPrice;
    private EditText etOnline;
    private EditText etLocation;
    private EditText etOfferDetails;

    private String Institution;
    private String Title;
    private String Email;
    private String Phone;
    private String StartDate;
    private String EndDate;
    private String StartTime;
    private String EndTime;
    private String price;
    private String location;
    private String online;
    private String mActiveTextView;
    private String mFullAddress;
    private int mYear;
    private int mMonth;
    private int mDay;
    private double mLng;
    private double mLat;
    private double mFirebaseLng;
    private double mFirebaseLat;
    private Switch aSwitch;

    public static EditCourseFragment newInstance(CoursesModel post) {
        EditCourseFragment fragment = new EditCourseFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_COURSE, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCoursesModel = (CoursesModel) getArguments().getSerializable(KEY_COURSE);

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.editCourseTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);

        }
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_edit_courses, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        ivImg = parentView.findViewById(R.id.iv_img_edit);
        etProviderName = parentView.findViewById(R.id.et_edit_institution);
        etTitle = parentView.findViewById(R.id.et_edit_title);
        etPrice = parentView.findViewById(R.id.et_price_edit);
        rbLocation = parentView.findViewById(R.id.rb_location);
        etOnline = parentView.findViewById(R.id.et_edit_online);
        rbOnline = parentView.findViewById(R.id.rb_online);
        radioGroup = parentView.findViewById(R.id.rg_type_of_location);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_location) {
                    etLocation.setVisibility(View.VISIBLE);
                    etOnline.setVisibility(View.GONE);
                } else if (checkedId == R.id.rb_online) {
                    etLocation.setVisibility(View.INVISIBLE);
                    etOnline.setVisibility(View.VISIBLE);
                }
            }
        });


        ibImg = parentView.findViewById(R.id.ib_edit_Img);
        ibImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeImage();
            }
        });


        etSelectedStartDate = parentView.findViewById(R.id.et_edit_start_date);
        etSelectedStartDate.setInputType(InputType.TYPE_NULL);
        etSelectedStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(DATE_START);
            }
        });

        etSelectedEndDate = parentView.findViewById(R.id.et_edit_end_date);
        etSelectedEndDate.setInputType(InputType.TYPE_NULL);
        etSelectedEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(DATE_END);
            }
        });


        etStartTime = parentView.findViewById(R.id.et_edit_start_time);
        etStartTime.setInputType(InputType.TYPE_NULL);
        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(etStartTime);
            }
        });


        etEndTime = parentView.findViewById(R.id.et_edit_end_time);
        etEndTime.setInputType(InputType.TYPE_NULL);
        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(etEndTime);
            }
        });
        currentDate();


        etEmail = parentView.findViewById(R.id.et_edit_email);
        etPhone = parentView.findViewById(R.id.et_edit_phone);

        Button btnEdit = parentView.findViewById(R.id.btn_edit_course);

        //Added by Saira
        etLocation = parentView.findViewById(R.id.et_edit_location);
        etLocation.setInputType(InputType.TYPE_NULL);
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenPickLocationFragmentPressed();
            }
        });

        readCourseDataFromFirebase();

        //offer code
        TextView tvPercentage = parentView.findViewById(R.id.tv_percentage);
        TextView tvOfferTitle = parentView.findViewById(R.id.tv_offer_title);
        tvPercentage.setTextColor(getResources().getColor(R.color.buttonTextColorDisabled));
        tvPercentage.setEnabled(false);
        tvOfferTitle.setTextColor(getResources().getColor(R.color.buttonTextColorDisabled));
        tvOfferTitle.setEnabled(false);
        etOfferDetails = parentView.findViewById(R.id.et_offer_details);
        etOfferDetails.setEnabled(false);
        aSwitch = parentView.findViewById(R.id.switch_offer);
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

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chickValidationOfFields()) {

                    mCoursesModel.setTitle(Title);
                    mCoursesModel.setInstitution(Institution);
                    mCoursesModel.setStartTime(StartTime);
                    mCoursesModel.setEndTime(EndTime);
                    mCoursesModel.setStartDate(StartDate);
                    mCoursesModel.setEndDate(EndDate);
                    mCoursesModel.setEmail(Email);
                    mCoursesModel.setPhone(Phone);
                    mCoursesModel.setPaymentAmount(price);


                    if (rbLocation.isChecked()) {

                        if (mFullAddress != null) {
                            mCoursesModel.setLocation_lat(mLat);
                            mCoursesModel.setLocation_lng(mLng);
                            mCoursesModel.setLocationAddress(mFullAddress);
                            mCoursesModel.setLocationType(FIREBASE_KEY_LOCATION);
                        } else {
                            mCoursesModel.setLocation_lat(mFirebaseLat);
                            mCoursesModel.setLocation_lng(mFirebaseLng);
                            mCoursesModel.setLocationAddress(location);
                            mCoursesModel.setLocationType(FIREBASE_KEY_LOCATION);
                        }

                    } else {
                        mCoursesModel.setLocationType(FIREBASE_KEY_ONLINE);
                        mCoursesModel.setLocationAddress(etOnline.getText().toString());
                        mCoursesModel.setLocation_lat(0);
                        mCoursesModel.setLocation_lng(0);
                    }

                    //validation of offer
                    if (aSwitch.isChecked()) {
                        mCoursesModel.setOfferType(FIREBASE_KEY_OFFER_TYPE);
                        mCoursesModel.setOfferDetails(etOfferDetails.getText().toString());
                        courseDiscount();
                    } else {
                        mCoursesModel.setOfferType(null);
                        mCoursesModel.setOfferDetails(null);
                        mCoursesModel.setFinalPayment(null);
                    }


                    uploadToStorage(mCoursesModel);
                }
            }
        });


        return parentView;

    }

    private void courseDiscount(){
        double payment = Double.parseDouble(etPrice.getText().toString());
        double percentageDiscount = Double.parseDouble(etOfferDetails.getText().toString());
        double result = payment - ( payment * percentageDiscount / 100);
        mCoursesModel.setFinalPayment(String.valueOf(result));
    }

    private void readCourseDataFromFirebase() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_POSTS)
                .child(mCoursesModel.getPostId());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CoursesModel value = dataSnapshot.getValue(CoursesModel.class);
                if (value != null) {

                    etTitle.setText(value.getTitle());
                    etProviderName.setText(value.getInstitution());
                    etStartTime.setText(value.getStartTime());
                    etEndTime.setText(value.getEndTime());
                    etSelectedStartDate.setText(value.getStartDate());
                    etSelectedEndDate.setText(value.getEndDate());
                    etEmail.setText(value.getEmail());
                    etPhone.setText("" + value.getPhone());
                    etPrice.setText(value.getPaymentAmount());
                    if(value.getLocationType().equals(FIREBASE_KEY_LOCATION)){
                        rbLocation.setChecked(true);
                        if (mFullAddress != null){
                            etLocation.setText(mFullAddress);
                        }else {
                            etLocation.setText(value.getLocationAddress());
                        }
                        mFirebaseLat = value.getLocation_lat();
                        mFirebaseLng = value.getLocation_lng();
                    }else{
                        rbOnline.setChecked(true);
                        etOnline.setText(value.getLocationAddress());
                    }

                    if(getContext() != null) {
                        Glide.with(getContext()).load(value.getImage()).placeholder(R.mipmap.ic_launcher).into(ivImg);
                    }

                    if (value.getOfferType() != null) {
                        aSwitch.setChecked(true);
                        etOfferDetails.setText(value.getOfferDetails());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private boolean chickValidationOfFields() {
        Institution = etProviderName.getText().toString();
        Title = etTitle.getText().toString();
        Email = etEmail.getText().toString();
        Phone = etPhone.getText().toString();
        StartDate = etSelectedStartDate.getText().toString();
        EndDate = etSelectedEndDate.getText().toString();
        StartTime = etStartTime.getText().toString();
        EndTime = etEndTime.getText().toString();
        location = etLocation.getText().toString();
        price = etPrice.getText().toString();
        online = etOnline.getText().toString();

        boolean isTitle = Title.isEmpty();
        boolean isProvider = Institution.isEmpty();
        boolean isStartTime = StartTime.isEmpty();
        boolean isEndTime = EndTime.isEmpty();
        boolean isStartDate = StartDate.isEmpty();
        boolean isEndDate = EndDate.isEmpty();
        boolean isEmail = Email.isEmpty();
        boolean isPhone = Phone.isEmpty();
        boolean isLocation = location.isEmpty();
        boolean isPrice = price.isEmpty();
        boolean isOnline = online.isEmpty();


        boolean allMandatoryFiledFilled = true;

        if (isTitle) {
            etTitle.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        }

        if (isProvider) {
            etProviderName.setError(getResources().getString(R.string.error_empty_fields));
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
            etSelectedStartDate.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        }
        if (isEndDate) {
            etSelectedEndDate.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        }
        if (isEmail) {
            etEmail.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmail.setError(getResources().getString(R.string.error_invalid_email));
            allMandatoryFiledFilled = false;

        }

        if (isPhone) {
            etPhone.setError(getResources().getString(R.string.error_empty_fields));
            allMandatoryFiledFilled = false;
        } else if (etPhone.length() < 8 || etPhone.length() > 13) {
            etPhone.setError(getResources().getString(R.string.error_invalid_email));
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

    private void onEditPressed(CoursesModel p) {
        if (mListener != null) {
            mListener.onEditClickFormEditFragment(p);
        }
    }

    private void onOpenPickLocationFragmentPressed() {
        if (mListener != null) {
            mListener.onOpenPickLocationFragment();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof EditInterface) {
            mListener = (EditInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EditInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void currentDate() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);// get current year
        mMonth = c.get(Calendar.MONTH); // get current month, months count in calender starts from zero!
        mDay = c.get(Calendar.DAY_OF_MONTH); // get current day

    }

    private void changeImage() {
        Intent intentObj = new Intent();
        intentObj.setType("image/*");
        intentObj.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentObj, getString(R.string.select_image)), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {

            try {

                mImageUri = data.getData();
                Log.d("img-uri", mImageUri.toString());
                Log.d("img-uri-path", mImageUri.getPath());

                InputStream imageStream = mContext.getContentResolver().openInputStream(mImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ivImg.setImageBitmap(selectedImage);


            } catch (FileNotFoundException e) {
                Toast.makeText(mContext, R.string.image_not_found, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadToStorage(final CoursesModel coursesModel) {

        final StorageReference imgRef = mStorageRef.child(MyConstants.FIREBASE_KEY_STORAGE + coursesModel.getPostId());

        if (mImageUri == null) {
            //  mImageUri=Uri.parse(mCoursesModel.getImage());
            onEditPressed(coursesModel);
        } else {

            imgRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri imageFirebaseUrl) {
                                    coursesModel.setImage(imageFirebaseUrl.toString());

                                    onEditPressed(coursesModel);
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
        }

    }


    /**
     * this function will display date picker dialog
     */
    private void pickDate(String activeTextView) {

        mActiveTextView = activeTextView;

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, getListener(), mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    /**
     * @return this function will return DatePickerDialog.OnDateSetListener obj inorder to use it in DatePickerDialog constructor
     */
    private DatePickerDialog.OnDateSetListener getListener() {


        DatePickerDialog.OnDateSetListener obj = new DatePickerDialog.OnDateSetListener() {

            /**
             * this function will be called after user select a date and press ok!
             * this function will give the following :
             *
             * @param view
             * @param selectedYear   : the year that the user selected
             * @param selectedMonth  : the month that the user selected
             * @param selectedDay    : the day that the user selected
             */
            @Override
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                String month;
                String day;
                if ((selectedMonth + 1) < 10) {
                    month = "0" + (selectedMonth + 1);
                } else {
                    month = "" + (selectedMonth + 1);
                }
                if (selectedDay < 10) {
                    day = "0" + selectedDay;
                } else {
                    day = "" + selectedDay;
                }
                if (mActiveTextView.equals(DATE_START)) {
                    etSelectedStartDate.setText(selectedYear + "-" + month + "-" + day);
                } else {
                    etSelectedEndDate.setText(selectedYear + "-" + month + "-" + day);
                }

            }
        };


        return obj;


    }

    // Added by Saira

    public void updateLocation(String fullAddress, double locationLat, double locationLng) {
        mFullAddress = fullAddress;
        mLng = locationLng;
        mLat = locationLat;
        etLocation.setText(mFullAddress);
        // need to check a condition, if the user did not update the location?then??

    }

    public interface EditInterface {

        void onEditClickFormEditFragment(CoursesModel p);

        void onFragmentInteraction(String title);

        void onOpenPickLocationFragment();

        void hideBottomNavBar();

        void showBackButton(boolean show);


    }
}
