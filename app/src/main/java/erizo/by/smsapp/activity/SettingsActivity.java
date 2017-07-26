package erizo.by.smsapp.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.Button;

import erizo.by.smsapp.App;
import erizo.by.smsapp.R;

public class SettingsActivity extends Activity {

    Button simOne, simTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setiings_activity);
        simOne = (Button) findViewById(R.id.sim_one_button);
        simTwo = (Button) findViewById(R.id.sim_two_button);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (SubscriptionManager.from(this).getActiveSubscriptionInfoList().size() > 1) {
                simOne.setEnabled(true);
                simTwo.setEnabled(true);
            } else {
                simOne.setEnabled(true);
                simTwo.setEnabled(false);
                App.secondSimSettings.put("status", "false");
                simTwo.setBackgroundColor(Color.GRAY);
            }
        } else {
            simOne.setEnabled(true);
            simTwo.setEnabled(false);
            App.secondSimSettings.put("status", "false");
            simTwo.setBackgroundColor(Color.GRAY);
        }

        simOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsFirstSim.class);
                startActivity(intent);
            }
        });

        simTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsSecondSim.class);
                startActivity(intent);
            }
        });
    }

}
