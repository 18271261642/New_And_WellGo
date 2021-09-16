package com.truescend.gofit.pagers.ranking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.FriendListBean;
import com.sn.app.net.data.app.bean.FriendListBean.DataBean.FriendBean;
import com.sn.app.net.data.app.bean.RankingDefBean;
import com.sn.app.utils.AppUnitUtil;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sleep.SleepBean;
import com.sn.blesdk.db.data.sleep.SleepDao;
import com.sn.blesdk.db.data.sport.SportBean;
import com.sn.blesdk.db.data.sport.SportDao;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.SleepDataUtil;
import com.truescend.gofit.utils.UnitConversion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 作者:东芝(2019/01/11).
 * 功能:世界排行
 */
public class RankingPresenterImpl extends BasePresenter<IRankingContract.IView> implements IRankingContract.IPresenter {
    private IRankingContract.IView view;
    private boolean isLoadFriendList;
    public static final int TYPE_WEEK = 7;
    public static final int TYPE_MONTH = 30;


    public RankingPresenterImpl(IRankingContract.IView view) {
        this.view = view;
    }


    @Override
    public void loadFriendRanking() {
        if (!isLoadFriendList) {
            view.onLoading();
        }
        AppNetReq.getApi().friendlist().enqueue(new OnResponseListener<FriendListBean>() {
            @Override
            public void onResponse(final FriendListBean body) throws Throwable {
                isLoadFriendList = true;
                if (!isUIEnable()) {
                    return;
                }
                SNAsyncTask.execute(new SNVTaskCallBack() {
                    String distanceUnit;
                    String headUrl;
                    List<FriendListBean.DataBean> validData = new ArrayList<>();
                    int stepTotal;
                    float distanceTotal;
                    int selfLevelIndex = 1;

                    @Override
                    public void prepare() {
                        super.prepare();
                        distanceUnit = ResUtil.getString(R.string.unit_km);
                    }

                    @Override
                    public void run() throws Throwable {
                        List<FriendListBean.DataBean> data = body.getData();
                        UserBean user = AppUserUtil.getUser();
                        headUrl = user.getPortrait();
                        int user_id = user.getUser_id();

                        if (!IF.isEmpty(data)) {
                            //首次排序
                            Collections.sort(data, new Comparator<FriendListBean.DataBean>() {
                                @Override
                                public int compare(FriendListBean.DataBean o1, FriendListBean.DataBean o2) {
                                    return o2.getFriend().getSport().getStep() - o1.getFriend().getSport().getStep();
                                }
                            });
                            //添加最多4条数据, 同时排除0数据
                            for (FriendListBean.DataBean datum : data) {
                                FriendListBean.DataBean.FriendBean.SportBean sport = datum.getFriend().getSport();
                                if (DateUtil.equalsToday(sport.getToday_date()) && sport.getStep() > 0) {
                                    if (validData.size() < 4) {//固定5条 当大于4条时 不再添加, 因为等会要添加自己进去
                                        validData.add(datum);
                                    }
                                }
                            }
                        }
                        //创建自己 也加入排行列表, 此时一共最多5条
                        String currentDate = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
                        List<SportBean> sportBeans = SportDao.get(SportDao.class).queryForDay(user_id, currentDate);

                        FriendListBean.DataBean dataBean = new FriendListBean.DataBean();
                        dataBean.setFriend_user_id(user_id);
                        dataBean.setRemark("");
                        FriendBean friend = new FriendBean();
                        friend.setId(String.valueOf(user_id));
                        friend.setNickname(user.getNickname());
                        friend.setSign(user.getSign());
                        friend.setPortrait(user.getPortrait());
                        FriendBean.SportBean sport = new FriendBean.SportBean();

                        UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
                        if (!IF.isEmpty(sportBeans)) {
                            SportBean sportBean = sportBeans.get(0);
                            stepTotal = sportBean.getStepTotal();

                            sport.setStep(stepTotal);
                            sport.setDistance(sportBean.getDistanceTotal());
                            sport.setCalory(sportBean.getCalorieTotal());


                            //如果单位是英里,则需要转一下
                            if (unitConfig.distanceUnit == UnitConfig.DISTANCE_MILES) {
                                float miles = UnitConversion.kmToMiles( sportBean.getDistanceTotal());
                                distanceTotal = UnitConversion.toBandK(miles);
                            }else {
                                distanceTotal = UnitConversion.toBandK( sportBean.getDistanceTotal());
                            }

                        }
                        //如果单位是英里,则需要转一下
                        if (unitConfig.distanceUnit == UnitConfig.DISTANCE_MILES) {
                            distanceUnit = ResUtil.getString(R.string.unit_mile);
                        } else {
                            distanceUnit = ResUtil.getString(R.string.unit_km);
                        }


                        sport.setToday_date(currentDate);
                        friend.setSport(sport);
                        dataBean.setFriend(friend);
                        //自己 也加入排行列表
                        validData.add(dataBean);

                        //重排序
                        Collections.sort(validData, new Comparator<FriendListBean.DataBean>() {
                            @Override
                            public int compare(FriendListBean.DataBean o1, FriendListBean.DataBean o2) {
                                return o2.getFriend().getSport().getStep() - o1.getFriend().getSport().getStep();
                            }
                        });

                        //找到自己的排名
                        for (int i = 0; i < validData.size(); i++) {
                            FriendListBean.DataBean validDatum = validData.get(i);
                            if (validDatum.getFriend_user_id() == user_id) {
                                selfLevelIndex = i + 1;
                                break;
                            }
                        }


                    }

                    @Override
                    public void done() {
                        if(isUIEnable()) {
                            view.onLoadFriendRanking(validData, headUrl, selfLevelIndex, stepTotal, distanceTotal, distanceUnit);
                            view.onFinishRefresh();
                            view.onLoadingDone();
                        }
                    }

                    @Override
                    public void error(Throwable e) {
                        if(isUIEnable()) {
                            view.onFinishRefresh();
                            view.onLoadingDone();
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, String msg) {
                if(isUIEnable()) {
                    view.onFinishRefresh();
                    view.onLoadingDone();
                }
            }
        });

    }

    @Override
    public void loadSportRanking() {
        AppNetReq.getApi().loadSportRankingList().enqueue(new OnResponseListener<RankingDefBean>() {


            @Override
            public void onResponse(final RankingDefBean body) throws Throwable {
                SNAsyncTask.execute(new SNVTaskCallBack() {
                    String moreThanPercentText;
                    int max = -1;
                    int stepTotal = 0;
                    int headIconIndex = -1;
                    List<RankingDefBean.DataBean> data;
                    List<Integer> chartData;
                    String[][] rankingLabel;
                    Bitmap headIcon;

                    @Override
                    public void run() throws Throwable {
                        UserBean user = AppUserUtil.getUser();

                        data = body.getData();
                        chartData = new ArrayList<>();
                        int size = data.size();
                        rankingLabel = new String[size][2];

                        List<SportBean> sportBeans = SportDao.get(SportDao.class).queryForToday(user.getUser_id());
                        if (!IF.isEmpty(sportBeans)) {
                            SportBean sportBean = sportBeans.get(0);
                            stepTotal = sportBean.getStepTotal();
                        }
                        int totalPeople = 0;
                        int moreThanPeople = 0;
                        for (int i = 0; i < size; i++) {
                            RankingDefBean.DataBean datum = data.get(i);
                            int value = datum.getValue();
                            totalPeople += value;
                            max = Math.max(max, value);

                            int left = (int) datum.getLeft();
                            int right = (int) datum.getRight();
                            rankingLabel[i][0] = String.valueOf(left);
                            if (right == 0) {
                                rankingLabel[i][1] = "∞";
                            } else {
                                rankingLabel[i][1] = String.valueOf(right);
                            }
                            boolean case1 = stepTotal >= left && stepTotal <= right;
                            boolean case2 = right == 0 && left < stepTotal;
                            if (case1 || case2) {
                                headIconIndex = i;
                                moreThanPeople = totalPeople - value;
                                value += 1;/*1是自己*/
                            }
                            chartData.add(value);
                        }


                        moreThanPercentText = ResUtil.format(ResUtil.getString(R.string.content_more_than_user), percentage(totalPeople, moreThanPeople));

                        if (max > 0) {
                            max += 10;
                        }
                        if (max > 100) {
                            max += 50;
                        }
                        if (max > 1000) {
                            max += 500;
                        }
                        if (max > 10000) {
                            max += 5000;
                        }
                        if (max > 100000) {
                            max += 50000;
                        }

                        headIcon = createHeadIcon(user.getPortrait());

                    }

                    @Override
                    public void done() {
                        super.done();
                        if (isUIEnable()&&rankingLabel != null && chartData != null) {
                            view.onLoadSportRanking(rankingLabel, chartData, max, headIcon, headIconIndex, moreThanPercentText);
                        }
                    }

                    @Override
                    public void error(Throwable e) {
                        super.error(e);
                    }
                });
            }

            @Override
            public void onFailure(int ret, String msg) {

            }
        });
    }

    private int percentage(int totalPeople, int moreThanPeople) {
        int p = (int) (moreThanPeople * 1.0f / totalPeople * 100);
        //0%时 显示1% , 100%时显示99%
        if (p == 0) {
            p = 1;
        } else if (p == 100) {
            p = 99;
        }
        return p;
    }

    @Override
    public void loadSleepDurationRanking() {
        AppNetReq.getApi().loadSleepQualityRankingList().enqueue(new OnResponseListener<RankingDefBean>() {


            @Override
            public void onResponse(final RankingDefBean body) throws Throwable {
                SNAsyncTask.execute(new SNVTaskCallBack() {
                    String moreThanPercentText;
                    int max = -1;
                    int sleepTimeHour = -1;
                    int headIconIndex = -1;
                    List<RankingDefBean.DataBean> data;
                    List<Integer> chartData;
                    String[][] rankingLabel;
                    Bitmap headIcon;

                    @Override
                    public void run() throws Throwable {
                        UserBean user = AppUserUtil.getUser();

                        data = body.getData();
                        chartData = new ArrayList<>();
                        int size = data.size();
                        rankingLabel = new String[size][2];

                        List<SleepBean> sleepBeans = SleepDao.get(SleepDao.class).queryForToday(user.getUser_id());
                        try {
                            if (!IF.isEmpty(sleepBeans)) {
                                SleepBean sleepBean = sleepBeans.get(0);
                                sleepTimeHour = SleepDataUtil.getSleepTotal(sleepBean.getDeepTotal(), sleepBean.getLightTotal(), sleepBean.getSoberTotal()) / 60;
                            }
                        } catch (Exception ignored) {
                        }
                        int totalPeople = 0;
                        int moreThanPeople = 0;
                        for (int i = 0; i < size; i++) {
                            RankingDefBean.DataBean datum = data.get(i);
                            int value = datum.getValue();
                            totalPeople += value;
                            max = Math.max(max, value);

                            int left = (int) datum.getLeft();
                            int right = (int) datum.getRight();
                            rankingLabel[i][0] = ResUtil.format("%02d:%02d", left, 0);
                            rankingLabel[i][1] = ResUtil.format("%02d:%02d", right, 0);
                            if (left == 0 && right > 0) {
                                rankingLabel[i][0] = "<" + ResUtil.format("%02d:%02d", right, 0);
                                rankingLabel[i][1] = "";
                            } else if (right == 24 && left > 0) {
                                rankingLabel[i][0] = ">" + ResUtil.format("%02d:%02d", left, 0);
                                rankingLabel[i][1] = "";
                            }

                            boolean case1 = sleepTimeHour >= left && sleepTimeHour <= right;
                            boolean case2 = (right == 0 || right == 24) && left < sleepTimeHour;
                            if (case1 || case2) {
                                headIconIndex = i;
                                moreThanPeople = totalPeople - value;
                                value += 1;/*1是自己*/
                            }
                            chartData.add(value);
                        }


                        int percentage = percentage(totalPeople, moreThanPeople);
                        if (sleepTimeHour == -1) {
                            percentage = 1;
                        }
                        moreThanPercentText = ResUtil.format(ResUtil.getString(R.string.content_more_than_user), percentage);


                        if (max > 0) {
                            max += 10;
                        }
                        if (max > 100) {
                            max += 50;
                        }
                        if (max > 1000) {
                            max += 500;
                        }
                        if (max > 10000) {
                            max += 5000;
                        }
                        if (max > 100000) {
                            max += 50000;
                        }

                        headIcon = createHeadIcon(user.getPortrait());

                    }

                    @Override
                    public void done() {
                        super.done();
                        if (rankingLabel != null && chartData != null&&isUIEnable()) {
                            view.onLoadSleepDurationRanking(rankingLabel, chartData, max, headIcon, headIconIndex, moreThanPercentText);
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, String msg) {

            }
        });
    }

    @Override
    public void loadSleepTimeRanking() {
        AppNetReq.getApi().loadSleepTimeRankingList().enqueue(new OnResponseListener<RankingDefBean>() {


            @Override
            public void onResponse(final RankingDefBean body) throws Throwable {
                SNAsyncTask.execute(new SNVTaskCallBack() {
                    String moreThanPercentText;
                    int max = -1;
                    int sleepHour = -1;
                    int headIconIndex = -1;
                    List<RankingDefBean.DataBean> data;
                    List<Integer> chartData;
                    String[][] rankingLabel;
                    Bitmap headIcon;

                    @Override
                    public void run() throws Throwable {
                        UserBean user = AppUserUtil.getUser();

                        data = body.getData();
                        chartData = new ArrayList<>();
                        int size = data.size();
                        rankingLabel = new String[size][2];

                        List<SleepBean> sleepBeans = SleepDao.get(SleepDao.class).queryForToday(user.getUser_id());
                        try {
                            if (!IF.isEmpty(sleepBeans)) {
                                SleepBean sleepBean = sleepBeans.get(0);
                                String date = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM_SS, sleepBean.getSleepDetailsBeans().get(0).getBeginDateTime());
                                DateUtil.HMS hms = DateUtil.getHMSFromString(DateUtil.HH_MM_SS, date);
                                sleepHour = hms.getHour();
                            }
                        } catch (Exception ignored) {
                        }
                        int totalPeople = 0;
                        int moreThanPeople = 0;
                        for (int i = 0; i < size; i++) {
                            RankingDefBean.DataBean datum = data.get(i);
                            int value = datum.getValue();
                            totalPeople += value;
                            max = Math.max(max, value);

                            int left = (int) datum.getLeft();
                            int right = (int) datum.getRight();
                            rankingLabel[i][0] = ResUtil.format("%02d:%02d", left, 0);
                            rankingLabel[i][1] = ResUtil.format("%02d:%02d", right, 0);

                            boolean case1 = sleepHour >= left && sleepHour <= right;
                            boolean case2 = (right == 0 || right == 24) && left < sleepHour;
                            if (sleepHour != -1) {
                                if (case1 || case2) {
                                    headIconIndex = i;
                                    moreThanPeople = totalPeople - value;
                                    value += 1;/*1是自己*/
                                }
                            }
                            chartData.add(value);
                        }

                        int percentage = percentage(totalPeople, moreThanPeople);
                        if (sleepHour == -1) {
                            percentage = 99;
                        }
                        moreThanPercentText = ResUtil.format(ResUtil.getString(R.string.content_later_than_user), percentage);

                        if (max > 0) {
                            max += 10;
                        }
                        if (max > 100) {
                            max += 50;
                        }
                        if (max > 1000) {
                            max += 500;
                        }
                        if (max > 10000) {
                            max += 5000;
                        }
                        if (max > 100000) {
                            max += 50000;
                        }

                        headIcon = createHeadIcon(user.getPortrait());

                    }

                    @Override
                    public void done() {
                        super.done();
                        if (isUIEnable()&&rankingLabel != null && chartData != null) {
                            view.onLoadSleepTimeRanking(rankingLabel, chartData, max, headIcon, headIconIndex, moreThanPercentText);
                        }

                    }
                });
            }

            @Override
            public void onFailure(int ret, String msg) {

            }
        });
    }

    @Override
    public void loadWeekSleepDetailsChart(Calendar calendar) {
        final Calendar dateFirstWeek = DateUtil.getWeekFirstDate(calendar);
        final Calendar dateLastWeek = DateUtil.getWeekLastDate(calendar);
        requestQueryForBetweenDate(TYPE_WEEK, dateFirstWeek, dateLastWeek);
    }

    @Override
    public void loadMonthSleepDetailsChart(Calendar calendar) {
        Calendar dateFirstDay = DateUtil.getMonthFirstDate(calendar);
        Calendar dateLastDay = DateUtil.getMonthLastDate(calendar);
        requestQueryForBetweenDate(TYPE_MONTH, dateFirstDay, dateLastDay);
    }


    private void requestQueryForBetweenDate(final int dateType, final Calendar dateFirst, final Calendar dateLast) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            int curDeepTime;
            int curLightTime;
            int curSoberTime;
            int avgDeepTime;
            int avgLightTime;
            int avgSoberTime;
            String dateFromAndTo;
            String dateTo;
            String dateFrom;

            @Override
            public void run() throws Throwable {
                dateFrom = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateFirst);
                dateTo = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateLast);

                dateFromAndTo = ResUtil.format(ResUtil.getString(R.string.content_range_avg_time), dateFrom, dateTo);

                SleepDao sleepDao = SNBLEDao.get(SleepDao.class);
                int user_id = AppUserUtil.getUser().getUser_id();
                List<SleepBean> todaySleepBeans = sleepDao.queryForToday(user_id);
                if (!IF.isEmpty(todaySleepBeans)) {
                    SleepBean sleepBean = todaySleepBeans.get(0);
                    curDeepTime = sleepBean.getDeepTotal();
                    curLightTime = sleepBean.getLightTotal();
                    curSoberTime = sleepBean.getSoberTotal();
                }
                List<SleepBean> sleepBeans = sleepDao.queryForBetween(user_id, dateFrom, dateTo);
                int deepTotal = 0;
                int lightTotal = 0;
                int soberTotal = 0;
                int size = sleepBeans.size();
                for (int i = 0; i < size; i++) {
                    SleepBean sleepBean = sleepBeans.get(i);
                    deepTotal += sleepBean.getDeepTotal();
                    lightTotal += sleepBean.getLightTotal();
                    soberTotal += sleepBean.getSoberTotal();
                }
                avgDeepTime = Math.round(deepTotal * 1.0f / (size == 0 ? 1 : size));
                avgLightTime = Math.round(lightTotal * 1.0f / (size == 0 ? 1 : size));
                avgSoberTime = Math.round(soberTotal * 1.0f / (size == 0 ? 1 : size));


            }

            @Override
            public void done() {
                if(isUIEnable()) {
                    view.onLoadSleepDetailsChartData(dateFromAndTo, curDeepTime, curLightTime, curSoberTime, avgDeepTime, avgLightTime, avgSoberTime);
                }
            }
        });

    }

    private Bitmap createHeadIcon(String portrait) {
        Bitmap headIcon = null;
        try {
            if (portrait != null) {
                headIcon = Glide.with(App.getContext())
                        .asBitmap()
                        .load(portrait)
                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            }
        } catch (Exception e) {

        } finally {
            if (headIcon == null) {
                headIcon = BitmapFactory.decodeResource(App.getContext().getResources(), R.mipmap.img_test_picture);
            }
        }
        return headIcon;
    }

}
