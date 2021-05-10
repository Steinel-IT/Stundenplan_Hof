package com.steinel_it.stundenplanhof.parts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class SetupViewPager extends ViewPager {

    private boolean isSwipingEnabled = true;

    public SetupViewPager(Context context) {
        super(context);
    }

    public SetupViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isSwipingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isSwipingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setSwipeEnabled(boolean enabled) {
        this.isSwipingEnabled = enabled;
    }

}
