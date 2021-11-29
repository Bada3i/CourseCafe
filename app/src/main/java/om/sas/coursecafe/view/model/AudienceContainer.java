package om.sas.coursecafe.view.model;

import java.io.Serializable;
import java.util.ArrayList;

public class AudienceContainer implements Serializable {


    public AudienceContainer() {
    }

    ArrayList<UserModel> myAudianceList = new ArrayList<>();

    public ArrayList<UserModel> getMyAudianceList() {
        return myAudianceList;
    }

    public void setMyAudianceList(ArrayList<UserModel> myAudianceList) {
        this.myAudianceList = myAudianceList;
    }
}
