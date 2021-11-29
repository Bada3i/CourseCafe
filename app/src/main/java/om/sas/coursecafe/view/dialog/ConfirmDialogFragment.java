package om.sas.coursecafe.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import om.sas.coursecafe.view.model.CoursesModel;
import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.KEY_USER;

public class ConfirmDialogFragment extends DialogFragment {

    private static final String TEXT_MESSAGE = "text_message";
    private static final String TEXT_TITLE = "text_title";
    private static final String TEXT_POSITION = "text_position";
    private static final String KEY_COURSE = "course";

    private String textMessage, textTitle;
    private UserModel mUserModel;
    private int mPosition;
    private CoursesModel mCoursesModel;

    public interface ConfirmDialogFragmentListener {
        void onYesButtonClick(UserModel userModel,int position,CoursesModel coursesModel);
    }

    public static ConfirmDialogFragment newInstance(String title, String message, UserModel userModel,
                                                    int position, CoursesModel coursesModel) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putString(TEXT_MESSAGE, message);
        args.putString(TEXT_TITLE, title);
        args.putSerializable(KEY_USER, userModel);
        args.putInt(TEXT_POSITION, position);
        args.putSerializable(KEY_COURSE, coursesModel);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            textMessage = getArguments().getString(TEXT_MESSAGE);
            textTitle = getArguments().getString(TEXT_TITLE);
            mUserModel = (UserModel) getArguments().getSerializable(KEY_USER);
            mPosition = getArguments().getInt(TEXT_POSITION);
            mCoursesModel = (CoursesModel) getArguments().getSerializable(KEY_COURSE);
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        alertDialogBuilder.setTitle(textTitle);
        alertDialogBuilder.setMessage(textMessage);
        alertDialogBuilder.setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ConfirmDialogFragmentListener listener = (ConfirmDialogFragmentListener) getTargetFragment();
                if (listener != null) {
                    listener.onYesButtonClick(mUserModel,mPosition,mCoursesModel);
                }
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });

        return alertDialogBuilder.create();
    }
}
