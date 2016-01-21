package com.solidskulls.diaryline;

import android.graphics.Typeface;
import android.os.Bundle;
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
        editorParser=new EditorParser();
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
                if(dataBlockManager.addPackage(Html.toHtml(editorText.getText()))) {
                    Timber.d( "Uri:" + DataBlockManager.lastUri);
                    Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_LONG).show();
                    finish();
                }else
                    Timber.d("Add Failed");
                break;
            case EDITOR_MODE_UPDATE:
                if(dataBlockManager.updatePackage(Html.toHtml(editorText.getText()))){
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
        /**
         * The index holders. Default value is -1 indicating null
         */
        private int mStartIndexB =-1,mStartIndexI=-1, mStopIndexB =-1,mStopIndexI=-1;
        /**
         * Variables for Describing actions states.
         */
        private int mAction=0;

        /**
         * Constructor
         */
        EditorParser(){
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        // FIXME: 22/1/16 Minor problem with swift keyboard try to fix. If not possible ignore
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!IsEditable)     //If we have a looping call due to editing, then ignore it.
                return;
            if(before>0){       //If we are having some push backs before index's then calculate the new index values
                if(start<= mStartIndexB) {
                    mStartIndexB = (mStartIndexB + count) - before;
                    if(s.charAt(mStartIndexB)!='*')
                        mStartIndexB=-1;
                }
                if(start<=mStartIndexI) {
                    mStartIndexI = (mStartIndexI + count) - before;
                    if (s.charAt(mStartIndexI) != '_')
                        mStartIndexI = -1;
                }
                return;         //We got our index and don't want anything more.
            }

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
            }


            if(start< mStartIndexB)                     //When you are entering something other than symbols new before index, index is recalculated
                mStartIndexB +=count;
            if(start<mStartIndexI)
                mStartIndexI+=count;

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
                            s.replace(mStartIndexB, mStartIndexB + 1, "");
                            s.replace(mStopIndexB - 1, mStopIndexB, "");         //One character has been removed. So right now operating at one char back.
                            StyleSpan[] styleSpans = s.getSpans(mStartIndexB, mStopIndexB - 1, StyleSpan.class);
                            for (StyleSpan styleSpan : styleSpans) {            //Remove inner StyleSpans of same type.
                                if (styleSpan.getStyle() == Typeface.BOLD)
                                    s.removeSpan(styleSpan);
                            }
                                                                                //Lets apply new span
                            s.setSpan(new StyleSpan(Typeface.BOLD), mStartIndexB, mStopIndexB - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mAction = 0;                                        //Resets
                            mStartIndexB = -1;
                            mStopIndexB = -1;
                        } catch (Exception e) {
                            Timber.d(e, "Exception 1");
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
                                if (styleSpan.getStyle() == Typeface.ITALIC)
                                    s.removeSpan(styleSpan);
                            }
                                                        //Lets apply new span
                            s.setSpan(new StyleSpan(Typeface.ITALIC), mStartIndexI, mStopIndexI - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mAction = 0;                //Resets
                            mStartIndexI = -1;
                            mStopIndexI = -1;
                        }catch (Exception e){
                            Timber.d(e,"Exception 2");
                        }
                        break;
                }
                IsEditable = true;                      //UnLocking Loops
            }

        }
    }
}
