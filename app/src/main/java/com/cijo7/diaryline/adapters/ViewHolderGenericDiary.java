package com.cijo7.diaryline.adapters;

import android.view.View;
import android.widget.TextView;

import com.cijo7.diaryline.R;
import com.cijo7.diaryline.Utility.HtmlSpannableParser;
import com.cijo7.diaryline.ui.DateView;

/**
 * Created by cijo-saju on 24/1/16.
 *
 */
class ViewHolderGenericDiary extends VewHolderGeneric {

    private DateView dateView;
    private TextView title,text;
    ViewHolderGenericDiary(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        dateView=(DateView)itemView.findViewById(R.id.card_diary_date);
        title=(TextView)itemView.findViewById(R.id.card_diary_title);
        text=(TextView)itemView.findViewById(R.id.card_diary_text);

    }

    /**
     * Set the date to the view
     * @param date milliseconds representation.
     */
    @Override
    public void setDate(String date){
        dateView.setDate(date);
    }


    @Override
    public void setTitle(String  title) {
        this.title.setText(title);
    }

    @Override
    public void setText(String  text) {
        this.text.setText(HtmlSpannableParser.toSpannable(text));
    }
}
