package om.sas.coursecafe.view.model;

import java.io.Serializable;

public class UserModel implements Serializable {

    private static UserModel instance;

    private String fullName;
    private String email;
    private String firebaseId;
    private String profilePic;
    private String description;
    private String phoneNum;
    private String usertype;
    private String courseRegisterDate;
    private int keyAudience;
    private NotificationContainer myNotification;
    private String userStatus;
    private int reportUser;

    public UserModel() {
    }

    public static synchronized UserModel getInstance() {
        if (instance == null) {
            synchronized (UserModel.class) {
                if (instance == null) {
                    instance = new UserModel();
                }
            }
        }
        return instance;
    }

    public void clearUserDetails() {
        this.fullName = null;
        this.email = null;
        this.firebaseId = null;
        this.profilePic = null;
        this.description = null;
        this.phoneNum = null;
        this.userStatus = null;
        this.courseRegisterDate = null;
        this.keyAudience = 0;
        this.reportUser = 0;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
*/
    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getCourseRegisterDate() {
        return courseRegisterDate;
    }

    public void setCourseRegisterDate(String courseRegisterDate) {
        this.courseRegisterDate = courseRegisterDate;
    }


    public int getKeyAudience() {
        return keyAudience;
    }

    public void setKeyAudience(int keyAudience) {
        this.keyAudience = keyAudience;
    }

    public NotificationContainer getMyNotification() {
        return myNotification;
    }

    public void setMyNotification(NotificationContainer myNotification) {
        this.myNotification = myNotification;
    }


    public int getReportUser() {
        return reportUser;
    }

    public void setReportUser(int reportUser) {
        this.reportUser = reportUser;
    }
}
