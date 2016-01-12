package com.solidskulls.diaryline;

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
import android.support.annotation.NonNull;

import java.util.HashMap;

public class ContentManager extends ContentProvider {


    /* Database Constants*/
    private static SQLiteDatabase db=null;
    static final String DATABASE_NAME="dbDiary";
    static final String DATABASE_TABLE_NAME="tbPage";
    static final int DATABASE_VERSION=1;
    static final String DATE="date";
    static final String TEXT="textField";
    private static final String CREATE_DB_TABLE =" CREATE TABLE " + DATABASE_TABLE_NAME + " ( " +  DATE+ " INTEGER PRIMARY KEY, " + TEXT  +" TEXT NOT NULL);";

    /*Content Provider Constants*/
    static final String PROVIDER_NAME="solidskulls.diaryline";
    static final String URL="content://"+PROVIDER_NAME+"/"+DATABASE_TABLE_NAME;
    static final Uri CONTENT_URI=Uri.parse(URL);
    static final int ITEM=1;
    static final int LIST=2;
    static final String CONTENT_TYPE_ITEM="vnd.android.cursor.item/vnd.solidskulls.diaryline";
    static final String CONTENT_TYPE_LIST="vnd.android.cursor.dir/vnd.solidskulls.diaryline";

    private static HashMap<String, String> DIARY_PROJECTION_MAP;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,DATABASE_TABLE_NAME+"/",LIST);
        uriMatcher.addURI(PROVIDER_NAME,DATABASE_TABLE_NAME+"/#",ITEM);
    }

    private static class DataBaseManagerHelper extends SQLiteOpenHelper{

        DataBaseManagerHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_DB_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
            onCreate(db);
        }
    }

    public ContentManager() {
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DataBaseManagerHelper dbHelper = new DataBaseManagerHelper(context);
        /**
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
            case ITEM:return CONTENT_TYPE_ITEM;
            case LIST:return CONTENT_TYPE_LIST;
            default:
                throw new IllegalArgumentException("Invalid URI"+uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values)  {
        long rowId=db.insert(DATABASE_TABLE_NAME,"",values);

            if (rowId > 0)
                return ContentUris.withAppendedId(CONTENT_URI, rowId);
            throw new SQLException("Unable to add record:" + uri);
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        qb.setTables(DATABASE_TABLE_NAME);
        switch (uriMatcher.match(uri)){
            case LIST:
            qb.setProjectionMap(DIARY_PROJECTION_MAP);
            break;
            case ITEM:
                qb.appendWhere(DATE+"="+uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri:"+uri);
        }
        if(sortOrder==null)
            sortOrder=DATE;
        return  qb.query(db,projection,selection,selectionArgs,null,null,sortOrder);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updated;
        switch (uriMatcher.match(uri)){
            case ITEM:
                String where=DATE+"="+uri.getLastPathSegment();
                if(selection!=null)
                    where+=" AND "+selection;
                updated=db.update(DATABASE_TABLE_NAME,values,where,selectionArgs);
                break;
            case LIST:
                updated=db.update(DATABASE_TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI for update operation"+uri);
        }
        return updated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int deleted;
        switch (uriMatcher.match(uri)){
            case ITEM:
                String where=DATE+"="+uri.getLastPathSegment();
                if(selection!=null)
                    where+=" AND "+selection;
                deleted=db.delete(DATABASE_TABLE_NAME,where,selectionArgs);
                break;
            case LIST:
                deleted=db.delete(DATABASE_TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI for delete operation"+uri);
        }
        return deleted;
    }
}
