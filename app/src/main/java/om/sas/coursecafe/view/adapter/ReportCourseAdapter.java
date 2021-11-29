package om.sas.coursecafe.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.UserModel;

public class ReportCourseAdapter extends BaseAdapter {

    private ArrayList<CoursesModel> mItemsArrayList;
    private Context mContext;
    private onDelReportCourseClickListener mListener;


    public ReportCourseAdapter(Context context, onDelReportCourseClickListener listener) {
        mContext = context;
        mItemsArrayList = new ArrayList<>();
        mListener = listener;
    }

    public void updateReportCourseArrayList(ArrayList<CoursesModel> newItems) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_post,
                    viewGroup,false);
        }


        CircleImageView ivImage = view.findViewById(R.id.iv_course_img);
        TextView tvCourseTitle = view.findViewById(R.id.tv_course_title);
        TextView tvOwnerEmail = view.findViewById(R.id.tv_owner_email);
        TextView tvReportNum = view.findViewById(R.id.tv_report_num);
        TextView tvReport = view.findViewById(R.id.tv_num);
        Button btnDelete = view.findViewById(R.id.btn_delete);

        tvReportNum.setVisibility(View.VISIBLE);
        tvReport.setVisibility(View.VISIBLE);



        final CoursesModel coursesModel  = (CoursesModel) getItem(i);
        tvCourseTitle.setText(coursesModel.getTitle());
        tvOwnerEmail.setText(coursesModel.getEmail());
        tvReportNum.setText(coursesModel.getReport() + "");
        Glide.with(mContext).load(coursesModel.getImage()).placeholder(R.mipmap.ic_launcher).into(ivImage);

        btnDelete.setText(R.string.delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mListener != null){
                    mListener.OnDelReportCourseClick(i,coursesModel);
                }
            }
        });

        return view;
    }

    public interface onDelReportCourseClickListener {
        void OnDelReportCourseClick(int position, CoursesModel coursesModel);
    }

}
