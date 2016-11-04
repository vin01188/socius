package com.vince.socius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Confirmation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public final static String EXTRA_ADDRESS = "com.vince.socius.ADDRESS";
    public final static String EXTRA_TIME = "com.vince.socius.TIME";
    public final static String EXTRA_NUMBER = "com.vince.socius.NUMBER";
    public final static String EXTRA_SERVICE = "com.vince.socius.SERVICE";

    EditText description;
    EditText number;
    Spinner hourspin;
    Spinner amspin;
    String hour;
    String amorpm;
    String address1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        String hour = "1";
        String amorpm = "PM";
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        description = (EditText) findViewById(R.id.description);
        hourspin = (Spinner) findViewById(R.id.time_spinner);
        amspin = (Spinner) findViewById(R.id.am);
        number = (EditText) findViewById(R.id.number);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.hour_array, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.am_array, android.R.layout.simple_spinner_dropdown_item);

        Calendar calendar = Calendar.getInstance();
        int inithour = calendar.get(Calendar.HOUR);
        int initpos = (inithour - 1) % 12;

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourspin.setAdapter(adapter);
        hourspin.setOnItemSelectedListener(this);

        hourspin.setSelection(initpos);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amspin.setAdapter(adapter1);
        amspin.setOnItemSelectedListener(this);
        amspin.setSelection(calendar.get(Calendar.AM_PM));

        Intent intent = getIntent();
        String address = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView addressConfirmation = (TextView) findViewById(R.id.addressConf);
        address1 = address;
        addressConfirmation.append(" " + address + "?" + " And when were you at this location?");
    }

    public void yesMark(View view) {

        if (!hour.equals("")) {

            int newMin = Integer.parseInt(hour);
            String desc = description.getText().toString();
            int num = Integer.parseInt(number.getText().toString());
            String temp = amorpm;

            Intent intent = new Intent(this, Summary.class);
            final int result = 10;
            intent.putExtra(EXTRA_ADDRESS, address1);
            intent.putExtra(EXTRA_NUMBER, Integer.toString(num));
            intent.putExtra(EXTRA_SERVICE, desc);
            intent.putExtra(EXTRA_TIME, newMin + " " + temp);
            startActivityForResult(intent, result);


        } else {
            Toast toast = Toast.makeText(this, "Enter minutes", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void noMark(View view) {
        Intent goingBack = new Intent();
        goingBack.putExtra("Confirmation", false);
        setResult(RESULT_OK, goingBack);
        finish();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String selected = (String) parent.getItemAtPosition(pos);
        if (selected.equals("PM") || selected.equals("AM")) {
            amorpm = selected;
        } else {
            hour = selected;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 10) {
                boolean isConf = data.getBooleanExtra("Confirmation", false);
                if (isConf) {

                    int newMin = Integer.parseInt(hour);
                    String desc = description.getText().toString();
                    String num = number.getText().toString();
                    String temp = amorpm;

                    Intent goingBack = new Intent();

                    goingBack.putExtra("Number", num);
                    goingBack.putExtra("Minutes", newMin);
                    goingBack.putExtra("AmOrPm", temp);
                    goingBack.putExtra("Confirmation", true);
                    goingBack.putExtra("Description", desc);
                    setResult(RESULT_OK, goingBack);
                    finish();
                }
            }
        }
    }
}