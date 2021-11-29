package om.sas.coursecafe.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.NotificationModel;

public class NotificationListAdapter extends BaseAdapter {



    private Context mContext;
    private ArrayList<NotificationModel> mItemsArrayList;
    private NotificationAdapterInterfaceListener mListener;


    public NotificationListAdapter(Context context) {
        mContext = context;
        mItemsArrayList = new ArrayList<>();
        mListener= (NotificationAdapterInterfaceListener) context;

    }


    public void updateNotificationArrayList(ArrayList<NotificationModel> newItems) {
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

    @Override
    public View getView(final int i, View itemView, ViewGroup viewGroup) {


        if (itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.notification_list_item, viewGroup, false);
        }

        TextView tvName =itemView.findViewById(R.id.tv_user_name_notification);
        ImageView ivImage=itemView.findViewById(R.id.img_user_image_notification);
        TextView tvMessage=itemView.findViewById(R.id.tv_msg_notification);
        TextView tvDate=itemView.findViewById(R.id.tv_date_notification);

        TextView tvPaypalLink = itemView.findViewById(R.id.tv_payment_link);

        final NotificationModel mNotificationModel = (NotificationModel) getItem(i);
        tvName.setText(mNotificationModel.getSenderPostTitle());
        tvMessage.setText(mNotificationModel.getNotification());
        tvDate.setText(mNotificationModel.getNotificationDate());
        Glide.with(mContext).load(mNotificationModel.getNotificationImage()).placeholder(R.mipmap.ic_launcher).into(ivImage);


        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickNotificationImage(mNotificationModel.getNotificationFromPostId());
            }
        });


        // TODO: there should be condition, if the User is Accepted, then the Link is clickable
        if(mNotificationModel.getKeyNotification()== MyConstants.KEY_ACCEPT){
            tvPaypalLink.setVisibility(View.VISIBLE);
            tvPaypalLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OpenPaypalPaymentFragment(mNotificationModel.getNotificationFromPostId());

                    Toast.makeText(mContext,"Course ID from Notification to Payment is: "+mNotificationModel.getNotificationFromPostId() , Toast.LENGTH_SHORT).show();
                }
            }
        }); }else {
            tvPaypalLink.setVisibility(View.INVISIBLE);
        }
        return itemView;
    }





    // Interface Methods
    public interface NotificationAdapterInterfaceListener {

        void OpenPaypalPaymentFragment(String courseId);
        void onClickNotificationImage(String notificationFromPostId);
    }
}
