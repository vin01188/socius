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

public class EditActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    EditText description;
    Spinner hourspin;
    Spinner amspin;
    String hour;
    String amorpm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        String hour = "1";
        String amorpm = "PM";
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        description = (EditText) findViewById(R.id.description);
        hourspin = (Spinner) findViewById(R.id.time_spinner);
        amspin = (Spinner) findViewById(R.id.am);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.hour_array, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.am_array,android.R.layout.simple_spinner_dropdown_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourspin.setAdapter(adapter);
        hourspin.setOnItemSelectedListener(this);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amspin.setAdapter(adapter1);
        amspin.setOnItemSelectedListener(this);

        Intent intent = getIntent();
        String address = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String description1 = intent.getStringExtra(MainActivity.EXTRA_DESCRIPTION);
        TextView addressConfirmation = (TextView) findViewById(R.id.editConf);
        addressConfirmation.append(" " + address + "?" + " And when were you at this location?");
        description.setText(description1);

    }

    public void yesMark(View view) {

        if (!hour.equals("")) {
            int newMin = Integer.parseInt(hour);
            String desc = description.getText().toString();
            Intent goingBack = new Intent();
            String temp = amorpm;
            goingBack.putExtra("Minutes",newMin);
            goingBack.putExtra("AmOrPm", temp);
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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        String selected = (String) parent.getItemAtPosition(pos);
        if (selected.equals("PM") || selected.equals("AM")) {
            amorpm = selected;
        }else{
            hour = selected;
        }
    }

    public void onNothingSelected(AdapterView<?> parent){

    }
}
