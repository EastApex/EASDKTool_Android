package com.apex.sdk;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.multidex.MultiDex;

import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.utils.LogData2File;
import com.apex.bluetooth.utils.LogUtils;
import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;

import org.greenrobot.greendao.identityscope.IdentityScopeType;

public class DemoApp extends Application {
    public static DaoSession mSession;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.setShowLog(true);
        // initGreenDaoDb();
        //  LogData2File.getInstance().init(this);
        //  LogData2File.getInstance().setSaveLog(true);
        //  LogData2File.getInstance().setSaveOriginalData(true);
    }

    private void initGreenDaoDb() {
        //  EABleManager.getInstance().initDB(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
