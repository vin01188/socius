package com.vince.socius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Summary extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        Intent intent = getIntent();
        String address = intent.getStringExtra(Confirmation.EXTRA_ADDRESS);
        String time = intent.getStringExtra(Confirmation.EXTRA_TIME);
        String num = intent.getStringExtra(Confirmation.EXTRA_NUMBER);
        String service = intent.getStringExtra(Confirmation.EXTRA_SERVICE);

        TextView addressConfirmation = (TextView) findViewById(R.id.addressDesc);
        addressConfirmation.append(address);
        TextView timeView = (TextView)findViewById(R.id.timeDesc);
        timeView.append(time);
        TextView numView = (TextView) findViewById(R.id.numDesc);
        numView.append(num);
        TextView serviceView = (TextView) findViewById(R.id.serviceDesc);
        serviceView.append(service);


    }

    public void editFinal(View view) {
        Intent goingBack = new Intent();
        goingBack.putExtra("Confirmation", false);
        setResult(RESULT_OK, goingBack);
        finish();
    }

    public void submitFinal(View view){
        Intent goingBack = new Intent();
        goingBack.putExtra("Confirmation", true);
        setResult(RESULT_OK, goingBack);
        finish();
    }
}
