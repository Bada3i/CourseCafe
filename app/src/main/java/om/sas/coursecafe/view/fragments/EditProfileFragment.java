package om.sas.coursecafe.view.fragments;

import android.Manifest;
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

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;
import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.UserModel;

public class EditProfileFragment extends Fragment {


    private static final String TAG = "edit-profile";
    private static final int PICK_IMAGE = 10;
    private static final int PICK_FROM_GALLERY = 100;

    private StorageReference mStorageRef;
    private DatabaseReference mRef;

    private UserModel mUser;
    private Context mContext;
    private EditText etDescription, etEmail, etPhone, etUserName;

    private CircleImageView civViewImg;
    private Uri mImageUri;
    private String mImage;
    private ImageView ivBackgroundEdit;

    private EditProfileFragmentListener mListener;

    public static EditProfileFragment newInstance(UserModel user) {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(MyConstants.KEY_USER, user);
        editProfileFragment.setArguments(args);
        return editProfileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (UserModel) getArguments().getSerializable(MyConstants.KEY_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.editProfileTitle));
            mListener.hideBottomNavBar();
            mListener.showBackButton(true);
        }

        View parentView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (currentUser != null) {
            mRef = database.getReference(MyConstants.FIREBASE_KEY_USERS).child(currentUser.getUid());
        }
        mStorageRef = FirebaseStorage.getInstance().getReference();

        ivBackgroundEdit = parentView.findViewById(R.id.iv_background_edit);
        civViewImg = parentView.findViewById(R.id.ib_view_img);
        FloatingActionButton fabAddImg = parentView.findViewById(R.id.ib_add_img);
        fabAddImg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                requestDynamicPermission();
            }
        });

        etUserName = parentView.findViewById(R.id.et_user_name);
        etDescription = parentView.findViewById(R.id.et_information);
        etEmail = parentView.findViewById(R.id.et_email);
        etPhone = parentView.findViewById(R.id.et_phone);

        Button btnEdit = parentView.findViewById(R.id.btn_edit);

        readUsersFromFirebase();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserModel userDetails = mUser;
                if (shouldEdit(userDetails)) {
                    uploadToStorage(userDetails);
                }
            }

        });


        return parentView;
    }

    private boolean shouldEdit(UserModel userDetails) {

        String username = etUserName.getText().toString();
        String description = etDescription.getText().toString();
        String email = etEmail.getText().toString();
        String phoneNumber = etPhone.getText().toString();

        if (TextUtils.isEmpty(username)) {
            etUserName.setError(getResources().getString(R.string.enter_user_name));
            return false;
        } else {
            userDetails.setFullName(username);
        }

        if (TextUtils.isEmpty(description)) {
            userDetails.setDescription(null);
        } else {
            userDetails.setDescription(description);
        }

        if (TextUtils.isEmpty(email)) {
            userDetails.setEmail(null);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmail.setError(getResources().getString(R.string.enter_email_address));
            return false;
        } else {
            userDetails.setEmail(email);
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            userDetails.setPhoneNum(null);
        } else if (etPhone.length() < 8 || etPhone.length() > 8) {
            etPhone.setError(getResources().getString(R.string.enter_valid_phone));
            return false;
        } else {
            userDetails.setPhoneNum((phoneNumber));
        }

        return true;
    }


    private void readUsersFromFirebase() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel value = dataSnapshot.getValue(UserModel.class);
                if (value != null) {
                    etUserName.setText(value.getFullName());
                    etDescription.setText(value.getDescription());
                    etEmail.setText(value.getEmail());
                    if (value.getPhoneNum() != null) {
                        etPhone.setText(value.getPhoneNum());
                    } else {
                        etPhone.setText("");
                    }
                    if (getContext() != null) {
                        Glide.with(mContext).load(value.getProfilePic()).placeholder(R.drawable.ic_person).into(civViewImg);
                        Glide.with(mContext).load(value.getProfilePic()).placeholder(R.color.bubble_gum_pink).into(ivBackgroundEdit);
                        mImage = value.getProfilePic();
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

    private void openGallery() {
        Intent intentObj = new Intent();
        intentObj.setType(MyConstants.IMAGE_PATH);
        intentObj.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentObj, getResources().getString(R.string.select_picture)), PICK_IMAGE);
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
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    civViewImg.setImageBitmap(selectedImage);
                }

            } catch (FileNotFoundException e) {
                Toast.makeText(mContext, getResources()
                        .getString(R.string.image_not_found), Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void uploadToStorage(final UserModel userModel) {

        if (mImageUri != null) {

            final StorageReference imgRef = mStorageRef.child(MyConstants.FIREBASE_KEY_STORAGE + userModel.getEmail());
            imgRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri imageFirebaseUrl) {
                                    userModel.setProfilePic(imageFirebaseUrl.toString());
                                    onEditPressed(userModel);

                                    Log.d("imageUrl", imageFirebaseUrl.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.d("failToUpload", exception.toString());
                        }
                    });

        } else {
            userModel.setProfilePic(mImage);
            onEditPressed(userModel);
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

    private void onEditPressed(UserModel userModel) {
        if (mListener != null) {
            mListener.onEditClick(userModel);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof EditProfileFragmentListener) {
            mListener = (EditProfileFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EditProfileFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface EditProfileFragmentListener {
        void onEditClick(UserModel userModel);
        void onFragmentInteraction(String title);
        void hideBottomNavBar();
        void showBackButton(boolean show);
    }
}
