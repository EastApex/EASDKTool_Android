package com.apex.sdk;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

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
      //  initGreenDaoDb();
      //  LogData2File.getInstance().init(this);
      //  LogData2File.getInstance().setSaveLog(true);
      //  LogData2File.getInstance().setSaveOriginalData(true);
    }

    private void initGreenDaoDb() {
      //  DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "sport.db");
      //  SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        // db.disableWriteAheadLogging();
      //  DaoMaster daoMaster = new DaoMaster(db);
      //  mSession = daoMaster.newSession(IdentityScopeType.None);
    }
}
