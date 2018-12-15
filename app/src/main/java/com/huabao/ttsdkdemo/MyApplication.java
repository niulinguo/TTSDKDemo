package com.huabao.ttsdkdemo;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.content.Context;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.pgyersdk.crash.PgyCrashManager;
import com.tencent.bugly.crashreport.CrashReport;

import net.useiov.nepenthes_sdk.api.TicManager;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Niles
 * Date 2018/3/23
 * Email niulinguo@163.com
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
        Utils.init(this);

        PgyCrashManager.register(this);

        final CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setUploadProcess(true);
        CrashReport.initCrashReport(getApplicationContext(), "b58f2dadf4", true, strategy);

        if (AppUtils.getAppPackageName().equals(getCurrentProcessName())) {
            // init SDK on main process
            TicManager.init(this, new Notification());
        }
    }

    /**
     * get current process name
     */
    private String getCurrentProcessName() {
        final int pid = android.os.Process.myPid();
        final ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            final List<ActivityManager.RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
            if (processes != null) {
                for (ActivityManager.RunningAppProcessInfo process : processes) {
                    if (process.pid == pid) {
                        return process.processName;
                    }
                }
            }
        }
        return null;
    }
}
