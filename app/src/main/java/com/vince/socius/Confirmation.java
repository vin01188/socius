package com.vince.socius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
    boolean campingSupplies;
    boolean transportation;
    boolean medical;
    boolean toilet;


    //food subCategories
    CheckBox coffee;
    CheckBox cannedGoods;
    CheckBox sandwiches;
    CheckBox fruit;
    CheckBox water;
    CheckBox granolaBars;

    //clothes subCategories
    CheckBox jeans;
    CheckBox socks;
    CheckBox coats;
    CheckBox hats;
    CheckBox gloves;
    CheckBox blankets;
    CheckBox shoes;
    CheckBox boots;
    CheckBox underwear;

    //Toiletries Subcategories
    CheckBox wetWipes;
    CheckBox feminineCareProducts;
    CheckBox handSanitizer;
    CheckBox soap;
    CheckBox deodorant;
    CheckBox shampoo;
    CheckBox lotion;
    CheckBox toiletPaper;

    //Camp supplies
    CheckBox trashBags;
    CheckBox tents;
    CheckBox sleepingBags;
    CheckBox rope;
    CheckBox tarps;
    CheckBox tools;
    CheckBox flashlights;
    CheckBox batteries;

    //Transportation
    CheckBox bikes;
    CheckBox busTickets;
    CheckBox rides;

    //Medical
    CheckBox pregnancyCare;
    CheckBox prescriptionMedication;
    CheckBox nonPrescriptionMedication;
    CheckBox firstAid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        food = false;
        toilet = false;
        clothes = false;
        campingSupplies = false;
        transportation = false;
        medical = false;


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

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxOther);
                    checkBox.setChecked(true);
                }else{
                    CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxOther);
                    checkBox.setChecked(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        hourspin = (Spinner) findViewById(R.id.time_spinner);
        amspin = (Spinner) findViewById(R.id.am);
        minutespin = (Spinner)findViewById(R.id.minute_spinner);
        //number = (EditText) findViewById(R.id.number);
        peoplespin = (Spinner) findViewById(R.id.numPeople_spinner);

        //subCategory checkBox initializations

        //Food boxes
        coffee = (CheckBox) findViewById(R.id.coffeeBox);
        cannedGoods = (CheckBox) findViewById(R.id.cannedGoodsBox);
        sandwiches = (CheckBox) findViewById(R.id.sandwichesBox);
        fruit = (CheckBox) findViewById(R.id.fruitBox);
        water = (CheckBox) findViewById(R.id.waterBox);
        granolaBars = (CheckBox) findViewById(R.id.granolaBox);

        //Clothes Boxes
        jeans = (CheckBox) findViewById(R.id.jeansBox);
        socks = (CheckBox) findViewById(R.id.socksBox);
        coats = (CheckBox) findViewById(R.id.coatsBox);
        hats = (CheckBox) findViewById(R.id.hatsBox);
        gloves = (CheckBox) findViewById(R.id.glovesBox);
        blankets = (CheckBox) findViewById(R.id.blanketsBox);
        shoes = (CheckBox) findViewById(R.id.shoesBox);
        boots = (CheckBox) findViewById(R.id.bootsBox);
        underwear = (CheckBox) findViewById(R.id.underwearBox);

        //Toiletries
        wetWipes = (CheckBox) findViewById(R.id.wetWipesBox);
        feminineCareProducts = (CheckBox) findViewById(R.id.feminineBox);
        handSanitizer = (CheckBox) findViewById(R.id.handSanitizerBox);
        soap = (CheckBox) findViewById(R.id.soapBox);
        deodorant = (CheckBox) findViewById(R.id.deodorantBox);
        shampoo = (CheckBox) findViewById(R.id.shampooBox);
        lotion = (CheckBox) findViewById(R.id.lotionBox);
        toiletPaper = (CheckBox) findViewById(R.id.toiletPaperBox);

        //Camp Supplies
        trashBags = (CheckBox) findViewById(R.id.trashBox);
        tents = (CheckBox) findViewById(R.id.tentBox);
        sleepingBags = (CheckBox) findViewById(R.id.sleepingBagBox);
        rope = (CheckBox) findViewById(R.id.ropeBox);
        tarps = (CheckBox) findViewById(R.id.tarpsBox);
        tools = (CheckBox) findViewById(R.id.toolsBox);
        flashlights = (CheckBox) findViewById(R.id.flashLightBox);
        batteries = (CheckBox) findViewById(R.id.batteriesBox);

        //Transportation
        bikes = (CheckBox) findViewById(R.id.bikesBox);
        busTickets = (CheckBox) findViewById(R.id.busTicketsBox);
        rides = (CheckBox) findViewById(R.id.ridesBox);

        //Medical
        pregnancyCare = (CheckBox) findViewById(R.id.pregnancyBox);
        prescriptionMedication = (CheckBox) findViewById(R.id.prescriptionBox);
        nonPrescriptionMedication = (CheckBox) findViewById(R.id.nonPrescriptionMedicationBox);
        firstAid = (CheckBox) findViewById(R.id.firstAidBox);


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
        if(campingSupplies) desc += "Camp Supplies \n";
        if(transportation) desc += "Transportation \n";
        if(medical) desc += "Dental Health \n";
        if(toilet) desc += "Toiletries \n";
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
                if (checked) {
                    food = true;
                    coffee.setVisibility(View.VISIBLE);
                    cannedGoods.setVisibility(View.VISIBLE);
                    sandwiches.setVisibility(View.VISIBLE);
                    fruit.setVisibility(View.VISIBLE);
                    water.setVisibility(View.VISIBLE);
                    granolaBars.setVisibility(View.VISIBLE);

                }
                else {
                    food = false;
                    coffee.setVisibility(View.GONE);
                    cannedGoods.setVisibility(View.GONE);
                    sandwiches.setVisibility(View.GONE);
                    fruit.setVisibility(View.GONE);
                    water.setVisibility(View.GONE);
                    granolaBars.setVisibility(View.GONE);
                }
                break;
            case R.id.campBox:
                if(checked) {
                    campingSupplies = true;
                    trashBags.setVisibility(View.VISIBLE);
                    tents.setVisibility(View.VISIBLE);
                    sleepingBags.setVisibility(View.VISIBLE);
                    rope.setVisibility(View.VISIBLE);
                    tarps.setVisibility(View.VISIBLE);
                    tools.setVisibility(View.VISIBLE);
                    flashlights.setVisibility(View.VISIBLE);
                    batteries.setVisibility(View.VISIBLE);

                }else {
                    campingSupplies = false;
                    trashBags.setVisibility(View.GONE);
                    tents.setVisibility(View.GONE);
                    sleepingBags.setVisibility(View.GONE);
                    rope.setVisibility(View.GONE);
                    tarps.setVisibility(View.GONE);
                    tools.setVisibility(View.GONE);
                    flashlights.setVisibility(View.GONE);
                    batteries.setVisibility(View.GONE);

                }
                break;
            case R.id.transportationBox:
                if(checked) {
                    transportation = true;
                    bikes.setVisibility(View.VISIBLE);
                    busTickets.setVisibility(View.VISIBLE);
                    rides.setVisibility(View.VISIBLE);
                }else {
                    transportation = false;
                    bikes.setVisibility(View.GONE);
                    busTickets.setVisibility(View.GONE);
                    rides.setVisibility(View.GONE);
                }
                break;
            case R.id.medicalBox:
                if(checked) {
                    medical = true;
                    pregnancyCare.setVisibility(View.VISIBLE);
                    prescriptionMedication.setVisibility(View.VISIBLE);
                    nonPrescriptionMedication.setVisibility(View.VISIBLE);
                    firstAid.setVisibility(View.VISIBLE);
                }else {
                    medical = false;
                    pregnancyCare.setVisibility(View.GONE);
                    prescriptionMedication.setVisibility(View.GONE);
                    nonPrescriptionMedication.setVisibility(View.GONE);
                    firstAid.setVisibility(View.GONE);
                }
                break;
            case R.id.clothesBox:
                if(checked) {
                    clothes = true;
                    jeans.setVisibility(View.VISIBLE);
                    socks.setVisibility(View.VISIBLE);
                    coats.setVisibility(View.VISIBLE);
                    hats.setVisibility(View.VISIBLE);
                    gloves.setVisibility(View.VISIBLE);
                    blankets.setVisibility(View.VISIBLE);
                    shoes.setVisibility(View.VISIBLE);
                    boots.setVisibility(View.VISIBLE);
                    underwear.setVisibility(View.VISIBLE);
                }else {
                    clothes = false;
                    jeans.setVisibility(View.GONE);
                    socks.setVisibility(View.GONE);
                    coats.setVisibility(View.GONE);
                    hats.setVisibility(View.GONE);
                    gloves.setVisibility(View.GONE);
                    blankets.setVisibility(View.GONE);
                    shoes.setVisibility(View.GONE);
                    boots.setVisibility(View.GONE);
                    underwear.setVisibility(View.GONE);
                }
                break;
            case R.id.toiletBox:
                if(checked) {
                    toilet = true;
                    wetWipes.setVisibility(View.VISIBLE);
                    feminineCareProducts.setVisibility(View.VISIBLE);
                    handSanitizer.setVisibility(View.VISIBLE);
                    soap.setVisibility(View.VISIBLE);
                    deodorant.setVisibility(View.VISIBLE);
                    shampoo.setVisibility(View.VISIBLE);
                    lotion.setVisibility(View.VISIBLE);
                    toiletPaper.setVisibility(View.VISIBLE);
                }else {
                    toilet = false;
                    wetWipes.setVisibility(View.GONE);
                    feminineCareProducts.setVisibility(View.GONE);
                    handSanitizer.setVisibility(View.GONE);
                    soap.setVisibility(View.GONE);
                    deodorant.setVisibility(View.GONE);
                    shampoo.setVisibility(View.GONE);
                    lotion.setVisibility(View.GONE);
                    toiletPaper.setVisibility(View.GONE);
                }
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
                    if(campingSupplies) desc += "Camp Supplies | ";
                    if(transportation) desc += "Transportation | ";
                    if(medical) desc += "Medical | ";
                    if(toilet) desc += "Toiletries | ";
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