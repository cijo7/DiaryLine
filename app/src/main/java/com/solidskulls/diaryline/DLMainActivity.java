package com.solidskulls.diaryline;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

public class DLMainActivity extends AppCompatActivity implements NotifyTasks.OnFragmentInteractionListener,DiaryTextPreview.OnFragmentInteractionListener {

    private boolean mNotificationStatus =false;
    static int COUNT=20000;
    NavigatorView navigatorView;
    private ViewPager viewPager;
    private DLFragmentPageAdapter dlFragmentPageAdapter;

    private static int mPosition=COUNT-1;
    public Coordinator mCoordinator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlmain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Timber.uprootAll();
        if(BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        DataBlockManager.init(this);

        viewPager=(ViewPager)findViewById(R.id.viewPager);
        navigatorView=(NavigatorView)findViewById(R.id.navigator_view);
        dlFragmentPageAdapter=new DLFragmentPageAdapter(getSupportFragmentManager());

        mCoordinator=new Coordinator();
    }
    @Override
    public void onStart(){
        super.onStart();


        navigatorView.navigatorViewInIt(DataBlockManager.SCREEN_WIDTH, DataBlockManager.SCREEN_HEIGHT);//Initialise navigation view


        navigatorView.setNavigationData(DataBlockManager.getCurrentMilliseconds()-((long)(COUNT-mPosition-1))*24*60*60*1000);

        viewPager.setAdapter(dlFragmentPageAdapter);
        viewPager.getLayoutParams().height= ViewGroup.LayoutParams.WRAP_CONTENT;
        ViewGroup.LayoutParams l=viewPager.getLayoutParams();
        l.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPosition > position) {                                         //Swipe Right
                    navigatorView.updateNavigatorAnimation(true);
                }else if (mPosition < position) {                                   //Swipe Left
                    navigatorView.updateNavigatorAnimation(false);
                }

                mPosition = position;
                //dlFragmentPageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(mPosition);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDiaryEdit();
            }
        });

        /*Intent intent=new Intent(this,LauncherTaskBG.class);
        intent.putExtra(LauncherTaskBG.MESSAGE, LauncherTaskBG.SKIP);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+5*1000,pendingIntent);
        Timber.d("Alarm Set");*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
                                                                            // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dlmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                   // Handle action bar item clicks here. The action bar will automatically handle clicks on
                                                                            // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
                                                                            //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,Settings.class));
        }else if(id==R.id.action_help)
            startActivity(new Intent(this,Help.class));
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the result of the activity.
     * @param requestCode the request code to intent
     * @param requestResult request result code
     * @param intent intent passed back
     */
    @Override
    public void onActivityResult(int requestCode,int requestResult,Intent intent){
        super.onActivityResult(requestCode,requestResult,intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mCoordinator!=null)
            mCoordinator.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DiaryTextPreview.recycleBitmap();//Recycle the static bitmap. Just in case.
    }



    /**
     * Called when diary is to be edited
     */
    private void onDiaryEdit(){
        Intent intent=new Intent(this,Editor.class);
        intent.putExtra(Editor.EDITOR_MODE, Editor.EDITOR_MODE_ADD);
        intent.putExtra(Editor.EDITOR_INIT_OFFSET_DAYS, COUNT - 1 - mPosition);
        startActivity(intent);
    }


    /** Event Listeners **/
    /**
     * Event Listener for Notifications fragment
     * @param action the action code
     */
    public void onNotifyInteraction(String action){
        switch (action){
            case NotifyTasks.ACTION_CLOSE:
                if(mNotificationStatus) {//Some notification is there, then close it.
                    mNotificationStatus = false;
                }
                break;
            case NotifyTasks.ACTION_EDITOR://If choose editor open editor
                Intent i=new Intent(this,Editor.class);
                i.putExtra(Editor.EDITOR_MODE, Editor.EDITOR_MODE_ADD);
                startActivity(i);
                break;
        }
    }


    /**
     * Helper class of MainActivity
     */
    public class Coordinator implements Runnable {
        private Thread thread;
        private boolean threadActive=true;

        Coordinator(){
            thread=new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            //Maintains date values
            while(threadActive){
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    Timber.d(e,"Thread has been Interrupted");
                }
            }
        }

        /**
         * Stops the thread
         */
        public void stop(){
            thread.interrupt();
            threadActive=false;
        }
    }

}
