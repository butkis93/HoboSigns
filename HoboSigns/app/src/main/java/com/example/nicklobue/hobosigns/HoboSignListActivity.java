package com.example.nicklobue.hobosigns;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.ArrayList;

import util.HoboSign;

import static util.ParseDatabaseManager.getSignsByLocation;

public class HoboSignListActivity extends AppCompatActivity {

    private ArrayList<HoboSign> hoboSignList = null;
    private HoboSignListAdapter hsAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobo_sign_list);

        setContentView(R.layout.activity_hobo_sign_list);
        ListView list = (ListView)findViewById(android.R.id.list);

        list.setFooterDividersEnabled(false);
        hsAdapter = new HoboSignListAdapter(getApplicationContext());
        list.setAdapter(hsAdapter);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        // this gives a warning but I'm ignoring it
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //1000 feet

        if(location == null) {
            //TODO: error?
        }

       hoboSignList = getSignsByLocation(location, 0.189394);

        if(hoboSignList == null) {
            //TODO: error?
        }

        for(HoboSign sign : hoboSignList) {
            hsAdapter.add(sign);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Fire an Intent to display the given hobo sign
//
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hobo_sign_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.filter_settings) {
            Bitmap desiredShape = getDesiredShapeFromUser();

            Toast.makeText(getApplicationContext(), "Filtering...",
                    Toast.LENGTH_LONG).show();

            hsAdapter.filter(desiredShape);
            Toast.makeText(getApplicationContext(), "Done filtering", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO: implement this
    private Bitmap getDesiredShapeFromUser() {
        return null;
        //return new Bitmap();
    }

}
