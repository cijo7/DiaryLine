package com.solidskulls.diaryline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by cijo-saju on 17/1/16.
 *
 */
public class NavigatorView extends View {
    public NavigatorView(Context context){
        super(context);
    }
    public NavigatorView(Context context,AttributeSet attributeSet){
        super(context, attributeSet);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
    }
}
