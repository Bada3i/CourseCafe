package om.sas.coursecafe.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.dialog.ConfirmDialogFragment;
import om.sas.coursecafe.view.fragments.PendingUserFragment;
import om.sas.coursecafe.view.model.UserModel;

public class PendingOwnerAdapter extends BaseAdapter {

    private onButtonPendingUserClickListener mListener;
    private ArrayList<UserModel> mItemsArrayList;
    private final Context mContext;


    public PendingOwnerAdapter(Context context, onButtonPendingUserClickListener listener) {
        mContext = context;
        mItemsArrayList = new ArrayList<>();
        mListener = listener;
    }

    public void updatePendingUserArrayList(ArrayList<UserModel> newItems) {
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
        Button btnAcceptUser = view.findViewById(R.id.btn_accept_user);
        btnAcceptUser.setText(R.string.accept);

        final UserModel userModel = (UserModel) getItem(i);
        tvOwnerName.setText(userModel.getFullName());
        tvOwnerEmail.setText(userModel.getEmail());

        btnAcceptUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OnButtonPendingUserClick(userModel,i);
                }
            }
        });

        return view;
    }

    public interface onButtonPendingUserClickListener {
        void OnButtonPendingUserClick(UserModel userModel, int position);
    }

}
