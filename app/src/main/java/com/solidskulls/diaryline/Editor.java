package com.solidskulls.diaryline;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Editor extends AppCompatActivity {

    static final String EDITOR_MODE="EditorMode";
    static final String EDITOR_INIT_DAYS="totalDays";
    static final int EDITOR_MODE_ADD=1;
    static final int EDITOR_MODE_UPDATE=2;
    private int editorMode;
    private long days;
    private EditText editorText;

    private static DataBlockManager dataBlockManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras=getIntent().getExtras();
        editorMode=extras.getInt(EDITOR_MODE);
        if(editorMode==EDITOR_MODE_UPDATE)
            days=extras.getLong(EDITOR_INIT_DAYS);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        editorText=(EditText)findViewById(R.id.diaryInputString);
    }

    @Override
    public void onStart(){
        super.onStart();
        switch (editorMode){
            case EDITOR_MODE_ADD:
                dataBlockManager=new DataBlockManager();
                break;
            case EDITOR_MODE_UPDATE:
                dataBlockManager=new DataBlockManager(days);
                dataBlockManager.readPackage();
                editorText.setText(dataBlockManager.getStringData());
                break;
        }
    }
    /**
     * Publish the editing
     * @param view The View
     */
    public void publish(View view) {

        switch (editorMode){
            case EDITOR_MODE_ADD:
                dataBlockManager.addPackage(editorText.getText().toString());
                break;
            case EDITOR_MODE_UPDATE:
                dataBlockManager.updatePackage(editorText.getText().toString());
                break;
            default:
                Log.d("Editor","Invalid Mode");
                break;
        }
        Log.d("Editor","Uri:"+DataBlockManager.lastUri);
        Snackbar.make(view, "Saved", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
