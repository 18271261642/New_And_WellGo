package com.truescend.gofit.utils;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.storage.UserStorage;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.sport.SportBean;
import com.sn.blesdk.db.data.sport.SportDao;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author Created by 泽鑫 on 2018/6/1.
 */
public class UploadGoogleFitHelper {
    private Context context;

    public UploadGoogleFitHelper(Context context) {
        this.context = context;
    }

    public void startAutoSync() {
        int user_id = -1;
        try {
            UserBean user  = AppUserUtil.getUser();
            if (user != null) {
                  user_id = user.getUser_id();
            }
        } catch (Exception e) {
            user_id = UserStorage.getUserId();
        }
        final int finalUser_id = user_id;
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                List<SportBean> beans = SportDao.get(SportDao.class).queryForToday(finalUser_id);
                if (!IF.isEmpty(beans)) {
                    uploadStepsToGoogleFit(beans);
                }
            }
        });
    }

    private void uploadStepsToGoogleFit(List<SportBean> list) {
        final DataSet dataSet = GoogleFitTool.getStepsDataSet(context);
        //n天总数据
        for (SportBean sportBean : list) {
            //一天的详细数据
            List<SportBean.StepBean> steps = sportBean.getSteps();
            for (SportBean.StepBean step : steps) {
                long startTime = 0;
                try {
                    startTime = DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM_SS, step.getDateTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long endTime = startTime + 29 * 60 * 1000L;
                DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                dataPoint.getValue(Field.FIELD_STEPS).setInt(step.getValue());
                dataSet.add(dataPoint);
            }
        }

        GoogleFitTool.insertStepsToGoogle(context, dataSet)
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if (!task.isSuccessful()) {
                            return GoogleFitTool.updateStepsToGoogle(context, dataSet);
                        }else {
                            return null;
                        }
                    }
                });

    }

}
