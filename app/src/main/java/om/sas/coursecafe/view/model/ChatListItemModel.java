package om.sas.coursecafe.view.model;

import java.io.Serializable;

public class ChatListItemModel implements Serializable {
    private String chatOwner;
    private String sender;
    private String chatHistory;
    private String lastMessage;
    private String msgTime;
    private String nodeIDRef;




    public String getChatOwner() {
        return chatOwner;
    }

    public void setChatOwner(String chatOwner) {
        this.chatOwner = chatOwner;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(String chatHistory) {
        this.chatHistory = chatHistory;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getNodeIDRef() {
        return nodeIDRef;
    }

    public void setNodeIDRef(String nodeIDRef) {
        this.nodeIDRef = nodeIDRef;
    }

    }


