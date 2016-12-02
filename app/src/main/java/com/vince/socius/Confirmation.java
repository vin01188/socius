package com.vince.socius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class Confirmation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public final static String EXTRA_ADDRESS = "com.vince.socius.ADDRESS";
    public final static String EXTRA_TIME = "com.vince.socius.TIME";
    public final static String EXTRA_NUMBER = "com.vince.socius.NUMBER";
    public final static String EXTRA_SERVICE = "com.vince.socius.SERVICE";

    EditText description;
    //EditText number;
    Spinner hourspin;
    Spinner amspin;
    Spinner minutespin;
    Spinner peoplespin;

    String minute;
    String hour;
    String amorpm;
    String address1;
    String numberOfPeople;

    boolean food;
    boolean clothes;
    boolean medical;
    boolean toilet;
    boolean dontknow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        food = false;
        toilet = false;
        clothes = false;
        medical = false;
        dontknow = false;

        String hour = "1";
        String amorpm = "PM";
        String minute = "00";
        numberOfPeople = "1";
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        description = (EditText) findViewById(R.id.description);
        hourspin = (Spinner) findViewById(R.id.time_spinner);
        amspin = (Spinner) findViewById(R.id.am);
        minutespin = (Spinner)findViewById(R.id.minute_spinner);
        //number = (EditText) findViewById(R.id.number);
        peoplespin = (Spinner) findViewById(R.id.numPeople_spinner);



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.hour_array, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.am_array, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adaptermin = ArrayAdapter.createFromResource(this, R.array.minute_array, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapternum = ArrayAdapter.createFromResource(this, R.array.number_array, android.R.layout.simple_spinner_dropdown_item);

        Calendar calendar = Calendar.getInstance();
        int inithour = calendar.get(Calendar.HOUR);
        int initpos = (inithour - 1) % 12;
        int initmin = calendar.get(Calendar.MINUTE);


        //hour = Integer.toString(inithour);



        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourspin.setAdapter(adapter);
        hourspin.setOnItemSelectedListener(this);

        hourspin.setSelection(initpos);

        adaptermin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutespin.setAdapter(adaptermin);
        minutespin.setOnItemSelectedListener(this);
        minutespin.setSelection(initmin);

        //minute = (String)minutespin.getSelectedItem();

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amspin.setAdapter(adapter1);
        amspin.setOnItemSelectedListener(this);
        amspin.setSelection(calendar.get(Calendar.AM_PM));

        adapternum.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        peoplespin.setAdapter(adapternum);
        peoplespin.setOnItemSelectedListener(this);
        peoplespin.setSelection(0);

        Intent intent = getIntent();
        String address = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView addressConfirmation = (TextView) findViewById(R.id.addressConf);
        address1 = address;
        addressConfirmation.append(address);

    }

    public void yesMark(View view) {

        int newMin = Integer.parseInt(hour);
        String desc = "";
        if(food) desc += "Food \n";
        if(clothes) desc += "Clothes \n";
        if(medical) desc += "Medical \n";
        if(toilet) desc += "Toiletries \n";
        if(dontknow) desc += "Do not know \n";
        if(!description.getText().toString().equals("")) desc += description.getText().toString();
        //else if(desc.length() > 3) desc = desc.substring(0,desc.length() - 3);
        String temp = amorpm;

        Intent intent = new Intent(this, Summary.class);
        final int result = 10;
        intent.putExtra(EXTRA_ADDRESS, address1);
        intent.putExtra(EXTRA_NUMBER, numberOfPeople);
        intent.putExtra(EXTRA_SERVICE, desc);
        intent.putExtra(EXTRA_TIME, newMin + ":" + minute + " " + temp);
        startActivityForResult(intent, result);
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
        }else if(parent.getCount() == 12) {
            hour = selected;
        }else if (parent.getCount() == 10){
            numberOfPeople = selected;
        }else{
            minute = selected;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    //checkbox actions
    public void onCheckBoxClicked(View view){
        boolean checked = ((CheckBox) view).isChecked();

        //Check which checkbox was clicked
        switch(view.getId()){
            case R.id.foodBox:
                if (checked)
                    food = true;
                else
                    food = false;
                break;
            case R.id.medicalBox:
                if(checked)
                    medical = true;
                else
                    medical = false;
                break;
            case R.id.clothesBox:
                if(checked)
                    clothes = true;
                else
                    clothes = false;
                break;
            case R.id.toiletBox:
                if(checked)
                    toilet = true;
                else
                    toilet = false;
                break;
            case R.id.idk:
                if(checked)
                    dontknow = true;
                else
                    dontknow = false;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 10) {
                boolean isConf = data.getBooleanExtra("Confirmation", false);
                if (isConf) {

                    int newMin = Integer.parseInt(hour);
                    String newMinReal = String.copyValueOf(minute.toCharArray());
                    String desc = "";
                    if(food) desc += "Food | ";
                    if(clothes) desc += "Clothes | ";
                    if(medical) desc += "Medical | ";
                    if(toilet) desc += "Toiletries | ";
                    if(dontknow) desc += "Do not know | ";
                    if(!description.getText().toString().equals("")) desc += description.getText().toString();
                    else if(desc.length() > 3) desc = desc.substring(0,desc.length() - 3);
                    String temp = amorpm;

                    Intent goingBack = new Intent();

                    goingBack.putExtra("Number", numberOfPeople);
                    goingBack.putExtra("Minutes", newMin);
                    goingBack.putExtra("MinutesReal", newMinReal);
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