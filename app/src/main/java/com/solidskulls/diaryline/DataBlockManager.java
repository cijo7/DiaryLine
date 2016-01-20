package com.solidskulls.diaryline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Created by cijo-saju on 12/1/16.
 * CS Inc
 */
public class DataBlockManager {

    private static ContentManager contentManager;
    private static long currentMilliSeconds;
    private static int currentTDays;

    private String string=null;
    private int tDays;
    private long mMilliSeconds;

    static Uri lastUri;



    //Global DATA

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    // TODO: 14/1/16 Date change don't work properly

    /**
     * Creates a Heart DataBlock which is responsible for Initiating purposes.
     */
    public static void init(Context context){
        contentManager =new ContentManager();

        Calendar calendar=Calendar.getInstance();
        currentMilliSeconds=calendar.getTimeInMillis();

        Timber.d(SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.LONG).format(calendar.getTime()) + " Before");
//// FIXME: 16/1/16 Bug when used in Daylight saving time.
        long tMilliSeconds=calendar.getTimeInMillis()+TimeZone.getDefault().getRawOffset();
        calendar.setTimeInMillis(tMilliSeconds);
        currentTDays=(int)(tMilliSeconds/(1000*60*60*24));

        DiaryTextPreview.updateBitmap(context);//Initialise Sign Bitmap

        DisplayMetrics metrics=new DisplayMetrics();//Find out Screen Size
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        SCREEN_WIDTH=metrics.widthPixels;
        SCREEN_HEIGHT=metrics.heightPixels;


        Timber.d("Default Date:"+currentTDays+" Days  Milliseconds:"+currentMilliSeconds);

    }

    /**
     *Create a new DataBlock with information from past
     * @param offsetDays number of days in past to retrieve
     */
    DataBlockManager(int offsetDays){

        Date date=new Date();
        date.setTime(mMilliSeconds=(currentMilliSeconds-((long)offsetDays)*24*60*60*1000));//Lets offset

        tDays=currentTDays-offsetDays;
        Timber.d(tDays+" Day");
        string=null;
    }

    /**
     * Adds a new Data Package to the System database
     */
    public boolean addPackage(String text){
        ContentValues values = new ContentValues();
        values.put(ContentManager.DATE, tDays);
        values.put(ContentManager.TEXT, text);
        try {
            lastUri = contentManager.insert(ContentManager.CONTENT_URI, values);
        }catch (SQLException e){
            Timber.d(e, "Unable to insert data to DB.");
        }
        return lastUri != null;
    }

    public boolean updatePackage(String text){
        int res = 0;
        ContentValues values = new ContentValues();
        values.put(ContentManager.TEXT,text);
        try {
            res = contentManager.update(ContentManager.CONTENT_URI, values, ContentManager.DATE + "=" + tDays, null);
        }catch (SQLException e){
            Timber.d("Unable to update data to DB.");
        }
        if(res==0)
            Timber.d("Unable to update record");
        return  res!=0;
    }

    /**
     * //todo Optimise reading for packages
     * Read the package and fill the string with data
     */
    public boolean readPackage(){
        String URL = ContentManager.URL;
        Uri uri = Uri.parse(URL);
        boolean status=false;
        try {
            Cursor c = contentManager.query(uri, null, ContentManager.DATE + "=" +tDays, null, ContentManager.DATE);

            if(c!=null) {
                if (c.moveToFirst()) {
                    do {
                        string = c.getString(c.getColumnIndex(ContentManager.TEXT));
                        status=true;
                    } while (c.moveToNext());
                    c.close();
                }
            }else
                Timber.d("Retrieve Failed");
        }catch (IllegalArgumentException e){
            Timber.d("We passed illegal arguments");
        }
        return status;
    }

    public boolean ifExists(){
        String URL = ContentManager.URL;
        Uri uri = Uri.parse(URL);
        boolean status=false;
        try {
            Cursor c = contentManager.query(uri, null, ContentManager.DATE + "=" +tDays, null, ContentManager.DATE);

            if(c!=null) {
                if (c.moveToFirst()) {
                    do {
                        if((c.getString(c.getColumnIndex(ContentManager.TEXT)))!=null)
                            status=true;
                    } while (c.moveToNext());
                    c.close();
                }
            }else
                Timber.d("Retrieve Failed");
        }catch (IllegalArgumentException e){
            Timber.d("We passed illegal arguments");
        }
        return status;
    }

    /**
     * Getter method of String
     * @return Return the content of dataBlock
     */
    public String getStringData(){
        return string;
    }

    /**
     * Access the current milliseconds
     * @return Milliseconds until now
     */
    public long getMilliSeconds(){
        return mMilliSeconds;
    }

    public static long getCurrentMilliseconds(){
        return currentMilliSeconds;
    }
}
