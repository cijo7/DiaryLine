package com.solidskulls.diaryline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by cijo-saju on 17/1/16.
 *
 */
public class NavigatorView extends View {
    private Paint mPaintBackground;
    private Paint mPaintTextDate,mPaintTextMonth;
    private Paint mPaintCircle;
    private static int WIDTH;
    private static int HEIGHT;

    public NavigatorView(Context context){
        super(context);
    }
    public NavigatorView(Context context,AttributeSet attributeSet){
        super(context, attributeSet);
        mPaintBackground =new Paint(Paint.DITHER_FLAG);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setStrokeWidth(3);
        mPaintTextDate =new Paint(mPaintBackground);
        mPaintCircle=new Paint(mPaintBackground);

        mPaintBackground.setColor(ContextCompat.getColor(context,R.color.navigationView_background));
        mPaintCircle.setColor(ContextCompat.getColor(context, R.color.navigationView_circle));

        mPaintTextDate.setTextAlign(Paint.Align.CENTER);
        mPaintTextMonth=new Paint(mPaintTextDate);

        mPaintTextDate.setColor(ContextCompat.getColor(context, R.color.navigationView_textDate));
        mPaintTextMonth.setColor(ContextCompat.getColor(context, R.color.navigationView_textMonthYear));

        invalidate();
    }


    /**
     * Initialise navigator
     * @param w Width
     * @param h Height
     */
    public static void navigatorViewInIt(int w,int h){
        WIDTH=w;
        HEIGHT=h/5;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, WIDTH, HEIGHT, mPaintBackground);

        canvas.drawCircle(WIDTH / 2, HEIGHT / 2, HEIGHT / 4, mPaintCircle);
        canvas.drawText("26", WIDTH / 2, HEIGHT / 2+HEIGHT / 12, mPaintTextDate);
        canvas.drawText("July 2016",WIDTH/2,3*HEIGHT/4+HEIGHT/6, mPaintTextMonth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mPaintTextDate.setTextSize(HEIGHT / 4);
        mPaintTextMonth.setTextSize(HEIGHT/6);
        setMeasuredDimension(WIDTH, HEIGHT);
    }
}
