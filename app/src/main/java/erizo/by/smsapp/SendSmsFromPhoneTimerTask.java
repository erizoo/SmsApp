package erizo.by.smsapp;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import java.util.List;
import java.util.Queue;
import java.util.TimerTask;

import erizo.by.smsapp.model.Message;

import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static android.telephony.SmsManager.getDefault;
import static android.telephony.SmsManager.getSmsManagerForSubscriptionId;

public class SendSmsFromPhoneTimerTask extends TimerTask implements SmsStatus {

    private static final String TAG = SendSmsFromPhoneTimerTask.class.getSimpleName();

    private Queue<Message> messages;
    private SmsManager smsManager;
    private PendingIntent sentPi, deliverPi;
    private Context context;

    public SendSmsFromPhoneTimerTask(Queue<Message> smsList,
                                     int simSlot,
                                     PendingIntent sentPi,
                                     PendingIntent deliverPi,
                                     Context context) {
        this.messages = smsList;
        this.sentPi = sentPi;
        this.deliverPi = deliverPi;
        this.context = context;
        if (Build.VERSION.SDK_INT <= LOLLIPOP_MR1) {
            smsManager = SmsManager.getDefault();
        } else {
            smsManager = getSmsManager(simSlot);
        }
    }

    @Override
    public void run() {
        if (!messages.isEmpty()) {
            for (Message message : messages) {
                if (message.getStatus() == null) {
                    smsManager.sendTextMessage(message.getPhone(),
                            null,
                            message.getMessage(),
                            sentPi,
                            deliverPi);
                    Log.d(TAG, "sms sent " + message);
                    message.setStatus(SMS_PENDING);
                    Log.d(TAG, "changed status to 110 : " + message);
                }
            }
        }
    }

    @TargetApi(LOLLIPOP_MR1)
    private SmsManager getSmsManager(int simSlot) {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager
                .getActiveSubscriptionInfoList();
        try {
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                if (subscriptionInfo.getSimSlotIndex() == simSlot) {
                    return getSmsManagerForSubscriptionId(subscriptionInfo.getSubscriptionId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDefault();
    }
}
