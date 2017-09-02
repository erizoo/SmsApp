package erizo.by.smsapp.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import erizo.by.smsapp.model.Mail;

/**
 * Created by valera on 2.9.17.
 */

public class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final String TAG = SendEmailAsyncTask.class.getSimpleName();

    private Mail mail;

    public SendEmailAsyncTask(Mail mail) {
        this.mail = mail;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            mail.send();
            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(TAG, "Bad account details");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            Log.e(TAG, "Email failed");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
