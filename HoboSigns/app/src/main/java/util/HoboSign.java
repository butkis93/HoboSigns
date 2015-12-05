package util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;

import java.io.ByteArrayOutputStream;

/**
 * Created by nicklobue on 11/19/15.
 * Will be used as a wrapper class to hold a
 * picture file representing a hobosign and
 * the location of the hobosign
 */
public class HoboSign {

    Location location;
    Bitmap sign;
    //Bitmap overlay;

    public HoboSign(Location location, Bitmap sign) {
        this.location = location;
        this.sign = sign;
        //this.overlay = overlay;
    }

    public HoboSign(Intent intent) {
        this.sign = (Bitmap) intent.getParcelableExtra("sign");
        this.location = (Location) intent.getParcelableExtra("location");
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Bitmap getSign() {
        return sign;
    }

    public void setSign(Bitmap sign) {
        this.sign = sign;
    }

    public byte[] getSignByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sign.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public Intent packageToIntent() {
        Intent intent = new Intent();
        intent.putExtra("location", location);
        intent.putExtra("sign", sign);
        return intent;
    }

    /* public Bitmap getOverlay() {
        return overlay;
    }

    public void setOverlay(Bitmap overlay) {
        this.overlay = overlay;
    } */
}
