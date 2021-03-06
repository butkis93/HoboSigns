package com.example.nicklobue.hobosigns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.Parse;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class HoboSignsMain extends AppCompatActivity {

    static boolean alreadyStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobo_signs_main);

        if (alreadyStarted == false) {
        /* Initialize parse local datastore */
            Parse.enableLocalDatastore(this);

        /* Initialize connection to parse with api keys */
            Parse.initialize(this, "1J9hTNVJ2Z36fhubap7BrlJmzd2lC7nd7eglw9OT",
                    "g2gOHfKR04uVTB2yuoyjnBQ8YPwxpxw757oc4ukw");
            alreadyStarted = true;
        }

        //Followed these instructions to install OpenCV:
        //http://stackoverflow.com/questions/17767557/how-to-use-opencv-in-android-studio-using-gradle-build-tool
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,
                HoboSignsMain.this, mOpenCVCallBack)) {
            Log.i("TEST", "Cannot connect to OpenCV Manager");
        }

        /* References to buttons for creating hobosigns and viewing hobosigns
         */
        Button view = (Button) findViewById(R.id.view_hobosigns);
        Button create = (Button) findViewById(R.id.create_hobosigns);
        ImageView logo = (ImageView) findViewById(R.id.logo);

        logo.setImageResource(R.drawable.hobo_signs_logo);

        /* Create on click listener to start proper activities */
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Create intents and start activity */
                Intent hoboSignIntent = new Intent(HoboSignsMain.this,
                        HoboSignListActivity.class);
                startActivity(hoboSignIntent);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Create intent and start activity */
                startActivity(new Intent(getApplicationContext(),CreateHoboSignActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hobo_signs_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            Log.i("TEST", "I'm in the onManagerConnected callback!");
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    //stuff
                    Log.i("Init-OpenCV", "OpenCV loaded succesfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
}
