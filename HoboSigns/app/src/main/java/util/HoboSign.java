package util;

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

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
