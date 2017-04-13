package com.cijo7.diaryline.adapters;

import android.view.View;
import android.widget.TextView;

import com.cijo7.diaryline.R;
import com.cijo7.diaryline.Utility.HtmlSpannableParser;

/**
 * Created by cijo-saju on 24/1/16.
 * View Holder for general notes
 */
class ViewHolderGenericNotes extends VewHolderGeneric {
    private TextView title,notes;
    ViewHolderGenericNotes(View itemView) {
        super(itemView);
        title=(TextView)itemView.findViewById(R.id.card_notes_title);
        notes=(TextView)itemView.findViewById(R.id.card_notes_text);
        itemView.setOnClickListener(this);
    }

    @Override
    public void setDate(String date) {

    }

    @Override
    public void setTitle(String title) {
        this.title.setText(title);
    }

    /**
     * Sets the card notes preview to display.
     * @param notes note contents
     */
    @Override
    public void setText(String notes) {
        this.notes.setText(HtmlSpannableParser.toSpannable(notes));
    }
}
