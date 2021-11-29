package om.sas.coursecafe.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.model.Chat;
import om.sas.coursecafe.view.model.UserModel;

public class MassageAdapter extends RecyclerView.Adapter<MassageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    private Context mContext;
    private ArrayList<Chat> mChat;
    private String imageurl;

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private UserModel userModel ;

    public MassageAdapter(Context context, String imageurl) {
        mChat= new ArrayList<>();
        mContext = context;
        this.imageurl = imageurl;
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

    }

    public void updateMassageAdapter(ArrayList<Chat> mChat) {
        this.mChat = mChat;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MassageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MassageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView show_message;
        ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
           // profile_image = itemView.findViewById(R.id.image_profile1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;

        } else {
            return MSG_TYPE_LEFT;
        }
    }
}


