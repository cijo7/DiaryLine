package com.solidskulls.diaryline;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import java.util.Calendar;

import timber.log.Timber;

public class LauncherTaskBG extends BroadcastReceiver {
    static String MESSAGE ="WhatToDo";
    @Override
    public void onReceive(Context context,Intent intent){
        Timber.d("We are in broadcast it!");
        Bundle e=intent.getExtras();
        boolean t=e.getBoolean(MESSAGE);
        if(t) {
            setAlarm(context);
            return;
        }
        // FIXME: 20/1/16 On works when app is open @priority(100)
        PushNotificationHelper.notify(context, "Reminder", "Write your today's diary", "Whats in your mind today?", 1);
        setAlarm(context);// TODO: 26/1/16 Don't set alarm if diary entry alredy added


    }

    /**
     * Sets an alarm for updater
     * @param context Context
     */
    public static void setAlarm(Context context){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources=context.getResources();
        if(sharedPreferences.getBoolean(resources.getString(R.string.pref_notification_on),true)) {    //Create new One only when notifications enabled
            String time=sharedPreferences.getString(resources.getString(R.string.pref_notification_timer),resources.getString(R.string.pref_notifications_timer_value));
            if(time.equals("")) {
                Timber.d("Empty time");
                return;
            }
            Intent intentT = new Intent(context, LauncherTaskBG.class);
            intentT.putExtra(MESSAGE, false);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentT, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, getOffsetToTimer(time), pendingIntent);
            Timber.d( "Set alarm on " + DateUtils.formatDateTime(context,getOffsetToTimer(time),DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_SHOW_DATE));
        }
    }

    private static long getOffsetToTimer(String time){
        try {
            int hour = Integer.parseInt(time.split("[: ]")[0]);
            if(hour==12)                                                                            //Calender only support 0-11 for HOUR
                hour=0;
            int minutes=Integer.parseInt(time.split("[: ]")[1]);
            int AM_PM=(time.split("[: ]")[2]).equals("AM")?Calendar.AM:Calendar.PM;

            Calendar calendar=Calendar.getInstance(),t=Calendar.getInstance();
            calendar.set(Calendar.HOUR, hour);
            calendar.set(Calendar.MINUTE,minutes);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.AM_PM, AM_PM);
            long difference=calendar.getTimeInMillis()-t.getTimeInMillis();
            if(difference<=0)
                calendar.setTimeInMillis(calendar.getTimeInMillis()+24*60*60*1000);//Increment a day

            return calendar.getTimeInMillis();
        }catch (Exception e){
            Timber.d(e,"Unable to parse timer");
        }
        return  Calendar.getInstance().getTimeInMillis()+24*60*60*1000+1000*60*5;
    }


}
/**
 *
 * Possible Bugs:
 * The Pending intent may get updated from other places and may poses a problem for consecutive alarm set. A rare case situation. @priority low
 */