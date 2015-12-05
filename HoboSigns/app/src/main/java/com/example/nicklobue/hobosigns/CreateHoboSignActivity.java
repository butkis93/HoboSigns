package com.example.nicklobue.hobosigns;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import java.io.OutputStream;
import android.content.ContentValues;
import android.graphics.Bitmap.CompressFormat;
import android.provider.MediaStore.Images.Media;
import android.widget.Toast;

import util.HoboSign;

public class CreateHoboSignActivity extends Activity implements View.OnClickListener,
        View.OnTouchListener {

    ImageView chosenImageView;
    Button choosePicture;
    Button savePicture;
    Button clearDrawing;

    Bitmap bmp;
    Bitmap alteredBitmap;
    Canvas canvas;
    Paint paint;
    Matrix matrix;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;

    final String TAG = "Debugging";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("Drawing", "On create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_hobo_sign);

        chosenImageView = (ImageView) this.findViewById(R.id.ChosenImageView);
        choosePicture = (Button) this.findViewById(R.id.ChoosePictureButton);
        savePicture = (Button) this.findViewById(R.id.SavePictureButton);
        clearDrawing = (Button) this.findViewById(R.id.ClearPictureButton);

        savePicture.setOnClickListener(this);
        choosePicture.setOnClickListener(this);
        clearDrawing.setOnClickListener(this);
        chosenImageView.setOnTouchListener(this);
        chosenImageView.setDrawingCacheEnabled(true);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    public void onClick(View v) {

        if (v == choosePicture) {
            Log.v(TAG,"Choosing picture");
            dispatchTakePictureIntent();
        } else if (v == savePicture) {
            Log.v(TAG,"Saving picture");
            if (alteredBitmap != null) {
                HoboSign hoboSign = new HoboSign(null,chosenImageView.getDrawingCache());
                Toast.makeText(this,"Hobo Sign Created",Toast.LENGTH_SHORT);
            }
        } else if (v == clearDrawing) {
            Log.v(TAG, "Clear drawing");
            setDrawingArea();
        }
    }

    void setDrawingArea(){
        alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
                .getHeight(), bmp.getConfig());
        Log.v(TAG,"width is " + bmp.getWidth() + " height is " + bmp.getHeight());
        canvas = new Canvas(alteredBitmap);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(35);
        matrix = new Matrix();
        canvas.drawBitmap(bmp, matrix, paint);

        chosenImageView.destroyDrawingCache();
        chosenImageView.setImageBitmap(alteredBitmap);
        chosenImageView.setOnTouchListener(this);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            Uri imageFileUri = intent.getData();
            try {
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                        imageFileUri), null, bmpFactoryOptions);

                bmpFactoryOptions.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                        imageFileUri), null, bmpFactoryOptions);

                setDrawingArea();
            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
        }
    }
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = getPointerCoords(event)[0];//event.getX();
                downy = getPointerCoords(event)[1];//event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                chosenImageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                chosenImageView.invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    final float[] getPointerCoords(MotionEvent e) {
        final int index = e.getActionIndex();
        final float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix matrix = new Matrix();
        chosenImageView.getImageMatrix().invert(matrix);
        matrix.postTranslate(chosenImageView.getScrollX(), chosenImageView.getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }
}