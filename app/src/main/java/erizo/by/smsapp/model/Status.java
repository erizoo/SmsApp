package erizo.by.smsapp.model;

public class Status {

    private String messageID;
    private String status;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Status{" +
                "messageID='" + messageID + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
