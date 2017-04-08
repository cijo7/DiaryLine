package com.solidskulls.diaryline;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Created by cijo-saju on 12/1/16.
 *
 */
class DLFragmentPageAdapter extends FragmentStatePagerAdapter {
    DLFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return DiaryTextPreview.newInstance(position%(DLMainActivity.COUNT/2));
    }

    @Override
    public int getCount() {
        return DLMainActivity.COUNT;
    }
}
