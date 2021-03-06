package com.cijo7.diaryline.Utility;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by cijo-saju on 20/1/16.
 *
 */
public class BitmapWrapper {
    private Bitmap bitmap=null;
    private int reqWidth;
    private Context mContext;
    private String fileName;

    /**
     * Constructor
     * @param b Bitmap Image
     * @param context Context of Caller Activity
     * @param rW Required Width. Image is scaled on basis of this.
     * @param filename Filename to be used
     */
    public BitmapWrapper(Bitmap b,Context context,int rW,String filename){
        bitmap=b;
        reqWidth=rW;
        mContext=context;
        fileName=filename;
    }

    int getReqWidth() {
        return reqWidth;
    }



    Bitmap getBitmap() {
        return bitmap;
    }

    public Context getContext() {
        return mContext;
    }

    String getFileName() {
        return fileName;
    }
    void recycleBitmap(){
        if(bitmap!=null)
        bitmap.recycle();
    }
}
