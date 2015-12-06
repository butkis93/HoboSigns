package com.example.nicklobue.hobosigns;

import android.graphics.Bitmap;

public class GlobalSign {
    static Bitmap globalSign;

    private GlobalSign() {

    }

    public static void setGlobalSign(Bitmap sign) {
        globalSign = sign;
    }

    public static Bitmap getGlobalSign() {
        return globalSign;
    }

}
