package util;

import android.location.Location;

import com.parse.ParseObject;

import java.util.ArrayList;

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
     * @param location
     */
    public static ArrayList<HoboSign> getSignsByLocation(Location location){
        /* Create new arraylist to store hobosigns that are
         * fetched from the database
         */
        ArrayList<HoboSign> hoboSigns = new ArrayList<>();




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
