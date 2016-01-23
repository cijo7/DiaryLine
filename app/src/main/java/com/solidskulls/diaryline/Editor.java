package com.solidskulls.diaryline;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import timber.log.Timber;

public class Editor extends AppCompatActivity {

    static final String EDITOR_MODE="EditorMode";
    static final String EDITOR_INIT_OFFSET_DAYS ="totalDays";
    static final int EDITOR_MODE_ADD=1;
    static final int EDITOR_MODE_UPDATE=2;
    private int editorMode;
    private int offsetDays;
    private EditText editorText;
    private static DataBlockManager dataBlockManager;

    private EditorParser editorParser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras=getIntent().getExtras();
        offsetDays =extras.getInt(EDITOR_INIT_OFFSET_DAYS);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        editorText=(EditText)findViewById(R.id.diaryInputString);
        editorParser=new EditorParser(this);
    }

    @Override
    public void onStart(){
        super.onStart();

        dataBlockManager=new DataBlockManager(offsetDays);
        if(dataBlockManager.ifExists())
            editorMode=EDITOR_MODE_UPDATE;
        else
            editorMode=EDITOR_MODE_ADD;
        switch (editorMode){
            case EDITOR_MODE_UPDATE://todo optimise
                dataBlockManager.readPackage();
                editorText.setText(Html.fromHtml(dataBlockManager.getStringData()), TextView.BufferType.SPANNABLE);
                break;
        }
        editorText.addTextChangedListener(editorParser);
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

        switch (editorMode){
            case EDITOR_MODE_ADD:
                if(dataBlockManager.addPackage(HtmlSpannableParser.toHtml(editorText.getText()))) {
                    Timber.d( "Uri:" + DataBlockManager.lastUri);
                    Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_LONG).show();
                    finish();
                }else
                    Timber.d("Add Failed");
                break;
            case EDITOR_MODE_UPDATE:
                if(dataBlockManager.updatePackage(HtmlSpannableParser.toHtml(editorText.getText()))){
                    Toast.makeText(getBaseContext(), "Updated", Toast.LENGTH_LONG).show();
                    finish();
                }else
                    Timber.d("Update Failed");
                break;
            default:
                Timber.d("Invalid Mode");
                break;
        }
    }
    // TODO: 13/1/16 Decide if you wanna go back automatically.
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

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        // FIXME: 22/1/16 Minor problem with swift keyboard try to fix. If not possible ignore
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!IsEditable)     //If we have a looping call due to editing, then ignore it.
                return;
            if(count>0&&before==0){//Nothing gets removed
                if(count==1){//Typing char by char
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
                                    mStartIndexH = 0;
                                    mAction = ACTION_HEADING_ADD;
                                } else if (start > 1) {
                                    if (s.charAt(start - 1) == '\n') {
                                        mStartIndexH = start;
                                        mAction = ACTION_HEADING_ADD;
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
                        case '>':
                            mStartIndexQ=start;
                            mAction=ACTION_QUOTE_BLOCK_ADD;
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
                }else{//Pasting new content without removing anything
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
            }else if(count==0&&before>0){//Cutting old content. Nothing new is added. Also Back pop.
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
            }else if(count>0&&before>0){//Cutting old and Pasting new content
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
                if(start<=mStartIndexH)
                    mStartIndexH+=(count-before);
                //Quote Block
                if(start<=mStartIndexQ)
                    mStartIndexQ+=(count-before);
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
                        QuoteSpanModern[] quoteSpans=s.getSpans(mStartIndexH,mStartIndexH+1,QuoteSpanModern.class);
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
