package util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicklobue on 11/19/15.
 */
public class ParseDatabaseManager {

    /**
     * Override constructor with private constructor
     * so no instance can be created
     */
    private ParseDatabaseManager() {

    }

    /**
     * This utility method will take in a location
     * and return an arraylist of hobosigns from the
     * database within the determined radius of the user
     *
     * May need to be performed in an async task if there
     * is too much of a delay!!!
     * @param currentLocation
     * @param radius (in miles:: 500ft = .095miles)
     *
     * @return ArrayList of HoboSigns
     */
    public static ArrayList<HoboSign> getSignsByLocation(Location currentLocation, double radius) {
        /* Create new arraylist to store hobosigns that are
         * fetched from the database
         */
        final ArrayList<HoboSign> hoboSigns = new ArrayList<>();

        /* Create a parse query object that will be used
         * to grab all of the hobo signs within the radius
         */
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Signs");
        query.whereWithinMiles("locationCenter", new ParseGeoPoint(currentLocation.getLatitude(),
                currentLocation.getLongitude()), radius);

        /* Execute query */
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                /* The query was carried out with no errors */
                if(e == null) {
                    for(ParseObject sign: objects) {
                        /* Create temporary location of current sign */
                        Location tempLocation = new Location("ParseSignLocation");
                        tempLocation.setLatitude(sign.getParseGeoPoint("locationCenter").getLatitude());
                        tempLocation.setLongitude(sign.getParseGeoPoint("locationCenter").getLongitude());

                        try {
                            /* Retrieving image stored in parse */
                            byte[] bArray = sign.getParseFile("ImageFile").getData();
                            Bitmap tempImage = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);

                            /* Retrieving overlay image
                            byte[] overlayArray = sign.getParseFile("OverlayImage").getData();
                            Bitmap tempOverlay = BitmapFactory.decodeByteArray(overlayArray, 0, overlayArray.length); */

                            /* Create new hobosign to add to arraylist */
                            hoboSigns.add(new HoboSign(tempLocation, tempImage));
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    Log.i("ParseDBManager", "Error in call to parse find in background" + e);
                }
            }
        });


        return hoboSigns;
    }

    /**
     * This utility method will take in a hobosign as
     * a parameter and save it to the database
     */
    public static void saveOrUpdate(HoboSign hoboSign) {
        /* Parse object to be pushed to database */
        ParseObject sign = new ParseObject("Signs");
        sign.put("locationCenter", hoboSign.getLocation());
        sign.put("ImageFile", hoboSign.getSign());
        //sign.put("Overlay", hoboSign.getOverlay());
    }
}
