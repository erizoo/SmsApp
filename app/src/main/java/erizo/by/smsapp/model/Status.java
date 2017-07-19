package erizo.by.smsapp.model;

/**
 * Created by Erizo on 16.07.2017.
 */

public class Status {

    private String messageID;
    private String status;

    public Status() {
    }

    public Status(String messageID, String status) {
        this.messageID = messageID;
        this.status = status;
    }

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
