package erizo.by.smsapp.model;

public class Message {

    private String messageID;
    private String phone;
    private String message;
    private String sendTime;
    private String expTime;
    private String simID;
    private String status;
    private String internalSimIds;

    public Message() {
    }

    public Message(String phone, String message, String internalSimIds) {
        this.message = message;
        this.phone = phone;
        this.internalSimIds = internalSimIds;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getExpTime() {
        return expTime;
    }

    public void setExpTime(String expTime) {
        this.expTime = expTime;
    }

    public String getSimID() {
        return simID;
    }

    public void setSimID(String simID) {
        this.simID = simID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInternalSimIds() {
        return internalSimIds;
    }

    public void setInternalSimIds(String internalSimIds) {
        this.internalSimIds = internalSimIds;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageID='" + messageID + '\'' +
                ", phone='" + phone + '\'' +
                ", message='" + message + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", expTime='" + expTime + '\'' +
                ", simID='" + simID + '\'' +
                ", status='" + status + '\'' +
                ", internalSimIds='" + internalSimIds + '\'' +
                '}';
    }
}
