package com.solidskulls.diaryline.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.solidskulls.diaryline.Utility.FontCatcher;
import com.solidskulls.diaryline.R;

import timber.log.Timber;

/**
 *A Date Widget which will display date in the format DAY MON YEAR
 * Created by cijo-saju on 24/1/16.
 *
 */
public class DateView extends View {
    private Paint mPaintDate;
    private Paint mPaintMonthYear;
    private String date="18",month="JAN",year="2016";
    private float height,width, dY, mY, yY,dX,mX,yX;

    public DateView(Context context) {
        super(context);
    }

    public DateView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        TypedArray typedArray=context.obtainStyledAttributes(attributeSet,
                R.styleable.DateView);
        mPaintDate=new Paint(Paint.DITHER_FLAG);
        mPaintDate.setTypeface(FontCatcher.get(context,
                typedArray.getString(R.styleable.DateView_fontName)));
        mPaintDate.setTextAlign(Paint.Align.LEFT);
        mPaintMonthYear=new Paint(mPaintDate);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int hMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        int width,height;
        if(wMode==MeasureSpec.EXACTLY){
            width=widthSize;
        }else if(wMode==MeasureSpec.AT_MOST){
            width=Math.min(widthSize,500);
        }else {
            width=500;
        }
        if(hMode==MeasureSpec.EXACTLY){
            height=heightSize;
        }else if(hMode==MeasureSpec.AT_MOST){
            height=Math.min(50,heightSize);
        }else {
            height=50;
        }
        this.height=height;
        this.width=width;
        init();
        setMeasuredDimension(width, height);
    }
    private void init(){
        mPaintDate.setTextSize(height);
        mPaintMonthYear.setTextSize(height * 2 / 5);

        Rect rect=new Rect();
        mPaintDate.getTextBounds(date,0,date.length(),rect);
        dX=0;
        dY=height/2-rect.exactCenterY();

        mY=height/2;
        yY=height/2-rect.exactCenterY();
        yX=mX=width/2;
    }

    /**
     * Sets the display date of diary.
     * @param str Date string separated by spaces.
     */
    public void setDate(String str){
        String[] arr=str.split(" ");
        try {
            date = arr[0];
            month = arr[1];
            year = arr[2];
        }catch(Exception e){
            Timber.d(e," Exception DV");
        }
        Rect rect=new Rect();
        mPaintDate.getTextBounds(date,0,date.length(),rect);
        yY=dY =height/2-rect.exactCenterY();//Both at same level
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(date, dX, dY, mPaintDate);
        canvas.drawText(month, mX, mY, mPaintMonthYear);
        canvas.drawText(year,yX,yY,mPaintMonthYear);
    }
}
