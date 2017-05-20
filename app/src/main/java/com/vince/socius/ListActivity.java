package com.vince.socius;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    Person[] peopleArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArrayList<Person> peopleList = (ArrayList<Person>) getIntent().getSerializableExtra("peopleList");
        ListView listView = (ListView) findViewById(R.id.listView);

        CustomAdapter myAdapter = new CustomAdapter(this, peopleList);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person temp = (Person) parent.getItemAtPosition(position);
                String address = temp.getAddress();
                double lattitude = temp.getLattitude();
                double longitude = temp.getLongitude();
                Intent goingBack = new Intent();
                goingBack.putExtra("lat", lattitude);
                goingBack.putExtra("long", longitude);
                setResult(RESULT_OK, goingBack);
                finish();
            }
        });

    }

    class CustomAdapter extends BaseAdapter{

        private ArrayList<Person> myList;
        private Activity parentActivity;
        private LayoutInflater inflater;

        public CustomAdapter (Activity parent,ArrayList<Person> l) {
            parentActivity = parent;
            myList = l;
            inflater = (LayoutInflater) parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public Object getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null){
                view = inflater.inflate(R.layout.listitem, null);
            }
            TextView textname= (TextView)view.findViewById(R.id.textView_name);
            TextView textDescription = (TextView) view.findViewById(R.id.textView_description);
            TextView textStatus = (TextView) view.findViewById(R.id.textView_status);
            Person myPerson = myList.get(position);
            String description = myPerson.getDescription();
            if (description.length() > 0){
                description = description.substring(0,description.length() - 2);
            }

            textname.setText("Address: " + myPerson.getAddress());
            textDescription.setText("Needs: " + description);
            textStatus.setText("Status: " + myPerson.getStatus());
            String[] times = myPerson.getTime().split("/");

            if (times.length >= 5) {
                String year = times[0];
                String month = times[1];
                String day = times[2];
                String hour = times[3];
                String min = times[4];
                String am = times[5];

                String markerTime = "Time posted: " + hour + ":" + min + " " + am + "  " +
                        " Date Posted: " + month + "/" + day;
                TextView time = (TextView)view.findViewById(R.id.textView_time);
                time.setText(markerTime);
            }
            return view;
        }
    }

}
