package com.cijo7.diaryline.data;

import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

/**
 * Database manager for diary.
 */

public class DiaryManager {
	public static boolean wroteToday(Context context) {
		boolean res= false;
		try {
			Cursor c = context.getContentResolver().query(ContentManager.Entry.CONTENT_URI, null, ContentManager.Entry.ENTRY_DATE + " LIKE '" + (new SimpleDateFormat("yyyy-MM-dd", Locale.US)).format(Calendar.getInstance().getTime()) + "%'", null, null);

			if (c != null) {
				if (c.moveToFirst()) {
					res = true;
				}
				c.close();
			} else
				Timber.d("Retrieve Failed");
		} catch (IllegalArgumentException e) {
			Timber.d(e, "We passed illegal arguments");
		}
		return res;
	}
}
