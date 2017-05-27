package com.lin.mymusicplayer.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.lin.mymusicplayer.interfaze.Tintable;

/**
 * Created by Lin on 2017/5/26.
 */

public class TintProgressBar extends ProgressBar implements Tintable {

    private AppCompatProgressBarHelper mProgressBarHelper;

    public TintProgressBar(Context context) {
        super(context);
    }

    public TintProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TintProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void tint() {

    }
}
