package erizo.by.smsapp.model;

public class Message {

    private String messageID;
    private String phone;
    private String message;
    private String sendTime;
    private String expTime;

    public Message() {
    }

    public Message(String messageID, String phone, String message, String sendTime, String expTime) {
        this.messageID = messageID;
        this.phone = phone;
        this.message = message;
        this.sendTime = sendTime;
        this.expTime = expTime;
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

    @Override
    public String toString() {
        return "Message{" +
                "messageID='" + messageID + '\'' +
                ", phone='" + phone + '\'' +
                ", message='" + message + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", expTime='" + expTime + '\'' +
                '}';
    }
}
