package com.cijo7.diaryline;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cijo7.diaryline.data.DiaryManager;
import com.crashlytics.android.Crashlytics;
import com.cijo7.diaryline.data.AppConstants;
import com.cijo7.diaryline.data.DataBlockContainer;
import com.cijo7.diaryline.ui.NavigatorView;

import io.fabric.sdk.android.Fabric;
import java.util.Calendar;

import timber.log.Timber;

public class DLMainActivity extends AppCompatActivity implements
		NotifyTasks.OnFragmentInteractionListener,
		DiaryTextPreview.OnContentInteractionListener,
		Parcelable,
		NavigationView.OnNavigationItemSelectedListener{

    private boolean mNotificationStatus =false;
    static int COUNT=20000;
    NavigatorView navigatorView;
    private ViewPager viewPager;
    private DLFragmentPageAdapter dlFragmentPageAdapter;
    FloatingActionButton fab;

    private static int mPosition=COUNT/2;
    public Coordinator mCoordinator;
    private boolean notPopped =true;

	@SuppressWarnings("unused")
	public DLMainActivity(){

	}

	protected DLMainActivity(Parcel in) {
		mNotificationStatus = in.readByte() != 0;
		notPopped = in.readByte() != 0;
		mPosition = in.readInt();
	}

	public static final Creator<DLMainActivity> CREATOR = new Creator<DLMainActivity>() {
		@Override
		public DLMainActivity createFromParcel(Parcel in) {
			return new DLMainActivity(in);
		}

		@Override
		public DLMainActivity[] newArray(int size) {
			return new DLMainActivity[size];
		}
	};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_dlmain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this,
				drawer,
				toolbar,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close){
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (getSupportActionBar()!=null)
					getSupportActionBar().setTitle("DL");
			}
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if(getSupportActionBar()!=null)
					getSupportActionBar().setTitle("Nano");
			}
		};

		drawer.addDrawerListener(toggle);
		toggle.syncState();
		if (getSupportActionBar()!=null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			getSupportActionBar().setHomeButtonEnabled(true);
		}

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

        Timber.uprootAll();
        if(BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
        Timber.plant(new CrashlyticsTree());

       // DataBlockManager.init(this);

        viewPager=(ViewPager)findViewById(R.id.viewPager);
        navigatorView=(NavigatorView)findViewById(R.id.navigator_view);
        dlFragmentPageAdapter=new DLFragmentPageAdapter(getSupportFragmentManager());

        //mCoordinator=new Coordinator();
    }
    @Override
    public void onStart(){
        super.onStart();

        EnvironmentVariables.initialise(this);
        navigatorView.navigatorViewInIt(EnvironmentVariables.SCREEN_WIDTH, EnvironmentVariables.SCREEN_HEIGHT);//Initialise navigation view

        navigatorView.setNavigationData(Calendar.getInstance().getTimeInMillis() +(mPosition-(COUNT/2))*24*60*60*1000);

        viewPager.setAdapter(dlFragmentPageAdapter);/*
        viewPager.getLayoutParams().height= ViewGroup.LayoutParams.WRAP_CONTENT;
        ViewGroup.LayoutParams l=viewPager.getLayoutParams();
        l.height= ViewGroup.LayoutParams.WRAP_CONTENT;*/
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPosition > position) {                                         //Swipe Right
                    navigatorView.updateNavigatorAnimation(true);
                } else if (mPosition < position) {                                   //Swipe Left
                    navigatorView.updateNavigatorAnimation(false);
                }

                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(mPosition);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
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
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_camera) {
			// Handle the camera action
		} else if (id == R.id.nav_gallery) {

		} else if (id == R.id.nav_slideshow) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
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
        super.onActivityResult(requestCode, requestResult, intent);
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
	 * Display the popup
	 */
    private  void showPopup(){
        if(notPopped){//If not notPopped, then pop
            notPopped =false;
	        LinearLayout ln=(LinearLayout) findViewById(R.id.popup_buttons);
	        if (ln!=null)
	            ln.setVisibility(View.VISIBLE);
            /*
             * Lets add listeners to the buttons.
             */

            Animation animation= AnimationUtils.loadAnimation(this,R.anim.scaleup);
            Animation animation1=AnimationUtils.loadAnimation(this,R.anim.scaleup),animation2=AnimationUtils.loadAnimation(this,R.anim.scaleup);
            TextView buttons;

            fab.animate().rotation(45).setDuration(300).start();

            buttons=(TextView)findViewById(R.id.popup_list);
	        if (buttons != null) {
		        buttons.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                openListEditor();
		            }
		        });
	        }
	        animation.setStartOffset(100);
	        if (buttons != null) {
		        buttons.setAnimation(animation);
	        }

	        buttons=(TextView)findViewById(R.id.popup_notes);
	        if (buttons != null) {
		        buttons.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                onDiaryEdit(Editor.NOTES);
		            }
		        });
	        }
	        animation1.setStartOffset(200);
	        if (buttons != null) {
		        buttons.setAnimation(animation1);
	        }

	        buttons=(TextView)findViewById(R.id.popup_diary);
	        if (buttons != null) {
		        if(DiaryManager.wroteToday(this)){
			        buttons.setVisibility(View.GONE);
		        }else{
			        buttons.setVisibility(View.VISIBLE);
			        buttons.setOnClickListener(new View.OnClickListener() {
				        @Override
				        public void onClick(View v) {
					        onDiaryEdit(Editor.DIARY);
				        }
			        });
		        }
	        }
	        animation2.setStartOffset(300);
	        if (buttons != null) {
		        buttons.setAnimation(animation2);
	        }
        }else {//Lets remove every thing
            notPopped =true;

            fab.animate().rotation(0).setDuration(100).start();
	        LinearLayout ln=(LinearLayout) findViewById(R.id.popup_buttons);
	        if (ln!=null)
                ln.setVisibility(View.GONE);

	        TextView tv=(TextView) findViewById(R.id.popup_list);
            if(tv!=null)
            	tv.setOnClickListener(null);
	        tv=(TextView) findViewById(R.id.popup_notes);
	        if(tv!=null)
		        tv.setOnClickListener(null);
	        tv=(TextView) findViewById(R.id.popup_diary);
	        if(tv!=null)
		        tv.setOnClickListener(null);
        }
    }

    /**
     * Called to add a new list.
     */
    private void openListEditor(){
        startActivity(new Intent(this,EditorList.class));
        if(!notPopped)  //Close popup on exit from activity.
            showPopup();
    }

    /**
     * Called to update an existing list.
     * @param container the content to update.
     */
    private void openListEditor(DataBlockContainer container){
        Intent intent=new Intent(this,EditorList.class);
        intent.putExtra(EditorList.CONTENT,container);
        startActivity(intent);
    }

    /**
     * Called when diary is to be added.
     * @param type The type of content for editor.
     */
    private void onDiaryEdit(int type){
        Intent intent=new Intent(this,Editor.class);
        intent.putExtra(Editor.EDITOR_MODE, Editor.MODE_ADD);
        intent.putExtra(Editor.EDITOR_TYPE,type);
        startActivity(intent);
        if(!notPopped)
            showPopup();
    }

    /**
     * Called when diary contents like notes or diary is to be updated.
     * @param type The type of content for editor.
     * @param container The contents to be updated.
     */
    private void onDiaryEdit(int type,DataBlockContainer container){
        Intent intent=new Intent(this,Editor.class);
        intent.putExtra(Editor.EDITOR_MODE, Editor.MODE_UPDATE);
        intent.putExtra(Editor.EDITOR_TYPE,type);
        intent.putExtra(Editor.DATA_ID,container);
        startActivity(intent);
    }

    /* Event Listeners */
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
                i.putExtra(Editor.EDITOR_MODE, Editor.MODE_ADD);
                startActivity(i);
                break;
        }
    }

	@Override
    public void onItemSelected(DataBlockContainer position) {
        switch (position.getTag()) {
            case AppConstants.LISTS:
                openListEditor(position);
                break;
            case AppConstants.NOTES:
                onDiaryEdit(Editor.NOTES,position);
                break;
            case AppConstants.DIARY:
                onDiaryEdit(Editor.DIARY,position);
                break;
        }
        if(!notPopped)
            showPopup();
    }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (mNotificationStatus?1:0));
		dest.writeByte((byte) (notPopped?1:0));
		dest.writeInt(mPosition);
	}

	/**
     * Helper class of MainActivity
     */
    private class Coordinator implements Runnable {
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
            Timber.i("Thread exited");
        }

        /**
         * Stops the thread
         */
        void stop(){
            threadActive=false;
        }
    }

    private class CrashlyticsTree extends Timber.Tree{
        private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
        private static final String CRASHLYTICS_KEY_TAG = "tag";
        private static final String CRASHLYTICS_KEY_MESSAGE = "message";

        @Override
        protected void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable t) {

            Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
            Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
            Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);

            if (t == null) {
                Crashlytics.logException(new Exception(message));
            } else {
                Crashlytics.logException(t);
            }
        }
    }
}
