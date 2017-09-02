package erizo.by.smsapp.timertasks;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;

import erizo.by.smsapp.SmsStatus;
import erizo.by.smsapp.model.Message;

import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static android.telephony.SmsManager.getDefault;
import static android.telephony.SmsManager.getSmsManagerForSubscriptionId;
import static erizo.by.smsapp.SmsStatus.SMS_PENDING;

public class SendSmsFromPhoneTimerTask extends TimerTask {

    private static final String TAG = SendSmsFromPhoneTimerTask.class.getSimpleName();

    private ArrayList<PendingIntent> sentIntents = new ArrayList<>();
    private ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();

    private Queue<Message> messages;
    private SmsManager smsManager;
    private PendingIntent sentPi, deliverPi;
    private Context context;
    private Intent sentIntent;
    private Intent deliverIntent;
    private Integer systemErrorCounter;

    public SendSmsFromPhoneTimerTask(Queue<Message> smsList,
                                     int simSlot,
                                     PendingIntent sentPi,
                                     PendingIntent deliverPi,
                                     Context context,
                                     Intent sentIntent,
                                     Intent deliverIntent,
                                     Integer systemErrorCounter) {
        this.messages = smsList;
        this.sentPi = sentPi;
        this.deliverPi = deliverPi;
        this.context = context;
        this.sentIntent = sentIntent;
        this.deliverIntent = deliverIntent;
        this.systemErrorCounter = systemErrorCounter;
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
                    if (message.getMessage().length() > 60) {
                        ArrayList<String> parts = smsManager.divideMessage(message.getMessage());
                        int numParts = parts.size();
                        for (int i = 0; i < numParts; i++) {
                            sentIntents.add(PendingIntent.getBroadcast(context, 0, sentIntent, 0));
                            deliveryIntents.add(PendingIntent.getBroadcast(context, 0, deliverIntent, 0));
                        }
                        smsManager.sendMultipartTextMessage(
                                message.getPhone(),
                                null,
                                parts,
                                sentIntents,
                                deliveryIntents);

                    } else {
                        smsManager.sendTextMessage(message.getPhone(),
                                null,
                                message.getMessage(),
                                sentPi,
                                deliverPi);
                    }
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