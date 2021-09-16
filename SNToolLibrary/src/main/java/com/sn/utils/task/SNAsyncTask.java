package com.sn.utils.task;

/**
 * 作者:东芝(2017/12/1).
 * 功能:异步任务  不堵塞UI
 */

public class SNAsyncTask {

    public static void execute(SNVTaskCallBack vtask) {
        if (vtask == null) return;
        vtask.exec();
    }

}
