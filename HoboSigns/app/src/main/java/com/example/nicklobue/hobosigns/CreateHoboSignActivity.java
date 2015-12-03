package com.example.nicklobue.hobosigns;

import java.io.OutputStream;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * Created by Alexandria BenDebba on 11/10/15.
 */
public class CreateHoboSignActivity extends Activity implements OnClickListener {

    private static final String TAG = "CallCamera";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 0;

    DrawableImageView choosenImageView;
    Button choosePicture;
    Button savePicture;

    Bitmap bmp;
    Bitmap alteredBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_hobo_sign);

        choosenImageView = (DrawableImageView) this.findViewById(R.id.ChoosenImageView);
        choosePicture = (Button) this.findViewById(R.id.ChoosePictureButton);
        savePicture = (Button) this.findViewById(R.id.SavePictureButton);

        savePicture.setOnClickListener(this);
        choosePicture.setOnClickListener(this);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    public void onClick(View v)
    {
        if (v == choosePicture)
        {
            /*Log.v("Drawing", "Choosing picture");
            Intent choosePictureIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(choosePictureIntent, 0);*/
            Log.v("Drawing","Taking picture");
            dispatchTakePictureIntent();
        }
        else if (v == savePicture)
        {
            if (alteredBitmap != null)
            {
                Log.v("Drawing", "Saving picture");
                ContentValues contentValues = new ContentValues(3);
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Draw On Me");

                Uri imageFileUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                try {
                    OutputStream imageFileOS = getContentResolver()
                            .openOutputStream(imageFileUri);
                    alteredBitmap
                            .compress(Bitmap.CompressFormat.JPEG, 90, imageFileOS);
                    Toast t = Toast
                            .makeText(this, "Saved!", Toast.LENGTH_SHORT);
                    t.show();

                } catch (Exception e) {
                    Log.v("EXCEPTION", e.getMessage());
                }
            } else {
                Log.v("Drawing","No changes made");
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            Uri imageFileUri = intent.getData();
            try {
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                bmp = BitmapFactory
                        .decodeStream(
                                getContentResolver().openInputStream(
                                        imageFileUri), null, bmpFactoryOptions);

                bmpFactoryOptions.inJustDecodeBounds = false;
                bmp = BitmapFactory
                        .decodeStream(
                                getContentResolver().openInputStream(
                                        imageFileUri), null, bmpFactoryOptions);

                alteredBitmap = Bitmap.createBitmap(bmp.getWidth(),
                        bmp.getHeight(), bmp.getConfig());

                choosenImageView.setNewImage(alteredBitmap, bmp);
            }
            catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
        }
    }
}

