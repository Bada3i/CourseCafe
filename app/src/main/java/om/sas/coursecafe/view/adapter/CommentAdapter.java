package om.sas.coursecafe.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.activity.MainActivity;
import om.sas.coursecafe.view.activity.MainAppActivity;
import om.sas.coursecafe.view.fragments.CommentsFragment;
import om.sas.coursecafe.view.fragments.ProfileFragment;
import om.sas.coursecafe.view.model.Comment;

import om.sas.coursecafe.view.model.UserModel;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mComment;
    private FirebaseUser firebaseUser;
    private String postid;
    private CommentListAdapterListerner mListener;


    public CommentAdapter(Context mContext, List<Comment> mComment,String postid) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postid = postid;
        mListener = (CommentListAdapterListerner) mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(position);


        holder.comment.setText(comment.getComment());

        getUserInfo(holder.image_profile, holder.username, comment.getPublisher());



        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onProfileImageClick(comment.getPublisher());
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (comment.getPublisher().equals(firebaseUser.getUid())){
                    AlertDialog alertDialog =new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to to delete ?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yas", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_COMMENTS).child(postid).
                                    child(comment.getCommentid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(mContext,"Deleted!",Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.dismiss();
                        }
                    });


                    alertDialog.show();
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);


        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisher) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_USERS).child(publisher);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Glide.with(mContext).load(userModel.getProfilePic()).into(imageView);
                username.setText(userModel.getFullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // Interface Methods
    public interface CommentListAdapterListerner {


        void onProfileImageClick(String userModelId);
        
    }
}
