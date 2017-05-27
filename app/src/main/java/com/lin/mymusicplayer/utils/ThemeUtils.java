package com.lin.mymusicplayer.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;

/**
 * Created by Lin on 2017/5/27.
 */

public class ThemeUtils {

    private static final ThreadLocal<TypedValue> TL_TYPED_VALUE = new ThreadLocal<>();

    public static final int[] DISABLED_STATE_SET = new int[]{-android.R.attr.state_enabled};
    public static final int[] ENABLED_STATE_SET = new int[]{android.R.attr.state_enabled};
    public static final int[] FOCUSED_STATE_SET = new int[]{android.R.attr.state_focused};
    public static final int[] ACTIVATED_STATE_SET = new int[]{android.R.attr.state_activated};
    public static final int[] PRESSED_STATE_SET = new int[]{android.R.attr.state_pressed};
    public static final int[] CHECKED_STATE_SET = new int[]{android.R.attr.state_checked};
    public static final int[] SELECTED_STATE_SET = new int[]{android.R.attr.state_selected};
    public static final int[] EMPTY_STATE_SET = new int[0];

    private static final int[] TEMP_ARRAY = new int[1];

    public static Drawable tintDrawable(Drawable drawable, int color, PorterDuff.Mode mode){
        if (drawable == null)
            return null;
        Drawable wrapper = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(wrapper,color);
        DrawableCompat.setTintMode(drawable, mode);
        return wrapper;
    }

    public static switchColor mSwitchColor;

    static int replaceColor(Context context, int color){
        return mSwitchColor == null? Color.TRANSPARENT:mSwitchColor.replaceColor(context, color);
    }


    public interface switchColor{
        int replaceColorById(Context context, int colorId);

        int replaceColor(Context context, int color);
    }
}
