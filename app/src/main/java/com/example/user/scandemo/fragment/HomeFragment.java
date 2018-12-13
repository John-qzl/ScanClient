package com.example.user.scandemo.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.user.scandemo.Base.BaseFragment;
import com.example.user.scandemo.Base.PagerSlidingTabStrip;
import com.example.user.scandemo.Bean.CheckDetailBean;
import com.example.user.scandemo.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiaozhili on 2018/9/28 22:22.
 */

@SuppressLint("ValidFragment")
public class HomeFragment extends BaseFragment{

    private FragmentAll fragmentAll;
    private FragmentChecked fragmentCheck;
    private FragmentUncheck fragmentUchecked;
    private MyPagerAdapter myAdapter;
    private ViewPager pager;
    private List<CheckDetailBean> checkDetailBeanList = new ArrayList<>();
    private String checkID;
    /**
     * PagerSlidingTabStrip的实例
     */
    private PagerSlidingTabStrip tabs;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    public HomeFragment(List<CheckDetailBean> checkDetailBeanList, String checkID) {
        this.checkDetailBeanList = checkDetailBeanList;
        this.checkID = checkID;
    }

    private List<CheckDetailBean> initdate(int type) {
        List<CheckDetailBean> list = new ArrayList<>();
        //筛选所有盘点数据
        if (type == 0) {
            list = checkDetailBeanList;
        }
        //筛选未盘点数据
        else if (type == 1) {
            for (int i = 0; i < checkDetailBeanList.size(); i++) {
                if (checkDetailBeanList.get(i).getStatus().equals("否")) {
                    list.add(checkDetailBeanList.get(i));
                }
            }
        }
        //筛选已盘点数据
        else if (type == 2) {
            for (int j = 0; j < checkDetailBeanList.size(); j++) {
                if (checkDetailBeanList.get(j).getStatus().equals("是")) {
                    list.add(checkDetailBeanList.get(j));
                }
            }
        }
        return list;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init() {
        setOverflowShowingAlways();
        dm = getResources().getDisplayMetrics();
        pager = (ViewPager) getRootView().findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) getRootView().findViewById(R.id.tabs);
        myAdapter = new MyPagerAdapter(getChildFragmentManager());
        pager.setAdapter(myAdapter);
        tabs.setViewPager(pager);
        setTabsValue();
    }

    @Override
    protected void viewClick(View view) {

    }

    @Override
    protected void lazyLoad() {

    }
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(getActivity());
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#45c01a"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"未盘点", "已盘点", "所有设备"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0:
                    if (fragmentUchecked == null) {
                        fragmentUchecked = new FragmentUncheck( checkID);
                    }
                    return fragmentUchecked;
                case 1:
                    if (fragmentCheck == null) {
                        fragmentCheck = new FragmentChecked(initdate(2), checkID);
                    }
                    return fragmentCheck;
                case 2:
                    if (fragmentAll == null) {
                        fragmentAll = new FragmentAll(initdate(0), checkID);
                    }
                    return fragmentAll;
                default:
                    return null;
            }
        }

    }
}
