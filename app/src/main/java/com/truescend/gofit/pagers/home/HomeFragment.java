package com.truescend.gofit.pagers.home;


import android.app.Activity;
import android.content.Intent;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sn.app.storage.UserStorage;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.SNToast;
import com.truescend.gofit.App;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseFragment;
import com.truescend.gofit.pagers.base.adapter.InfinitePagerAdapter;
import com.truescend.gofit.pagers.base.dialog.CalendarDialog;
import com.truescend.gofit.pagers.home.adapter.HomeHeadPagerAdapter;
import com.truescend.gofit.pagers.home.adapter.ItemTouchEditAdapterHelper;
import com.truescend.gofit.pagers.home.adapter.ItemTouchEditAdapterHelper.ItemObject;
import com.truescend.gofit.pagers.home.diet.bean.ItemUserDietCardView;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.views.InfiniteViewPager;
import com.truescend.gofit.views.QuickViewPager;
import com.truescend.gofit.views.SportChartViewSwitcher;
import com.truescend.gofit.views.TitleLayout;
import com.truescend.gofit.views.VerticalScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2017/11/17).
 * 功能:主页(运动图表/睡眠+心率+血氧+血压 item )界面
 */
public class HomeFragment extends BaseFragment<HomePresenterImpl, IHomeContract.IView> implements IHomeContract.IView {
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.vpHead)
    InfiniteViewPager vpHead;
//    @BindView(R.id.cvHomeSleep)
//    View cvHomeSleep;
//    @BindView(R.id.cvHomeCheck)
//    View cvHomeCheck;
//    @BindView(R.id.cvHomeHeart)
//    View cvHomeHeart;
//    @BindView(R.id.cvHomeBloodPressure)
//    View cvHomeBloodPressure;
//    @BindView(R.id.cvHomeBloodOxygen)
//    View cvHomeBloodOxygen;

    @BindView(R.id.cvHomeUserDiet)
    View cvHomeUserDiet;

    @BindView(R.id.ivWeatherIcon)
    ImageView ivWeatherIcon;

    @BindView(R.id.tvWeatherTemperature)
    TextView tvWeatherTemperature;
    @BindView(R.id.tvWeatherTypeName)
    TextView tvWeatherTypeName;

    @BindView(R.id.rlRefresh)
    SmartRefreshLayout rlRefresh;

    @BindView(R.id.mGridRecyclerView)
    RecyclerView mGridRecyclerView;

    @BindView(R.id.mNestedScrollView)
    VerticalScrollView mNestedScrollView;

    @BindView(R.id.tvEditMode)
    TextView tvEditMode;

    //    @BindView(R.id.mGridLayout)
