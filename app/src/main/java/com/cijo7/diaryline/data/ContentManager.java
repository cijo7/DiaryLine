package com.cijo7.diaryline.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.HashMap;

public class ContentManager extends ContentProvider {
    /* Database Constants*/
    private static SQLiteDatabase db=null;
    static final String DATABASE_NAME="dbDiary.db";
    static final int DATABASE_VERSION=1;
    static final String PROVIDER_NAME="com.cijo7.diaryline";


    /**
     * <h1>Table Notes</h1>
     * The table holds the data about Notes of type like Lists and Custom Notes.<br/>
     *
     * Table columns are :ID,TITLE,TEXT,TAG,DATE,REMINDER
     */
    static abstract class Notes implements BaseColumns{
         static final String TABLE_NAME ="tbNotes";
        /**
         * Title of note. This cannot be null.<br/>
         * TYPE text
         */
        static final String NOTES_TITLE ="title";
        /**
         * The content of notes. This is usually used to store the xml of text.<br/>
         * TYPE text
         */
        static final String NOTES_TEXT ="textField";
        /**
         * The tag indicating type of note.<br/>
         * TYPE integer
         */
        static final String NOTES_TAG ="tags";
        /**
         * The date of creation of note.<br/>
         * TYPE text
         */
        static final String NOTES_DATE ="date";
        /**
         * The date on which reminder is set<br/>
         * TYPE text
         */
        static final String NOTES_REMINDER="reminder";
        private static final String CREATE_DB_TABLE ="CREATE TABLE " + TABLE_NAME + " ( " +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                 NOTES_TITLE +" TEXT NOT NULL,"+ NOTES_TEXT +" TEXT NOT NULL,"+ NOTES_TAG +" INTEGER,"+ NOTES_DATE + " TEXT,"+NOTES_REMINDER+" TEXT);";
        private static final String URL="content://"+PROVIDER_NAME+"/"+ TABLE_NAME;
        /**
         * Content URI of Notes.
         */
        static final Uri CONTENT_URI =Uri.parse(URL);
        private static final String CONTENT_TYPE_ID ="vnd.android.cursor.item/vnd.cijo7.diaryline.dataN";
        private static final String CONTENT_TYPE ="vnd.android.cursor.dir/vnd.cijo7.diaryline.dataN";
        /**
         * Indicator of type of request.<br/>
         * <i>SINGLE_ROW</i>  It's a single request.<br/>
         * <i>MULTI_ROW</i>     It's a multiple request.
         */
        private static final int SINGLE_ROW =1, MULTI_ROW =2;
    }

    /**
     * <h1>Table Tags</h1>
     * The table holds the data about custom tags made by user.<br/>
     *
     *l Table columns are: ID,NAME,ICON,COLOR.
     */
    static abstract class Tags implements BaseColumns{
        private static final String TABLE_NAME="tbTags";
        /**
         * Name of the tag.<br/>
         * TYPE text
         */
        static final String TAG_NAME ="tagName";
        /**
         * Color of the tag. This is stored as an id corresponding to each built in Icons. <br/>
         * TYPE integer
         */
        static final String TAG_ICON="tagIcon";
        /**
         * Color of the tag. This is stored as an id corresponding to each built in Colors. <br/>
         * TYPE integer
         */
        static final String TAG_COLOR="tagColor";
        private static final String CREATE_DB_TABLE="CREATE TABLE "+ TABLE_NAME+" ( "+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+ TAG_NAME +
                " TEXT NOT NULL,"+TAG_ICON+" INTEGER,"+TAG_COLOR+" INTEGER);";
        private static final String URL="content://"+PROVIDER_NAME+"/"+ TABLE_NAME;
        /**
         * The Uri
         */
        static final Uri CONTENT_URI =Uri.parse(URL);
        private static final String CONTENT_TYPE_ID="vnd.android.cursor.item/vnd.cijo7.diaryline.meta";
        private static final String CONTENT_TYPE="vnd.android.cursor.dir/vnd.cijo7.diaryline.meta";
        private static final int SINGLE_ROW =3, MULTI_ROW =4;
    }

