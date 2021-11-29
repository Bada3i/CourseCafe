package om.sas.coursecafe.view.model;

import java.io.Serializable;
import java.util.ArrayList;

public class NotificationContainer implements Serializable {

    public NotificationContainer() {
    }

    ArrayList<NotificationModel> myNotificationList = new ArrayList<>();

    public ArrayList<NotificationModel> getMyNotificationList() {
        return myNotificationList;
    }

    public void setMyNotificationList(ArrayList<NotificationModel> myNotificationList) {
        this.myNotificationList = myNotificationList;
    }
}
