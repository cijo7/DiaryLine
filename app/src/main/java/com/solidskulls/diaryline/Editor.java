package com.solidskulls.diaryline;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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
            case EDITOR_MODE_UPDATE:
                dataBlockManager.readPackage();
                editorText.setText(dataBlockManager.getStringData());
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

        switch (editorMode){
            case EDITOR_MODE_ADD:
                if(dataBlockManager.addPackage(editorText.getText().toString())) {
                    Timber.d( "Uri:" + DataBlockManager.lastUri);
                    setResult(EDITOR_MODE_ADD);
                    finish();
                }else
                    Timber.d("Add Failed");
                break;
            case EDITOR_MODE_UPDATE:
                if(dataBlockManager.updatePackage(editorText.getText().toString())){
                    setResult(EDITOR_MODE_UPDATE);
                    finish();
                }else
                    Timber.d("Update Failed");
                break;
            default:
                Timber.d("Invalid Mode");
                break;
        }
    }
    //// TODO: 13/1/16 Decide if you wanna go back automatically.
}
