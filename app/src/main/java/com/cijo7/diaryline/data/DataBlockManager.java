package com.cijo7.diaryline.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import com.cijo7.diaryline.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cijo-saju on 12/1/16.
 * A Singleton class handling all the data.
 */
public class DataBlockManager {

   // private static DataBlockManager dataBlockManagerInstance=new DataBlockManager();

    private static Uri lastUri;


    /**
     * Creates a Heart DataBlock which is responsible for Initiating purposes.
     */
    @SuppressWarnings("unused")
    public void init(Context context){
   /*     contentManager =new ContentManager();

        Calendar calendar=Calendar.getInstance();
        currentMilliSeconds=calendar.getTimeInMillis();

        Timber.d(SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.LONG).format(calendar.getTime()) + " Before");
// FIXME: 16/1/16 Bug when used in Daylight saving time.
        long tMilliSeconds=calendar.getTimeInMillis()+TimeZone.getDefault().getRawOffset();
        calendar.setTimeInMillis(tMilliSeconds);
        currentTDays=(int)(tMilliSeconds/(1000*60*60*24));

        DiaryTextPreview.updateBitmap(context);//Initialise Sign Bitmap




        Timber.d("Default Date:"+currentTDays+" Days  Milliseconds:"+currentMilliSeconds);
*/
    }


    private DataBlockManager(){

    }

    /**
     * Adds a new Data Package to the System database
     * @param dataBlockContainer the data blocks to be added.
     * @param context The current Context.
     */
    public static boolean addNotes(DataBlockContainer dataBlockContainer,Context context){
        ContentValues values = new ContentValues();

        values.put(ContentManager.Notes.NOTES_TITLE, dataBlockContainer.getTitle());
        values.put(ContentManager.Notes.NOTES_TEXT,dataBlockContainer.getText());
        values.put(ContentManager.Notes.NOTES_TAG,dataBlockContainer.getTag());
        values.put(ContentManager.Notes.NOTES_DATE,dataBlockContainer.getDate());
        values.put(ContentManager.Notes.NOTES_REMINDER,dataBlockContainer.getReminder());
        try {
            lastUri = context.getContentResolver().insert(ContentManager.Notes.CONTENT_URI, values);
        }catch (SQLException e){
            Timber.d(e, "Unable to insert data to DB.");
        }
        Timber.d("Added at:"+lastUri);
        return lastUri != null;
    }


    public static boolean updateNotes(DataBlockContainer dataBlockContainer,Context context){
        int res = 0;
        ContentValues values = new ContentValues();
        values.put(ContentManager.Notes.NOTES_TITLE,dataBlockContainer.getTitle());
        values.put(ContentManager.Notes.NOTES_TEXT,dataBlockContainer.getText());
        values.put(ContentManager.Notes.NOTES_TAG,dataBlockContainer.getTag());
        values.put(ContentManager.Notes.NOTES_DATE,dataBlockContainer.getDate());
        values.put(ContentManager.Notes.NOTES_REMINDER,dataBlockContainer.getReminder());
        try {
            res = context.getContentResolver().update(ContentManager.Notes.CONTENT_URI, values, ContentManager.Notes._ID + "=" + dataBlockContainer.getId(), null);
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
    public static List<DataBlockContainer> readNotes(String date,Context context){
        List<DataBlockContainer> dataBlockContainers= new ArrayList<>();
        DataBlockContainer dataBlockContainer;
        try {
            Cursor c = context.getContentResolver().query(ContentManager.Notes.CONTENT_URI, null,ContentManager.Notes.NOTES_REMINDER+" LIKE '"+date+"%' OR ("+ContentManager.Notes.NOTES_DATE+" LIKE '"+date+"%' AND reminder='')", null, null);

            if(c!=null) {
                if (c.moveToFirst()) {
                    do {
                        dataBlockContainer=new DataBlockContainer();

                        dataBlockContainer.setTitle(c.getString(c.getColumnIndex(ContentManager.Notes.NOTES_TITLE)));
                        dataBlockContainer.setText(c.getString(c.getColumnIndex(ContentManager.Notes.NOTES_TEXT)));
                        dataBlockContainer.setDate(c.getString(c.getColumnIndex(ContentManager.Notes.NOTES_DATE)));
                        dataBlockContainer.setReminder(c.getString(c.getColumnIndex(ContentManager.Notes.NOTES_REMINDER)));
                        dataBlockContainer.setTag(c.getString(c.getColumnIndex(ContentManager.Notes.NOTES_TAG)));
                        dataBlockContainer.setId(c.getLong(c.getColumnIndex(ContentManager.Notes._ID)));
                        if(dataBlockContainer.notEmpty())
                            dataBlockContainers.add(dataBlockContainer);
                    } while (c.moveToNext());

                }
                c.close();
            }else
                Timber.d("Retrieve Failed");
            c = context.getContentResolver().query(ContentManager.Entry.CONTENT_URI, null,ContentManager.Entry.ENTRY_DATE + " LIKE '" + date+ "%'", null, null);

            if(c!=null) {
                if (c.moveToFirst()) {
                    do {
                        dataBlockContainer=new DataBlockContainer();

                        dataBlockContainer.setTitle(context.getString(R.string.diaryTitle));
                        dataBlockContainer.setText(c.getString(c.getColumnIndex(ContentManager.Entry.ENTRY_TEXT)));
                        dataBlockContainer.setDate(c.getString(c.getColumnIndex(ContentManager.Entry.ENTRY_DATE)));
                        dataBlockContainer.setTag(AppConstants.DIARY);
                        dataBlockContainer.setId(c.getLong(c.getColumnIndex(ContentManager.Entry._ID)));
                        if(dataBlockContainer.notEmpty())
                            dataBlockContainers.add(dataBlockContainer);
                    } while (c.moveToNext());
                }
                c.close();
            }else
                Timber.d("Retrieve Failed");
        }catch (IllegalArgumentException e){
            Timber.d(e,"We passed illegal arguments");
        }
        return dataBlockContainers;
    }



    /**
     * Adds a new Data Package to the System database
     * @param dataBlockContainer the data blocks to be added.
     * @param context The current Context.
     */
    public static boolean addDiary(DataBlockContainer dataBlockContainer,Context context){
        ContentValues values = new ContentValues();

        values.put(ContentManager.Entry.ENTRY_TEXT, dataBlockContainer.getText());
        values.put(ContentManager.Entry.ENTRY_DATE,dataBlockContainer.getDate());
        try {
            lastUri = context.getContentResolver().insert(ContentManager.Entry.CONTENT_URI, values);
        }catch (SQLException e){
            Timber.d(e, "Unable to insert data to DB.");
        }
        Timber.d("Added at:"+lastUri);
        return lastUri != null;
    }


    public static boolean updateDiary(DataBlockContainer dataBlockContainer,Context context){
        int res = 0;
        ContentValues values = new ContentValues();
        values.put(ContentManager.Entry.ENTRY_TEXT,dataBlockContainer.getText());
        try {
            res = context.getContentResolver().update(ContentManager.Entry.CONTENT_URI, values, ContentManager.Notes._ID + "=" + dataBlockContainer.getId(), null);
        }catch (SQLException e){
            Timber.d("Unable to update data to DB.");
        }
        if(res==0)
            Timber.d("Unable to update record");
        return  res!=0;
    }

}
