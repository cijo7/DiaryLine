package com.solidskulls.diaryline;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cijo-saju on 12/1/16.
 * CS Inc
 */
public class DataBlockManager {

    private static ContentManager contentManager;
    private String string;
    private int tDays;
    private long tMilliSeconds;
    private String mDate;

    static Uri lastUri;

    DataBlockManager(){
        contentManager =new ContentManager();

        Date date=new Date();
        tMilliSeconds=date.getTime();
        mDate=SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(date.getTime());
        Log.d("Time", mDate);

        tDays=(int)(tMilliSeconds/(1000*60*60*24));
        string=null;
    }

    /**
     *
     * @param milliSeconds number of milliseconds passed
     */
    DataBlockManager(long milliSeconds){
        contentManager =new ContentManager();

        Date date=new Date();
        date.setTime(milliSeconds);
        mDate=SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(date.getTime());
        tMilliSeconds=milliSeconds;

        tDays=(int)(tMilliSeconds/(1000*60*60*24));
        string=null;
    }

    /**
     * Adds a new Data Package to the System database
     */
    public boolean addPackage(String text){
        ContentValues values = new ContentValues();
        values.put(ContentManager.DATE, tDays);
        values.put(ContentManager.TEXT,text);
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
            res = contentManager.update(ContentManager.CONTENT_URI, values, ContentManager.DATE+"="+tDays, null);
        }catch (SQLException e){
            Log.d("DBM Update","Unable to update data to DB.");
        }
        if(res==0)
            Log.d("DBM Update","Unable to update record");
        return  res!=0;
    }

    /**
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


    public long getMilliSeconds(){
        return tMilliSeconds;
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
    public long oldDaySec(long mSeconds,int no){
        return mSeconds-(1000*60*60*24)*no;
    }
}
