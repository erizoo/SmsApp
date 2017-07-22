package erizo.by.smsapp;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TimerTask;

import erizo.by.smsapp.model.Message;

import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;

/**
 * Created by valera on 22.7.17.
 */

public class SendSmsFromPhoneTimerTask extends TimerTask {

    private Queue<Message> smsList;
    private Map<String, String> simSettings;
    private SmsManager smsManager;
    private PendingIntent sentPi, deliverPi;
    private Context context;

    public SendSmsFromPhoneTimerTask(Queue<Message> smsList,
                                     Map<String, String> simSettings,
                                     PendingIntent sentPi,
                                     PendingIntent deliverPi,
                                     Context context) {
        this.smsList = smsList;
        this.simSettings = simSettings;
        this.sentPi = sentPi;
        this.deliverPi = deliverPi;
        this.context = context;
        if (Build.VERSION.SDK_INT <= LOLLIPOP_MR1) {
            smsManager = SmsManager.getDefault();
        } else {
            smsManager = getSmsManager(Integer.valueOf(simSettings.get("sim_slot")));
        }
    }

    @Override
    public void run() {
        if (!smsList.isEmpty()) {
            for (Message message : smsList) {
                smsManager.sendTextMessage(message.getPhone(), null, message.getMessage(), sentPi, deliverPi);
                message.setStatus("110");
            }
        }
    }

    @TargetApi(LOLLIPOP_MR1)
    private List<Integer> getSimList() {
        final ArrayList<Integer> simCardList = new ArrayList<>();
        SubscriptionManager subscriptionManager;
        subscriptionManager = SubscriptionManager.from(context);
        final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager
                .getActiveSubscriptionInfoList();
        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            simCardList.add(subscriptionId);
        }
        return simCardList;
    }

    @TargetApi(LOLLIPOP_MR1)
    private SmsManager getSmsManager(int id) {
        return SmsManager.getSmsManagerForSubscriptionId(id);
    }
}
