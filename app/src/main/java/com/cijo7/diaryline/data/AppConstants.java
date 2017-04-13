package com.cijo7.diaryline.data;

/**
 * Constants for the App.
 */
public class AppConstants {
    public static final String NOTES="notes", DIARY="diary",LISTS="lists";
    private static AppConstants ourInstance = new AppConstants();
    public static final int quoteColor = 0xff0000ff;

    public static AppConstants getInstance() {
        return ourInstance;
    }

    private AppConstants() {
    }
}