    /**
     * <h1>Table Entry</h1>
     * The table contains the data of the daily diary entry.<br/>
     *
     * Table columns are: TEXT,DATE
     */
    static abstract class Entry implements BaseColumns{
        private static final String TABLE_NAME="tbEntry";
        /**
         * Text of the diary entry. <br/>
         * TYPE text
         */
        static final String ENTRY_TEXT="text";
        /**
         * Date of diary entry creation.<br/>
         * TYPE text
         */
        static final String ENTRY_DATE="date";
        private static final String CREATE_DB_TABLE="CREATE TABLE "+TABLE_NAME+" ( "+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+ENTRY_TEXT+" TEXT NOT NULL,"+ ENTRY_DATE+" TEXT NOT NULL);";
        private static final String CONTENT_TYPE="vnd.android.cursor.dir/vnd.cijo7.diaryline.dataE";
        private static final String CONTENT_TYPE_ID="vnd.android.cursor.item/vnd.cijo7.diaryline.dataE";
        static final Uri CONTENT_URI=Uri.parse("content://"+PROVIDER_NAME+"/"+TABLE_NAME);
        private static final int MULTI_ROW =5, SINGLE_ROW =6;
    }


    private static HashMap<String, String> PROJECTION_MAP_NOTES,PROJECTION_MAP_TAGS,PROJECTION_MAP_ENTRY;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, Notes.TABLE_NAME +"/", Notes.MULTI_ROW);
        uriMatcher.addURI(PROVIDER_NAME, Notes.TABLE_NAME + "/#", Notes.SINGLE_ROW);
        uriMatcher.addURI(PROVIDER_NAME,Tags.TABLE_NAME+"/", Tags.MULTI_ROW);
        uriMatcher.addURI(PROVIDER_NAME,Tags.TABLE_NAME+"/#", Tags.SINGLE_ROW);
        uriMatcher.addURI(PROVIDER_NAME,Entry.TABLE_NAME+"/",Entry.MULTI_ROW);
        uriMatcher.addURI(PROVIDER_NAME, Entry.TABLE_NAME + "/#", Entry.SINGLE_ROW);

        PROJECTION_MAP_NOTES =new HashMap<>();
        PROJECTION_MAP_NOTES.put(Notes._ID, Notes._ID);
        PROJECTION_MAP_NOTES.put(Notes.NOTES_TITLE, Notes.NOTES_TITLE);
        PROJECTION_MAP_NOTES.put(Notes.NOTES_TEXT, Notes.NOTES_TEXT);
        PROJECTION_MAP_NOTES.put(Notes.NOTES_TAG, Notes.NOTES_TAG);
        PROJECTION_MAP_NOTES.put(Notes.NOTES_DATE, Notes.NOTES_DATE);
        PROJECTION_MAP_NOTES.put(Notes.NOTES_REMINDER, Notes.NOTES_REMINDER);

        PROJECTION_MAP_TAGS =new HashMap<>();
        PROJECTION_MAP_TAGS.put(Tags._ID, Tags._ID);
        PROJECTION_MAP_TAGS.put(Tags.TAG_NAME, Tags.TAG_NAME);
        PROJECTION_MAP_TAGS.put(Tags.TAG_ICON,Tags.TAG_ICON);
        PROJECTION_MAP_TAGS.put(Tags.TAG_COLOR, Tags.TAG_COLOR);

        PROJECTION_MAP_ENTRY=new HashMap<>();
        PROJECTION_MAP_ENTRY.put(Entry._ID,Entry._ID);
        PROJECTION_MAP_ENTRY.put(Entry.ENTRY_TEXT,Entry.ENTRY_TEXT);
        PROJECTION_MAP_ENTRY.put(Entry.ENTRY_DATE,Entry.ENTRY_DATE);

    }

    private static class DataBaseManagerHelper extends SQLiteOpenHelper{

        DataBaseManagerHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(Notes.CREATE_DB_TABLE);
            db.execSQL(Tags.CREATE_DB_TABLE);
            db.execSQL(Entry.CREATE_DB_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){// FIXME: 25/1/16 Set a way to update from database versions
            db.execSQL("DROP TABLE IF EXISTS " + Notes.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS "+Tags.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS "+Entry.TABLE_NAME);
            onCreate(db);
        }
    }

    public ContentManager() {
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DataBaseManagerHelper dbHelper = new DataBaseManagerHelper(context);
        /*
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        if(db.isReadOnly()){
            db.close();
            db=null;
            return false;
        }
        return true;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        // at the given URI.
        switch (uriMatcher.match(uri)){
            case Notes.MULTI_ROW:return Notes.CONTENT_TYPE;
            case Notes.SINGLE_ROW:return Notes.CONTENT_TYPE_ID;
            case Tags.MULTI_ROW:return Tags.CONTENT_TYPE;
            case Tags.SINGLE_ROW:return Tags.CONTENT_TYPE_ID;
            case Entry.MULTI_ROW:return Entry.CONTENT_TYPE;
            case Entry.SINGLE_ROW: return Entry.CONTENT_TYPE_ID;
            default:
                throw new IllegalArgumentException("Invalid URI"+uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values)  {
        long rowId;
        switch (uriMatcher.match(uri)) {
            case Notes.MULTI_ROW:
            rowId = db.insert(Notes.TABLE_NAME, "", values);

                if(rowId>0){
                    Uri _uri=ContentUris.withAppendedId(Notes.CONTENT_URI,rowId);
                    if(getContext()!=null)
                        getContext().getContentResolver().notifyChange(_uri,null);
                    return  _uri;
                }
            case Tags.MULTI_ROW:
                rowId=db.insert(Tags.TABLE_NAME,"",values);
                if(rowId>0){
                    Uri _uri=ContentUris.withAppendedId(Tags.CONTENT_URI,rowId);
                    if(getContext()!=null)
                        getContext().getContentResolver().notifyChange(_uri,null);
                    return  _uri;
                }
            case Entry.MULTI_ROW:
                rowId=db.insert(Entry.TABLE_NAME,"",values);
                if(rowId>0) {
                    Uri _uri = ContentUris.withAppendedId(Entry.CONTENT_URI, rowId);
                    if(getContext()!=null)
                        getContext().getContentResolver().notifyChange(_uri,null);
                    return _uri;
                }
                break;


        }
        throw new SQLException("Unable to add record:" + uri);
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)){
            case Notes.MULTI_ROW:
                qb.setTables(Notes.TABLE_NAME);
                qb.setProjectionMap(PROJECTION_MAP_NOTES);
            break;
            case Notes.SINGLE_ROW:
                qb.setTables(Notes.TABLE_NAME);
                break;
            case Tags.MULTI_ROW:
                qb.setTables(Tags.TABLE_NAME);
                qb.setProjectionMap(PROJECTION_MAP_TAGS);
                break;
            case Tags.SINGLE_ROW:
                qb.setTables(Tags.TABLE_NAME);
                break;
            case Entry.MULTI_ROW:
                qb.setTables(Entry.TABLE_NAME);
                qb.setProjectionMap(PROJECTION_MAP_ENTRY);
                break;
            case Entry.SINGLE_ROW:
                qb.setTables(Entry.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri:"+uri);
        }
        if(sortOrder==null)//If no order is specified we will sort it by order of insertion.
            sortOrder= BaseColumns._ID+" DESC";
        Cursor c=  qb.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        if(getContext()!=null)
            c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updated;
        switch (uriMatcher.match(uri)){
            case Notes.SINGLE_ROW:
               /* String where= ENTRY_DATE +"="+uri.getLastPathSegment();
                if(selection!=null)
                    where+=" AND "+selection;*/
                updated=db.update(Notes.TABLE_NAME,values,selection,selectionArgs);
                break;
            case Notes.MULTI_ROW:
                updated=db.update(Notes.TABLE_NAME,values,selection,selectionArgs);
                break;
            case Tags.SINGLE_ROW:
                updated=db.update(Tags.TABLE_NAME,values,selection,selectionArgs);
                break;
            case Tags.MULTI_ROW:
                updated=db.update(Tags.TABLE_NAME,values,selection,selectionArgs);
                break;
            case Entry.MULTI_ROW:
                updated=db.update(Entry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case Entry.SINGLE_ROW:
                updated=db.update(Entry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI for update operation"+uri);
        }
        if(getContext()!=null)
            getContext().getContentResolver().notifyChange(uri,null);
        return updated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int deleted;
        switch (uriMatcher.match(uri)){
            case Notes.SINGLE_ROW:
               /* String where= ENTRY_DATE +"="+uri.getLastPathSegment();
                if(selection!=null)
                    where+=" AND "+selection;*/
                deleted=db.delete(Notes.TABLE_NAME,selection,selectionArgs);
                break;
            case Notes.MULTI_ROW:
                deleted=db.delete(Notes.TABLE_NAME,selection,selectionArgs);
                break;
            case Tags.MULTI_ROW:
                deleted=db.delete(Tags.TABLE_NAME,selection,selectionArgs);
                break;
            case Tags.SINGLE_ROW:
                deleted=db.delete(Tags.TABLE_NAME,selection,selectionArgs);
                break;
            case Entry.MULTI_ROW:
                deleted=db.delete(Entry.TABLE_NAME,selection,selectionArgs);
                break;
            case Entry.SINGLE_ROW:
                deleted=db.delete(Entry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI for delete operation"+uri);
        }
        if(getContext()!=null)
            getContext().getContentResolver().notifyChange(uri,null);
        return deleted;
    }
}
