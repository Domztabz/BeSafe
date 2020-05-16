package ke.co.visualdiagnoser.besafe.ui.home.newHome;

import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class SliderItemFragmentAdapter extends FragmentPagerAdapter {
    //    protected static final int[] CONTENT = new int[]{R.drawable.bakeries, R.drawable.chair, R.drawable.furniture, R.drawable.model_african,};
    protected static final String[] Titles = new String[]{"one", "2", "3", "4",};

    private static final String TAG = "TestFragmentAdapter";

    private int mCount = 8;

    public SliderItemFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem()" );
        if(position == 0) {
            return new ShareFragment();

        } else {
            return new ItemFragment();

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return SliderItemFragmentAdapter.Titles[position % 2];
    }
}
