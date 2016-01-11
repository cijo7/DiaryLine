package com.solidskulls.diaryline;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class Editor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button done = (Button) findViewById(R.id.button_publish);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddName(view);
                Snackbar.make(view, "Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void onClickAddName(View view) {
// Add a new student record
        ContentValues values = new ContentValues();
        values.put(ContentManager.TEXT,((EditText)findViewById(R.id.diaryInputString)).getText().toString());
        values.put(ContentManager.DATE,getDay());
        Uri uri = (new ContentManager()).insert(
                ContentManager.CONTENT_URI, values);
        Toast.makeText(getBaseContext(),
                uri.toString(), Toast.LENGTH_LONG).show();
    }

    public static long getDay(){
        return (Calendar.getInstance().getTimeInMillis()/(1000*60*60*24));
    }
}
