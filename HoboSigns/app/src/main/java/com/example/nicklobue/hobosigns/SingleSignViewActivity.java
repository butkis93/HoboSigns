package com.example.nicklobue.hobosigns;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by Daniel on 11/22/2015.
 */
public class SingleSignViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.single_sign_view);

        Intent intent = getIntent();
        Bitmap img = null;

        ImageView sign = (ImageView) findViewById(R.id.single_sign);
        sign.setImageBitmap(img);
    }
}
