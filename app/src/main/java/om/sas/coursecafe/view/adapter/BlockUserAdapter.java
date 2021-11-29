package om.sas.coursecafe.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.dialog.ConfirmDialogFragment;
import om.sas.coursecafe.view.fragments.ProfileFragment;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_NOT_PENDING;

public class BlockUserAdapter extends BaseAdapter{

    private ArrayList<UserModel> mItemsArrayList;
    private final Context mContext;
    private onUnBlockClickListener mListener;



    public BlockUserAdapter(Context context, onUnBlockClickListener listener) {
        mContext = context;
        mItemsArrayList = new ArrayList<>();
        mListener = listener;
    }

    public void updateBlockUserArrayList(ArrayList<UserModel> newItems) {
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
        Button btnUnblockUser = view.findViewById(R.id.btn_accept_user);
        btnUnblockUser.setText(R.string.unblock);

        final UserModel userModel = (UserModel) getItem(i);
        tvOwnerName.setText(userModel.getFullName());
        tvOwnerEmail.setText(userModel.getEmail());

        btnUnblockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener != null) {
                    mListener.OnUnBlockClick(userModel,i);
                }
            }
        });

        return view;
    }


    public interface onUnBlockClickListener {
        void OnUnBlockClick(UserModel userModel, int position);
    }
}
