package com.solidskulls.diaryline;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by cijo-saju on 26/1/16.
 */
public class EnvironmentVariables {
    //Global DATA

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    private EnvironmentVariables(){

    }
    public static void initialise(Context context){
        DisplayMetrics metrics=new DisplayMetrics();//Find out Screen Size
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        SCREEN_WIDTH=metrics.widthPixels;
        SCREEN_HEIGHT=metrics.heightPixels;
    }

}
