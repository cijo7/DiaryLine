package com.solidskulls.diaryline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.solidskulls.diaryline.util.BitmapCompressor;
import com.solidskulls.diaryline.util.BitmapWrapper;



public class SignatureMaker extends AppCompatActivity {

    private SignatureDrawHelper signatureDrawHelper;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signatureDrawHelper=new SignatureDrawHelper(this);
        setContentView(signatureDrawHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signature, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.signature_done:
                save();
                finish();
                break;
            case R.id.signature_reset:
                signatureDrawHelper.reset();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void save(){

        new BitmapCompressor().execute(new BitmapWrapper(bitmap,this,DataBlockManager.SCREEN_WIDTH/4,"sign.png"));
        //// TODO: 16/1/16 Make the file size smaller by scaling

    }




    public class SignatureDrawHelper extends View {
        private Paint mPaint;
        private Paint dotPaint;
        private Path mPath,dotPath;
        private Canvas mCanvas;
        private float tX,tY;
        private float strokeWidth;
        private boolean isActive=false;


        public SignatureDrawHelper(Context context){
            super(context);
            mPath=new Path();
            dotPath=new Path();

            strokeWidth=40;
            mPaint=new Paint(Paint.DITHER_FLAG);
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.BLACK);
            mPaint.setStrokeJoin(Paint.Join.MITER);
            mPaint.setStrokeWidth(strokeWidth);
            dotPaint=new Paint(mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            dotPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldW, int oldH) {
            super.onSizeChanged(w, h, oldW, oldH);

            bitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
            mCanvas=new Canvas(bitmap);
        }


        public void reset(){
            mCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(bitmap, 0, 0, mPaint);//Previous Drawing states
            canvas.drawPath(mPath, mPaint);//Real time Drawing happens here
            canvas.drawPath(dotPath,dotPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x=event.getX();
            float y=event.getY();
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            mPath.moveTo(x, y);
                            tX=x;
                            tY=y;
                            invalidate();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            mPath.quadTo(tX,tY,(tX+x)/2,(tY+y)/2);
                            tX=x;
                            tY=y;
                            isActive=true;
                            invalidate();
                            break;
                        case MotionEvent.ACTION_UP:
                            mPath.quadTo(tX,tY,(tX+x)/2,(tY+y)/2);
                            if(!isActive) {
                                dotPath.addCircle(x, y, strokeWidth / 2, Path.Direction.CW);
                                mCanvas.drawPath(dotPath,dotPaint);
                                dotPath.reset();
                            }
                            mCanvas.drawPath(mPath,mPaint);
                            mPath.reset();
                            isActive=false;
                            invalidate();
                            break;
                    }
            return  true;
        }
    }
}
