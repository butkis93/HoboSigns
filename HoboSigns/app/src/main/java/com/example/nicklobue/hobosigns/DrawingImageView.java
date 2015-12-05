package com.example.nicklobue.hobosigns;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class DrawingImageView extends ImageView implements View.OnTouchListener {

    Bitmap bmp;
    Canvas canvas;
    Paint paint;
    Matrix matrix;

    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;

    public DrawingImageView(Context context) {
        super(context);
        setOnTouchListener(this);
    }
    public DrawingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public DrawingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    public void setNewImage() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        int w = ((View)getParent()).getWidth();
        int h = ((View)getParent()).getHeight();
        bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        canvas = new Canvas(bmp);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(35);
        matrix = new Matrix();
        canvas.drawBitmap(bmp, matrix, paint);
        setImageBitmap(bmp);
    }

    @Override
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
                this.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                this.invalidate();
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
        getImageMatrix().invert(matrix);
        matrix.postTranslate(getScrollX(), getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }
}
