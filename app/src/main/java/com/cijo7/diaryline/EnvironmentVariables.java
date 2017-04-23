package com.cijo7.diaryline;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Environment variables
 */
public class EnvironmentVariables {
    //Global DATA

    public static int SCREEN_WIDTH;
    static int SCREEN_HEIGHT;
    private static DisplayMetrics metrics;

    private EnvironmentVariables(){

    }
    static void initialise(Context context){
        metrics=new DisplayMetrics();//Find out Screen Size
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        SCREEN_WIDTH=metrics.widthPixels;
        SCREEN_HEIGHT=metrics.heightPixels;
    }

    public static int getPixelsFromDp(int dp){
        return (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dp, metrics );
    }

}