//    AutoFitGridLayout mGridLayout;
//    private ItemCardView sleepCardView;
//    private ItemCardView checkCardView;
//    private ItemCardView heartCardView;
//    private ItemCardView bloodPressureCardView;
//    private ItemCardView bloodOxygenCardView;
    private HomeHeadPagerAdapter mStepChartPagerAdapter;
    private CalendarDialog calendarDialog;

    private int lastItemPosition;
    private String lastTodayDate;
    private ItemUserDietCardView itemUserDietCardView;
    private ItemTouchEditAdapterHelper mItemHelper;
    private ItemTouchEditAdapterHelper.ItemAdapter itemAdapter;
    private ArrayList<ItemObject> itemTotal = new ArrayList<>();
    private ArrayList<ItemObject> itemUnSupport = new ArrayList<>();


    @Override
    protected HomePresenterImpl initPresenter() {
        return new HomePresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onCreate() {
        initTitle();
        initCalendarSelector();
        initItems();
        initHeadViewPager();
        initRefreshLayout();
    }

    @Override
    protected void onVisible() {
        refreshCurrentSelectedDateData();
        getPresenter().requestWeatherData();
    }

    private void refreshCurrentSelectedDateData() {
        if (!isVisible()) {
            return;
        }

        if (!IF.isEmpty(lastTodayDate)) {
            String curTodayDate = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
            if (DateUtil.getDateOffset(curTodayDate, lastTodayDate) >= 1) {
                //检测到跨天!, 刷新界面为最新时间
                App.setSelectedCalendar(DateUtil.getCurrentCalendar());
            }
        }
        //刷新标题
        setTitleDateData();
        //刷新内容
        reLoadData();
        //刷新可滑动状态
        vpHead.setCanScroll(!DateUtil.equalsToday(App.getSelectedCalendar()));

    }

    /**
     * 初始化日历选择器
     */
    private void initCalendarSelector() {
        calendarDialog = new CalendarDialog(getActivity());
        calendarDialog.setCalendarClickListener(mCalendarClickListener);
    }

    /**
     * 初始化标题
     */
    private void initTitle() {

        tlTitle.setTitle(R.string.title_today);
        tlTitle.setTitleOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.setSelectedCalendar(DateUtil.getCurrentCalendar());
                refreshCurrentSelectedDateData();
            }
        });
        //设置左边一张日历图片，设置右边两张图片，1，分享图片按钮 2，统计图片按钮

        tlTitle.addLeftItem(TitleLayout.ItemBuilder.Builder()
                .set9PatchModelEnable(true)
                .setIconSizeRatio(42)
                .setIcon(R.drawable.icon_calendar)
                .setText(DateUtil.getDate(DateUtil.DAY, App.getSelectedCalendar()))
                .setTextSize(12)
                .setTextViewTag("tvCalendarTextView")
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendarDialog.show();
                        calendarDialog.setDate(App.getSelectedCalendar());

                    }
                }));


        tlTitle.addRightItem(TitleLayout.ItemBuilder.Builder().

                setIcon(R.mipmap.icon_data).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PageJumpUtil.startSportActivity(getActivity());
                    }
                }));


    }

    /**
     * 初始化视图
     */
    protected void initItems() {

        itemUserDietCardView = new ItemUserDietCardView(cvHomeUserDiet);
        itemUserDietCardView.setDietListMealClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageJumpUtil.startDietListMealActivity(HomeFragment.this);
            }
        });
        itemUserDietCardView.setDietTargetSettingClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageJumpUtil.startDietTargetSettingActivity(HomeFragment.this);
            }
        });
        itemUserDietCardView.setDietStatisticsClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageJumpUtil.startDietStatisticsActivity(getActivity());
            }
        });
        itemUserDietCardView.setDietPlanThinBodyButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageJumpUtil.startDietTargetSettingActivity(HomeFragment.this);
            }
        });



        mItemHelper = new ItemTouchEditAdapterHelper(getActivity());
        itemAdapter = mItemHelper.getAdapter();
        //所有的item 初始化

        //这个总item列表是全部item,不管显示不显示,而且数量一经初始化一次, 以后是不会变化的
        itemTotal.clear();
        itemTotal.add(new ItemObject(ItemObject.ITEM_CARD_RANKING, true, true, null));
        itemTotal.add(new ItemObject(ItemObject.ITEM_CARD_SPORT_MODE, true, true, null));
        itemTotal.add(new ItemObject(ItemObject.ITEM_CARD_SLEEP, true, true, null));
        itemTotal.add(new ItemObject(ItemObject.ITEM_CARD_CHECK, true, true, null));
        itemTotal.add(new ItemObject(ItemObject.ITEM_CARD_HEART, true, true, null));
        itemTotal.add(new ItemObject(ItemObject.ITEM_CARD_BLOOD_PRESSURE, true, true, null));
        itemTotal.add(new ItemObject(ItemObject.ITEM_CARD_BLOOD_OXYGEN, true, true, null));

        //按上次的排序规则进行优先模糊排序
        List<Integer> homeItemOrder = UserStorage.getHomeItemOrder();
        if (!IF.isEmpty(homeItemOrder)) {
            sortByRules(homeItemOrder, itemTotal);
        }
        itemAdapter.setList(itemTotal);


        mItemHelper.attachToRecyclerView(mGridRecyclerView);
        mItemHelper.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onClick(tvEditMode);
                return false;
            }
        });

        mItemHelper.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (itemAdapter.isEditMode()) {
                    return;
                }
                ItemTouchEditAdapterHelper.ItemObject item = itemAdapter.getItem(position);
                switch (item.getItemType()) {
                    case ItemObject.ITEM_CARD_SLEEP:
                        PageJumpUtil.startSleepActivity(mContext);
                        break;
                    case ItemObject.ITEM_CARD_CHECK:
                        if (!SNBLEHelper.isConnected()) {
                            SNToast.toast(R.string.toast_band_is_disconnect);
                            return;
                        }
                        PageJumpUtil.startHealthCheckActivity(mContext);
                        break;
                    case ItemObject.ITEM_CARD_HEART:
                        PageJumpUtil.startHeartActivity(mContext);
                        break;
                    case ItemObject.ITEM_CARD_BLOOD_PRESSURE:
                        PageJumpUtil.startPressureActivity(mContext);
                        break;
                    case ItemObject.ITEM_CARD_BLOOD_OXYGEN:
                        PageJumpUtil.startOxygenActivity(mContext);
                        break;
                    case ItemObject.ITEM_CARD_RANKING:
                        PageJumpUtil.startRankingActivity(mContext);
                        break;
                    case ItemObject.ITEM_CARD_SPORT_MODE:
                        PageJumpUtil.startSportModeActivity(mContext);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.tvEditMode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvEditMode:
                List<ItemObject> curItems = itemAdapter.getList();


                if (itemAdapter.isEditMode()) {
                    List<ItemObject> itemClose = new ArrayList<>();
                    List<Integer> itemTypeClose = new ArrayList<>();
                    //记录已关闭的Item
                    for (ItemObject itemObject : curItems) {
                        if (!itemObject.isOpen()) {
                            itemClose.add(itemObject);
                            itemTypeClose.add(itemObject.getItemType());
                        }
                    }
                    //移除掉已关闭的item 不在界面显示
                    curItems.removeAll(itemClose);
                    UserStorage.setHomeItemClose(itemTypeClose);

                    //退出编辑模式
                    itemAdapter.setEditMode(false);
                    //保存排序
                    UserStorage.setHomeItemOrder(itemAdapter.getItemTypeOrder());
                } else {
                    List<ItemObject> historyItemClose = getHistoryItemClose();
                    //把已关闭的item 恢复回来, 以便编辑
                    if (!historyItemClose.isEmpty()) {
                        curItems.addAll(historyItemClose);
                    }

                    //进入编辑模式
                    itemAdapter.setEditMode(true);
                }

                break;
        }
    }


    /**
     * 初始化头部(运动圆形+图表)ViewPager
     */
    private void initHeadViewPager() {
        mStepChartPagerAdapter = new HomeHeadPagerAdapter(getContext());
        vpHead.setAdapter(new InfinitePagerAdapter(mStepChartPagerAdapter));
//        vpHead.setPageTransformer(true, new GalleryTransformer());
        lastItemPosition = vpHead.getRealCurrentItem();


        vpHead.addOnPageChangeListener(onPageChangeListener);
    }

    /**
     * 初始化刷新控件和事件
     */
    private void initRefreshLayout() {
        //手动刷新
        rlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshCurrentSelectedDateData();
                getPresenter().requestStartDeviceDataSync();
            }
        });
    }

    @Override
    public void onDeviceDataSyncSuccess() {
        if (rlRefresh != null && rlRefresh.isRefreshing()) {
            rlRefresh.finishRefresh();
        }
    }

    /**
     * 设置标题日期相关数据
     */
    private void setTitleDateData() {

        Calendar calendar = App.getSelectedCalendar();
        String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
        if (DateUtil.equalsToday(date)) {
            tlTitle.setTitle(R.string.title_today);
            lastTodayDate = date;
        } else {
            tlTitle.setTitle(date);
        }
        //显示天
        tlTitle.setTextWithTag("tvCalendarTextView", DateUtil.getDate(DateUtil.DAY, calendar));

    }


    /**
     * 日历选择器事件
     */
    private CalendarDialog.OnCalendarClickListener mCalendarClickListener = new CalendarDialog.OnCalendarClickListener() {
        @Override
        public void onDayChanged(Calendar date) {
            //重新赋值 给当前时间
            App.getSelectedCalendar().setTimeInMillis(date.getTimeInMillis());
            refreshCurrentSelectedDateData();
        }

        @Override
        public void onMonthChanged(Calendar date) {
            //TODO 数据查询
        }
    };

    /**
     * 顶部图表滑动监听
     */
    private QuickViewPager.SimpleOnPageChangeListener onPageChangeListener = new QuickViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            if (lastItemPosition != position) {
                if (lastItemPosition > position) {
                    //让日期 前进或后退
                    App.getSelectedCalendar().add(Calendar.DAY_OF_MONTH, -1);
                }
                if (lastItemPosition < position) {
                    App.getSelectedCalendar().add(Calendar.DAY_OF_MONTH, +1);
                }
            }
            lastItemPosition = position;

            refreshCurrentSelectedDateData();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);

        }
    };

    private void reLoadData() {
        getPresenter().requestLoadStepChart(App.getSelectedCalendar());
        getPresenter().requestLoadSleepItemData(App.getSelectedCalendar());
        getPresenter().requestLoadHeartRateItemData(App.getSelectedCalendar());
        getPresenter().requestLoadBloodOxygenItemData(App.getSelectedCalendar());
        getPresenter().requestLoadBloodPressureItemData(App.getSelectedCalendar());
        getPresenter().requestLoadSportModeItemData(App.getSelectedCalendar());
        getPresenter().requestLoadDietStatisticsItemData(App.getSelectedCalendar());
    }


    @Override
    public void onUpdateStepChartData(int targetStepValue, int currentStepValue, float distanceTotal, float caloriesTotal, String distanceUnit, List<Integer> data) {

        if (vpHead == null || mStepChartPagerAdapter == null || mStepChartPagerAdapter.getCount() == 0)
            return;
        List<SportChartViewSwitcher> list = mStepChartPagerAdapter.getList();
        SportChartViewSwitcher switcher = list.get(vpHead.getCurrentItem());
        if (switcher == null) return;
        //更新顶部滚动ViewPager里的圆形进度条步数数据 和 步数柱状图
        switcher.setStepCircleProgressChartData(targetStepValue, currentStepValue, distanceTotal, caloriesTotal,distanceUnit );
        switcher.setStepBarChartData(targetStepValue, currentStepValue, data);
    }

    @Override
    public void onUpdateSleepItemData(CharSequence title, CharSequence content) {
        mItemHelper.notifyItemDataChange(ItemObject.ITEM_CARD_SLEEP, new ItemTouchEditAdapterHelper.ItemDataWrapper(title, content));
    }

    @Override
    public void onUpdateHeartRateItemData(CharSequence title, CharSequence content) {
        mItemHelper.notifyItemDataChange(ItemObject.ITEM_CARD_HEART,new ItemTouchEditAdapterHelper.ItemDataWrapper(title, content));
    }

    @Override
    public void onUpdateBloodOxygenItemData(CharSequence title, CharSequence content) {
        mItemHelper.notifyItemDataChange(ItemObject.ITEM_CARD_BLOOD_OXYGEN, new ItemTouchEditAdapterHelper.ItemDataWrapper(title, content));
    }

    @Override
    public void onUpdateBloodPressureItemData(CharSequence title, CharSequence content) {
        mItemHelper.notifyItemDataChange(ItemObject.ITEM_CARD_BLOOD_PRESSURE, new ItemTouchEditAdapterHelper.ItemDataWrapper(title, content));
    }

    @Override
    public void onUpdateSportModeItemData(@DrawableRes int icon, CharSequence subTitle1, CharSequence subTitle2, CharSequence subTitle3) {
        mItemHelper.notifyItemDataChange(ItemObject.ITEM_CARD_SPORT_MODE, new ItemTouchEditAdapterHelper.ItemDataWrapper(subTitle1, subTitle2, subTitle3,icon));
    }

    @Override
    public void onUpdateDietStatisticsItemData(CharSequence content) {
        itemUserDietCardView.setDietCalorieData(content);
    }

    @Override
    public void onUpdateDietMealDetailsItemData(CharSequence content) {
        itemUserDietCardView.setDietMealDetails(content);
    }

    @Override
    public void onPause() {
        super.onPause();
        //用户点击其他界面时 退出编辑模式
        if (itemAdapter != null && itemAdapter.isEditMode()) {
            itemAdapter.setEditMode(false);
        }
    }

    private List<Integer> rules = Arrays.asList(
            ItemObject.ITEM_CARD_RANKING,
            ItemObject.ITEM_CARD_SPORT_MODE,
            ItemObject.ITEM_CARD_SLEEP,
            ItemObject.ITEM_CARD_CHECK,
            ItemObject.ITEM_CARD_HEART,
            ItemObject.ITEM_CARD_BLOOD_PRESSURE,
            ItemObject.ITEM_CARD_BLOOD_OXYGEN

    );
    @Override
    public void onResume() {
        super.onResume();



        DeviceInfo deviceInfo = DeviceType.getCurrentDeviceInfo();
        if (deviceInfo != null) {
            boolean supportHeartRate = deviceInfo.isSupportHeartRate();
            boolean supportBloodPressure = deviceInfo.isSupportBloodPressure();
            boolean supportBloodOxygen = deviceInfo.isSupportBloodOxygen();
            boolean supportSportMode = deviceInfo.isSupportSportMode();
            boolean supportDiet = deviceInfo.isSupportDiet() && BuildConfig.isSupportDiet;
            cvHomeUserDiet.setVisibility(supportDiet ? View.VISIBLE : View.GONE);


            //每次切换手环 或刷新时  都刷新itemSupport列表
            itemUnSupport.clear();
            for (int i = 0; i < itemTotal.size(); i++) {
                ItemObject itemObject = itemTotal.get(i);
                switch (itemObject.getItemType()) {
                    case ItemObject.ITEM_CARD_SLEEP:
                        //默认支持睡眠
                        break;
                    case ItemObject.ITEM_CARD_CHECK:
                        if (!(supportHeartRate || supportBloodPressure || supportBloodOxygen)) {
                            itemObject.setSupport(false);
                            itemUnSupport.add(itemObject);
                        }
                        break;
                    case ItemObject.ITEM_CARD_HEART:
                        if (!supportHeartRate) {
                            itemObject.setSupport(false);
                            itemUnSupport.add(itemObject);
                        }
                        break;
                    case ItemObject.ITEM_CARD_BLOOD_PRESSURE:
                        if (!supportBloodPressure) {
                            itemObject.setSupport(false);
                            itemUnSupport.add(itemObject);
                        }
                        break;
                    case ItemObject.ITEM_CARD_BLOOD_OXYGEN:
                        if (!supportBloodOxygen) {
                            itemObject.setSupport(false);
                            itemUnSupport.add(itemObject);
                        }
                        break;
                    case ItemObject.ITEM_CARD_SPORT_MODE:
                        if (!supportSportMode) {
                            itemObject.setSupport(false);
                            itemUnSupport.add(itemObject);
                        }
                        break;
                }
            }
        }
        //恢复到总 item数量 , 移除所有不支持的item, 不显示它们
        List<ItemObject> list = itemAdapter.getList();
        List<Integer> homeItemOrder = UserStorage.getHomeItemOrder();
        if (!IF.isEmpty(homeItemOrder)) {

            //----------------2019-06-04新增, 用户如果编辑过,那么后面如果更新新的卡片会默认显示在第二个卡片-----------------
            List<Integer> homeItemClose = UserStorage.getHomeItemClose();
            ArrayList<ItemObject> news = new ArrayList<>();
            for (int i = 0; i < itemTotal.size(); i++) {
                ItemObject itemObject = itemTotal.get(i);
                if (itemObject.isSupport()&&itemObject.isOpen()&&!homeItemClose.contains(itemObject.getItemType())) {
                    news.add(itemObject);
                }
            }

            if (homeItemOrder.size() < news.size()) {
                for (int j = 0; j < news.size(); j++) {
                    int itemType = news.get(j).getItemType();
                    if(!homeItemOrder.contains(itemType)){
                        homeItemOrder.add(1,itemType);//默认放第二个
                    }
                }
            }
            //----------------2019-06-04新增-----------------
            //----------------2019-06-04之前的代码如下-----------------
            sortByRules(homeItemOrder, itemTotal);
        }
        list.clear();
        list.addAll(itemTotal);
        list.removeAll(itemUnSupport);

        List<ItemObject> historyItemClose = getHistoryItemClose();
        //是否更换手环
        if (historyItemClose.isEmpty()) {
            if (homeItemOrder.isEmpty()) {
                //重置开关
                for (ItemObject itemObject : list) {
                    itemObject.setOpen(true);
                }
                //重置排序
                sortByRules(rules, (ArrayList<ItemObject>) list);
            }
        } else {
            list.removeAll(historyItemClose);
        }
        itemAdapter.notifyDataSetChanged();
    }

    public static void sortByRules(List<Integer> rules, ArrayList<ItemObject> src) {
        ArrayList<ItemObject> src_temp = new ArrayList<>(src);
        ArrayList<ItemObject> temp = new ArrayList<>(src_temp);
        final ArrayList<Integer> rules_temp = new ArrayList<>(rules);

        //按 rules取交集
        temp.retainAll(rules_temp);
        //按 rules排序
        Collections.sort(temp, new Comparator<ItemObject>() {
            public int compare(ItemObject o1, ItemObject o2) {
                int io1 = rules_temp.indexOf(o1);
                int io2 = rules_temp.indexOf(o2);
                return io1 - io2;
            }
        });
        //取差集 (取除了rules的)
        src_temp.removeAll(temp);
        //正常排序
        Collections.sort(src_temp, new Comparator<ItemObject>() {
            @Override
            public int compare(ItemObject o1, ItemObject o2) {
                return o1.getItemType() - o2.getItemType();
            }
        });
        //符合自定义规则的放到前面
        src_temp.addAll(0, temp);
        src.clear();
        src.addAll(src_temp);
    }

    @NonNull
    private List<ItemObject> getHistoryItemClose() {
        List<Integer> homeItemClose = UserStorage.getHomeItemClose();
        List<ItemObject> itemClose = new ArrayList<>();
        if (homeItemClose != null) {
            for (ItemObject itemObject : itemTotal) {
                for (Integer itemType : homeItemClose) {
                    if (itemObject.getItemType() == itemType) {
                        itemObject.setOpen(false);
                        itemObject.setData(null);
                        itemClose.add(itemObject);
                    }
                }
            }
        }
        return itemClose;
    }


    @Override
    public void onUpdateWeatherData(int weatherType, String weatherTemperatureRange, String weatherQuality) {
        tvWeatherTemperature.setText(weatherTemperatureRange);
        switch (weatherType) {
//            case 0x00://无
            case 0x01://晴
                ivWeatherIcon.setImageResource(R.mipmap.icon_weather_sunny_day_2);
                tvWeatherTypeName.setText(R.string.content_weather_sunny_day);
                break;
            case 0x02://多云
                ivWeatherIcon.setImageResource(R.mipmap.icon_weather_cloudy_2);
                tvWeatherTypeName.setText(R.string.content_weather_cloudy);
                break;
            case 0x14://雪
                ivWeatherIcon.setImageResource(R.mipmap.icon_weather_snow_2);
                tvWeatherTypeName.setText(R.string.content_weather_snow);
                break;
            case 0x03://阴天
                ivWeatherIcon.setImageResource(R.mipmap.icon_weather_cloudy_day_2);
                tvWeatherTypeName.setText(R.string.content_weather_cloudy_day);
                break;
            case 0x07://雨
                ivWeatherIcon.setImageResource(R.mipmap.icon_weather_rain_2);
                tvWeatherTypeName.setText(R.string.content_weather_rain);
                break;
        }
    }

    @Override
    public void onUpdateDietPlanThinBodyEnableStatus(boolean enable) {
        itemUserDietCardView.setDietPlanThinBodyButtonEnable(enable);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PageJumpUtil.REQUEST_CODE_RESULT && resultCode == Activity.RESULT_OK) {
            //检测到变化, 重新获取刷新今天的进餐数据
            getPresenter().requestLoadNetworkDietStatisticsItemData(App.getSelectedCalendar());
        }
    }
}

