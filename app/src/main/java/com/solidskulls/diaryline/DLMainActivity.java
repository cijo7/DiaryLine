package com.solidskulls.diaryline;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;

public class DLMainActivity extends AppCompatActivity implements NotifyTasks.OnFragmentInteractionListener,DiaryTextPreview.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    NotifyTasks n;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlmain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Calendar c=Calendar.getInstance();

        Log.d("Date","Days :"+(c.getTimeInMillis()/(1000*60*60*24)));

    }
    @Override
    public void onStart(){
        super.onStart();
        fragmentManager=getFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        if(true){
            n= NotifyTasks.newInstance("Add your biography", "editor");
            DiaryTextPreview dt=new DiaryTextPreview();
            fragmentTransaction.add(R.id.container,n,"NOTIFY");
            fragmentTransaction.add(R.id.container,dt);
        }
        fragmentTransaction.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dlmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,Settings.class));
        }else if(id==R.id.action_help)
            startActivity(new Intent(this,Help.class));
        return super.onOptionsItemSelected(item);
    }


    /** Event Listeners **/
    public void onNotifyInteraction(int action){
        switch (action){
            case NotifyTasks.CLOSE:
                getFragmentManager().beginTransaction().remove(n).commit();
                break;
            case NotifyTasks.EDITOR:Intent i=new Intent(this,Editor.class);
                startActivity(i);
                break;
        }
    }
    public void onDiaryPreviewInteraction(){

    }

}
