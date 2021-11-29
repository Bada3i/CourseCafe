package om.sas.coursecafe.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;

import om.sas.coursecafe.view.fragments.ChatsListFragment;
import om.sas.coursecafe.view.fragments.CommentsFragment;
import om.sas.coursecafe.view.fragments.DetailsFragment;
import om.sas.coursecafe.view.fragments.MessageFragment;
import om.sas.coursecafe.view.model.CoursesModel;

import om.sas.coursecafe.view.model.UserModel;

public class CoursesListAdapter extends RecyclerView.Adapter<CoursesListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<CoursesModel> mItemsArrayList;
    private CoursesModel mcoursesModel;
    private CourseListAdapterListerner mListener;
    private FirebaseUser firebaseUser;
    private ImageView ivImageCourseList;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseLike;


    public CoursesListAdapter(Context c ) {
        mContext = c;
        mItemsArrayList = new ArrayList<>();

        mListener = (CourseListAdapterListerner) c;

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }


    public void updateCourseArrayList(ArrayList<CoursesModel> newItems) {
        mItemsArrayList = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.course_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final CoursesModel courseItem = mItemsArrayList.get(position);
        mcoursesModel = new CoursesModel();
        //course image
        Glide.with(mContext)
                .load(courseItem.getImage())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.ivImageCourseList);

        // course post date
        holder.datediff.setText(getTimeDiff(courseItem));

        //course provider name
        holder.tvUserNameCourseOwner.setText(courseItem.getPostProviderName());

        //course provider profile image
        Glide.with(mContext).load(courseItem.getPostProviderImage()).placeholder(R.mipmap.ic_launcher).into(holder.crImgViewProfile);


        holder.crImgViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener != null) {
                    mListener.onProfileImageClick(courseItem.getPostProviderId());
                }

            }
        });

        getComment(courseItem.getPostId(), holder.comments);

        //OnClick POST image it wll move to details fragment
        holder.ivImageCourseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mListener.goToDetailScreen(courseItem);

            }
        });

        //OnClick comment icon
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToCommentScreen(courseItem.getPostId(), courseItem.getPostProviderId());

            }
        });
//        //OnClick view comments text
//        holder.comments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mListener.goToCommentScreen(courseItem.getPostId(), courseItem.getPostProviderId());
//
//            }
//        });
        //OnClick share icon
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              mListener.shareCourseWithFriends(courseItem);

            }
        });

        holder.ivLikes.setTag(position);
        postLike(courseItem, holder.ivLikes, holder.tvNumLike);

        holder.ivLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseLike.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(courseItem.getPostId()).hasChild(mAuth.getCurrentUser().getUid())) {
                            mDatabaseLike.child(courseItem.getPostId()).child(mAuth.getCurrentUser().getUid()).removeValue();
                            holder.ivLikes.setImageResource(R.drawable.ic_star_border);
                            getNumLikes(holder.tvNumLike, courseItem);

                        } else {
                            mDatabaseLike.child(courseItem.getPostId()).child(mAuth.getCurrentUser().getUid())
                                    .setValue(mAuth.getCurrentUser().getUid());
                            holder.ivLikes.setImageResource(R.drawable.ic_star);
                            getNumLikes(holder.tvNumLike, courseItem);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        ImageView profilePic;
        Button btn;

        ImageView ivImageCourseList;
        TextView datediff;
        TextView tvUserNameCourseOwner;
        CircleImageView crImgViewProfile;

        ConstraintLayout parentLayout;
        ImageView comment,share;
        TextView comments;

        TextView tvNumLike;
        ImageView ivLikes;

        private MyViewHolder(View view) {
            super(view);

            ivImageCourseList = view.findViewById(R.id.iv_img_courseslist);
            datediff = view.findViewById(R.id.time_of_uplode_post);
            tvUserNameCourseOwner = view.findViewById(R.id.tv_username_coursesList);
            crImgViewProfile = view.findViewById(R.id.iv_courseOwner_profileImage);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            comment = view.findViewById(R.id.iv_comment);
            comments = view.findViewById(R.id.tv_comments_num);
            share = view.findViewById(R.id.iv_share);

            tvNumLike = itemView.findViewById(R.id.tv_num_of_like);
            ivLikes = itemView.findViewById(R.id.iv_like);

        }


    }

    //COMMENT part
    private void getComment(final String postId, final TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_COMMENTS).child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText( dataSnapshot.getChildrenCount() + "  Comments");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    // get time difference to use it to display the date of posting the course

    private String getTimeDiff(CoursesModel thisCourse) {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

        try {
            Date date1 = simpleDateFormat.parse(thisCourse.getPostSubmitTime());//POST TIME
            Date date2 = simpleDateFormat.parse(getCurrentDate());// USER currentTime

            return printDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "error";
    }

    // get current date, in order to sort the course (up coming courses without showing expired courses)
    private String getCurrentDate() {

        return android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date()).toString();
    }

    // formatting the posting date
    private String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthsInMilli = daysInMilli * 29;

        long elapsedMonths = different / monthsInMilli;
        different = different % monthsInMilli;


        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        //String result = elapsedDays + " days, " + elapsedHours + " hours, " + elapsedMinutes + " elapsedMinutes, " + elapsedSeconds + " seconds";

        String result;

        if (elapsedMonths > 0) {
            result = elapsedMonths + " months ago ";
        } else if (elapsedDays > 0) {
            result = elapsedDays + " day ago";
        } else if (elapsedHours > 0) {
            result = elapsedHours + " hours ago";
        } else if (elapsedMinutes > 0) {
            result = elapsedMinutes + " minutes ago";
        } else {
            result = elapsedSeconds + " seconds ago";
        }


        Log.d("TimeDiff", result);
        return result;

    }



    //add by huda
    // like the course post
    private void postLike(final CoursesModel thisPost, final ImageView mlikes, final TextView tvNumLike) {
        mDatabaseLike = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_LIKES);
        mDatabaseLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.child(thisPost.getPostId()).hasChild(mAuth.getCurrentUser().getUid())) {
                    mlikes.setImageResource(R.drawable.ic_star_border);
                    getNumLikes(tvNumLike, thisPost);
                } else {
                    mDatabaseLike.child(thisPost.getPostId()).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                    mlikes.setImageResource(R.drawable.ic_star);
                    getNumLikes(tvNumLike, thisPost);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNumLikes(final TextView tvNumLike, CoursesModel thisPost) {
        DatabaseReference reference = FirebaseDatabase.getInstance().
                getReference(MyConstants.FIREBASE_KEY_LIKES).child(thisPost.getPostId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvNumLike.setText(dataSnapshot.getChildrenCount() + " Interested");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // Interface Methods
    public interface CourseListAdapterListerner {


        void onProfileImageClick(String userModelId);
        void goToDetailScreen(CoursesModel courseItem);
        void shareCourseWithFriends(CoursesModel courseItem);
        void goToCommentScreen(String postId, String postProviderId);

    }
}