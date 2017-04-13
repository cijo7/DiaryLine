package com.cijo7.diaryline.Utility;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

import timber.log.Timber;

/**
 * Created by cijo-saju on 24/1/16.
 * Te ultimate font catching
 */
public class FontCatcher {
    private static Hashtable<String,Typeface> fontCache=new Hashtable<>();

    /**
     * Caches and returns a typeface.
     * @param context Current context
     * @param fontName Font Resource name
     * @return Typeface
     */
    public static Typeface get(Context context,String fontName){
        Typeface tf=fontCache.get(fontName);
        if(tf==null){

            try {
                tf=Typeface.createFromAsset(context.getAssets(),"fonts/"+fontName);
            } catch (Exception e) {
                Timber.d(e,"Exception FC");
                return null;
            }
            fontCache.put(fontName,tf);
        }
        return tf;
    }
}
