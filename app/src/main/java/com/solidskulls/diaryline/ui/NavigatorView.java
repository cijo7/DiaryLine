package com.solidskulls.diaryline.ui;

import android.animation.Animator;
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
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.solidskulls.diaryline.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cijo-saju on 17/1/16.
 *
 */
public class NavigatorView extends View  {
    public int NAV_STRING_COUNT=6;
    public CircularArrayString circularArrayString;

    private String mMonthYear;
    private Paint mPaintBackground;
    private Paint mPaintTextDate,mPaintTextMonth;
    private Paint mPaintTextToUp, mPaintTextToDown;
    private Paint mPaintCircle;
    private int WIDTH=768;
    private int HEIGHT=1248/5;
    private float offset=0,lCircleX=0,rCircleX=0;
    private int[] circlePositionX;
    private float radius,scaleUp=1,scaleDown=2;
    private boolean[] mSwipeRight;
    private int mCount=0 , animateStackCount =0,animateUnStackCount=0;
    private ValueAnimator animate;
    private float textSize;
    private SimpleDateFormat simpleDateFormat,simpleDate;
    private Date mDateSetter;
    private long shownMilliSec;


    public NavigatorView(Context context){
        super(context);
    }

    /**
     * Constructor of NavigatorView
     * @param context Context of parent activity
     * @param attributeSet The attributes from xml
     */
    public NavigatorView(Context context,AttributeSet attributeSet){
        super(context, attributeSet);
        mPaintBackground =new Paint(Paint.DITHER_FLAG);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setStrokeWidth(3);
        mPaintTextDate =new Paint(mPaintBackground);
        mPaintCircle=new Paint(mPaintBackground);

        mPaintBackground.setColor(ContextCompat.getColor(context, R.color.navigationView_background));
        mPaintCircle.setColor(ContextCompat.getColor(context, R.color.navigationView_circle));

        mPaintTextDate.setTextAlign(Paint.Align.CENTER);
        mPaintTextMonth=new Paint(mPaintTextDate);

        mPaintTextDate.setColor(ContextCompat.getColor(context, R.color.navigationView_textDate));

        mPaintTextToDown =new Paint(mPaintTextDate);
        mPaintTextToUp =new Paint(mPaintTextDate);
        mPaintTextMonth.setColor(ContextCompat.getColor(context, R.color.navigationView_textMonthYear));

        mDateSetter =new Date();
        simpleDateFormat=new SimpleDateFormat("dd", Locale.getDefault());
        simpleDate=new SimpleDateFormat("MMMM yyyy",Locale.getDefault());


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
        mSwipeRight=new boolean[1];
        mSwipeRight[0]=false;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, WIDTH, HEIGHT, mPaintBackground);
        if(!isInEditMode()) {//todo replace this guy on release

            if (mSwipeRight[mCount]) {

                canvas.drawCircle(circlePositionX[1] + offset, HEIGHT / 2, radius * scaleUp, mPaintCircle);
                canvas.drawCircle(circlePositionX[2] + offset, HEIGHT / 2, radius * scaleDown, mPaintCircle);
                canvas.drawCircle(circlePositionX[3] + circlePositionX[0], HEIGHT / 2, radius, mPaintCircle);
                canvas.drawCircle(lCircleX + circlePositionX[0], HEIGHT / 2, radius, mPaintCircle);

                canvas.drawText(circularArrayString.getString(1), circlePositionX[1] + offset, circularArrayString.getVerticalCentre(1), mPaintTextToUp);
                canvas.drawText(circularArrayString.getString(2), circlePositionX[2] + offset, circularArrayString.getVerticalCentre(2), mPaintTextToDown);
                canvas.drawText(circularArrayString.getString(3), circlePositionX[3] + circlePositionX[0], circularArrayString.getVerticalCentre(3), mPaintTextDate);
                canvas.drawText(circularArrayString.getString(0), lCircleX + circlePositionX[0], circularArrayString.getVerticalCentre(0), mPaintTextDate);
            } else {
                canvas.drawCircle(circlePositionX[1] - circlePositionX[0], HEIGHT / 2, radius, mPaintCircle);
                canvas.drawCircle(circlePositionX[2] + offset, HEIGHT / 2, radius * scaleDown, mPaintCircle);
                canvas.drawCircle(circlePositionX[3] + offset, HEIGHT / 2, radius * scaleUp, mPaintCircle);
                canvas.drawCircle(rCircleX - circlePositionX[0], HEIGHT / 2, radius, mPaintCircle);

                canvas.drawText(circularArrayString.getString(1), circlePositionX[1] - circlePositionX[0], circularArrayString.getVerticalCentre(1), mPaintTextDate);
                canvas.drawText(circularArrayString.getString(2), circlePositionX[2] + offset, circularArrayString.getVerticalCentre(2), mPaintTextToDown);
                canvas.drawText(circularArrayString.getString(3), circlePositionX[3] + offset, circularArrayString.getVerticalCentre(3), mPaintTextToUp);
                canvas.drawText(circularArrayString.getString(4), rCircleX - circlePositionX[0], circularArrayString.getVerticalCentre(4), mPaintTextDate);
            }

            canvas.drawText(mMonthYear, WIDTH / 2, 3 * HEIGHT / 4 + HEIGHT / 6, mPaintTextMonth);
        }
    }

    // TODO: 20/1/16 Make this view smarted and adapt to its surroundings by reducing its dependence.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        setMeasuredDimension(WIDTH, HEIGHT);
    }

    /**
     * The View Animating logic for transverse movements.
     */
    private void animateView(){
        PropertyValuesHolder scale =PropertyValuesHolder.ofFloat("scale", 0, 1);
        if(mSwipeRight[mCount]) {
            PropertyValuesHolder circleOffSet = PropertyValuesHolder.ofFloat("offset", offset, offset + WIDTH / 4);
            PropertyValuesHolder externalCircle = PropertyValuesHolder.ofInt("external", 0,(int) (WIDTH / 4 + radius));
            animate = ValueAnimator.ofPropertyValuesHolder(circleOffSet, externalCircle, scale);
            animate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    circlePositionX[0]=(int) animation.getAnimatedValue("external");
                    offset = (float) animation.getAnimatedValue("offset");
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
                    clearAnimationStack();
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
            animate = ValueAnimator.ofPropertyValuesHolder(circleOffSet, externalCircle,scale);
            animate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    circlePositionX[0] = (int) animation.getAnimatedValue("external");
                    offset = (float) animation.getAnimatedValue("offset");
                    float factor = (float) animation.getAnimatedValue("scale");
                    scaleUp = 1 + factor;
                    scaleDown = 2 - factor;
                    mPaintTextToUp.setTextSize(textSize + textSize * factor);
                    mPaintTextToDown.setTextSize(textSize * 2 - textSize * factor);
                    circularArrayString.reCalculateBounds(3, 2);
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
                    circlePositionX[0] = 0;
                    scaleUp = 1;
                    scaleDown = 2;
                    mPaintTextToUp.setTextSize(textSize);
                    mPaintTextToDown.setTextSize(textSize * 2);
                    clearAnimationStack();
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

    }

    /**
     * Collapse or ReEnter the view.
     * @param collapse True if view is collapsing
     */
    @SuppressWarnings("unused")
    public void collapseView(boolean collapse){
        ValueAnimator animator;
        if(collapse)
            animator=ValueAnimator.ofInt(HEIGHT,0);
        else
            animator=ValueAnimator.ofInt(0,HEIGHT);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams)getLayoutParams();
                lp.height=(int)animation.getAnimatedValue();
                setLayoutParams(lp);
            }
        });
        animator.start();
    }

    /**
     * Request for a new View Animation. The requests are added to a animation stack and implemented via animateView()
     * @param swipeRight True if user has swiped towards right.
     */
    public void updateNavigatorAnimation(boolean swipeRight){
        if(animateStackCount ==0) {
            animateStackCount++;//Stack Increased
            mSwipeRight=new boolean[1];
            mSwipeRight[0]=swipeRight;
            updateNavData();
            animateView();
            animate.setDuration(300);
            animate.start();
            return;
        }
        animateStackCount++;
        boolean[] tmp=new boolean[animateStackCount];
        tmp[animateStackCount-1]=swipeRight;
        int i=0;
        while (tmp.length-1>i){
            tmp[i]=mSwipeRight[i];
            i++;
        }
        mSwipeRight=tmp;
    }

    /**
     * Once animation is finished the animation stack needs to be cleared and made ready for next
     * Set of animations. This method is called from animator's animation end listener.
     */
    public void clearAnimationStack(){
        animateUnStackCount++;
        if(animateStackCount>animateUnStackCount){//Checking for next stack
            mCount++;
            updateNavData();
            animateView();
            animate.setDuration(200);
            animate.start();
        }else {//If we don't have anything in stack lets reset mCount for set of animations
            mCount = 0;
            animateStackCount=0;
            animateUnStackCount=0;
        }
    }

    /**
     * Initialises Navigator View with default data.
     * @param milliSec Set display date in millisecond
     */
    public void setNavigationData(long milliSec){
        shownMilliSec=milliSec;
        milliSec+=(long)((NAV_STRING_COUNT /2)-1)*24*60*60*1000;
        mDateSetter.setTime(milliSec);

        String[] days=new String[NAV_STRING_COUNT];
        days[0]="";
        //Puts the string to format {null,17,18,19,20,21}
        for(int i=NAV_STRING_COUNT-1;i>0;i--){
            days[i]=simpleDateFormat.format(mDateSetter);
            milliSec-=24*60*60*1000;
            mDateSetter.setTime(milliSec);
        }
        circularArrayString.refresh(days);

        mDateSetter.setTime(shownMilliSec);
        updateMonth(simpleDate.format(mDateSetter));
    }

    /**
     * Update the Navigation date
     */
    private void updateNavData(){
        if(mSwipeRight[mCount]){
            mDateSetter.setTime(shownMilliSec-(NAV_STRING_COUNT/2)*24*60*60*1000);
            circularArrayString.update(simpleDateFormat.format(mDateSetter));
            shownMilliSec-=24*60*60*1000;
        }else {
            mDateSetter.setTime(shownMilliSec+(NAV_STRING_COUNT/2)*24*60*60*1000);
            circularArrayString.update(simpleDateFormat.format(mDateSetter));
            shownMilliSec+=24*60*60*1000;
        }
        mDateSetter.setTime(shownMilliSec);
        updateMonth(simpleDate.format(mDateSetter));
    }


    /**
     * Updates the displayed Month
     * @param string Updated Month and Year.
     */
    public void updateMonth(String string){
        mMonthYear=string;
    }

    /**
     * Class implements a circular Array to be manipulated by animation
     */
    private class CircularArrayString {
        private String[] array;
        private int index;
        private float[] textCentreY;
        private int height;
        private Rect tmp=new Rect(0,0,0,0);
        CircularArrayString(String... strings){
            array=strings;
            index=0;//0 is the update array
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

        /**
         * Reset the data with in the
         * @param strings Strings to add to new Queue.
         */
        void refresh(String... strings){
            array=strings;
            index=1;//Not 0. 0is the Update array
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

        /**
         * Retrieve the String Value from Circular queue.
         * @param i Index of String in queue.
         * @return String Value
         */
        String getString(int i){
            return array[(index+i)%(array.length)];
        }

        /**
         * Pushes the circular array forward.
         */
        void pushBackward(){
            index--;
            if(index<0)
                index=array.length-1;
        }

        /**
         * Pushes the circular array forward
         */
        void pushForward(){
            index++;
            if(index>(array.length-1))
                index=0;
        }

        /**
         * Return the Vertical Centre of a text. The value is usually computed in the early logic's.
         * @param i The index of Text Who's Vertical coordinates you need.
         * @return Returns the Y coordinates of Text
         */
        float getVerticalCentre(int i){
            return textCentreY[(index+i)%(array.length)];
        }

        /**
         * Recalculates the bounds for scaling texts
         * @param i The Up Scale Index
         * @param j The Down Scale Index
         */
        void reCalculateBounds(int i, int j){
            int idUp=(index+i)%(array.length);
            int idDown=(index+j)%(array.length);
            mPaintTextToUp.getTextBounds(array[idUp],0,array[idUp].length(),tmp);
            textCentreY[idUp]=height-tmp.exactCenterY();
            mPaintTextToDown.getTextBounds(array[idDown],0,array[idDown].length(),tmp);
            textCentreY[idDown]=height-tmp.exactCenterY();
        }
        /**
         * Adds the String to Writing head of Queue
         * @param value Value of String to Display
         */
        void update(String value){
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
