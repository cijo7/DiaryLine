package com.solidskulls.diaryline;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by cijo-saju on 17/1/16.
 *
 */
public class NavigatorView extends View  {
    private Paint mPaintBackground;
    private Paint mPaintTextDate,mPaintTextMonth;
    private Paint mPaintTextToUp, mPaintTextToDown;
    private int startColor;
    private Paint mPaintCircle;
    private int WIDTH;
    private int HEIGHT;
    private float offset=0,lCircleX=0,rCircleX=0;
    private static CircularArrayString circularArrayString;
    private int[] circlePositionX;
    private float radius,scaleUp=1,scaleDown=2;
    private boolean mSwipeRight=false;
    private int lastColorCode=-1;
    private float textSize;


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

        mPaintTextToDown =new Paint(mPaintTextDate);
        mPaintTextToUp =new Paint(mPaintTextDate);
        mPaintTextMonth.setColor(ContextCompat.getColor(context, R.color.navigationView_textMonthYear));
        startColor=ContextCompat.getColor(context, R.color.background1);

    }


    /**
     * Initialise navigator
     * @param w Width
     * @param h Height
     */
    public void navigatorViewInIt(int w,int h){
        WIDTH=w;
        HEIGHT=h/5;
        radius=HEIGHT/8;
        textSize=HEIGHT/8;
        lCircleX=(int)(-radius);
        rCircleX=(int)(WIDTH+radius);
        circlePositionX=new int[]{0,WIDTH/4,WIDTH/2,(WIDTH/4)*3};
        mPaintTextDate.setTextSize(textSize);
        mPaintTextMonth.setTextSize(HEIGHT / 6);
        mPaintTextToUp.setTextSize(textSize);
        mPaintTextToDown.setTextSize(textSize * 2);
        circularArrayString =new CircularArrayString("0","1","2","3","4");
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, WIDTH, HEIGHT, mPaintBackground);

        if(mSwipeRight) {

            canvas.drawCircle(circlePositionX[1] + offset, HEIGHT / 2, radius*scaleUp, mPaintCircle);
            canvas.drawCircle(circlePositionX[2]+ offset, HEIGHT / 2, radius*scaleDown, mPaintCircle);
            canvas.drawCircle(circlePositionX[3]+circlePositionX[0], HEIGHT / 2, radius, mPaintCircle);
            canvas.drawCircle(lCircleX+circlePositionX[0] , HEIGHT / 2, radius, mPaintCircle);

            canvas.drawText(circularArrayString.getNumber(1), circlePositionX[1] + offset,circularArrayString.getVerticalCentre(1), mPaintTextToUp);
            canvas.drawText( circularArrayString.getNumber(2), circlePositionX[2] + offset, circularArrayString.getVerticalCentre(2), mPaintTextToDown);
            canvas.drawText(circularArrayString.getNumber(3),circlePositionX[3] +circlePositionX[0],circularArrayString.getVerticalCentre(3), mPaintTextDate);
            canvas.drawText(circularArrayString.getNumber(0), lCircleX+circlePositionX[0],circularArrayString.getVerticalCentre(0), mPaintTextDate);
        }else{
            canvas.drawCircle(circlePositionX[1] - circlePositionX[0], HEIGHT / 2, radius ,mPaintCircle);
            canvas.drawCircle(circlePositionX[2] + offset, HEIGHT / 2, radius*scaleDown, mPaintCircle);
            canvas.drawCircle(circlePositionX[3]+ offset, HEIGHT / 2, radius*scaleUp, mPaintCircle);
            canvas.drawCircle(rCircleX-circlePositionX[0] , HEIGHT / 2, radius, mPaintCircle);

            canvas.drawText(  circularArrayString.getNumber(1), circlePositionX[1] -circlePositionX[0],circularArrayString.getVerticalCentre(1), mPaintTextDate);
            canvas.drawText( circularArrayString.getNumber(2), circlePositionX[2] + offset,circularArrayString.getVerticalCentre(2), mPaintTextToDown);
            canvas.drawText( circularArrayString.getNumber(3),circlePositionX[3]+ offset,circularArrayString.getVerticalCentre(3), mPaintTextToUp);
            canvas.drawText( circularArrayString.getNumber(0), rCircleX - circlePositionX[0], circularArrayString.getVerticalCentre(0), mPaintTextDate);
        }

        canvas.drawText("July 2016", WIDTH / 2, 3 * HEIGHT / 4 + HEIGHT / 6, mPaintTextMonth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        setMeasuredDimension(WIDTH, HEIGHT);
    }

    public void updateNavigatorAnimation(boolean swipeRight){
        final int end=getRandomColor();
        ValueAnimator animate;
        mSwipeRight=swipeRight;
        PropertyValuesHolder scale =PropertyValuesHolder.ofFloat("scale", 0, 1);
        PropertyValuesHolder color = PropertyValuesHolder.ofObject("color", new ArgbEvaluator(), startColor, end);
        startColor=end;
        if(swipeRight) {
            PropertyValuesHolder circleOffSet = PropertyValuesHolder.ofFloat("offset", offset, offset + WIDTH / 4);
            PropertyValuesHolder externalCircle = PropertyValuesHolder.ofInt("external", 0,(int) (WIDTH / 4 + radius));
            animate = ValueAnimator.ofPropertyValuesHolder(circleOffSet, externalCircle, color,scale);
            animate.setDuration(500);
            animate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    circlePositionX[0]=(int) animation.getAnimatedValue("external");
                    offset = (float) animation.getAnimatedValue("offset");
                    mPaintBackground.setColor((int) animation.getAnimatedValue("color"));
                    float factor=(float)animation.getAnimatedValue("scale");
                    scaleUp=1+factor;
                    scaleDown=2-factor;
                    mPaintTextToUp.setTextSize(textSize + textSize * factor);
                    mPaintTextToDown.setTextSize(textSize * 2 - textSize * factor);
                    circularArrayString.reCalculateBounds(1,2);
                    invalidate();
                }
            });
            animate.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {//Swipe Right
                    offset = 0;
                    circularArrayString.pushBackward();
                    circlePositionX[0]=0;
                    scaleDown=2;
                    scaleUp=1;
                    mPaintTextToUp.setTextSize(textSize);
                    mPaintTextToDown.setTextSize(textSize * 2);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else { //Swipe Left
            PropertyValuesHolder circleOffSet = PropertyValuesHolder.ofFloat("offset", offset, offset - WIDTH / 4);
            PropertyValuesHolder externalCircle = PropertyValuesHolder.ofInt("external", 0, (int)(WIDTH / 4 + radius));
            animate = ValueAnimator.ofPropertyValuesHolder(circleOffSet, externalCircle, color,scale);
            animate.setDuration(500);
            animate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    circlePositionX[0]=(int) animation.getAnimatedValue("external");
                    offset = (float) animation.getAnimatedValue("offset");
                    mPaintBackground.setColor((int) animation.getAnimatedValue("color"));
                    float factor=(float)animation.getAnimatedValue("scale");
                    scaleUp=1+factor;
                    scaleDown=2-factor;
                    mPaintTextToUp.setTextSize(textSize + textSize * factor);
                    mPaintTextToDown.setTextSize(textSize * 2 - textSize * factor);
                    circularArrayString.reCalculateBounds(3,2);
                    invalidate();
                }
            });
            animate.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {//Swipe Left
                    offset = 0;
                    circularArrayString.pushForward();
                    circlePositionX[0]=0;
                    scaleUp=1;
                    scaleDown=2;
                    mPaintTextToUp.setTextSize(textSize);
                    mPaintTextToDown.setTextSize(textSize * 2);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        animate.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
            }
        });
        animate.start();

    }
    private int getRandomColor(){
    Random random=new Random();
    int r;
        if ((r=random.nextInt(10))==lastColorCode){
            if(r<9)
                r++;
            else
                r=0;
        }
        lastColorCode=r;
        switch (r){
            case 0:
                return ContextCompat.getColor(getContext(), R.color.background1);
            case 1:
                return ContextCompat.getColor(getContext(), R.color.background2);
            case 2:
                return ContextCompat.getColor(getContext(), R.color.background3);
            case 3:
                return ContextCompat.getColor(getContext(), R.color.background4);
            case 4:
                return ContextCompat.getColor(getContext(), R.color.background5);
            case 5:
                return ContextCompat.getColor(getContext(), R.color.background6);
            case 6:
                return ContextCompat.getColor(getContext(), R.color.background7);
            case 7:
                return ContextCompat.getColor(getContext(), R.color.background8);
            case 8:
                return ContextCompat.getColor(getContext(), R.color.background9);
            case 9:
                return ContextCompat.getColor(getContext(), R.color.background10);
            default:
                return ContextCompat.getColor(getContext(), R.color.background1);
        }
    }

