package com.solidskulls.diaryline;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for showing and canceling push helper
 * notifications.
 * <p/>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
class PushNotificationHelper {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "PushHelper";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p/>
     * Make sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    static void notify(final Context context,
                       final String ticker, String title, String text, final int number) {
        final Resources res = context.getResources();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                        // Set required fields, including the small icon, the
                        // notification title, and text.
                .setSmallIcon(R.drawable.popup_icon)
                .setContentTitle(title)
                .setContentText(text)

                        // All fields below this line are optional.

                        // Use a default priority (recognized on devices running Android
                        // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                        // Set ticker text (preview) information for this notification.
                .setTicker(ticker)

                        // Show a number. This is useful when stacking notifications of
                        // a single type.
                .setNumber(number)

                        // If this notification relates to a past or upcoming event, you
                        // should set the relevant time information using the setWhen
                        // method below. If this call is omitted, the notification's
                        // timestamp will by set to the time at which it was shown.
                        // upcoming event. The sole argument to this method should be
                        // the notification timestamp in milliseconds.
                        //.setWhen(...)

                        // Set the pending intent to be initiated when the user touches
                        // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context,DLMainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                        // Example additional actions for this notification. These will
                        // only show on devices running Android 4.1 or later, so you
                        // should ensure that the activity in this notification's
                        // content intent provides access to the same actions in
                        // another way.
                .addAction(
                        R.drawable.ic_action_stat_share,
                        "Open Editor",
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context,Editor.class).putExtra(Editor.EDITOR_INIT_OFFSET_DAYS,0),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                        // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_TAG, 0, notification);
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, String, String, int)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG, 0);
    }
}
