package com.solidskulls.diaryline.ui;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Created by cijo-saju on 23/1/16.
 *
 */
public class HeaderSpan extends MetricAffectingSpan  {
    private final float mProportion;
    @SuppressWarnings("unused")
    private static final float H1 = 1.5f,H2=1.4f,H3=1.3f,H4=1.2f,H5=1.1f,H6=1f;


    public HeaderSpan(){
        mProportion=H1;
    }
    @SuppressWarnings("unused")
    public HeaderSpan(float level){
        if(level<=H1&&level>=H6)
            mProportion=level;
        else
            mProportion=H6;
    }
    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTextSize(p.getTextSize() * mProportion);
        apply(p,Typeface.BOLD);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTextSize(tp.getTextSize() * mProportion);
        apply(tp,Typeface.BOLD);
    }

    /**
     * The h tag level
     * @return tag level
     */
    public int getLevel(){
        return  (int)((1.6f-mProportion)*10);
    }

    private static void apply(Paint paint, int style) {
        int oldStyle;

        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int want = oldStyle | style;

        Typeface tf;
        if (old == null) {
            tf = Typeface.defaultFromStyle(want);
        } else {
            tf = Typeface.create(old, want);
        }

        int fake = want & ~tf.getStyle();

        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }
}
