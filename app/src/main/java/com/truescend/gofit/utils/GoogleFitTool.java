package com.truescend.gofit.utils;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sn.utils.SNLog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * GoogleFit工具类
 * Author Created by 泽鑫 on 2018/5/31.
 */
public class GoogleFitTool {
    private static final String TAG = "GoogleFitTool";
    public static final int GOOGLE_FIT_PERMISSION_REQUEST_CODE = 1001;

    //检查权限
    public static boolean hasPermission(Context context) {
        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .build();
        GoogleSignInAccount gsa = GoogleSignIn.getAccountForExtension(context, fitnessOptions);
        return GoogleSignIn.hasPermissions(gsa, fitnessOptions);
    }

    //请求权限
    public static void requestPermission(Context context) {
        GoogleSignInOptionsExtension fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();
        GoogleSignInAccount gsa = GoogleSignIn.getAccountForExtension(context, fitnessOptions);
        GoogleSignIn.requestPermissions((Activity) context,
                GOOGLE_FIT_PERMISSION_REQUEST_CODE,
                gsa,
                fitnessOptions);
    }

    //读取一年的数据
    public static Task<DataReadResponse> readStepsFromGoogle(Context context){
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(context);
        if (gsa == null) {
            return null;
        }
        return Fitness.getHistoryClient(context, gsa).readData(readRequest);

    }


    //封装数据用的，类似List，将数据点（DataPoint）add进去
    public static DataSet getStepsDataSet(Context context){
        DataSource stepsSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setAppPackageName(context)
                .setStreamName("Steps upload")
                .setType(DataSource.TYPE_RAW)
                .build();

        return DataSet.create(stepsSource);
    }

    //插入数据
    public static Task<Void> insertStepsToGoogle(Context context,DataSet dataSet) {
        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(context);
        return Fitness.getHistoryClient(context, gsa).insertData(dataSet).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    SNLog.i("GoogleFit: 插入数据成功");
                }else {
                    SNLog.i("GoogleFit: 插入数据失败， 原因是：\n" + task.getException());
                }
            }
        });
    }

    //更新数据
    public static Task<Void> updateStepsToGoogle(Context context, DataSet dataSet) {
        List<DataPoint> dataPoints = dataSet.getDataPoints();
        long startTime = dataPoints.get(0).getStartTime(TimeUnit.MILLISECONDS);
        long endTime = dataPoints.get(dataPoints.size() - 1).getEndTime(TimeUnit.MILLISECONDS);
        DataUpdateRequest request = new DataUpdateRequest.Builder()
                .setDataSet(dataSet)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(context);
        return Fitness.getHistoryClient(context, gsa).updateData(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    SNLog.i("GoogleFit: 更新数据成功");
                }else {
                    SNLog.i("GoogleFit: 更新数据失败， 原因是：\n" + task.getException());
                }
            }
        });
    }

    //删除数据
    public static Task<Void> deleteStepsFromGoogle(Context context, long startTime, long endTime) {
        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();
        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(context);
        if (gsa == null) {
            return null;
        }
        return Fitness.getHistoryClient(context, gsa).deleteData(request);
    }

    //取消关联GoogleFit
    public static Task<Void> disconnectedToGoogleFit(Context context) {
        GoogleSignInOptionsExtension fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder().addExtension(fitnessOptions).build();
        GoogleSignInClient client = GoogleSignIn.getClient(context, signInOptions);
        return client.revokeAccess();
    }

}
