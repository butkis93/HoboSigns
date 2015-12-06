package com.example.nicklobue.hobosigns;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import util.HoboSign;

/**
 * Created by Daniel on 11/22/2015.
 */
public class SingleSignViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.single_sign_view);

        /* Intent intent = getIntent();
        HoboSign hoboSign = new HoboSign(intent); */

        ImageView sign = (ImageView) findViewById(R.id.single_sign);
        sign.setImageBitmap(GlobalSign.getGlobalSign());

        Button home = (Button) findViewById(R.id.back_home_button);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HoboSignsMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
