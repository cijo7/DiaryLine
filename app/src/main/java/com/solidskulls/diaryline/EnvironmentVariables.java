package com.solidskulls.diaryline;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Environment variables
 */
public class EnvironmentVariables {
    //Global DATA

    public static int SCREEN_WIDTH;
    static int SCREEN_HEIGHT;

    private EnvironmentVariables(){

    }
    static void initialise(Context context){
        DisplayMetrics metrics=new DisplayMetrics();//Find out Screen Size
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        SCREEN_WIDTH=metrics.widthPixels;
        SCREEN_HEIGHT=metrics.heightPixels;
    }

}
