package com.vince.socius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        isStaff = false;

        submit = (Button)findViewById(R.id.moveButton);

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

}
