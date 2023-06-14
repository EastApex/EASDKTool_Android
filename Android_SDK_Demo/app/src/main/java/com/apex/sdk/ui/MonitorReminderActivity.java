package com.apex.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.MonitorReminderCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleMonitorReminder;
import com.apex.bluetooth.model.QueryInfo;
import com.apex.sdk.R;
import com.apex.sdk.dialog.CupDialog;
import com.apex.sdk.dialog.MonthDialog;
import com.apex.sdk.dialog.NotDisturbDialog;
import com.apex.sdk.dialog.ReminderDialog;
import com.apex.sdk.dialog.StepLimitDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MonitorReminderActivity extends AppCompatActivity {
    private final int CYCLE_CODE = 0xFF;
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.alarm_switch)
    Switch aSwitch;
    @BindView(R.id.schedule_type)
    TextView typeView;
    @BindView(R.id.remind_time)
    TextView intervalView;
    @BindView(R.id.cycle)
    TextView cycleText;
    @BindView(R.id.reminder_method)
    TextView startTimeView;
    @BindView(R.id.schedule_name)
    TextView endTimeView;
    @BindView(R.id.water_cup)
    TextView cupText;
    @BindView(R.id.steps)
    TextView stepsView;
    @BindView(R.id.layout_steps)
    TableRow stepsTableRow;
    @BindView(R.id.layout_water)
    TableRow waterTableRow;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    private ReminderDialog reminderDialog;
    private CupDialog cupDialog;
    private NotDisturbDialog startDialog, endDialog;
    private StepLimitDialog stepLimitDialog;
    private MonthDialog sleepDialog;
    private int startHour, endHour, startMinute, endMinute, intervalTime, cycleTime, type, cup, steps;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x40) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                startHour = 8;
                startMinute = 0;
                endHour = 18;
                endMinute = 0;
                intervalTime = 30;
                cycleTime = 0;
                startTimeView.setText(startHour + ":" + startMinute);
                endTimeView.setText(endHour + ":" + endMinute);
                intervalView.setText(intervalTime + getString(R.string.minute));
                aSwitch.setChecked(false);
                type = 0;
                cup = 1;
                typeView.setText(getString(R.string.drinking));
                String remindTime = "";
                byte cTime = (byte) (cycleTime & 0xFF);
                if (cTime == 0x7F) {
                    remindTime = (getString(R.string.every_day) + ",");
                } else if (cTime == 0) {
                    remindTime = (getString(R.string.only_once) + ",");
                } else {
                    String timeString = Integer.toBinaryString(cTime);
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
                }
                if (!TextUtils.isEmpty(remindTime)) {
                    remindTime = (remindTime.substring(0, remindTime.length() - 1) + " ");
                }
                cycleText.setText(remindTime);
                waterTableRow.setVisibility(View.VISIBLE);
                cupText.setText(cup + "");

            } else if (msg.what == 0x41) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                EABleMonitorReminder eaBleMonitorReminder = (EABleMonitorReminder) msg.obj;
                if (eaBleMonitorReminder != null) {
                    Log.e(TAG, "有数据");
                    startHour = eaBleMonitorReminder.getBegin_hour();
                    startMinute = eaBleMonitorReminder.getBegin_minute();
                    endHour = eaBleMonitorReminder.getEnd_hour();
                    endMinute = eaBleMonitorReminder.getEnd_minute();
                    intervalTime = eaBleMonitorReminder.getInterval();
                    cycleTime = eaBleMonitorReminder.getWeek_cycle_bit();
                    startTimeView.setText(startHour + ":" + startMinute);
                    endTimeView.setText(endHour + ":" + endMinute);
                    steps = eaBleMonitorReminder.getStep_threshold();
                    cup = eaBleMonitorReminder.getCup();
                    intervalView.setText(intervalTime + getString(R.string.minute));
                    type = eaBleMonitorReminder.getEaBleMonitorType().getValue();
                    int on_off = eaBleMonitorReminder.getReminderSwitch();
                    if (on_off == 1) {
                        aSwitch.setChecked(true);
                    } else {
                        aSwitch.setChecked(false);
                    }
                    if (type == 0) {
                        typeView.setText(getString(R.string.drinking));
                        stepsTableRow.setVisibility(View.GONE);
                        waterTableRow.setVisibility(View.VISIBLE);
                        //  cup = eaBleMonitorReminder.getCup();
                        cupText.setText(cup + "");
                    } else if (type == 1) {
                        typeView.setText(getString(R.string.Hand_washing));
                        stepsTableRow.setVisibility(View.VISIBLE);
                        waterTableRow.setVisibility(View.GONE);
                        stepsView.setText(steps + "");

                    }
                    String remindTime = "";
                    byte cTime = (byte) (cycleTime & 0xFF);
                    if (cTime == 0x7F) {
                        remindTime = (getString(R.string.every_day) + ",");
                    } else if (cTime == 0) {
                        remindTime = (getString(R.string.only_once) + ",");
                    } else {
                        String timeString = Integer.toBinaryString(cTime);
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
                    }
                    if (!TextUtils.isEmpty(remindTime)) {
                        remindTime = (remindTime.substring(0, remindTime.length() - 1) + " ");
                    }
                    cycleText.setText(remindTime);
                }
            } else if (msg.what == 0x42) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                finish();
            } else if (msg.what == 0x43) {
                Toast.makeText(MonitorReminderActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_reminder);
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
        cycleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MonitorReminderActivity.this, CycleActivity.class), CYCLE_CODE);
            }
        });
        startTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDialog == null) {
                    startDialog = new NotDisturbDialog(MonitorReminderActivity.this, getString(R.string.start_time));
                    startDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                        @Override
                        public void selectData(int hour, int minute) {
                            startTimeView.setText(hour + ":" + minute);
                            startHour = hour;
                            startMinute = minute;
                        }
                    });
                }
                if (!startDialog.isShowing()) {
                    startDialog.show();
                }
            }
        });
        endTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (endDialog == null) {
                    endDialog = new NotDisturbDialog(MonitorReminderActivity.this, getString(R.string.end_time));
                    endDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                        @Override
                        public void selectData(int hour, int minute) {
                            endTimeView.setText(hour + ":" + minute);
                            endHour = hour;
                            endMinute = minute;
                        }
                    });
                }
                if (!endDialog.isShowing()) {
                    endDialog.show();
                }

            }
        });
        intervalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sleepDialog == null) {
                    sleepDialog = new MonthDialog(MonitorReminderActivity.this, getString(R.string.interval_time));
                    sleepDialog.setSelectListener(new MonthDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            intervalView.setText(sex + getString(R.string.minute));
                            intervalTime = sex;
                        }
                    });
                }
                if (!sleepDialog.isShowing()) {
                    sleepDialog.show();
                }
            }
        });
        typeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminderDialog == null) {
                    reminderDialog = new ReminderDialog(MonitorReminderActivity.this);
                    reminderDialog.setSelectListener(new ReminderDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            if (sex.equalsIgnoreCase(getString(R.string.drinking))) {
                                type = 0;
                                waterTableRow.setVisibility(View.VISIBLE);
                                stepsTableRow.setVisibility(View.GONE);
                            } else if (sex.equalsIgnoreCase(getString(R.string.Hand_washing))) {
                                type = 1;
                                waterTableRow.setVisibility(View.GONE);
                                stepsTableRow.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                if (!reminderDialog.isShowing()) {
                    reminderDialog.show();
                }
            }
        });
        EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
        if (state == EABleConnectState.STATE_CONNECTED) {
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(MonitorReminderActivity.this);
            }
            waitingDialog.show();
            QueryInfo queryInfo = new QueryInfo();
            queryInfo.setQueryWatchInfoType(QueryWatchInfoType.monitor_reminder);
            queryInfo.setDataType(1);
            EABleManager.getInstance().queryInfo(queryInfo, new MonitorReminderCallback() {
                @Override
                public void mutualFail(int errorCode) {
                    Log.e(TAG, "读取错误");
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x40);
                    }

                }

                @Override
                public void monitorReminder(EABleMonitorReminder reminder) {
                    if (mHandler != null) {
                        Message message = new Message();
                        message.what = 0x41;
                        message.obj = reminder;
                        mHandler.sendMessage(message);
                    }
                }
            });
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(MonitorReminderActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleMonitorReminder eaBleMonitorReminder = new EABleMonitorReminder();
                    eaBleMonitorReminder.setEaBleMonitorType(type == 0 ? EABleMonitorReminder.EABleMonitorType.drink : EABleMonitorReminder.EABleMonitorType.washHands);
                    eaBleMonitorReminder.setReminderSwitch(aSwitch.isChecked() ? 1 : 0);
                    eaBleMonitorReminder.setWeek_cycle_bit(cycleTime);
                    eaBleMonitorReminder.setInterval(intervalTime);
                    eaBleMonitorReminder.setBegin_hour(startHour);
                    eaBleMonitorReminder.setBegin_minute(startMinute);
                    eaBleMonitorReminder.setEnd_hour(endHour);
                    eaBleMonitorReminder.setEnd_minute(endMinute);
                    eaBleMonitorReminder.setCup(cup);
                    eaBleMonitorReminder.setStep_threshold(steps);
                    EABleManager.getInstance().addMonitorReminder(eaBleMonitorReminder, new GeneralCallback() {
                        @Override
                        public void result(boolean success) {
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
                }
            }
        });
        cupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cupDialog == null) {
                    cupDialog = new CupDialog(MonitorReminderActivity.this);
                    cupDialog.setSelectListener(new CupDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            cupText.setText(sex + getString(R.string.Cup));
                            cup = sex;
                        }
                    });
                }
                if (!cupDialog.isShowing()) {
                    cupDialog.show();
                }
            }
        });
        stepsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stepLimitDialog == null) {
                    stepLimitDialog = new StepLimitDialog(MonitorReminderActivity.this);
                    stepLimitDialog.setSelectListener(new StepLimitDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            stepsView.setText(sex + "");
                            steps = sex;
                        }
                    });
                }
                if (!stepLimitDialog.isShowing()) {
                    stepLimitDialog.show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED && requestCode == CYCLE_CODE) {
            cycleTime = data.getIntExtra("circle", 0);
            if (cycleTime == 0x7F) {
                cycleText.setText(getText(R.string.every_day));

            } else {
                cycleText.setText(data.getStringExtra("circleTime"));
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (waitingDialog != null) {
            waitingDialog = null;
        }
        if (reminderDialog != null) {
            reminderDialog.destroyDialog();
            reminderDialog = null;
        }
        if (startDialog != null) {
            startDialog.destroyDialog();
            startDialog = null;
        }
        if (endDialog != null) {
            endDialog.destroyDialog();
            endDialog = null;
        }
        if (sleepDialog != null) {
            sleepDialog.destroyDialog();
            sleepDialog = null;
        }
        if (cupDialog != null) {
            cupDialog.destroyDialog();
            cupDialog = null;
        }
        if (stepLimitDialog != null) {
            stepLimitDialog.destroyDialog();
            stepLimitDialog = null;
        }

    }
}
