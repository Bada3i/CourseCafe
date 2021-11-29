package om.sas.coursecafe.view.model;

public class NotificationModel {


    private String firebaseId;
    private String notificationSender;
    private String notificationSenderId;
    private String notificationReciver;
    private String notification;
    private String notificationDate;
    private String notificationImage;
    private String senderPostTitle;
    private String notificationFromPostId;
    private int keyNotification;
    private String courseStartDate;


    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getNotificationSender() {
        return notificationSender;
    }

    public void setNotificationSender(String notificationSender) {
        this.notificationSender = notificationSender;
    }

    public String getNotificationSenderId() {
        return notificationSenderId;
    }

    public void setNotificationSenderId(String notificationSenderId) {
        this.notificationSenderId = notificationSenderId;
    }

    public String getNotificationReciver() {
        return notificationReciver;
    }

    public void setNotificationReciver(String notificationReciver) {
        this.notificationReciver = notificationReciver;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }

    public String getSenderPostTitle() {
        return senderPostTitle;
    }

    public void setSenderPostTitle(String senderPostTitle) {
        this.senderPostTitle = senderPostTitle;
    }

    public String getNotificationFromPostId() {
        return notificationFromPostId;
    }

    public void setNotificationFromPostId(String notificationFromPostId) {
        this.notificationFromPostId = notificationFromPostId;
    }

    public int getKeyNotification() {
        return keyNotification;
    }

    public void setKeyNotification(int keyNotification) {
        this.keyNotification = keyNotification;
    }

    public String getCourseStartDate() {
        return courseStartDate;
    }

    public void setCourseStartDate(String courseStartDate) {
        this.courseStartDate = courseStartDate;
    }
}
