package com.solidskulls.diaryline.adapters;

import android.view.View;
import android.widget.TextView;

import com.solidskulls.diaryline.R;
import com.solidskulls.diaryline.Utility.HtmlSpannableParser;
import com.solidskulls.diaryline.ui.DateView;

/**
 * Created by cijo-saju on 24/1/16.
 *
 */
public class ViewHolderGenericDiary extends VewHolderGeneric {

    private DateView dateView;
    private TextView title,text;
    public ViewHolderGenericDiary(View itemView) {
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
