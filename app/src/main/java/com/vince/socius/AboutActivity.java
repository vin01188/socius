package com.vince.socius;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    Button howButton;
    Button whyButton;
    TextView mainMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        whyButton = (Button) findViewById(R.id.whoWeAre1);
        howButton = (Button) findViewById(R.id.howToUse1);
        mainMessage = (TextView) findViewById(R.id.aboutMessage);
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void whoClicked(View view){
        whyButton.setBackgroundResource(R.drawable.testback);
        howButton.setBackgroundResource(R.drawable.testback2);
        mainMessage.setText("Socius is an app that uses your contributions to track the needs of the " +
                "homeless comunity across the city. We're in Alpha now, but we're hoping to grow our service " +
                "so that the location data we collect can be used to " +
                "deliver services directly to those who need it most.");
        whyButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        howButton.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
    }

    public void howClicked (View view){
        howButton.setBackgroundResource(R.drawable.testback);
        whyButton.setBackgroundResource(R.drawable.testback2);
        mainMessage.setText("When you encounter someone who you think could use homelessness" +
                " services, let us know through the app. Eventually, we will have connections with" +
                " community partners who can address immediate needs. Please be aware that, " +
                "currently, we can only record need");
        howButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        whyButton.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
    }

}
