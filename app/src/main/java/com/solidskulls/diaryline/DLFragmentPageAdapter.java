package com.solidskulls.diaryline;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by cijo-saju on 12/1/16.
 */
public class DLFragmentPageAdapter extends FragmentStatePagerAdapter {
    private DataBlockManager  tmp,dataBlockManager;
    public DLFragmentPageAdapter(FragmentManager fm) {
        super(fm);
        tmp=new DataBlockManager();
    }

    @Override
    public Fragment getItem(int position) {
        dataBlockManager=new DataBlockManager(tmp.oldDaySec(tmp.getMilliSeconds(),DLMainActivity.COUNT-1-position));
        dataBlockManager.readPackage();
        String str=dataBlockManager.getStringData();
        if(str==null) {
            str = "Nothing to display";
        }
        return DiaryTextPreview.newInstance(str,dataBlockManager.printableDate());
    }

    @Override
    public int getCount() {
        return DLMainActivity.COUNT;
    }
}
