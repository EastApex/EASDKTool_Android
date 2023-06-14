package com.apex.sdk.db;


import com.apex.sdk.DemoApp;
import com.apex.sdk.db.daily.DailyData;
import com.apex.sdk.db.freq.StepFreqData;
import com.apex.sdk.db.gps.GpsData;
import com.apex.sdk.db.hr.HeartData;
import com.apex.sdk.db.multi.MultiData;
import com.apex.sdk.db.oxygen.BloodData;
import com.apex.sdk.db.pace.PaceData;
import com.apex.sdk.db.pressure.PressData;
import com.apex.sdk.db.resting.RestingRateData;
import com.apex.sdk.db.sleep.SleepData;
import com.greendao.gen.BloodDataDao;
import com.greendao.gen.DailyDataDao;
import com.greendao.gen.GpsDataDao;
import com.greendao.gen.HeartDataDao;
import com.greendao.gen.MultiDataDao;
import com.greendao.gen.PaceDataDao;
import com.greendao.gen.PressDataDao;
import com.greendao.gen.RestingRateDataDao;
import com.greendao.gen.SleepDataDao;
import com.greendao.gen.StepFreqDataDao;

import java.util.List;

public class GreenDaoManager {
    private static final String TAG = GreenDaoManager.class.getSimpleName();

    private volatile static GreenDaoManager manager = new GreenDaoManager();
    private DailyDataDao dailyDataDao;
    private StepFreqDataDao stepFreqDataDao;
    private GpsDataDao gpsDataDao;
    private HeartDataDao heartDataDao;
    private BloodDataDao bloodDataDao;
    private PaceDataDao paceDataDao;
    private PressDataDao pressDataDao;
    private SleepDataDao sleepDataDao;
    private MultiDataDao multiDataDao;
    private RestingRateDataDao restingRateDataDao;


    public static GreenDaoManager getInstance() {
        return manager;
    }

    private GreenDaoManager() {
        dailyDataDao = DemoApp.mSession.getDailyDataDao();
        stepFreqDataDao = DemoApp.mSession.getStepFreqDataDao();
        gpsDataDao = DemoApp.mSession.getGpsDataDao();
        heartDataDao = DemoApp.mSession.getHeartDataDao();
        bloodDataDao = DemoApp.mSession.getBloodDataDao();
        paceDataDao = DemoApp.mSession.getPaceDataDao();
        pressDataDao = DemoApp.mSession.getPressDataDao();
        sleepDataDao = DemoApp.mSession.getSleepDataDao();
        multiDataDao = DemoApp.mSession.getMultiDataDao();
        restingRateDataDao = DemoApp.mSession.getRestingRateDataDao();


    }

    public void insertBatchSportData(List<DailyData> cacheList) {
        if (cacheList == null || cacheList.isEmpty()) {
            return;
        }
        if (dailyDataDao == null) {
            dailyDataDao = DemoApp.mSession.getDailyDataDao();
        }
        dailyDataDao.insertOrReplaceInTx(cacheList);

    }

    public void insertBatchSleepData(List<SleepData> cacheList) {
        if (cacheList == null || cacheList.isEmpty()) {
            return;
        }
        if (sleepDataDao == null) {
            sleepDataDao = DemoApp.mSession.getSleepDataDao();
        }
        sleepDataDao.insertOrReplaceInTx(cacheList);

    }

    public void insertBatchHeartData(List<HeartData> cacheList) {
        if (cacheList == null || cacheList.isEmpty()) {
            return;
        }
        if (heartDataDao == null) {
            heartDataDao = DemoApp.mSession.getHeartDataDao();
        }
        heartDataDao.insertOrReplaceInTx(cacheList);
    }

    public void insertBatchRestingData(List<RestingRateData> cacheList) {
        if (cacheList == null || cacheList.isEmpty()) {
            return;
        }
        if (restingRateDataDao == null) {
            restingRateDataDao = DemoApp.mSession.getRestingRateDataDao();
        }
        restingRateDataDao.insertOrReplaceInTx(cacheList);
    }

    public void insertBatchOxygenData(List<BloodData> cacheList) {
        if (cacheList == null || cacheList.isEmpty()) {
            return;
        }
        if (bloodDataDao == null) {
            bloodDataDao = DemoApp.mSession.getBloodDataDao();
        }
        bloodDataDao.insertOrReplaceInTx(cacheList);
    }

    public void insertBatchPressureData(List<PressData> cacheList) {
        if (cacheList == null || cacheList.isEmpty()) {
            return;
        }
        if (pressDataDao == null) {
            pressDataDao = DemoApp.mSession.getPressDataDao();
        }
        pressDataDao.insertOrReplaceInTx(cacheList);
    }

    public void insertBatchGpsData(List<GpsData> cacheList) {
        if (cacheList == null || cacheList.isEmpty()) {
            return;
        }
        if (gpsDataDao == null) {
            gpsDataDao = DemoApp.mSession.getGpsDataDao();
        }
        gpsDataDao.insertOrReplaceInTx(cacheList);
    }

