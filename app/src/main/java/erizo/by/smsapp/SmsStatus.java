package erizo.by.smsapp;

/**
 * Created by valera on 22.7.17.
 */

public interface SmsStatus {
    String STATUS_UNDELIVERED = "undelivered";
    String STATUS_DELIVERED = "delivered";
    String STATUS_SENT = "sent";
    String STATUS_UNSENT = "unsent";
    String SET_MESSAGES_STATUS = "setMessageStatus";

    String SMS_PENDING = "110";
    String SMS_SENT = "115";
}
