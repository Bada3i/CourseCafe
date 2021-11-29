package om.sas.coursecafe.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.activity.MainAppActivity;
import om.sas.coursecafe.view.fragments.ChatsListFragment;
import om.sas.coursecafe.view.fragments.CommentsFragment;
import om.sas.coursecafe.view.fragments.MessageFragment;

import om.sas.coursecafe.view.model.ChatListItemModel;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.UtilityClass.getCurrentDate;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context mContext;
    private List<ChatListItemModel> mChatList;
    FirebaseUser firebaseUser;
    private boolean ischat;
    private ChatListAdapterListerner mListener;


    public ChatAdapter(Context mContext, List<ChatListItemModel> mChatList, boolean ischat ) {

        this.mChatList=mChatList;
        this.mContext = mContext;
        this.ischat = ischat;

        mListener = (ChatListAdapterListerner) mContext;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_list_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final ChatListItemModel chatItem = mChatList.get(position);

        getUserInfo(holder.image_profile, holder.username, chatItem.getChatOwner());




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener !=null) {
                    mListener.onChatListClick(chatItem.getChatOwner());
                }

            }
        });

        holder.last_time.setText(getTimeDiff(chatItem));

            holder.last_msg.setText(chatItem.getLastMessage());

    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_profile;
        private TextView username;
        private TextView last_time;
        private TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.receiver_image_profile);
            username = itemView.findViewById(R.id.receiver_name);
            last_time = itemView.findViewById(R.id.last_time);
            last_msg = itemView.findViewById(R.id.last_msg);


        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String courseOwner) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_USERS).child(courseOwner);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Glide.with(mContext).load(userModel.getProfilePic()).placeholder(R.drawable.ic_person).into(imageView);
                username.setText(userModel.getFullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // get time difference to use it to display the date of posting the course

    private String getTimeDiff(ChatListItemModel mchat) {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            Date date1 = simpleDateFormat.parse(mchat.getMsgTime());//POST TIME
            Date date2 = simpleDateFormat.parse(getCurrentDate());// USER currentTime

            Log.d("lastDateChat",mchat.getMsgTime());
            Log.d("currentDate",mchat.getMsgTime());

            return printDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "error";
    }

    // get current date, in order to sort the course (up coming courses without showing expired courses)
    private String getCurrentDate() {

        return android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date()).toString();
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

    // Interface Methods
    public interface ChatListAdapterListerner {

        void onChatListClick(String chatOwnerId);

    }
}


