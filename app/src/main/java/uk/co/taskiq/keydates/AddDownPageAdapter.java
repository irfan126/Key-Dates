package uk.co.taskiq.keydates;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Administrator on 18/05/2016.
 */
public class AddDownPageAdapter  extends FragmentStatePagerAdapter {


    int mNumOfTabs;

    public AddDownPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:

                DownloadFragment tab1 = new DownloadFragment();
                return tab1;

            case 1:
                RequestFragment tab2 = new RequestFragment();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