    public void insertBatchMultiData(List<MultiData> cacheList) {
        if (cacheList == null || cacheList.isEmpty()) {
            return;
        }
        if (multiDataDao == null) {
            multiDataDao = DemoApp.mSession.getMultiDataDao();
        }
        multiDataDao.insertOrReplaceInTx(cacheList);
    }

    public void insertBatchFreqData(List<StepFreqData> cacheList) {
        if (cacheList == null || cacheList.isEmpty()) {
            return;
        }
        if (stepFreqDataDao == null) {
            stepFreqDataDao = DemoApp.mSession.getStepFreqDataDao();
        }
        stepFreqDataDao.insertOrReplaceInTx(cacheList);
    }

    public void insertBatchPaceData(List<PaceData> cacheList) {
        if (cacheList == null || cacheList.isEmpty()) {
            return;
        }
        if (paceDataDao == null) {
            paceDataDao = DemoApp.mSession.getPaceDataDao();
        }
        paceDataDao.insertOrReplaceInTx(cacheList);
    }

    public DailyData queryLastSport() {
        if (dailyDataDao == null) {
            dailyDataDao = DemoApp.mSession.getDailyDataDao();
        }
        return dailyDataDao.queryBuilder().orderAsc(DailyDataDao.Properties.CurrentTime).limit(1).unique();
    }

    public List<DailyData> batchQuerySport(long startTime, long endTime) {
        if (dailyDataDao == null) {
            dailyDataDao = DemoApp.mSession.getDailyDataDao();
        }
        return dailyDataDao.queryBuilder().where(DailyDataDao.Properties.CurrentTime.ge(startTime), DailyDataDao.Properties.CurrentTime.le(endTime)).orderAsc(DailyDataDao.Properties.CurrentTime).list();
    }


    public List<SleepData> batchQuerySleep(long startTime, long endTime) {
        if (sleepDataDao == null) {
            sleepDataDao = DemoApp.mSession.getSleepDataDao();
        }
        return sleepDataDao.queryBuilder().where(SleepDataDao.Properties.CurrentTime.ge(startTime), SleepDataDao.Properties.CurrentTime.le(endTime)).orderAsc(SleepDataDao.Properties.CurrentTime).list();
    }

    public HeartData queryLastHeart() {
        if (heartDataDao == null) {
            heartDataDao = DemoApp.mSession.getHeartDataDao();
        }
        return heartDataDao.queryBuilder().orderDesc(HeartDataDao.Properties.CurrentTime).limit(1).unique();
    }

    public RestingRateData queryLastRestingRate() {
        if (restingRateDataDao == null) {
            restingRateDataDao = DemoApp.mSession.getRestingRateDataDao();
        }
        return restingRateDataDao.queryBuilder().orderDesc(RestingRateDataDao.Properties.CurrentTime).limit(1).unique();
    }

    public BloodData queryLastBlood() {
        if (bloodDataDao == null) {
            bloodDataDao = DemoApp.mSession.getBloodDataDao();
        }
        return bloodDataDao.queryBuilder().orderDesc(BloodDataDao.Properties.CurrentTime).limit(1).unique();
    }

    public PressData questLastPressure() {
        if (pressDataDao == null) {
            pressDataDao = DemoApp.mSession.getPressDataDao();
        }
        return pressDataDao.queryBuilder().orderDesc(PressDataDao.Properties.CurrentTime).limit(1).unique();
    }

    public List<GpsData> batchQueryGps(long startTime, long endTime) {
        if (gpsDataDao == null) {
            gpsDataDao = DemoApp.mSession.getGpsDataDao();
        }
        return gpsDataDao.queryBuilder().where(GpsDataDao.Properties.CurrentTime.ge(startTime), GpsDataDao.Properties.CurrentTime.le(endTime)).orderAsc(GpsDataDao.Properties.CurrentTime).list();
    }

    public MultiData queryLastMulti() {
        if (multiDataDao == null) {
            multiDataDao = DemoApp.mSession.getMultiDataDao();
        }
        return multiDataDao.queryBuilder().orderDesc(MultiDataDao.Properties.Begin_time_stamp).limit(1).unique();
    }


    public List<StepFreqData> batchQueryFreq(long startTime, long endTime) {
        if (stepFreqDataDao == null) {
            stepFreqDataDao = DemoApp.mSession.getStepFreqDataDao();
        }
        return stepFreqDataDao.queryBuilder().where(StepFreqDataDao.Properties.CurrentTime.ge(startTime), StepFreqDataDao.Properties.CurrentTime.le(endTime)).orderAsc(StepFreqDataDao.Properties.CurrentTime).list();
    }


    public List<PaceData> batchQueryPace(long startTime, long endTime) {
        if (paceDataDao == null) {
            paceDataDao = DemoApp.mSession.getPaceDataDao();
        }
        return paceDataDao.queryBuilder().where(PaceDataDao.Properties.CurrentTime.ge(startTime), PaceDataDao.Properties.CurrentTime.le(endTime)).orderAsc(PaceDataDao.Properties.CurrentTime).list();
    }

}
