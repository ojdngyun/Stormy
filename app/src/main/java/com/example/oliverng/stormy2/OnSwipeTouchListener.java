package com.example.oliverng.stormy2;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Detects left and right swipes across a view.
 */
public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private Context mContext;
    private int mWidth;

    public OnSwipeTouchListener(Context context, int width) {
        mContext = context;
        mWidth = width;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft(){}
    public void onSwipeRight(){}
    public void onSwipeTop(){}
    public void onSwipeBottom(){}
    public void onFastSwipeLeft(){}
    public void onFastSwipeRight(){}
    public void onFastSwipeTop(){}
    public void onFastSwipeBottom(){}
    public void onTapNext(){}
    public void onTapPrevious(){}

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }



    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 30;
        private static final int SWIPE_VELOCITY_THRESHOLD = 10;
        private static final int SWIPE_HIGHVELOCITY_THRESHOLD = 3000;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float origin = mWidth / 2;
            float x = e.getX();
            if(x > origin){
                onTapNext();
            }else{
                onTapPrevious();
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            float distanceX = e2.getX() - e1.getX();
//            float distanceY = e2.getY() - e1.getY();
//            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
//                if (distanceX > 0)
//                    onSwipeRight();
//                else
//                    onSwipeLeft();
//                return true;
//            }
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                                onSwipeRight();
                        } else {
                                onSwipeLeft();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            if(Math.abs(velocityY) > SWIPE_HIGHVELOCITY_THRESHOLD){
                                onFastSwipeBottom();
                            }else {
                                onSwipeBottom();
                            }
                        } else {
                            if(Math.abs(velocityY) > SWIPE_HIGHVELOCITY_THRESHOLD){
                                onFastSwipeTop();
                            }else {
                                onSwipeTop();
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return false;
        }
    }
}

