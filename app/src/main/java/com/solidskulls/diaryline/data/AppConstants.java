package com.solidskulls.diaryline.data;

/**
 * Created by cijo-saju on 4/2/16.
 */
public class AppConstants {
    public static final String NOTES="notes", DIARY="diary",LISTS="lists";
    private static AppConstants ourInstance = new AppConstants();

    public static AppConstants getInstance() {
        return ourInstance;
    }

    private AppConstants() {
    }
}
