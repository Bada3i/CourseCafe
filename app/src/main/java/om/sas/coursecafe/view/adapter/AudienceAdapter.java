package om.sas.coursecafe.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.UserModel;

public class AudienceAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserModel> mItemsArrayList;
    private TextView tvAudStatus;

    public AudienceAdapter (Context context) {
        mContext = context;
        mItemsArrayList = new ArrayList<>();
    }

    public void updateAudienceArrayList(ArrayList<UserModel> newItems) {
        mItemsArrayList = newItems;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mItemsArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return mItemsArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View itemView, ViewGroup viewGroup) {

        if (itemView==null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.audience_list_item, viewGroup, false);
        }

        TextView tvName = itemView.findViewById(R.id.tv_audience_name);
        ImageView ivImage = itemView.findViewById(R.id.img_audiance_image);
        TextView tvMessage = itemView.findViewById(R.id.tv_audience_contact_info);
        TextView tvDate = itemView.findViewById(R.id.tv_date_audiance);

        UserModel audienceObj = (UserModel) getItem(i);
        tvName.setText(audienceObj.getFullName());
        tvMessage.setText(audienceObj.getEmail());
        tvDate.setText(audienceObj.getCourseRegisterDate());
        Glide.with(mContext).load(audienceObj.getProfilePic()).placeholder(R.mipmap.ic_launcher).into(ivImage);

        tvAudStatus = itemView.findViewById(R.id.tv_audience_status);


        if(audienceObj.getKeyAudience()== MyConstants.KEY_ACCEPT){
            tvAudStatus.setText("Accept");
            tvAudStatus.setBackgroundResource(R.drawable.btn_shape_blue);
            tvAudStatus.setTextColor(R.color.blue);
        }else if (audienceObj.getKeyAudience()== MyConstants.KEY_REGECT){
            tvAudStatus.setText("Reject");
            tvAudStatus.setBackgroundResource(R.drawable.btn_shape_red);
            tvAudStatus.setTextColor(R.color.black);
        }else if(audienceObj.getKeyAudience()== MyConstants.KEY_PAID){
            tvAudStatus.setText("PAID");
            tvAudStatus.setBackgroundResource(R.drawable.btn_shape_green);
            tvAudStatus.setTextColor(R.color.greenDark);
        }else {
            tvAudStatus.setText("Waiting ...");
            tvAudStatus.setBackgroundResource(R.drawable.btn_shapes);
            tvAudStatus.setTextColor(R.color.darkish_pink);
        }


        return itemView;
    }
}
