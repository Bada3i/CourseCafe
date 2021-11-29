package om.sas.coursecafe.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import om.sas.coursecafe.R;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.NotificationModel;

public class NormalProfileRecyclerViewAdapter extends RecyclerView.Adapter<NormalProfileRecyclerViewAdapter.ViewHolder>{

    public interface OnItemProfileClickListener {
        void onItemClick(NotificationModel item);
    }

    private Context mContext;
    private ArrayList<NotificationModel> mItemsArrayList;
    private OnItemProfileClickListener listener;


    public NormalProfileRecyclerViewAdapter(Context context, ArrayList<NotificationModel> mItemsArrayList, OnItemProfileClickListener listener) {
        mContext = context;
        this.mItemsArrayList = mItemsArrayList;
        this.listener = listener;

    }

    public void updateNormalCourseArrayList(ArrayList<NotificationModel> newItems) {
        mItemsArrayList = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_profile, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mItemsArrayList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return (mItemsArrayList == null) ? 0 : mItemsArrayList.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView ivImg;
        TextView tvDate,tvCourseTitle;


        ViewHolder(View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.iv_img);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCourseTitle = itemView.findViewById(R.id.tv_course_title);
        }
        void bind(final NotificationModel item, final OnItemProfileClickListener listener) {
            tvCourseTitle.setText(item.getSenderPostTitle());
            tvDate.setText(item.getNotificationDate());
            Glide.with(mContext)
                    .load(item.getNotificationImage())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(ivImg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