public void update(String str){
    circularArrayString.update(str);
}



    private class CircularArrayString {
        private String[] array;
        private int index;
        private float[] textCentreY;
        private int height;
        private Rect tmp=new Rect(0,0,0,0);
        CircularArrayString(String... a){
            array=a;
            index=0;
            height=HEIGHT/2;
            int i=0;
            textCentreY=new float[array.length];
            while (array.length>i){
                mPaintTextDate.getTextBounds(array[i],0,array[i].length(),tmp);
                textCentreY[i]=height-tmp.exactCenterY();
                i++;
            }
            reCalculateBounds(1,2);
        }
        public String getNumber(int i){
            return array[(index+i)%(array.length)];
        }
        public void pushBackward(){
            index--;
            if(index<0)
                index=array.length-1;
        }

        public void pushForward(){
            index++;
            if(index>(array.length-1))
                index=0;
        }

        public float getVerticalCentre(int i){
            return textCentreY[(index+i)%(array.length)];
        }

        /**
         * Recalculates the bounds for scaling texts
         * @param i The Up Scale Index
         * @param j The Down Scale Index
         */
        public void reCalculateBounds(int i,int j){
            int idUp=(index+i)%(array.length);
            int idDown=(index+j)%(array.length);
            mPaintTextToUp.getTextBounds(array[idUp],0,array[idUp].length(),tmp);
            textCentreY[idUp]=height-tmp.exactCenterY();
            mPaintTextToDown.getTextBounds(array[idDown],0,array[idDown].length(),tmp);
            textCentreY[idDown]=height-tmp.exactCenterY();
        }
        public void update(String value){
            int head;
            if (index>0)
                head=index-1;
            else
                head=array.length-1;
            array[head]=value;

            mPaintTextDate.getTextBounds(array[head], 0, array[head].length(),tmp);
            textCentreY[head]=height-tmp.exactCenterY();
        }

    }
}
