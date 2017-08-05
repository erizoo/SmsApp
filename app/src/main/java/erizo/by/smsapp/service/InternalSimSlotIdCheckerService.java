package erizo.by.smsapp.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import erizo.by.smsapp.model.Message;

import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static erizo.by.smsapp.App.firstSimSettings;
import static erizo.by.smsapp.App.secondSimSettings;

/**
 * Created by Erizo on 05.08.2017.
 */

public class InternalSimSlotIdCheckerService implements Runnable {

    private static final String TAG = InternalSimSlotIdCheckerService.class.getSimpleName();

    private Message message = new Message();
    private String simSlot;
    private Context context;

    public InternalSimSlotIdCheckerService(String simSlot, Context context) {
        this.simSlot = simSlot;
        this.context = context;
    }

    @TargetApi(LOLLIPOP_MR1)
    @Override
    public void run() {
        SmsManager smsManager;
        if (firstSimSettings.get("simSlot").equals(simSlot)) {
            smsManager = SmsManager.getSmsManagerForSubscriptionId(Integer.valueOf(
                    firstSimSettings.get("android_sim_slot")));
            message.setMessage("First sim slot");
            message.setPhone(firstSimSettings.get("simId"));
        } else {
            smsManager = SmsManager.getSmsManagerForSubscriptionId(Integer.valueOf(
                    secondSimSettings.get("android_sim_slot")));
            message.setMessage("Second sim slot");
            message.setPhone(firstSimSettings.get("simId"));
        }
        smsManager.sendTextMessage(message.getPhone(), null, message.getMessage(), null, null);
        new Thread(new IncomeSmsChecker(context)).start();

    }

    private class IncomeSmsChecker implements Runnable {

        private boolean running = true;
        private Context context;

        IncomeSmsChecker(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            while (running) {
                Uri uriSMSURI = Uri.parse("content://sms/inbox");
                Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);

                if (cur != null && cur.moveToFirst()) {
                    do {
                        String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                        if (body.equals("First sim slot")) {
                            String simId = cur.getString(cur.getColumnIndexOrThrow("sub_id"));
                            Log.d(TAG, "SimId from cursor => " + cur.getString(cur.getColumnIndex("sub_id")));
                            firstSimSettings.put("internalSimId", simId);
                            running = false;
                            break;
                        } else if (body.equals("Second sim slot")) {
                            String simId = cur.getString(cur.getColumnIndexOrThrow("sub_id"));
                            Log.d(TAG, "SimId from cursor => " + cur.getString(cur.getColumnIndex("sub_id")));
                            secondSimSettings.put("internalSimId", simId);
                            running = false;
                            break;
                        }
                    } while (cur.moveToNext());
                }
            }
        }
    }
}
