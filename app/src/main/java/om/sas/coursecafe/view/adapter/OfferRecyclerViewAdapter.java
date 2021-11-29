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

public class OfferRecyclerViewAdapter extends RecyclerView.Adapter<OfferRecyclerViewAdapter.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(CoursesModel item);
    }

    private Context mContext;
    private ArrayList<CoursesModel> mItemsArrayList;
    private OnItemClickListener listener;

    public OfferRecyclerViewAdapter(Context context, ArrayList<CoursesModel> mItemsArrayList, OnItemClickListener listener) {
        mContext = context;
        this.mItemsArrayList = mItemsArrayList;
        this.listener = listener;
    }

    public void updateOfferArrayList(ArrayList<CoursesModel> newItems) {
        mItemsArrayList = newItems;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_offer_story, parent, false);
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

        CircleImageView crImgViewProfile ;
        TextView tvCourseName;

        ViewHolder(View itemView) {
            super(itemView);
            crImgViewProfile = itemView.findViewById(R.id.iv_img);
            tvCourseName = itemView.findViewById(R.id.tv_offer_course_name);

        }
        void bind(final CoursesModel item, final OnItemClickListener listener) {
            tvCourseName.setText(item.getTitle());
            Glide.with(mContext)
                    .load(item.getImage())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(crImgViewProfile);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}
