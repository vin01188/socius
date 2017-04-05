package com.vince.socius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AdminActivity extends AppCompatActivity {

    public Person current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        Intent intent = getIntent();
        String address = intent.getStringExtra("Address");
        String time = intent.getStringExtra("Time");
        String num = intent.getStringExtra("Number");
        String service = intent.getStringExtra("Service");

        TextView addressConfirmation = (TextView) findViewById(R.id.addressDesc);
        addressConfirmation.append(address);
        TextView timeView = (TextView) findViewById(R.id.timeDesc);
        timeView.append(time);
        TextView numView = (TextView) findViewById(R.id.numDesc);
        numView.append(num);
        TextView serviceView = (TextView) findViewById(R.id.serviceDesc);
        serviceView.append(service);

        current = getIntent().getParcelableExtra("PersonT");
        TextView resolveView = (TextView) findViewById(R.id.resolve);
        /*if (current.getIsNotDelete()){
            resolveView.setText("Not Resolved");
        }else{
            resolveView.setText("Resolved");
        }*/
        resolveView.setText(current.getStatus());

        TextView claimView = (TextView) findViewById(R.id.claimText);
        //if it has been claimed, show who claimed it as well
        if (current.getStatus().equals("Pending")){
            claimView.setVisibility(View.VISIBLE);
            claimView.setText("Claimed by: " + current.getClaimer());
        }else{
            claimView.setVisibility(View.GONE);
        }

        Button resolveButton = (Button) findViewById(R.id.editAll);
        Button claimButton  = (Button) findViewById(R.id.claimButton);

        if (current.getStatus().equals("Resolved" )) {
            resolveButton.setVisibility(View.GONE);
            claimButton.setVisibility(View.GONE);
        }

    }

    public void editFinal(View view) {
        Intent goingBack = new Intent();
        goingBack.putExtra("Temp", current);
        goingBack.putExtra("newStatus", "Resolved");
        setResult(RESULT_OK, goingBack);
        finish();
    }

    public void submitFinal(View view) {
        Intent goingBack = new Intent();
        goingBack.putExtra("newStatus",current.getStatus());
        setResult(RESULT_OK, goingBack);
        finish();
    }

    public void claim(View view){
        Intent goingBack = new Intent();
        goingBack.putExtra("newStatus", "Pending");
        setResult(RESULT_OK, goingBack);
        finish();

    }
}
