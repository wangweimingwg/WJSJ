package com.bm.wjsj.View.MyCardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.bm.wjsj.MainActivity;
import com.bm.wjsj.R;
import com.bm.wjsj.WJSJApplication;

/**
 * Created by dionysis_lorentzos on 5/8/14 for package com.lorentzos.swipecards
 * and project Swipe cards. Use with caution dinausaurs might appear!
 */

@SuppressLint("ClickableViewAccessibility")
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class FlingCardListener implements View.OnTouchListener {

    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final int parentWidth;
    private final FlingListener mFlingListener;
    private final Object dataObject;
    private final float halfWidth;
    private float BASE_ROTATION_DEGREES;

    private float aPosX;
    private float aPosY;
    private float aDownTouchX;
    private float aDownTouchY;
    private static final int INVALID_POINTER_ID = -1;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private View frame = null;

    private final int TOUCH_ABOVE = 0;
    private final int TOUCH_BELOW = 1;
    private int touchPosition;
    private final Object obj = new Object();
    private boolean isAnimationRunning = false;
    private float MAX_COS = (float) Math.cos(Math.toRadians(45));
    private ImageView zan;
    private ImageView buzan;
    private double linjiewidth;
    private int i = 0;
    private Drawable drablezan;
    private Drawable drablebuzan;
    float dox = 0;
    float mCenterX, mCenterY;
    private float mLastX;
    private float mLastY;
    private int ZstartT = 0;
    private int BstartT = 0;

    private int curentVersion=1;

    // float deltaY = touchY - mCenterY

    public FlingCardListener(View frame, Object itemAtPosition,
                             FlingListener flingListener) {
        this(frame, itemAtPosition, 15f, flingListener, 500);
    }

    public FlingCardListener(View frame, Object itemAtPosition,
                             float rotation_degrees, FlingListener flingListener, int width) {
        super();
        this.frame = frame;
        this.objectX = frame.getX();
        this.objectY = frame.getY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
        this.BASE_ROTATION_DEGREES = rotation_degrees;
        this.mFlingListener = flingListener;
        this.mCenterY = width;
        this.linjiewidth = width / 8;

        setAndroidSDKVersion();

        getChild();
        Log.e("WIDTH", width + "");

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                float x = 0, y = 0;
                try {
                    mActivePointerId = event.getPointerId(0);
                    x = event.getX(mActivePointerId);
                    y = event.getY(mActivePointerId);
                    mLastX = event.getX();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // Save the ID of this pointer


                // Remember where we started
                aDownTouchX = x;
                aDownTouchY = y;
                // mCenterX = x;
                dox = event.getRawX();
                mLastY = event.getY();
                mCenterX = mLastX;
                // to prevent an initial jump of the magnifier, aposX and aPosY must
                // have the values from the magnifier frame
                if (aPosX == 0) {
                    aPosX = frame.getX();
                }
                if (aPosY == 0) {
                    aPosY = frame.getY();
                }

                if (y < objectH / 2) {
                    touchPosition = TOUCH_ABOVE;
                } else {
                    touchPosition = TOUCH_BELOW;
                }

                break;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                resetCardViewOnStack();
                restDrable();
                Log.e("action", "ACTION_UP");
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                ZstartT = 0;
                restDrable();
                break;
            case MotionEvent.ACTION_MOVE:
                // Find the index of the active pointer and fetch its position
                final int pointerIndexMove = event
                        .findPointerIndex(mActivePointerId);
                final float xMove = event.getX(pointerIndexMove);
                final float yMove = event.getY(pointerIndexMove);
                float mx = event.getRawX();
                float my = event.getY();

                // from
                // http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Calculate the distance moved
                final float dx = xMove - aDownTouchX;
                final float dy = yMove - aDownTouchY;
                float bx = mx - dox;
                double ss = bx / linjiewidth;
                if (bx > 1) {
                    ZstartT = (int) (ss * 255);
                    if(curentVersion<21) {
                        drablebuzan.setAlpha(0);
                    }
                    else{
                        buzan.getBackground().setAlpha(0);
                    }
                    if (ZstartT > 255) {
                        ZstartT = 255;
                    }
                    if(curentVersion<21) {
                        drablezan.setAlpha((int) (ZstartT));
                    }
                    else{
                        zan.getBackground().setAlpha(ZstartT);
                    }

                } else if (bx < -1) {

                    BstartT = (int) (Math.abs(ss) * 255);
                    if(curentVersion<21) {
                        drablezan.setAlpha(0);
                    }
                    else{
                        zan.getBackground().setAlpha(0);
                    }
//                    Log.e("ֵ", BstartT + "");
                    if (BstartT > 255) {
                        BstartT = 255;
                    }
                    if(curentVersion<21) {
                        drablebuzan.setAlpha((int) (BstartT));
                    }
                    else{
                        buzan.getBackground().setAlpha(BstartT);
                    }

                }
                buzan.invalidate();
                zan.invalidate();

                // Move the frame
                aPosX += dx;
                aPosY += dy;

                // calculate the rotation degrees
                float distobjectX = aPosX - objectX;
                float rotation = BASE_ROTATION_DEGREES * 2.f * distobjectX
                        / parentWidth;
                if (touchPosition == TOUCH_BELOW) {
                    rotation = -rotation;
                }

                // in this area would be code for doing something with the view as
                // the frame moves.
                frame.setX(aPosX);
                frame.setY(aPosY);
                frame.setRotation(rotation);
                break;

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                restDrable();
                Log.e("action", "ACTION_UP");

                break;
            }
        }

        return true;
    }

    private void getChild() {
        if (frame == null) {
            return;
        }
        zan = (ImageView) frame.findViewById(R.id.zan);
        buzan = (ImageView) frame.findViewById(R.id.buzan);
        drablezan = zan.getResources().getDrawable(R.mipmap.zan);
        drablebuzan = buzan.getResources().getDrawable(R.mipmap.buzan);
        restDrable();
        // buzan.getBackground().setAlpha(100);

    }

    private void restDrable() {
        String ss=getHandSetInfo();
        //int version=getAndroidSDKVersion();
        if(curentVersion<21) {
            drablezan.setAlpha(0);
            drablebuzan.setAlpha(0);
            buzan.invalidate();
            zan.invalidate();
        }
        else{
            buzan.getBackground().setAlpha(0);
            zan.getBackground().setAlpha(0);
            //drablezan.setAlpha(0);
            //drablebuzan.setAlpha(0);
            buzan.invalidate();
            zan.invalidate();
        }
    }

    private  void setAndroidSDKVersion() {
        try {
            curentVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            //Log.e(e.toString());
        }
    }

    private String getHandSetInfo(){
        String handSetInfo=
                "手机型号:" + android.os.Build.MODEL +
                        ",SDK版本:" + android.os.Build.VERSION.SDK +
                        ",系统版本:" + android.os.Build.VERSION.RELEASE+
                        ",软件版本:"+getAppVersionName(WJSJApplication.app);
        return handSetInfo;

    }

    //获取当前版本号
    private  String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo("com.bm.wjsj", 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }



    private boolean resetCardViewOnStack() {
        if (aPosX + halfWidth < leftBorder()) {
            // Left Swipe
            onSelected(true, getExitPoint(-objectW), 100);
        } else if (aPosX + halfWidth > rightBorder()) {
            // Right Swipe
            onSelected(false, getExitPoint(parentWidth), 100);
        } else {
            float abslMoveDistance = Math.abs(aPosX - objectX);
            aPosX = 0;
            aPosY = 0;
            aDownTouchX = 0;
            aDownTouchY = 0;
            frame.animate().setDuration(200)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .x(objectX).y(objectY).rotation(0);
            if (abslMoveDistance < 4.0) {
                mFlingListener.onClick(dataObject);
            }
        }
        return false;
    }

    public float leftBorder() {
        return parentWidth / 4.f;
    }

    public float rightBorder() {
        return 3 * parentWidth / 4.f;
    }

    public void onSelected(final boolean isLeft, float exitY, long duration) {

        isAnimationRunning = true;
        float exitX;
        if (isLeft) {
            exitX = -objectW - getRotationWidthOffset();
        } else {
            exitX = parentWidth + getRotationWidthOffset();
        }

        this.frame.animate().setDuration(duration)
                .setInterpolator(new AccelerateInterpolator()).x(exitX)
                .y(exitY).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isLeft) {
                    mFlingListener.onCardExited();
                    mFlingListener.leftExit(dataObject);
                } else {
                    mFlingListener.onCardExited();
                    mFlingListener.rightExit(dataObject);
                }
                isAnimationRunning = false;
            }
        }).rotation(getExitRotation(isLeft));
    }

    /**
     * Starts a default left exit animation.
     */
    public void selectLeft() {
        if (!isAnimationRunning)
            onSelected(true, objectY, 350);
    }

    /**
     * Starts a default right exit animation.
     */
    public void selectRight() {
        if (!isAnimationRunning)
            onSelected(false, objectY, 200);
    }

    private float getExitPoint(int exitXPoint) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression = new LinearRegression(x, y);

        // Your typical y = ax+b linear regression
        return (float) regression.slope() * exitXPoint
                + (float) regression.intercept();
    }

    private float getExitRotation(boolean isLeft) {
        float rotation = BASE_ROTATION_DEGREES * 2.f * (parentWidth - objectX)
                / parentWidth;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isLeft) {
            rotation = -rotation;
        }
        return rotation;
    }

    /**
     * When the object rotates it's width becomes bigger. The maximum width is
     * at 45 degrees.
     * <p/>
     * The below method calculates the width offset of the rotation.
     */
    private float getRotationWidthOffset() {
        return objectW / MAX_COS - objectW;
    }

    public void setRotationDegrees(float degrees) {
        this.BASE_ROTATION_DEGREES = degrees;
    }

    protected interface FlingListener {
        public void onCardExited();

        public void leftExit(Object dataObject);

        public void rightExit(Object dataObject);

        public void onClick(Object dataObject);
    }

}