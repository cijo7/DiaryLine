package com.solidskulls.diaryline;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by cijo-saju on 12/1/16.
 *
 */
public class DLFragmentPageAdapter extends FragmentStatePagerAdapter {
    private DataBlockManager  dataBlockManager;
    public DLFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        dataBlockManager=new DataBlockManager(DLMainActivity.COUNT - 1 - position);
        dataBlockManager.readPackage();
        String str=dataBlockManager.getStringData();
        return DiaryTextPreview.newInstance(str,dataBlockManager.printableDate());
    }

    @Override
    public int getCount() {
        return DLMainActivity.COUNT;
    }
}
