package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.activity.MainAppActivity;
import om.sas.coursecafe.view.adapter.CommentAdapter;
import om.sas.coursecafe.view.model.Comment;
import om.sas.coursecafe.view.model.CoursesModel;

import om.sas.coursecafe.view.model.UserModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsFragment extends Fragment {
  private RecyclerView recyclerView;
  private CommentAdapter commentAdapter;
  private List<Comment> commentList;

  private EditText addComment;
  private ImageView imageProfile;
  private TextView post;

  private String postId;
  private String publisherId;
  FirebaseUser firebaseUser;
  CoursesModel mCoursesModel;
  private CommentInterface mListener;
  private Context mContext;
  private static final String KEY_POST = "param1";


  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  public CommentsFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   * <p>
   * .
   *
   * @return A new instance of fragment CommentsFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static CommentsFragment newInstance(String postId, String publisherId) {

    Bundle args = new Bundle();

    args.putString(ARG_PARAM1, postId);
    args.putString(ARG_PARAM2, publisherId);
    CommentsFragment fragment = new CommentsFragment();
    fragment.setArguments(args);

    return fragment;
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      postId = getArguments().getString(ARG_PARAM1);
      publisherId = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View parentView = inflater.inflate(R.layout.fragment_comments, container, false);

//    final Toolbar toolbar = parentView.findViewById(R.id.toolbar);
//    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Comments");
//    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
//
//    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        getActivity().onBackPressed();
//
//      }
//    });

    if (mListener != null) {
      mListener.onFragmentInteraction("Comments");
      mListener.hideBottomNavBar();
      mListener.showBackButton(true);
    }


    recyclerView = parentView.findViewById(R.id.recyclerview);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    commentList = new ArrayList<>();
    commentAdapter = new CommentAdapter(getContext(), commentList,postId);
    recyclerView.setAdapter(commentAdapter);

    addComment = parentView.findViewById(R.id.add_comment);
    imageProfile = parentView.findViewById(R.id.image_profile);
    post = parentView.findViewById(R.id.post);
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    mCoursesModel = new CoursesModel();

    post.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (addComment.getText().toString().equals("")) {
          Toast.makeText(getActivity(), "you cant send empty comment", Toast.LENGTH_SHORT).show();
        }  else {
          addComment();
        }
      }
    });

    getImage();
    readComment();
    return parentView;
  }

  private void addComment() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_COMMENTS)
            .child(postId);
    String commentid =reference.push().getKey();
    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("comment", addComment.getText().toString());
    hashMap.put("publisher", firebaseUser.getUid());
    hashMap.put("commentid",commentid);

    reference.child(commentid).setValue(hashMap);
    addComment.setText("");
  }

  private void getImage() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_USERS).child(firebaseUser.getUid());
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        UserModel userModel = dataSnapshot.getValue(UserModel.class);
//                Glide.with(getActivity().getApplicationContext().getApplicationContext()).load(userModel.getProfilePic()).into(imageProfile);


      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public void onAddPressed() {
    if (mListener != null) {
      mListener.OnPostClick();
    }
  }



  private void readComment() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_COMMENTS).child(postId);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        commentList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Comment comment = snapshot.getValue(Comment.class);
          commentList.add(comment);
        }
        commentAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }


  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    mContext = context;
    if (context instanceof CommentInterface) {
      mListener = (CommentInterface) context;
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

  public interface CommentInterface {

    void OnPostClick();

    void onFragmentInteraction(String pageTitle);
    void hideBottomNavBar();
    void showBackButton(boolean show);
  }
}