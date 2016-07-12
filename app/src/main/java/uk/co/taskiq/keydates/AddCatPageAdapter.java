package uk.co.taskiq.keydates;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Administrator on 11/10/2015.
 */
public class AddCatPageAdapter  extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public AddCatPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:

                CreateCatFragment tab1 = new CreateCatFragment() ;
                return tab1;
            case 1:
                DownloadFragment tab2 = new DownloadFragment();
                return tab2;

            case 2:
                UploadFragment tab3 = new UploadFragment();
                return tab3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}