package com.vince.socius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Confirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        Intent intent = getIntent();
        String address = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView addressConfirmation = (TextView) findViewById(R.id.addressConf);
        addressConfirmation.append(" " + address + "?");
    }

    public void yesMark(View view) {
        Intent goingBack = new Intent();
        goingBack.putExtra("Confirmation", true);
        setResult(RESULT_OK, goingBack);
        finish();
    }

    public void noMark(View view) {
        Intent goingBack = new Intent();
        goingBack.putExtra("Confirmation", false);
        setResult(RESULT_OK,goingBack);
        finish();
    }
}
