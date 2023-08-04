package com.apex.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.SleepCheckCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleAutoCheckSleep;
import com.apex.sdk.R;
import com.apex.sdk.dialog.NotDisturbDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SleepMonitoringActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private String tempRest;
    @BindView(R.id.cycle)
    TextView restText;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.start_time)
    TextView startText;
    @BindView(R.id.end_time)
    TextView endText;
    private NotDisturbDialog startDialog;
    private NotDisturbDialog endDialog;
    private EABleAutoCheckSleep eaBleAutoCheckSleep;
    private int tempStartHour, tempStartMinute, tempEndHour, tempEndMinute;
    private final int CYCLE_CODE = 0xFF;
    private int cycleTime;
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
                eaBleAutoCheckSleep = (EABleAutoCheckSleep) msg.obj;
                int cycle = eaBleAutoCheckSleep.getWeek_cycle_bit();
                String remindTime = "";
                if (cycle == 0x7F) {//每天
                    remindTime += (getString(R.string.every_day));
                } else {
                    String timeString = Integer.toBinaryString(cycle & 0xFF);
                    if (timeString.length() < 8) {
                        int size = 8 - timeString.length();
                        for (int i = 0; i < size; i++) {
                            timeString = ("0" + timeString);
                        }
                    }
                    String sundayTag = timeString.substring(7, timeString.length());
                    String mondayTag = timeString.substring(6, 7);
                    String tuesdayTag = timeString.substring(5, 6);
                    String wednesdayTag = timeString.substring(4, 5);
                    String thursdayTag = timeString.substring(3, 4);
                    String fridayTag = timeString.substring(2, 3);
                    String saturday = timeString.substring(1, 2);
                    if (sundayTag.equalsIgnoreCase("1")) {
                        remindTime += (getString(R.string.sun) + ",");
                    }
                    if (mondayTag.equalsIgnoreCase("1")) {
                        remindTime += (getString(R.string.mon) + ",");
                    }
                    if (tuesdayTag.equalsIgnoreCase("1")) {
                        remindTime += (getString(R.string.tue) + ",");
                    }
                    if (wednesdayTag.equalsIgnoreCase("1")) {
                        remindTime += (getString(R.string.wed) + ",");
                    }
                    if (thursdayTag.equalsIgnoreCase("1")) {
                        remindTime += (getString(R.string.thur) + ",");
                    }
                    if (fridayTag.equalsIgnoreCase("1")) {
                        remindTime += (getString(R.string.fri) + ",");
                    }
                    if (saturday.equalsIgnoreCase("1")) {
                        remindTime += (getString(R.string.sat) + ",");
                    } else {

                    }
                    if (!TextUtils.isEmpty(remindTime)) {
                        remindTime = (remindTime.substring(0, remindTime.length() - 1) + " ");
                    }
                }
                restText.setText(remindTime);
                startText.setText(eaBleAutoCheckSleep.getBegin_hour() + ":" + eaBleAutoCheckSleep.getBegin_minute());
                endText.setText(eaBleAutoCheckSleep.getEnd_hour() + ":" + eaBleAutoCheckSleep.getEnd_minute());
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SleepMonitoringActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                restText.setText(tempRest);
            } else if (msg.what == 43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SleepMonitoringActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x44) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                startText.setText(tempStartHour + ":" + tempStartMinute);
            } else if (msg.what == 0x45) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                endText.setText(tempEndHour + ":" + tempEndMinute);
            } else if (msg.what == 0x46) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                restText.setText(tempRest);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_monitoring);
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
                waitingDialog = new WaitingDialog(SleepMonitoringActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.sleep_check, new SleepCheckCallback() {
                @Override
                public void sleepInfo(EABleAutoCheckSleep eaBleAutoCheckSleep) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleAutoCheckSleep;
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
        restText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SleepMonitoringActivity.this, CycleActivity.class), CYCLE_CODE);

            }
        });
        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (startDialog == null) {
                        startDialog = new NotDisturbDialog(SleepMonitoringActivity.this, getString(R.string.start_time));
                        startDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                            @Override
                            public void selectData(int hour, int minute) {
                                if (eaBleAutoCheckSleep == null) {
                                    eaBleAutoCheckSleep = new EABleAutoCheckSleep();
                                    eaBleAutoCheckSleep.setEnd_hour(10);
                                    eaBleAutoCheckSleep.setEnd_minute(10);
                                    eaBleAutoCheckSleep.setWeek_cycle_bit(1);
                                }
                                tempStartHour = hour;
                                tempStartMinute = minute;
                                eaBleAutoCheckSleep.setBegin_hour(hour);
                                eaBleAutoCheckSleep.setBegin_minute(minute);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SleepMonitoringActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setAutoSleepCheck(eaBleAutoCheckSleep, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x44);
                                        }
                                    }

                                    @Override
                                    public void mutualFail(int errorCode) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!startDialog.isShowing()) {
                        startDialog.show();
                    }
                }
            }
        });
        endText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (endDialog == null) {
                        endDialog = new NotDisturbDialog(SleepMonitoringActivity.this, getString(R.string.end_time));
                        endDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                            @Override
                            public void selectData(int hour, int minute) {
                                if (eaBleAutoCheckSleep == null) {
                                    eaBleAutoCheckSleep = new EABleAutoCheckSleep();
                                    eaBleAutoCheckSleep.setBegin_hour(8);
                                    eaBleAutoCheckSleep.setBegin_minute(8);
                                    eaBleAutoCheckSleep.setWeek_cycle_bit(1);
                                }
                                tempEndHour = hour;
                                tempEndMinute = minute;
                                eaBleAutoCheckSleep.setEnd_hour(hour);
                                eaBleAutoCheckSleep.setEnd_minute(minute);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SleepMonitoringActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setAutoSleepCheck(eaBleAutoCheckSleep, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x45);
                                        }
                                    }

                                    @Override
                                    public void mutualFail(int errorCode) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!endDialog.isShowing()) {
                        endDialog.show();
                    }
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
        if (startDialog != null) {
            startDialog.dismiss();
            startDialog.destroyDialog();
            startDialog = null;
        }
        if (endDialog != null) {
            endDialog.dismiss();
            endDialog.destroyDialog();
            endDialog = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED && requestCode == CYCLE_CODE) {
            cycleTime = data.getIntExtra("circle", 0);
            if (cycleTime == 0x7F) {
                tempRest = getString(R.string.every_day);

            } else {
                tempRest = data.getStringExtra("circleTime");
            }
            if (eaBleAutoCheckSleep == null) {
                eaBleAutoCheckSleep = new EABleAutoCheckSleep();
                eaBleAutoCheckSleep.setBegin_hour(8);
                eaBleAutoCheckSleep.setBegin_minute(8);
                eaBleAutoCheckSleep.setEnd_hour(10);
                eaBleAutoCheckSleep.setEnd_minute(10);
            }
            eaBleAutoCheckSleep.setWeek_cycle_bit(cycleTime);
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(SleepMonitoringActivity.this);
            }
            if (!waitingDialog.isShowing()) {
                waitingDialog.show();
            }
            EABleManager.getInstance().setAutoSleepCheck(eaBleAutoCheckSleep, new GeneralCallback() {
                @Override
                public void result(boolean success,int reason) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x46);
                    }
                }

                @Override
                public void mutualFail(int errorCode) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x43);
                    }
                }
            });
        }
    }
}
