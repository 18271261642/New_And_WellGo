package com.truescend.gofit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sn.utils.storage.SNStorage;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.main.MainActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 作者:东芝(2017/8/16).
 * 描述:主题工具
 * 注意,该类被其他子库反射取主题色,不能被混淆
 */
public class ThemeUtil {

    private static int mAccentColor =-1;
    private static int mPrimaryColor = -1;

    public static void tintImageResource(ImageView view, @DrawableRes int imageRes) {
        Drawable drawable = view.getResources().getDrawable(imageRes);
        tintBackground(view, drawable);
    }
    public static void tintTextColorResource(TextView tv,int defColor ) {
        tintTextColorResource(tv,defColor,false);
    }
    public static void tintTextColorResource(TextView tv,int defColor,boolean reverse) {
        if(reverse)
        {
            tv.setTextColor(createColorStateList(getAccentColor(),defColor , defColor, getAccentColor()));
        }else {
            tv.setTextColor(createColorStateList(defColor, getAccentColor(), getAccentColor(), defColor));
        }
    }
    private static ColorStateList createColorStateList( int pressed, int selected, int checked,int normal) {
        int[] colors = new int[] { pressed, selected, checked, normal};
        int[][] states = new int[4][];
        states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };
        states[1] = new int[] { android.R.attr.state_selected, android.R.attr.state_enabled };
        states[2] = new int[] { android.R.attr.state_checked,android.R.attr.state_enabled};
        states[3] = new int[] {};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    private static int getNormalColor(ColorStateList colorStateList) {
        return colorStateList.getColorForState(new int[]{android.R.attr.state_enabled}, 0);
    }

    private static int getCheckedColor(ColorStateList colorStateList) {
        return colorStateList.getColorForState(new int[]{android.R.attr.state_checked}, 0);
    }

    private static int getSelectedColor(ColorStateList colorStateList) {
        return colorStateList.getColorForState(new int[]{android.R.attr.state_selected}, 0);
    }

    public static void tintBackground(View view) {
        tintBackground(view, view.getBackground());
    }

    public static void tintLTRBBackground(TextView view, int index) {
        Drawable[] compoundDrawables = view.getCompoundDrawables();
        if (compoundDrawables != null && compoundDrawables.length == 4) {
            Drawable compoundDrawable = compoundDrawables[index];
            if(compoundDrawable==null)
            {
                return;
            }
            compoundDrawable.setColorFilter(getTintColor(view), PorterDuff.Mode.SRC_ATOP);
            view.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        }
    }

    private static int getTintColor( View view) {
        return getAccentColor( );
    }

    public static void tintBackground(View view, Drawable background) {
        if (background != null) {
            if (background instanceof StateListDrawable) {
                try {
                    Drawable stateListDrawable = tintStateListCompat(view, background);
                    setDrawable(view, stateListDrawable);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                background.setColorFilter(getAccentColor(), PorterDuff.Mode.SRC_ATOP);
                setDrawable(view, background);
            }
        }
    }

    private static void setDrawable(View view, Drawable stateListDrawable) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(stateListDrawable);
        } else {
            view.setBackground(stateListDrawable);
        }
    }

    @NonNull
    private static StateListDrawable tintStateListCompat(View view,  Drawable background) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(!(background instanceof StateListDrawable))
        {
            return null;
        }
        StateListDrawable listDrawable = (StateListDrawable) background;
        Method getStateDrawableMethod = StateListDrawable.class.getDeclaredMethod("getStateDrawable", int.class);
        Method getStateCountMethod = StateListDrawable.class.getDeclaredMethod("getStateCount");
        Method getStateSetMethod = StateListDrawable.class.getDeclaredMethod("getStateSet", int.class);
        getStateDrawableMethod.setAccessible(true);
        getStateCountMethod.setAccessible(true);
        getStateSetMethod.setAccessible(true);
        StateListDrawable stateListDrawable = new StateListDrawable();
        int count = (int) getStateCountMethod.invoke(listDrawable);
        for (int i = 0; i < count; i++) {
            Drawable drawable =  (Drawable) getStateDrawableMethod.invoke(listDrawable, i );
            int[] getStateSet = (int[]) getStateSetMethod.invoke(listDrawable, i);
            if (i == 0) {
                if(drawable instanceof GradientDrawable)
                {
                    GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                    gradientDrawable.setColor(getAccentColor());


                    drawable = gradientDrawable;
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable.setColorFilter(getAccentColor(), PorterDuff.Mode.SRC_ATOP);
                } else {//compat 低版本4.4
                    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                    int intrinsicWidth = drawable.getIntrinsicWidth();
                    int intrinsicHeight = drawable.getIntrinsicHeight();
                    if (intrinsicWidth == -1 && intrinsicHeight == -1) {
                        intrinsicWidth = 50;
                        intrinsicHeight = 50;
                    }
                    Bitmap bitmap = Bitmap.createBitmap(
                            intrinsicWidth,
                            intrinsicHeight,
                            config);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
                    drawable.draw(canvas);

                    Paint paint = new Paint();
                    paint.setColorFilter(new PorterDuffColorFilter(getAccentColor(), PorterDuff.Mode.SRC_ATOP));
                    Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
                    Canvas canvasResult = new Canvas(bitmapResult);
                    canvasResult.drawBitmap(bitmap, 0, 0, paint);
                    drawable = new BitmapDrawable(view.getResources(), bitmapResult);
                }
            } else {
                if (view.getTag() != null && view.getTag().equals("tint_other_side")) {
                    if (drawable instanceof GradientDrawable) {
                        GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                        gradientDrawable.setStroke(2, getAccentColor());
                        drawable = gradientDrawable;
                    }
                }
            }
            stateListDrawable.addState(getStateSet, drawable);
        }
        return stateListDrawable;
    }

    private static int themes[]={
            R.style.AppTheme1,
//            R.style.AppTheme2,
//            R.style.AppTheme3,
//            R.style.AppTheme4,
//            R.style.AppTheme5,
//            R.style.AppTheme6,
//            R.style.AppTheme7,
//            R.style.AppTheme8,
//            R.style.AppTheme9,
//            R.style.AppTheme10,
//            R.style.AppTheme11,
    };
    public static void initTheme(Activity activity){
        int theme = themes[SNStorage.getValue("theme",0)];
        activity.setTheme(theme);
    }

    public static void resetTheme(Activity activity,int themeIndex){
        SNStorage.setValue("theme",themeIndex);
        ThemeUtil.mAccentColor = -1;
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(0,0);
        activity.finish();
    }

    public static void initInThemeActivity(Context context) {
        if(ThemeUtil.mAccentColor ==-1) {
            TypedValue typedValue = new TypedValue();
            TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
            int color = a.getColor(0, 0);
            a.recycle();
            ThemeUtil.mAccentColor = color;
        }
        if(ThemeUtil.mPrimaryColor ==-1) {
            TypedValue typedValue = new TypedValue();
            TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
            int color = a.getColor(0, 0);
            a.recycle();
            ThemeUtil.mPrimaryColor = color;
        }
    }


    public static int getAccentColor( ){
        return mAccentColor;
    }
    public static int getPrimaryColor( ){
        return mPrimaryColor;
    }
}
