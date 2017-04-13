package com.cijo7.diaryline.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by cijo-saju on 20/1/16.
 *Bitmap compressor is capable of compressing images for usage as per display requirement
 */
public class BitmapCompressor extends AsyncTask<BitmapWrapper,Void ,Void>{
    @Override
    protected Void  doInBackground(BitmapWrapper... bitmapWrappers) {
        int w,h,rW;
        float sX;
        Bitmap bitmap;
        Matrix matrix=new Matrix();
        Context context;
        FileOutputStream fileOutputStream=null;
        try {
            for (BitmapWrapper bitmapWrapper : bitmapWrappers) {
                w = bitmapWrapper.getBitmap().getWidth();
                h = bitmapWrapper.getBitmap().getHeight();
                rW = bitmapWrapper.getReqWidth();
                context = bitmapWrapper.getContext();
                sX = (float) rW / w;                                                                //Width dependent Scaling
                matrix.postScale(sX, sX);
                bitmap = Bitmap.createBitmap(bitmapWrapper.getBitmap(), 0, 0, w, h, matrix, true);
                bitmapWrapper.recycleBitmap();
                try {
                    fileOutputStream = context.openFileOutput(bitmapWrapper.getFileName(), Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, fileOutputStream);               //137kb on test device. I think we need another way to achieve more.
                } catch (FileNotFoundException e) {                                                 // Its enough for initial release.
                    Timber.d(e,"File Not Found");
                }
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
                    Timber.d("Compressed Size:" + bitmap.getAllocationByteCount() / 1024 + " Kb");
                else
                    Timber.d("Compressed Size:" + bitmap.getByteCount() / 1024 + " Kb");
                bitmap.recycle();
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Timber.d(e,"IOException" );
                    }
                }
            }
        }catch (Exception e){
            Timber.d(e,"Exception");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Timber.d("Task Completed");
    }
}
