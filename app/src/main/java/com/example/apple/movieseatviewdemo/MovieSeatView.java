package com.example.apple.movieseatviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.security.spec.DSAParameterSpec;

/**
 * Created by apple on 2017/4/21.
 */

public class MovieSeatView extends View {

    private TypeNotPresentException typeNotPresentException;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private int mCenterX;
    private int mCenterY;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean isFirstDraw;

    public MovieSeatView(Context context) {
        super(context);
        init();
    }

    public MovieSeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        mCenterX = mBitmap.getWidth() / 2;
        mCenterY = mBitmap.getHeight() / 2;

        mMatrix = new Matrix();

        isFirstDraw = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.YELLOW);

//        mMatrix.postScale(2, 2, 0, 0);//以上的两步，还不如这一步搞定要好，直接默认围绕左上角放大
//      居中显示这个bitmap
        if (isFirstDraw) {
            isFirstDraw = false;
            mMatrix.postTranslate(mScreenWidth / 2 - mCenterX, mScreenHeight / 2 - mCenterY);
        }
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        得到外界传来的尺寸
        mScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
        mScreenHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.e("ccc", "onMeasure: " + mScreenWidth);
        Log.e("ccc", "onMeasure: " + mScreenHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleGestureDetector.onTouchEvent(event);

        return true;//必须返回true，mScaleGestureDetector才会生效
    }

    private float mScaleFactor;
    ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor = detector.getScaleFactor();

//            下面这个判断，是让这个bitmap在缩放时候，最大不超过原图的3.5倍，最小不小于原图的0.5倍
            if (getMatrixScaleY() * mScaleFactor < 3.5 && getMatrixScaleY() * mScaleFactor > 0.5) {
//             这里的判断算法很巧妙，不是直接用累计比例是否小雨3.5去判断，而是乘上了一个mScaleFactor，
//                因为缩放时候，会冒出去，使得getMatrixScaleY>3.5，这样以后再也进不来这个判断了，
//              但是乘上了mScaleFactor之后，由于在一次缩放过程中，mScaleFactor会一直减少或增大，
//              使得getMatrixScaleY() * mScaleFactor 的乘积，回落进3.5内部
//            让bitmap围绕屏幕中心缩放
                mMatrix.postScale(mScaleFactor, mScaleFactor, mScreenWidth / 2, mScreenHeight / 2);
                Log.e("ccc", "onMeasure: xxx" + mScaleFactor);
                Log.e("ccc", "onMeasure: yyyyyyy" + getMatrixScaleY());

                invalidate();
            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    });


    float[] m = new float[9];

    /**
     * 得到Matrix里累计的Y轴缩放比例
     *
     * @return
     */
    private float getMatrixScaleY() {
        mMatrix.getValues(m);
        return m[4];
    }
}
