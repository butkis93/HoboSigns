package util;

import android.location.Location;
import android.media.Image;

/**
 * Created by nicklobue on 11/19/15.
 * Will be used as a wrapper class to hold a
 * picture file representing a hobosign and
 * the location of the hobosign
 */
public class HoboSign {

    Location location;
    Image sign;
    //Image overlay;

    public HoboSign(Location location, Image sign) {
        this.location = location;
        this.sign = sign;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Image getSign() {
        return sign;
    }

    public void setSign(Image sign) {
        this.sign = sign;
    }

    /* public Image getOverlay() {
        return overlay;
    }

    public void setOverlay(Image overlay) {
        this.overlay = overlay;
    } */
}
