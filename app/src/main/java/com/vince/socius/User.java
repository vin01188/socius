package com.vince.socius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private String uid;
    private boolean isStaff;
    Button submit;
    Button howButton;
    Button whyButton;
    TextView mainMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        isStaff = false;

        submit = (Button)findViewById(R.id.moveButton);
        whyButton = (Button) findViewById(R.id.whoWeAre);
        howButton = (Button) findViewById(R.id.howToUse);
        mainMessage = (TextView) findViewById(R.id.textView4);

        Intent intent = getIntent();
        uid = intent.getStringExtra(MainActivity.EXTRA_UID);
        readUserDataFromFirebase(new Runnable(){
            public void run(){
                //put in button here
                if (isStaff) {
                    submit.setText("Open Requests");
                }else{
                    submit.setText("Let us know");
                }


            }

        });
    }


    public void openMain(View view) {
        Intent goingBack = new Intent();
        setResult(RESULT_OK, goingBack);
        goingBack.putExtra("IsStaff",isStaff);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void readUserDataFromFirebase(final Runnable onLoaded){
        mFirebaseDatabaseReference.child("Staff").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String role = dataSnapshot.getValue(String.class);
                if(role == null){
                    isStaff = false;
                }else if (role.equals("staff")){
                    isStaff = true;
                }else{
                    isStaff = false;
                }
                onLoaded.run();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void whoClicked(View view){
        whyButton.setBackgroundResource(R.drawable.testback);
        howButton.setBackgroundResource(R.drawable.testback2);
        mainMessage.setText("Socius is an app that uses your contributions to track the needs of the " +
                "homeless community across the city. We're in Alpha now, but we're hoping to grow our service " +
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
                "currently, we can only record need.");
        howButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        whyButton.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
    }

}
