package com.lin.mymusicplayer.widgets;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.lin.mymusicplayer.R;
import com.lin.mymusicplayer.utils.TintInfo;
import com.lin.mymusicplayer.utils.TintManager;

/**
 * Created by Lin on 2017/5/26.
 */

public class AppCompatProgressBarHelper extends AppCompatBaseHelper {

    private static final int ATTR[] = new int[]{
            R.attr.progressTint,
            R.attr.progressIndeterminateTint
    };

    private int mProgressTintResId;
    private int mIndeterminateTintResId;

    private TintInfo mProgressTintInfo;
    private TintInfo mIndeterminateTintInfo;



    public AppCompatProgressBarHelper(View mView, TintManager mTintManager) {
        super(mView, mTintManager);
    }

    @Override
    void loadFromAttribute(AttributeSet attrs, int defStyleAttr) {
        TypedArray array = mView.getContext().obtainStyledAttributes(attrs,ATTR,defStyleAttr,0);

        if (array.hasValue(0)){
            mProgressTintResId = array.getResourceId(0,0);
            setSupportProgressTint()
        }
    }


    public void setSupportProgressTint(ColorStateList tint){
        if (tint != null){
            if (mProgressTintInfo == null){
                mProgressTintInfo = new TintInfo();
            }
            mProgressTintInfo.mHasTintList = true;
            mIndeterminateTintInfo.mHasTintList = ColorStateList.valueOf(ThemeUtils.getColor(mView.getContext(),tint.getDefaultColor()));
        }
        applySupportIndeterminateTint();
    }


    private void applySupportIndeterminateTint(){
        Drawable mIndeterminateDrawable = ((ProgressBar) mView).getIndeterminateDrawable();
        if (mIndeterminateDrawable != null && mIndeterminateTintInfo != null){
            final TintInfo tintInfo = mIndeterminateTintInfo;
            if (tintInfo.mHasTintList || tintInfo.mHasTintMode){
                ((ProgressBar) mView).setIndeterminateDrawable(mIndeterminateDrawable = mIndeterminateDrawable.mutate());
                TintManager
            }
        }
    }

    @Override
    public void tint() {

    }
}
