package com.lin.mymusicplayer.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * Created by Lin on 2017/5/26.
 */

public class TintManager {

    private static final String TAG = "TintManager";
    private static final boolean DEBUG = false;
    private static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
    private static final String SKIP_DRAWABLE_TAG = "appcompat_skip_skip";

    private static final WeakHashMap<Context, TintManager> INSTANCE_CACHE = new WeakHashMap<>();
    private static final ColorFilterLruCache COLOR_FILTER_CACHE = new ColorFilterLruCache(6);

    private final Object mDrawableCacheLock = new Object();

    private WeakReference<Context> mContextRef;
    private SparseArray<ColorStateList> mCacheTintList;
    private SparseArray<WeakReference<Drawable.ConstantState>> mCacheDrawables;
    private SparseArray<String> mSkipDrawableIdTags;

    public TintManager(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    public static TintManager get(Context context) {
        if (context == null)
            return null;

        if (context instanceof ContextThemeWrapper){
            context = ((ContextThemeWrapper)context).getBaseContext();
        }

        if (context instanceof android.view.ContextThemeWrapper) {
            context = ((android.view.ContextThemeWrapper) context).getBaseContext();
        }

        TintManager tm = INSTANCE_CACHE.get(context);
        if (tm == null) {
            tm = new TintManager(context);
            INSTANCE_CACHE.put(context,tm);
        }

        return tm;
    }

    public static void tintViewDrawable(View view,Drawable drawable, TintInfo tintInfo){
        if (view == null || drawable == null)
            return;
        if (tintInfo.mHasTintList || tintInfo.mHasTintMode){
            drawable.mutate();
            if (drawable instanceof ColorDrawable){
                ((ColorDrawable) drawable).setColor(ThemeUtils.replaceColor(view.getContext(),
                        tintInfo.mTintList.getColorForState(view.getDrawableState(), tintInfo.mTintList.getDefaultColor())));
            } else {
                drawable.setColorFilter(createTintFilter(view.getContext(),
                        tintInfo.mHasTintList ? tintInfo.mTintList: null,
                        tintInfo.mHasTintMode ? tintInfo.mTintMode : DEFAULT_MODE ,
                        view.getDrawableState()));
            }
        } else {
            drawable.clearColorFilter();
        }

        if (Build.VERSION.SDK_INT <= 23){
            drawable.invalidateSelf();
        }
    }

    private static PorterDuffColorFilter createTintFilter(Context context, ColorStateList tint, PorterDuff.Mode tintMode, final int[] state){
        if (tint == null || tintMode == null){
            return null;
        }
        final int color = ThemeUtils.replaceColor(context, tint.getColorForState(state, tint.getDefaultColor()));
        return getPorterDuffColorFilter(color, tintMode);
    }

    private static PorterDuffColorFilter getPorterDuffColorFilter(int color, PorterDuff.Mode mode){
        PorterDuffColorFilter filter = COLOR_FILTER_CACHE.get(color, mode);

        if (filter == null){
            filter = new PorterDuffColorFilter(color, mode);
            COLOR_FILTER_CACHE.put(color, mode, filter);
        }
        return filter;
    }

    private static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter> {

        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public ColorFilterLruCache(int maxSize) {
            super(maxSize);
        }

        PorterDuffColorFilter get(int color, PorterDuff.Mode mode) {
            return get(generateCacheKey(color, mode));
        }

        PorterDuffColorFilter put(int color, PorterDuff.Mode mode, PorterDuffColorFilter filter) {
            return put(generateCacheKey(color, mode), filter);
        }

        private static int generateCacheKey(int color, PorterDuff.Mode mode) {
            int hashCode = 1;
            hashCode = 31*hashCode +color;
            hashCode = 31*hashCode + mode.hashCode();
            return hashCode;
        }

    }


    private static void printLog(String msg){
        if (DEBUG){
            Log.i(TAG, msg);
        }
    }

}
