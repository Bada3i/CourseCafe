package om.sas.coursecafe.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Calendar;

import om.sas.coursecafe.view.model.CoursesModel;

import om.sas.coursecafe.view.model.UserModel;

import static om.sas.coursecafe.view.MyConstants.FIREBASE_REPORT_USER;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_BLOCK;
import static om.sas.coursecafe.view.MyConstants.FIREBASE_USER_STATUS;

public class UtilityClass {

    // Add Methods Here which are used many Times
    public static String getCurrentDate() {

        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);// get current year
        int mMonth = calendar.get(Calendar.MONTH); // get current month, months count in calender starts from zero!
        int mDay = calendar.get(Calendar.DAY_OF_MONTH); // get current day
        return mDay + "/" + (mMonth + 1) + "/" + mYear; //display today's date!
    }


    public static void hideKeyBoard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
