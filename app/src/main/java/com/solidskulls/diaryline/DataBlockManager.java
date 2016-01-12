package com.solidskulls.diaryline;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by cijo-saju on 12/1/16.
 * CS Inc
 */
public class DataBlockManager {

    private static ContentManager contentManager;
    private String string;
    private long tDays;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mDate;

    static Uri lastUri;

    DataBlockManager(){
        contentManager =new ContentManager();
        Calendar calendar=Calendar.getInstance();
        tDays=calendar.getTimeInMillis()/(1000*60*60*24);

        SimpleDateFormat df=new SimpleDateFormat("dd MM yyyy");
        mDate=df.format(calendar.getTime()).toString();

        string=null;
    }

    DataBlockManager(long days){
        contentManager=new ContentManager();
        tDays=days;
        string=null;
    }

    /**
     * Adds a new Data Package to the System database
     */
    public void addPackage(String text){
        ContentValues values = new ContentValues();
        values.put(ContentManager.TEXT,text);
        values.put(ContentManager.DATE, tDays);
        try {
            lastUri = contentManager.insert(ContentManager.CONTENT_URI, values);
        }catch (SQLException e){
            Log.d("DBM Add","Unable to insert data to DB.");
        }
    }

    public void updatePackage(String text){
        int res = 0;
        ContentValues values = new ContentValues();
        values.put(ContentManager.TEXT,text);
        values.put(ContentManager.DATE, tDays);
        try {
            res = contentManager.update(ContentManager.CONTENT_URI, values, null,null);
        }catch (SQLException e){
            Log.d("DBM Add","Unable to insert data to DB.");
        }
        if(res==0)
            Log.d("DBM Update","Unable to update record");
    }

    /**
     * Read the package and fill the string with data
     */
    public void readPackage(){
        String URL = ContentManager.URL;
        Uri uri = Uri.parse(URL);
        try {
            Cursor c = contentManager.query(uri, null, ContentManager.DATE + "=" +tDays, null, ContentManager.DATE);

            if(c!=null) {
                if (c.moveToFirst()) {
                    do {
                        string = c.getString(c.getColumnIndex(ContentManager.TEXT));
                    } while (c.moveToNext());
                    c.close();
                }
            }else
                Log.d("DBM Read","Retrieve Failed");
        }catch (IllegalArgumentException e){
            Log.d("DBM Read","We passed illegal arguments");
        }
    }

    /**
     * Getter method of String
     * @return Return the content of datablock
     */
    public String getStringData(){
        return string;
    }

    /**
     * Get the number of days from beginning
     * @return Number of days from 1 Jan 1970
     */
    public long getDay(){
        return tDays;
    }
    public String printableDate(){
        return mDate;
    }
}
