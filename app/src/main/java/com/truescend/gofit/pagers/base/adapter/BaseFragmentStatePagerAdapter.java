package com.truescend.gofit.pagers.base.adapter;



import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;



import com.truescend.gofit.utils.FragmentFactory;
import com.truescend.gofit.utils.TabLayoutManager;

/**
 * 作者:东芝(2017/11/17).
 * 功能:ViewPager+Fragment 用
 */
public class BaseFragmentStatePagerAdapter extends FragmentPagerAdapter implements TabLayoutManager.TabIconPagerAdapter {
    private Context context;
    private String[] mTitles;
    private int[][] icons;

    public BaseFragmentStatePagerAdapter(Context context, FragmentManager fm, String[] mTitles, int[][] icons) {
        super(fm);
        this.mTitles = mTitles;
        this.icons = icons;
        this.context=context;

    }

    @Override
    public Fragment getItem(int arg0) {

        return  FragmentFactory.getFragmentFactoryInstance().getFragment(arg0);
    }

    @Override
    public int getCount() {

        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Nullable
    @Override
    public Drawable getPageTitleIconDrawable(int position) {
        return null;
    }

    @Override
    public int[] getPageTitleIconDrawableRes(int position) {
        return icons[position];
    }

}
