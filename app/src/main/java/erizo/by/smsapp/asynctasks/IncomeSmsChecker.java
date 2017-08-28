package erizo.by.smsapp.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Queue;

import erizo.by.smsapp.activity.MainActivity;
import erizo.by.smsapp.model.Message;

import static erizo.by.smsapp.App.firstSimSettings;
import static erizo.by.smsapp.App.secondSimSettings;
import static erizo.by.smsapp.activity.MainActivity.logService;

/**
 * Created by valera on 28.8.17.
 */

public class IncomeSmsChecker extends AsyncTask<String, Void, Void> {

    private final String TAG = IncomeSmsChecker.class.getSimpleName();

    private Context mContext;
    private Queue<Message> incomeMessages;

    public IncomeSmsChecker(Context mContext, Queue<Message> incomeMessages) {
        this.mContext = mContext;
        this.incomeMessages = incomeMessages;
    }

    @Override
    protected Void doInBackground(String... messageData) {
        Log.d(TAG, "start checking income sms");
        logService.appendLog(TAG + " " + "start checking income sms");
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = mContext.getContentResolver().query(uriSMSURI, null, null, null, null);
        if (cur != null) {
            while (cur.moveToNext()) {
                String address = cur.getString(cur.getColumnIndex("address"));
                String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                String simId = cur.getString(cur.getColumnIndexOrThrow("sub_id"));
                Log.d(TAG, "Body from cursor => " + cur.getString(cur.getColumnIndex("body")));
                Log.d(TAG, "SimId from cursor => " + cur.getString(cur.getColumnIndex("sub_id")));
                Log.d(TAG, "found sms : " + address + " " + body + " " + simId);
                logService.appendLog(TAG + " : " + address + " " + body + " " + simId);
                Log.d(TAG, "Address from cursor => " + cur.getString(cur.getColumnIndex("address")));
                if (isMessagesMatch(address, body, messageData[0], messageData[1])) {
                    String settingsSimId;
                    if (simId.equals(firstSimSettings.get("android_sim_slot"))) {
                        settingsSimId = firstSimSettings.get("simId");
                    } else {
                        settingsSimId = secondSimSettings.get("simId");
                    }
                    Message message = new Message(address, body, settingsSimId);
                    incomeMessages.add(message);
                    Log.d(TAG, "find message " + message);
                    logService.appendLog(TAG + " : " + "find message " + message);
                    break;
                    // TODO: 23.8.17 add income sms removing
                }
            }
            cur.close();
        }
        return null;
    }

    private boolean isMessagesMatch(String currentMessageAddress, String currentMessageBody, String incomeMessageAddress, String incomeMessageBody) {
        return (currentMessageAddress.equals(incomeMessageAddress) && currentMessageBody.equals(incomeMessageBody));
    }

}
