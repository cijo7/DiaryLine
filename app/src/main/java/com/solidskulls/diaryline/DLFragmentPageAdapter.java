package com.solidskulls.diaryline;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by cijo-saju on 12/1/16.
 *
 */
public class DLFragmentPageAdapter extends FragmentStatePagerAdapter {
    public DLFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return DiaryTextPreview.newInstance(position);
    }

    @Override
    public int getCount() {
        return DLMainActivity.COUNT;
    }
}
