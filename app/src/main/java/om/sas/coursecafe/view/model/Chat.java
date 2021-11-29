package om.sas.coursecafe.view.model;

import java.io.Serializable;

public class Chat  implements Serializable {
    private String sender;
    private String chatOwner;
    private String message;
    private String time;




    public Chat(String sender, String ChattingOwner, String message , String time , String status) {
        this.sender = sender;
        this.chatOwner = ChattingOwner;
        this.message = message;
        this.time = time;

    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getChatOwner() {
        return chatOwner;
    }

    public void setChatOwner(String chatOwner) {
        this.chatOwner = chatOwner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}