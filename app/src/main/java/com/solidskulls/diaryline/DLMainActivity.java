package com.solidskulls.diaryline;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DLMainActivity extends AppCompatActivity implements NotifyTasks.OnFragmentInteractionListener,DiaryTextPreview.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    NotifyTasks notifyTasks;
    private boolean notificationStatus =false;

    private DataBlockManager dataBlockManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlmain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataBlockManager=new DataBlockManager();
    }
    @Override
    public void onStart(){
        super.onStart();
        fragmentManager=getFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();

        dataBlockManager.readPackage();
        if(dataBlockManager.getStringData()!=null) {
            fragmentTransaction.add(R.id.container, new DiaryTextPreview().newInstance(dataBlockManager.getStringData(), dataBlockManager.printableDate()), "preview");
            fragmentTransaction.commit();
        }else
            onDiaryPreviewInteraction(DiaryTextPreview.NOTIFY_POPUP);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDiaryEdit(dataBlockManager);
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
    /**
     * Event Listener for Notifications fragment
     * @param action the action code
     */
    public void onNotifyInteraction(String action){
        switch (action){
            case NotifyTasks.CLOSE:
                if(notificationStatus) {
                    getFragmentManager().beginTransaction().remove(notifyTasks).commit();
                    notificationStatus = false;
                }
                break;
            case NotifyTasks.EDITOR:
                Intent i=new Intent(this,Editor.class);
                i.putExtra(Editor.EDITOR_MODE, Editor.EDITOR_MODE_ADD);
                startActivity(i);
                break;
        }
    }

    public void onDiaryEdit(DataBlockManager dataBlockManager){
        Intent intent=new Intent(this,Editor.class);
        intent.putExtra(Editor.EDITOR_MODE, Editor.EDITOR_MODE_UPDATE);
        intent.putExtra(Editor.EDITOR_INIT_DAYS,dataBlockManager.getDay());
        startActivity(intent);
    }
    public void onDiaryPreviewInteraction(int action){
        switch (action){
            case  DiaryTextPreview.NOTIFY_POPUP:
                if(!notificationStatus) {
                    notifyTasks = NotifyTasks.newInstance("Add your biography", NotifyTasks.ACTION_EDITOR);
                    fragmentTransaction.add(R.id.container, notifyTasks, "NOTIFY");
                    notificationStatus = true;
                }
                break;
        }
        fragmentTransaction.commit();

    }

}
