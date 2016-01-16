package com.solidskulls.diaryline;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by cijo-saju on 12/1/16.
 * CS Inc
 */
public class DataBlockManager {

    private static ContentManager contentManager;
    private static long currentMilliSeconds;
    private static int currentTDays;

    private String string;
    private int tDays;
    private String mDate;

    static Uri lastUri;

    // TODO: 14/1/16 Date change don't work properly

    /**
     * Creates a Heart DataBlock which is responsible for Initiating purposes.
     */
    DataBlockManager(){
        contentManager =new ContentManager();

        Calendar calendar=Calendar.getInstance();
        currentMilliSeconds=calendar.getTimeInMillis();

        Log.d("DataBlock",SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.LONG).format(calendar.getTime())+" Before");

        long tMilliSeconds=calendar.getTimeInMillis()+TimeZone.getDefault().getRawOffset();
        calendar.setTimeInMillis(tMilliSeconds);
        currentTDays=(int)(tMilliSeconds/(1000*60*60*24));

        string=null;

        mDate=SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.LONG).format(calendar.getTime());//Unnecessary
        Log.d("Default Date:",currentTDays+" Days  Milliseconds:"+currentMilliSeconds+" Format after:"+mDate);

    }

    /**
     *Create a new DataBlock with information from past
     * @param offsetDays number of days in past to retrieve
     */
    DataBlockManager(int offsetDays){

        Date date=new Date();
        date.setTime(currentMilliSeconds-offsetDays*24*60*60*1000);//Lets offset
        mDate=SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(date.getTime());

        tDays=currentTDays-offsetDays;
        Log.d("Date",tDays+" Day");
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
            Log.d("DBM Add","Unable to insert data to DB.");
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
            Log.d("DBM Update","Unable to update data to DB.");
        }
        if(res==0)
            Log.d("DBM Update","Unable to update record");
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
                Log.d("DBM Read","Retrieve Failed");
        }catch (IllegalArgumentException e){
            Log.d("DBM Read","We passed illegal arguments");
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
                Log.d("DBM Read","Retrieve Failed");
        }catch (IllegalArgumentException e){
            Log.d("DBM Read","We passed illegal arguments");
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
     * Get a String of date
     * @return String of Date in format DD MM YYYY
     */
    public String printableDate(){
        return mDate;
    }

    /**
     * The number of Seconds on Previous day
     * @param mSeconds Milliseconds
     * @param no No of Days
     * @return Milliseconds on old day
     */
    public static long oldDaySec(long mSeconds,int no){
        return mSeconds-(1000*60*60*24)*no;
    }

    /**
     * Access the current milliseconds
     * @return Milliseconds until now
     */
    public static long getCurrentMilliSeconds(){
        return currentMilliSeconds;
    }
}
