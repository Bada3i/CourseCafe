package om.sas.coursecafe.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_REPORT_USER;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_BLOCK;

public class ReportUserAdapter extends BaseAdapter {

    private ArrayList<UserModel> mItemsArrayList;
    private final Context mContext;
    private onBlockReportUserClickListener mListener;


    public ReportUserAdapter(Context context, onBlockReportUserClickListener listener) {
        mContext = context;
        mItemsArrayList = new ArrayList<>();
        mListener = listener;
    }

    public void updateReportUserArrayList(ArrayList<UserModel> newItems) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_user,
                    viewGroup,false);
        }

        TextView tvOwnerName = view.findViewById(R.id.tv_owner_name);
        TextView tvOwnerEmail = view.findViewById(R.id.tv_owner_email);
        TextView tvReportNum = view.findViewById(R.id.tv_report_num);
        TextView tvReport = view.findViewById(R.id.tv_num);
        Button btnBlockUser = view.findViewById(R.id.btn_accept_user);

        tvReportNum.setVisibility(View.VISIBLE);
        tvReport.setVisibility(View.VISIBLE);



        final UserModel userModel = (UserModel) getItem(i);
        tvOwnerName.setText(userModel.getFullName());
        tvOwnerEmail.setText(userModel.getEmail());
        tvReportNum.setText(userModel.getReportUser() + "");

        btnBlockUser.setText(R.string.block_user);
        btnBlockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mListener != null){
                    mListener.OnBlockReportUserClick(userModel,i);
                }
            }
        });

        return view;
    }

    public interface onBlockReportUserClickListener {
        void OnBlockReportUserClick(UserModel userModel, int position);
    }

}
