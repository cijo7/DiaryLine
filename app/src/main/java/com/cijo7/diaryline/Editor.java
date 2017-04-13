package com.cijo7.diaryline;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cijo7.diaryline.Utility.HtmlSpannableParser;
import com.cijo7.diaryline.data.AppConstants;
import com.cijo7.diaryline.data.DataBlockManager;
import com.cijo7.diaryline.ui.HeaderSpan;
import com.cijo7.diaryline.ui.QuoteSpanModern;
import com.cijo7.diaryline.data.DataBlockContainer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class Editor extends AppCompatActivity {

    /**
     * This defines if the editor should modify existing content or add new content<br/>
     * Possible values:{@link #MODE_ADD},{@link #MODE_UPDATE}.
     */
    static final String EDITOR_MODE="EditorMode";
    static final String EDITOR_INIT_OFFSET_DAYS ="totalDays";
    /**
     * The type of content editor is handling.<br/>
     * Possible values:{@link #NOTES},{@link #DIARY}.
     */
    static final String EDITOR_TYPE="type";
    /**
     * Editor modes.
     */
    static final int MODE_ADD =1, MODE_UPDATE =2;
    /**
     * Editor mode types.
     */
    static final int NOTES=11,DIARY=12;
    static final String DATA_ID="data";
    private int editorMode,contentType;
    private EditText title,editorText;
    private Button reminder;
    private Calendar reminderDate=Calendar.getInstance();
    private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US);
    private DataBlockContainer dataBlockContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras=getIntent().getExtras();
        editorMode=extras.getInt(EDITOR_MODE);
        contentType=extras.getInt(EDITOR_TYPE);
        if (editorMode == MODE_UPDATE)
            dataBlockContainer = extras.getParcelable(DATA_ID);
        else
            dataBlockContainer = new DataBlockContainer();
        if (contentType == NOTES)
            setContentView(R.layout.activity_editor_notes);
        else
            setContentView(R.layout.activity_editor_diary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        if(contentType==NOTES) {
            dataBlockContainer.setTag(AppConstants.NOTES);
            title = (EditText) findViewById(R.id.editor_title);
            reminder = (Button) findViewById(R.id.editor_reminder);
            reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatePickerDialog date;
                    final TimePickerDialog time;

                    time=new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            reminderDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            reminderDate.set(Calendar.MINUTE,minute);
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM dd, HH:mm a",Locale.getDefault());
                            reminder.setText(simpleDateFormat.format(reminderDate.getTime()));
                            dataBlockContainer.setReminder(format.format(reminderDate.getTime()));
                        }
                    },reminderDate.get(Calendar.HOUR_OF_DAY),reminderDate.get(Calendar.MINUTE),false);
                    date=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            reminderDate.set(Calendar.YEAR,year);
                            reminderDate.set(Calendar.MONTH,monthOfYear);
                            reminderDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                            time.show();
                        }
                    }, reminderDate.get(Calendar.YEAR), reminderDate.get(Calendar.MONTH), reminderDate.get(Calendar.DAY_OF_MONTH));
                    date.show();
                }
            });
        }else
            dataBlockContainer.setTag(AppConstants.DIARY);

        editorText=(EditText)findViewById(R.id.editor_text);
        // TODO: 4/2/16 Allow enabling or disabling the parser from settings.
        editorText.addTextChangedListener(new EditorParser(this));
    }

    private Context getContext(){
        return this;
    }
    @Override
    public void onStart(){
        super.onStart();
        switch (editorMode){
            case MODE_UPDATE:
                if(contentType==NOTES){
                    title.setText(dataBlockContainer.getTitle());
                    if(dataBlockContainer.getReminder()!=null){
                        try {
                            reminderDate.setTime(format.parse(dataBlockContainer.getReminder()));
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, HH:mm a", Locale.getDefault());
                            reminder.setText(simpleDateFormat.format(reminderDate.getTime()));
                        } catch (ParseException e) {
                            Timber.d(e, "Unable to parse reminder.");
                        }
                    }
                }
                editorText.setText(HtmlSpannableParser.toSpannable(dataBlockContainer.getText()));
                break;
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if (id == R.id.action_done)
            publish();
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Publish the editing
     */
    public void publish() {

        switch (editorMode) {
            case MODE_ADD:
                dataBlockContainer.setText(HtmlSpannableParser.toHtml(editorText.getText()));
                dataBlockContainer.setDate(format.format(new Date()));
                if(contentType==NOTES) {
                    dataBlockContainer.setTitle(title.getText().toString());
                    if (DataBlockManager.addNotes(dataBlockContainer, this)) {
                        Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
                    } else
                        Timber.d("Failed to add new notes");
                }else if(contentType==DIARY){
                    if(DataBlockManager.addDiary(dataBlockContainer,this))
                        Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
                    else
                        Timber.d("Failed to add new entry");
                }
                break;
            case MODE_UPDATE:
                dataBlockContainer.setText(HtmlSpannableParser.toHtml(editorText.getText()));
                if(contentType==NOTES) {
                    dataBlockContainer.setTitle(title.getText().toString());
                    if (DataBlockManager.updateNotes(dataBlockContainer, this)) {
                        Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
                    } else
                        Timber.i("Update Failed");
                }else if(contentType==DIARY){
                    if(DataBlockManager.updateDiary(dataBlockContainer,this))
                        Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
                } else
                    Timber.i("Update Failed");
                break;
            default:
                Timber.d("Invalid Mode");
                break;
        }
        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        Toast.makeText(this,getString(R.string.discard),Toast.LENGTH_SHORT).show();
        return super.onSupportNavigateUp();
    }
    private class EditorParser implements TextWatcher{
        private boolean IsEditable=true;//Looper lock
        /**
         * Actions to be done.
         */
        private static final int ACTION_BOLD = 623;
        private static final int ACTION_ITALIC = 103;
        private static final int ACTION_HEADING_ADD = 808;
        private static final int ACTION_HEADING_APPLY = 506;
        private static final int ACTION_HEADING_REMOVE=410;
        private static final int ACTION_QUOTE_BLOCK_ADD = 121;
        private static final int ACTION_QUOTE_BLOCK_APPLY = 249;
        private static final int ACTION_QUOTE_BLOCK_REMOVE = 986;
        /**
         * The index holders. Default value is -1 indicating null
         */
        private int mStartIndexB =-1,mStartIndexI=-1, mStopIndexB =-1,mStopIndexI=-1;
        private int mStartIndexH =-1,mStopIndexH=-1;
        private int mStartIndexQ=-1,mStopIndexQ=-1;
        /**
         * Variables for Describing actions states.
         */
        private int mAction=0;
        private int QuoteColor;

        /**
         * Constructor
         */
        EditorParser(Context context){
            QuoteColor=ContextCompat.getColor(context,R.color.quoteBlock);
        }

        private void setIndex(CharSequence s,int start){
            char ch=s.charAt(start);
            switch (ch){                                //are you one of our Marking Symbol
                case '*':                               // '*' are used for bold text. Bold text has a opening star and a closing star.
                    if(mStartIndexB==-1)                //are you a opener? If yes then we will remember you.
                        mStartIndexB =start;
                    else {                              //If there was a opener star then right now we encountered the closing star
                        mAction=ACTION_BOLD;            //So lets initialise some indicators for it.
                        mStopIndexB =start;
                    }
                    return;

                case '_':                               // '_' Italic text. Similar logic to bold.
                    if(mStartIndexI==-1)
                        mStartIndexI =start;
                    else{
                        mAction=ACTION_ITALIC;
                        mStopIndexI =start;
                    }
                    return;
                case '#':
                    if (mStartIndexH == -1)
                        if (start == 0) {
                            mStartIndexH = start;
                            mAction = ACTION_HEADING_ADD;
                        } else if (start > 0 && s.charAt(start - 1) == '\n') {
                            mStartIndexH = start;
                            mAction = ACTION_HEADING_ADD;
                        }
                    break;
                case '>':
                	if(mStartIndexQ == -1) {
		                if(start == 0) {
			                mStartIndexQ = start;
			                mAction = ACTION_QUOTE_BLOCK_ADD;
		                }else if(start > 0 && s.charAt(start-1) == '\n'){
			                mStartIndexQ = start;
			                mAction = ACTION_QUOTE_BLOCK_ADD;
		                }
	                }
                    break;
	            case '\n':

		            if (mStartIndexH > -1 && start > mStartIndexH) {
			            mStopIndexH = start - 1;
			            mAction = ACTION_HEADING_APPLY;
		            }
		            if (mStartIndexQ > -1 && start > mStartIndexQ) {
			            mStopIndexQ = start - 1;
			            mAction = ACTION_QUOTE_BLOCK_APPLY;
		            }
		            break;
            }
            if(start<=mStartIndexB)
                mStartIndexB++;
            if(start<=mStartIndexI)
                mStartIndexI++;
            //Heading
            if(start<=mStartIndexH&&ch!='#')
                mStartIndexH++;
            if(start<=mStartIndexQ&&ch!='>')
                mStartIndexQ++;
        }

        private void setIndexFromPasting(CharSequence s, int start, int count){
            int t;
            //Bold
            if(start<= mStartIndexB) {    //If paste before star
                mStartIndexB += count;      //Recalculate first star position
                if((t=s.subSequence(start,start+count).toString().indexOf('*'))!=-1) {//Do we have a the final star. If yes then make it our closer.
                    mStopIndexB = start + t;
                    if(mStopIndexB<mStartIndexB)
                        mStopIndexB--;//-1 to remove extra balancing
                    mAction=ACTION_BOLD;
                }
            }else if(mStartIndexB!=-1) {// after first star
                if ((t = s.subSequence(start, start + count).toString().indexOf('*')) != -1) {//Do we have a the final star. If yes then make it our closer.

                    mStopIndexB = start + t;
                    mAction = ACTION_BOLD;
                }
            }
            else//There is no star present
                if((t=s.subSequence(start,start+count).toString().indexOf('*'))!=-1)//Do we have a new star. If yes then make it our opener.
                    mStartIndexB=start+t;

            //Italic
            if(start<= mStartIndexI) {    //If paste before underscore
                mStartIndexI += count;      //Recalculate first underscore position
                if((t=s.subSequence(start,start+count).toString().indexOf('_'))!=-1) {//Do we have a the final underscore. If yes then make it our closer.
                    mStopIndexI = start + t;
                    if(mStopIndexI<mStartIndexI)
                        mStopIndexI--;//-1 to remove extra balancing
                    mAction=ACTION_ITALIC;
                }
            }else if(mStartIndexI!=-1) {// after first underscore
                if ((t = s.subSequence(start, start + count).toString().indexOf('_')) != -1) {//Do we have a the final underscore. If yes then make it our closer.

                    mStopIndexI = start + t;
                    mAction = ACTION_ITALIC;
                }
            }
            else//There is no underscore present
                if((t=s.subSequence(start,start+count).toString().indexOf('_'))!=-1)//Do we have a new underscore. If yes then make it our opener.
                    mStartIndexI=start+t;

            //Headings
            if(start<=mStartIndexH)
                mStartIndexH+=count;
            //Quote Block
            if(start<=mStartIndexQ)
                mStartIndexQ+=count;
        }

        private void setIndexPopping(int start, int before){
            //Bold
            if(start+before-1<mStartIndexB)//Cut from front
                mStartIndexB-=before;
            else if(start<=mStartIndexB&&start+before>mStartIndexB)//Cut the star
                mStartIndexB=-1;
            //Italic
            if(start+before-1<mStartIndexI)
                mStartIndexI-=before;
            else if(start<=mStartIndexI&&start+before>mStartIndexI)
                mStartIndexI=-1;

            //Header
            if(start+before-1<mStartIndexH)
                mStartIndexH-=before;
            else if(start<=mStartIndexH&&start+before>mStartIndexH)
                mAction = ACTION_HEADING_REMOVE;//Remove heading style when # is removed
            //Quote blocks
            if(start+before-1<mStartIndexQ)
                mStartIndexQ-=before;
            else if(start<=mStartIndexQ&&start+before>mStartIndexQ)
                mAction = ACTION_QUOTE_BLOCK_REMOVE;//Remove heading style when # is removed
        }

        private void setIndexPastePop(CharSequence s, int start,int before,int count){
            int t;
            //Bold
            if(start<=mStartIndexB&&start+before>mStartIndexB)//If the * is included in cut
                if((t=s.subSequence(start,start+count).toString().indexOf('*'))!=-1) {//If there is new star in paste.May also check for closer
                    int l=s.subSequence(start,start+count).toString().lastIndexOf('*');
                    if(t==l)//If we don't have multiple stars.
                        mStartIndexB = start + t;
                    else {//An almost rare case
                        mStartIndexB=start+t;
                        mStopIndexB=start+l;
                        mAction=ACTION_BOLD;
                    }
                }
                else
                    mStartIndexB=-1;
            else if(mStartIndexB!=-1) {//If we have a opener
                if(start<mStartIndexB)//Cut take place in front
                    mStartIndexB += (count - before);//Recalculate opener
                if((t=s.subSequence(start,start+count).toString().indexOf('*'))!=-1) {//If there is closer star in paste
                    mStopIndexB = start + t;//-1 To Nullify the balancing effect because balancing is already done
                    if(mStopIndexB<mStartIndexB)
                        mStopIndexB--;//-1 to remove extra balancing
                    mAction = ACTION_BOLD;
                }
            } else//There is no star present
                if((t=s.subSequence(start,start+count).toString().indexOf('*'))!=-1)//Do we have a new star. If yes then make it our opener.
                    mStartIndexB=start+t;
            //Italic
            if(start<=mStartIndexI&&start+before>mStartIndexI)//If the * is included in cut
                if((t=s.subSequence(start,start+count).toString().indexOf('_'))!=-1) {//If there is new star in paste.May also check for closer
                    int l=s.subSequence(start,start+count).toString().lastIndexOf('_');
                    if(t==l)//If we don't have multiple stars.
                        mStartIndexI = start + t;
                    else {//An almost rare case
                        mStartIndexI=start+t;
                        mStopIndexI=start+l;
                        mAction=ACTION_ITALIC;
                    }
                }
                else
                    mStartIndexI=-1;
            else if(mStartIndexI!=-1) {//If we have a opener
                if(start<mStartIndexI)//Cut take place in front
                    mStartIndexI += (count - before);//Recalculate opener
                if((t=s.subSequence(start,start+count).toString().indexOf('_'))!=-1) {//If there is closer star in paste
                    mStopIndexI = start + t;//-1 To Nullify the balancing effect because balancing is already done
                    if(mStopIndexI<mStartIndexI)
                        mStopIndexI--;//-1 to remove extra balancing
                    mAction = ACTION_ITALIC;
                }
            } else//There is no star present
                if((t=s.subSequence(start,start+count).toString().indexOf('_'))!=-1)//Do we have a new star. If yes then make it our opener.
                    mStartIndexI=start+t;

            //Heading
            if(start<mStartIndexH) {
                mStartIndexH += (count - before);
            }
            //Quote Block
            if(start<=mStartIndexQ)
                mStartIndexQ+=(count-before);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!IsEditable)     //If we have a looping call due to editing, then ignore it.
                return;
            if(count>0&&before==0){         //Nothing gets removed
                if(count==1){               //Typing char by char
                    setIndex(s,start);
                }else{                      //Pasting new content without removing anything
                    setIndexFromPasting(s,start,count);
                }
            }else if(count==0&&before>0){   //Cutting old content. Nothing new is added. Also Back pop.
                setIndexPopping(start,before);
            }else if(count>0&&before>0){    //Cutting old and Pasting new content
                setIndexPastePop(s,start,before,count);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (IsEditable) {
                IsEditable = false;           //Locking Loop

                switch (mAction) {            //do we need to perform some actions
                    case ACTION_BOLD:
                        try {
                            if (mStartIndexB > mStopIndexB) {                   //simply Swapping if they are obtained in descending order.
                                int t = mStartIndexB;
                                mStartIndexB = mStopIndexB;
                                mStopIndexB = t + 1;                            //Balance the increase in char length due to placement of '*' at
                            }                                                   // beginning after stop value was retried.
                            if(s.charAt(mStartIndexB)=='*')
                                s.replace(mStartIndexB, mStartIndexB + 1, "");
                            if(s.charAt(mStopIndexB-1)=='*')
                                s.replace(mStopIndexB - 1, mStopIndexB, "");         //One character has been removed. So right now operating at one char back.
                            StyleSpan[] styleSpans = s.getSpans(mStartIndexB, mStopIndexB - 1, StyleSpan.class);
                            for (StyleSpan styleSpan : styleSpans) {            //Remove inner StyleSpans of same type.
                                if (styleSpan.getStyle() == Typeface.BOLD) {
                                    if(s.getSpanStart(styleSpan)==mStartIndexB&&s.getSpanEnd(styleSpan)==mStopIndexB-1){
                                        s.removeSpan(styleSpan);
                                        mAction = 0;                //Resets
                                        mStartIndexB = -1;
                                        mStopIndexB = -1;
                                        IsEditable = true;
                                        return;
                                    }
                                    s.removeSpan(styleSpan);
                                }
                            }
                                                                                //Lets apply new span
                            s.setSpan(new StyleSpan(Typeface.BOLD), mStartIndexB, mStopIndexB - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mAction = 0;                                        //Resets
                            mStartIndexB = -1;
                            mStopIndexB = -1;
                        } catch (Exception e) {
                            Timber.d(e, "Unexpected Exception at ActB");
                        }
                        break;

                    case ACTION_ITALIC:                                         //Similar to bold
                        try {
                            if (mStartIndexI > mStopIndexI) {
                                int t = mStartIndexI;
                                mStartIndexI = mStopIndexI;
                                mStopIndexI = t + 1;
                            }
                            s.replace(mStartIndexI, mStartIndexI + 1, "");
                            s.replace(mStopIndexI - 1, mStopIndexI, "");
                            StyleSpan[] styleSpans = s.getSpans(mStartIndexI, mStopIndexI - 1, StyleSpan.class);
                            for (StyleSpan styleSpan : styleSpans) {
                                if (styleSpan.getStyle() == Typeface.ITALIC){
                                    if(s.getSpanStart(styleSpan)==mStartIndexI&&s.getSpanEnd(styleSpan)==mStopIndexI-1){
                                        s.removeSpan(styleSpan);
                                        mAction = 0;                //Resets
                                        mStartIndexI = -1;
                                        mStopIndexI = -1;
                                        IsEditable = true;
                                        return;
                                    }
                                    s.removeSpan(styleSpan);
                                }
                            }
                                                        //Lets apply new span
                            s.setSpan(new StyleSpan(Typeface.ITALIC), mStartIndexI, mStopIndexI - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mAction = 0;                //Resets
                            mStartIndexI = -1;
                            mStopIndexI = -1;
                        }catch (Exception e){
                            Timber.d(e,"Unexpected Exception at ActI");
                        }
                        break;
                    case ACTION_HEADING_ADD:
                        s.setSpan(new HeaderSpan(), mStartIndexH, mStartIndexH + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        mAction=0;
                        break;
                    case ACTION_HEADING_APPLY:
                        try {
                            s.replace(mStartIndexH, mStartIndexH + 1, "");
                            HeaderSpan[] removeSpans = s.getSpans(mStartIndexH, mStopIndexH, HeaderSpan.class);
                            for (HeaderSpan removeSpan : removeSpans)
                                s.removeSpan(removeSpan);
                            s.setSpan(new HeaderSpan(), mStartIndexH, mStopIndexH, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mAction = 0;
                            mStopIndexH = -1;
                            mStartIndexH = -1;
                        }catch (Exception e){
                            Timber.d(e,"Unexpected Exception at ActH");
                        }
                        break;
                    case ACTION_HEADING_REMOVE:
                        HeaderSpan[] headerSpans=s.getSpans(mStartIndexH,mStartIndexH+1,HeaderSpan.class);
                        for(HeaderSpan removeSpan:headerSpans)
                            s.removeSpan(removeSpan);
                        mAction=0;
                        mStopIndexH=-1;
                        mStartIndexH=-1;
                        break;
                    case ACTION_QUOTE_BLOCK_ADD:
                        s.setSpan(new QuoteSpanModern(QuoteColor),mStartIndexQ,mStartIndexQ+1,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        mAction=0;
                        break;
                    case ACTION_QUOTE_BLOCK_APPLY:
                        try {

                            s.replace(mStartIndexQ, mStartIndexQ + 1, "");
                            QuoteSpanModern[] removeSpans = s.getSpans(mStartIndexQ, mStopIndexQ, QuoteSpanModern.class);
                            for (QuoteSpanModern removeSpan : removeSpans)
                                s.removeSpan(removeSpan);
                            s.setSpan(new QuoteSpanModern(QuoteColor), mStartIndexQ, mStopIndexQ, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mStopIndexQ = -1;
                            mStartIndexQ = -1;
                            mAction = 0;
                        }catch (Exception e){
                            Timber.d(e,"Unexpected Exception at ActQ");
                        }
                        break;
                    case ACTION_QUOTE_BLOCK_REMOVE:
                        QuoteSpanModern[] quoteSpans=s.getSpans(mStartIndexQ,mStartIndexQ+1,QuoteSpanModern.class);
                        for(QuoteSpanModern removeSpan:quoteSpans)
                            s.removeSpan(removeSpan);
                        mAction=0;
                        break;
                }
                IsEditable = true;                      //UnLocking Loops
            }
        }
    }
}
