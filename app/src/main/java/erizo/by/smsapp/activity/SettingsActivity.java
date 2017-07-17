package erizo.by.smsapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import erizo.by.smsapp.R;

public class SettingsActivity  extends AppCompatActivity {

    private Button simOne, simTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setiings_activity);

        simOne = (Button) findViewById(R.id.sim_one_button);
        simTwo = (Button) findViewById(R.id.sim_two_button);
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
