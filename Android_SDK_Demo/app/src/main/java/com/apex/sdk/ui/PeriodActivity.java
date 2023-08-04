package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.PeriodCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABlePeriod;
import com.apex.bluetooth.model.EABlePhysiologyData;
import com.apex.bluetooth.utils.LogUtils;
import com.apex.sdk.R;
import com.apex.sdk.dialog.AgeDialog;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PeriodActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private AgeDialog ageDialog;
    private int tempRest;
    @BindView(R.id.start_time)
    TextView startText;
    @BindView(R.id.duration)
    TextView durationText;
    @BindView(R.id.cycle)
    TextView cycle;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    @BindView(R.id.tool)
    Toolbar toolbar;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x40) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                EABlePeriod eaBlePeriod = (EABlePeriod) msg.obj;
                List<EABlePeriod.EABlePeriodData> dataList = eaBlePeriod.getDataList();
                if (dataList == null || dataList.isEmpty()) {
                    return;
                }
                LogUtils.e(TAG, "获取到的经期:" + eaBlePeriod.toString());
                cycle.setText(dataList.size() + "");
                List<Integer> menstrualList = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    EABlePeriod.EABlePeriodData data = dataList.get(i);
                    if (data.getPeriodType() == EABlePeriod.PeriodType.menstrual) {
                        if (data.getDays() == 1) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(data.getTime_stamp() * 1000);
                            startText.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR));
                        }
                        menstrualList.add(data.getDays());

                    }
                }
                durationText.setText(Collections.max(menstrualList) + "");
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(PeriodActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Calendar calendar = Calendar.getInstance();
                startText.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR));
                durationText.setText(7 + "");
                cycle.setText(28 + "");
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(PeriodActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_period);
        unbinder = ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.mipmap.exit_page);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
        if (state == EABleConnectState.STATE_CONNECTED) {
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(PeriodActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.menstrual_cycle, new PeriodCallback() {
                @Override
                public void periodInfo(EABlePeriod eaBlePeriod) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBlePeriod;
                        mHandler.sendMessage(message);
                    }
                }


                @Override
                public void mutualFail(int errorCode) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x41);
                    }
                }
            });
        }
        //For example, the default current time is the start time of physiological period,
        // the duration of physiological period is 7 days, and the interval between physiological periods is 28 days
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(PeriodActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 5);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    long periodStartTime = calendar.getTimeInMillis();
                    int keepTime = 5;
                    int cycleTime = 30;
                    EABlePhysiologyData eaBlePhysiologyData = new EABlePhysiologyData();
                    eaBlePhysiologyData.setCycleTime(cycleTime);
                    eaBlePhysiologyData.setKeepTime(keepTime);
                    eaBlePhysiologyData.setStartTime(periodStartTime);
                    EABleManager.getInstance().setMenstrualCycle(eaBlePhysiologyData, false, new GeneralCallback() {
                        @Override
                        public void result(boolean success, int reason) {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x42);
                            }
                        }

                        @Override
                        public void mutualFail(int errorCode) {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x43);
                            }
                        }
                    });
                    /**
                     Log.e(TAG, "开始时间戳:" + periodStartTime);
                     calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);
                     long periodEndTime = calendar.getTimeInMillis();
                     calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 2);
                     long dangerousStartTime = calendar.getTimeInMillis();
                     calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 9);
                     long dangerousEndTime = calendar.getTimeInMillis();
                     EABlePeriod eaBlePeriod = new EABlePeriod();
                     List<EABlePeriod.EABlePeriodData> periodDataList = new ArrayList<>();
                     eaBlePeriod.setDataList(periodDataList);
                     for (int i = 0; i < 28; i++) {
                     EABlePeriod.EABlePeriodData eaBlePeriodData = new EABlePeriod.EABlePeriodData();
                     calendar.clear();
                     calendar.setTimeInMillis(periodStartTime);
                     calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + i);
                     long currentTime = calendar.getTimeInMillis();
                     if (currentTime < periodEndTime) {
                     eaBlePeriodData.setPeriodType(EABlePeriod.PeriodType.menstrual);
                     eaBlePeriodData.setDays(i + 1);
                     LogUtils.e(TAG, "经期第:" + (i + 1) + "天");
                     if (dangerousStartTime <= periodEndTime) {
                     dangerousStartTime = periodEndTime;
                     }
                     } else if (currentTime >= periodEndTime && currentTime < dangerousStartTime) {
                     eaBlePeriodData.setPeriodType(EABlePeriod.PeriodType.safety_period_1);
                     eaBlePeriodData.setDays((int) ((dangerousStartTime - currentTime) / 1000 / 3600 / 24));
                     LogUtils.e(TAG, "第一安全期,第:" + ((dangerousStartTime - currentTime) / 1000 / 3600 / 24) + "天");
                     } else if (currentTime >= dangerousStartTime && currentTime < dangerousEndTime) {
                     eaBlePeriodData.setPeriodType(EABlePeriod.PeriodType.ovulation);
                     eaBlePeriodData.setDays((int) ((currentTime - dangerousStartTime) / 1000 / 3600 / 24));
                     LogUtils.e(TAG, "危险期,第:" + ((currentTime - dangerousStartTime) / 1000 / 3600 / 24) + "天");
                     } else if (currentTime >= dangerousEndTime) {
                     eaBlePeriodData.setPeriodType(EABlePeriod.PeriodType.safety_period_2);
                     calendar.clear();
                     calendar.setTimeInMillis(periodStartTime);
                     calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 28);
                     eaBlePeriodData.setDays((int) ((calendar.getTimeInMillis() - currentTime) / 24 / 3600 / 1000));
                     LogUtils.e(TAG, "第二安全期,第:" + ((calendar.getTimeInMillis() - currentTime) / 24 / 3600 / 1000) + "天");
                     }
                     eaBlePeriodData.setTime_stamp(currentTime / 1000);
                     periodDataList.add(eaBlePeriodData);

                     }
                     EABleManager.getInstance().setMenstrualCycle(eaBlePeriod, new GeneralCallback() {
                    @Override public void result(boolean success) {
                    if (mHandler != null) {
                    mHandler.sendEmptyMessage(0x42);
                    }
                    }

                    @Override public void mutualFail(int errorCode) {
                    if (mHandler != null) {
                    mHandler.sendEmptyMessage(0x43);
                    }
                    }
                    });
                     */
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        if (ageDialog != null) {
            ageDialog.dismiss();
            ageDialog.destroyDialog();
            ageDialog = null;
        }
        super.onDestroy();
    }
}
