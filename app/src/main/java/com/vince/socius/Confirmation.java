package com.vince.socius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Confirmation extends AppCompatActivity {

    EditText minutes;
    EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        minutes = (EditText) findViewById(R.id.minutes);
        description = (EditText) findViewById(R.id.description);

        Intent intent = getIntent();
        String address = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView addressConfirmation = (TextView) findViewById(R.id.addressConf);
        addressConfirmation.append(" " + address + "?" + " And how many minutes ago were you at this location?");
    }

    public void yesMark(View view) {

        if (!minutes.getText().toString().equals("")) {
            int newMin = Integer.parseInt(minutes.getText().toString());
            String desc = description.getText().toString();
            Intent goingBack = new Intent();
            goingBack.putExtra("Minutes",newMin);
            goingBack.putExtra("Confirmation", true);
            goingBack.putExtra("Description", desc);
            setResult(RESULT_OK, goingBack);
            finish();
        }else{
            Toast toast = Toast.makeText(this, "Enter minutes", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    public void noMark(View view) {
        Intent goingBack = new Intent();
        goingBack.putExtra("Confirmation", false);
        setResult(RESULT_OK,goingBack);
        finish();
    }
}
