package com.example.nicklobue.hobosigns;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.util.ArrayList;
import java.util.List;

import util.HoboSign;

import static util.ParseDatabaseManager.getSignsByLocation;

public class HoboSignListActivity extends AppCompatActivity {

    private static String TAG = "OpenCV-Test";
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
                HoboSign sign = (HoboSign) hsAdapter.getItem(position);

                GlobalSign.setGlobalSign(sign.getSign());
                startActivity(new Intent(getApplicationContext(), SingleSignViewActivity.class));
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
            //Bitmap desiredShape = getDesiredShapeFromUser();
            startActivityForResult(new Intent(this, FilterActivity.class),0);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Toast.makeText(getApplicationContext(), "Filtering...",
                Toast.LENGTH_LONG).show();

        new FilterOperation().execute(GlobalSign.getGlobalSign());
        Toast.makeText(getApplicationContext(), "Done filtering", Toast.LENGTH_LONG).show();
    }

    private Bitmap getDesiredShapeFromUser() {
        DrawingImageView hoboSignFilter = new DrawingImageView(this.getApplicationContext());
        hoboSignFilter.setNewImage();
        return hoboSignFilter.getBmp();
    }

    private class FilterOperation extends AsyncTask<Bitmap, Void, ArrayList<Integer>> {
        @Override
        protected ArrayList<Integer> doInBackground(Bitmap... params) {
            Bitmap symbol = params[0];

            Bitmap desiredSymbol = HoboSign.getResizedBitmap(symbol, 128);

            ArrayList<Integer> listOfSignsToRemove = new ArrayList<>();
            Mat desiredMat = new Mat(desiredSymbol.getWidth(), desiredSymbol.getHeight(), CvType.CV_8UC1);
            Utils.bitmapToMat(desiredSymbol, desiredMat);

            FeatureDetector fd = FeatureDetector.create(FeatureDetector.ORB);
            DescriptorExtractor de = DescriptorExtractor.create(DescriptorExtractor.ORB);
            DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

            MatOfKeyPoint testKps = new MatOfKeyPoint();
            MatOfKeyPoint matchingImgKps = new MatOfKeyPoint();

            Mat testDescriptors = new Mat();
            Mat matchingImgDescriptors = new Mat();

            fd.detect(desiredMat, matchingImgKps);
            de.compute(desiredMat, matchingImgKps, matchingImgDescriptors);

            //Gather a list of all HoboSigns whose symbols do not match to within a certain
            //threshold
            int i = 0;
            //double threshold = 100000.0d;
            double threshold = 150.0d;
            for(int hoboSignIndex = 0; hoboSignIndex < hsAdapter.getCount(); hoboSignIndex++) {
                HoboSign sign = (HoboSign) hsAdapter.getItem(i);
                Bitmap img = sign.getSign();
                Mat m = new Mat(img.getWidth(), img.getHeight(), CvType.CV_8UC1);
                Utils.bitmapToMat(img, m);
                fd.detect(m, testKps);
                de.compute(m, testKps, testDescriptors);

                MatOfDMatch matches = new MatOfDMatch();
                matcher.match(matchingImgDescriptors, testDescriptors, matches);
                List<DMatch> list = matches.toList();

                double sum = 0;
                double score = 0;
                for (int j = 0; j < list.size(); j++) {
                    double d = list.get(j).distance;
                    sum += d * d;
                }

                score = sum / (list.size() * list.size());
                Log.i(TAG, "image + " + i + " had score: " + score + " with this many matches: " + list.size());

                if (score > threshold || list.size() < 3) {
                    listOfSignsToRemove.add(i);
                }

                i++;
            }

            return listOfSignsToRemove;
        }

        @Override
        public void onPostExecute(ArrayList<Integer> result) {

            hsAdapter.removeFromFilter(result);
        /*
            for(int i = hsAdapter.getCount() - 1; i >= 0; i++) {
                hsAdapter.remove(i);
            }
*/
/*
            for(Integer indx : result) {
                hsAdapter.remove(indx);
            }*/
            GlobalSign.clearCache();
        }
    }

    /*public void filter(final Bitmap symbol) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Initialize all of our OpenCV matching stuff
                Bitmap desiredSymbol = HoboSign.getResizedBitmap(symbol, 128);

                ArrayList<Integer> listOfSignsToRemove = new ArrayList<>();
                Mat desiredMat = new Mat(desiredSymbol.getWidth(), desiredSymbol.getHeight(), CvType.CV_8UC1);
                Utils.bitmapToMat(desiredSymbol, desiredMat);

                FeatureDetector fd = FeatureDetector.create(FeatureDetector.ORB);
                DescriptorExtractor de = DescriptorExtractor.create(DescriptorExtractor.ORB);
                DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

                MatOfKeyPoint testKps = new MatOfKeyPoint();
                MatOfKeyPoint matchingImgKps = new MatOfKeyPoint();

                Mat testDescriptors = new Mat();
                Mat matchingImgDescriptors = new Mat();

                fd.detect(desiredMat, matchingImgKps);
                de.compute(desiredMat, matchingImgKps, matchingImgDescriptors);

                //Gather a list of all HoboSigns whose symbols do not match to within a certain
                //threshold
                int i = 0;
                double threshold = 100000.0d;
                for(int hoboSignIndex = 0; hoboSignIndex < hsAdapter.getCount(); hoboSignIndex++) {
                    HoboSign sign = (HoboSign) hsAdapter.getItem(i);
                    Bitmap img = sign.getSign();
                    Mat m = new Mat(img.getWidth(), img.getHeight(), CvType.CV_8UC1);
                    Utils.bitmapToMat(img, m);
                    fd.detect(m, testKps);
                    de.compute(m, testKps, testDescriptors);

                    MatOfDMatch matches = new MatOfDMatch();
                    matcher.match(matchingImgDescriptors, testDescriptors, matches);
                    List<DMatch> list = matches.toList();

                    double sum = 0;
                    double score = 0;
                    for (int j = 0; j < list.size(); j++) {
                        double d = list.get(j).distance;
                        sum += d * d;
                    }

                    score = sum / (list.size() * list.size());

                    if (score < threshold) {
                        listOfSignsToRemove.add(i);
                    }

                    i++;
                }

                //Remove all HoboSigns we determined too different from the desired symbol
                for(Integer indx : listOfSignsToRemove) {
                    hsAdapter.remove(indx);
                }

            }

        });

        thread.start();

    } */

}
