package com.cijo7.diaryline.adapters;

import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cijo7.diaryline.EnvironmentVariables;
import com.cijo7.diaryline.Utility.DataParser;
import com.cijo7.diaryline.R;

import java.util.List;

import timber.log.Timber;

/**
 * Created by cijo-saju on 24/1/16.
 *
 */
class ViewHolderGenericLists extends VewHolderGeneric {

    private TextView title;
    private TextView lists;


    ViewHolderGenericLists(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        title=(TextView)itemView.findViewById(R.id.card_list_title);
        CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.card_list_checkbox);
        lists=(TextView)itemView.findViewById(R.id.card_listView);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void setDate(String milliSec) {

    }

    @Override
    public void setTitle(String title) {
        this.title .setText( title);
    }

    @Override
    public void setText(String list) {
        DataParser parser=new DataParser();//No parser should be reused.
        List<String> stringList=parser.textToList(list);
        int size;
        try {
            size = stringList.size();
        }catch (NullPointerException e){
            Timber.d(e,"Parser returned null");
            size=0;
        }
        SpannableStringBuilder stringBuilder=new SpannableStringBuilder();

        for (int i = 0; i < size; i++) {
            if(i>=3){
                if(size==4) {
                    stringBuilder.append(getLine(Integer.toString(i + 1)+")"+stringList.get(i)));
                    break;
                }
                int len=stringBuilder.length();
                stringBuilder.append("And ").append(Integer.toString(size - 3)).append(" more items in list.");
                stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), len, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.setSpan(new AlignmentSpan() {
                    @Override
                    public Layout.Alignment getAlignment() {
                        return Layout.Alignment.ALIGN_CENTER;
                    }
                }, len, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            }
            stringBuilder.append(getLine(Integer.toString(i + 1)+")"+stringList.get(i))).append('\n');
        }
        lists.setText(stringBuilder);
    }

    private String getLine(String text){
        float textWidth;
        int i=0,len=text.length();
        try {
            textWidth = lists.getPaint().measureText(text.substring(0, len));
            if (textWidth < EnvironmentVariables.SCREEN_WIDTH-EnvironmentVariables.getPixelsFromDp(24))
                return text;
            do {
                i++;
                textWidth = lists.getPaint().measureText(text.substring(0, len - i) + "...");
            } while (textWidth >= EnvironmentVariables.SCREEN_WIDTH-EnvironmentVariables.getPixelsFromDp(24));
        }catch (Exception e){
            Timber.d(e,"Failed to layout.");
        }
        return text.substring(0,len-i)+"...";
    }
}