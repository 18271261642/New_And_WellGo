package com.truescend.gofit.pagers.user;

import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.sport.SportDao;
import com.sn.utils.DateUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.CalendarUtil;
import com.truescend.gofit.utils.ResUtil;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

/**
 * 作者:东芝(2018/2/24).
 * 功能:用户运动数据 展示
 */

public class UserPresenterImpl extends BasePresenter<IUserContract.IView> implements IUserContract.IPresenter {
    private IUserContract.IView view;

    public UserPresenterImpl(IUserContract.IView view) {
        this.view = view;
    }



    @Override
    public void requestUserBestStatistical() {
        SNAsyncTask.execute(new SNVTaskCallBack() {

            private String NA;

            @Override
            public void prepare() {
                NA = ResUtil.getString(R.string.content_no_data);
            }

            @Override
            public void run() throws Throwable {
                SportDao sportDao = SportDao.get(SportDao.class);
                Map<String, Long> mBestDays = sportDao.queryBestDay(AppUserUtil.getUser().getUser_id());
                if (mBestDays.isEmpty()) {
                    publishToMainThread(1,NA,"0");
                } else {
                    Iterator<Map.Entry<String, Long>> iterator = mBestDays.entrySet().iterator();
                    if (iterator.hasNext()) {
                        Map.Entry<String, Long> next = iterator.next();
                        String date = next.getKey();
                        long stepTotal = next.getValue();

                        String bestDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, date);
                        publishToMainThread(1,bestDate,String.valueOf(stepTotal));
                    }
                }
                Map<String[], Long> mBestContinueDay = sportDao.queryBestContinueDay(AppUserUtil.getUser().getUser_id());
                if (mBestContinueDay.isEmpty()) {
                    publishToMainThread(2,NA,"0");
                } else {
                    Iterator<Map.Entry<String[], Long>> iterator = mBestContinueDay.entrySet().iterator();
                    if (iterator.hasNext()) {
                        Map.Entry<String[], Long> next = iterator.next();
                        String dates[] = next.getKey();
                        long stepTotal = next.getValue();

                        String startDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.MMDD, dates[0]);
                        String endDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.MMDD, dates[1]);
                        publishToMainThread(2, String.format("%s~%s", startDate, endDate),String.valueOf(stepTotal));
                    }
                }
                Map<String[], Long> mBestWeeks = sportDao.queryBestWeek(AppUserUtil.getUser().getUser_id());
                if (mBestWeeks.isEmpty()) {
                    publishToMainThread(3,NA,"0");
                } else {
                    Iterator<Map.Entry<String[], Long>> iterator = mBestWeeks.entrySet().iterator();
                    if (iterator.hasNext()) {
                        Map.Entry<String[], Long> next = iterator.next();
                        String dates[] = next.getKey();
                        long stepTotal = next.getValue();

                        Calendar calendar = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, dates[0]);

                        String startDate = DateUtil.convertCalendarToString(CalendarUtil.MMDD, DateUtil.getWeekFirstDate(calendar));
                        String endDate = DateUtil.convertCalendarToString(CalendarUtil.MMDD, DateUtil.getWeekLastDate(calendar));
                        publishToMainThread(3, String.format("%s~%s", startDate, endDate), String.valueOf(stepTotal));

                    }
                }
                Map<String[], Long> mBestMonths = sportDao.queryBestMonth(AppUserUtil.getUser().getUser_id());
                if (mBestMonths.isEmpty()) {
                    publishToMainThread(4,NA,"0");
                } else {
                    Iterator<Map.Entry<String[], Long>> iterator = mBestMonths.entrySet().iterator();
                    if (iterator.hasNext()) {
                        Map.Entry<String[], Long> next = iterator.next();
                        String dates[] = next.getKey();
                        long stepTotal = next.getValue();

                        String endDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMM, dates[1]);
                        publishToMainThread(4, endDate,String.valueOf(stepTotal));
                    }
                }

            }

            @Override
            public void error(Throwable e) {
                //最佳记录
                view.onUpdateUserBestDays(NA, NA);
                //连续达标
                view.onUpdateUserBestContinuesDays(NA, NA);
                //最佳星期
                view.onUpdateUserBestWeeks(NA, NA);
                //最佳月份
                view.onUpdateUserBestMonths(NA, NA);
            }

            @Override
            public void main(int what, Object... obj) {
                Object arg1 = obj[0];
                Object arg2 = obj[1];
                switch (what) {
                     case 1://最佳记录
                        view.onUpdateUserBestDays(((String) arg1), ((String) arg2));
                         break;
                     case 2://连续达标
                         view.onUpdateUserBestContinuesDays(((String) arg1), ((String) arg2));
                         break;
                     case 3://最佳星期
                         view.onUpdateUserBestWeeks(((String) arg1), ((String) arg2));
                         break;
                     case 4://最佳月份
                         view.onUpdateUserBestMonths(((String) arg1), ((String) arg2));
                         break;
                 }
            }
        });
    }
//
//    @Override
//    public void requestUserData() {
//
//    }
}
