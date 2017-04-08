package com.solidskulls.diaryline.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cijo-saju on 25/1/16.
 * <p>
 *     DataBlock Container is a wrapper class which wraps the individual data into a single entity.
 *     This can then be transported across classes for data display and manipulation.
 * </p>
 */
public class DataBlockContainer implements Parcelable{
    /**
     * Basic Data of a DataBlock type.
     */
    private String title,text,date,reminder;

    /**
     * Used by different DataBlock types to hold data specific to them. This saves us from having multiple tables for each
     * types.
     */
    private String tag;
    /**
     * The id of each data block corresponding to that in database.
     */
    private long id;

    /**
     * To Create an Empty data block which may be filled later.
     */
    public DataBlockContainer(){

    }

    /**
     * Constructor to initialise a dataBlock.
     * @param title title
     * @param text text content
     * @param date date of creation of note in the format yyyy-MM-DD hh:mm:ss
     * @param reminder reminder date in the format yyyy-MM-DD hh:mm:ss
     * @param tag tag data of type of note
     */
    public DataBlockContainer(String title,String text,String date,String reminder,String tag){
        this.title=title;
        this.text=text;
        this.date=date;
        this.reminder=reminder;
        this.tag = tag;
    }
    private DataBlockContainer(Parcel in){
        String[] str=new String[5];
        in.readStringArray(str);
        title=str[0];
        text=str[1];
        date=str[2];
        reminder=str[3];
        tag=str[4];
        id=in.readLong();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Return true when text and date is empty.
     * @return return true if text and is empty
     */
    boolean IsEmpty(){
        return (text.isEmpty()&&date.isEmpty());
    }


    public static final Parcelable.Creator CREATOR=new Parcelable.Creator(){

        @Override
        public Object createFromParcel(Parcel source) {
            return new DataBlockContainer(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new DataBlockContainer[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{title,text,date,reminder,tag});
        dest.writeLong(id);
    }
}
