package om.sas.coursecafe.view.model;

import java.io.Serializable;

public class AppInfoModel implements Serializable {

    private String aboutUsInfo;
    private String ContactUsInfo;
    private String adminName;
    private String adminEmail;

    public String getAboutUsInfo() {
        return aboutUsInfo;
    }

    public void setAboutUsInfo(String aboutUsInfo) {
        this.aboutUsInfo = aboutUsInfo;
    }

    public String getContactUsInfo() {
        return ContactUsInfo;
    }

    public void setContactUsInfo(String contactUsInfo) {
        ContactUsInfo = contactUsInfo;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
}
