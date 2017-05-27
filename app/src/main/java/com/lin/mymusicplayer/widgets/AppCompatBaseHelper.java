package com.lin.mymusicplayer.widgets;

import android.util.AttributeSet;
import android.view.View;

import com.lin.mymusicplayer.utils.TintManager;

/**
 * Created by Lin on 2017/5/26.
 */

public abstract class AppCompatBaseHelper {

    protected View mView;
    protected TintManager mTintManager;

    private boolean mSkipNextApply;

    public AppCompatBaseHelper(View mView, TintManager mTintManager) {
        this.mView = mView;
        this.mTintManager = mTintManager;
    }

    protected boolean skipNextApply(){
        if (mSkipNextApply){
            mSkipNextApply = false;
            return true;
        }
        mSkipNextApply = true;
        return false;
    }

    protected void setSkipNextApply(boolean flag) {
        mSkipNextApply = flag;
    }

    abstract void loadFromAttribute(AttributeSet attrs, int defStyleAttr);

    public abstract void tint();
}
