package com.apex.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.EditAttentionCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.CommonAction;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.model.EABleRemindRespond;
import com.apex.bluetooth.model.EABleReminder;
import com.apex.sdk.R;
import com.apex.sdk.dialog.MonthDialog;
import com.apex.sdk.dialog.NotDisturbDialog;
import com.apex.sdk.dialog.RemindDialog;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditAlarmActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.alarm_switch)
    Switch aSwitch;
    @BindView(R.id.cycle_time)
    TextView cycleText;
    @BindView(R.id.alarm_time)
    TextView timeText;
    @BindView(R.id.sleep_time)
    TextView sleepText;
    @BindView(R.id.reminder_method)
    TextView modeText;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    private final int CYCLE_CODE = 0xFF;
    private NotDisturbDialog timeDialog;
    private MonthDialog sleepDialog;
    private RemindDialog remindDialog;
    private String tempMode;
    private int sleepTime, alarmTimeHour, alarmTimeMinute, cycleTime;
    private int year;
    private int month;
    private int day;
    private int id;
    private int firstSwitch;
    private int secondSwitch;
    private String customContent;
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
                finish();
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(EditAlarmActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        unbinder = ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.mipmap.exit_page);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        id = getIntent().getIntExtra("alarmId", -1);
        int method = getIntent().getIntExtra("alarmMothed", 0);
        cycleTime = getIntent().getIntExtra("cycleTime", -1);
        customContent = getIntent().getStringExtra("customContent");
        year = getIntent().getIntExtra("year", -1);
        month = getIntent().getIntExtra("month", -1);
        day = getIntent().getIntExtra("day", -1);
        alarmTimeHour = getIntent().getIntExtra("hour", -1);
        alarmTimeMinute = getIntent().getIntExtra("minute", -1);
        firstSwitch = getIntent().getIntExtra("alarmSwitch", -1);
        secondSwitch = getIntent().getIntExtra("secondSwitch", -1);
        sleepTime = getIntent().getIntExtra("lazyTime", -1);
        int type = getIntent().getIntExtra("alarmType", -1);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        timeText.setText(alarmTimeHour + ":" + alarmTimeMinute);
        sleepText.setText(sleepTime + "");
        if (method == 0) {
            tempMode = getString(R.string.common_action_no_action);
        } else if (method == 1) {
            tempMode = getString(R.string.common_action_one_long_vibration);
        } else if (method == 2) {
            tempMode = getString(R.string.common_action_one_short_vibration);
        } else if (method == 3) {
            tempMode = getString(R.string.common_action_two_long_vibration);
        } else if (method == 4) {
            tempMode = getString(R.string.common_action_two_short_vibration);
        } else if (method == 5) {
            tempMode = getString(R.string.common_action_long_vibration);
        } else if (method == 6) {
            tempMode = getString(R.string.common_action_long_short_vibration);
        } else if (method == 7) {
            tempMode = getString(R.string.common_action_one_ring);
        } else if (method == 8) {
            tempMode = getString(R.string.common_action_two_ring);
        } else if (method == 9) {
            tempMode = getString(R.string.common_action_ring);
        } else if (method == 10) {
            tempMode = getString(R.string.common_action_one_vibration_ring);
        } else if (method == 11) {
            tempMode = getString(R.string.common_action_vibration_ring);
        }
        modeText.setText(tempMode);
        aSwitch.setChecked(firstSwitch == 0 ? false : true);
        cycleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(EditAlarmActivity.this, CycleActivity.class), CYCLE_CODE);
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firstSwitch = 1;
                } else {
                    firstSwitch = 0;
                }
            }
        });
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeDialog == null) {
                    timeDialog = new NotDisturbDialog(EditAlarmActivity.this, getString(R.string.alarm_clock));
                    timeDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                        @Override
                        public void selectData(int hour, int minute) {
                            timeText.setText(hour + ":" + minute);
                            alarmTimeHour = hour;
                            alarmTimeMinute = minute;
                        }
                    });
                }
                if (!timeDialog.isShowing()) {
                    timeDialog.show();
                }
            }
        });
        sleepText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sleepDialog == null) {
                    sleepDialog = new MonthDialog(EditAlarmActivity.this, getString(R.string.SnoozeTime));
                    sleepDialog.setSelectListener(new MonthDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            sleepText.setText(sex + "");
                            sleepTime = sex;
                        }
                    });
                }
                if (!sleepDialog.isShowing()) {
                    sleepDialog.show();
                }

            }
        });
        modeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remindDialog == null) {
                    remindDialog = new RemindDialog(EditAlarmActivity.this);
                    remindDialog.setSelectListener(new RemindDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            tempMode = sex;
                            modeText.setText(sex);
                        }
                    });
                }
                if (!remindDialog.isShowing()) {
                    remindDialog.show();
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(EditAlarmActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleReminder eaBleReminder = new EABleReminder();
                    eaBleReminder.setId(id);
                    eaBleReminder.setE_ops(EABleReminder.ReminderOps.edit);
                    EABleReminder.EABleReminderItem eaBleReminderItem = new EABleReminder.EABleReminderItem();
                    eaBleReminderItem.setSw(firstSwitch);
                    eaBleReminderItem.setYear(year);
                    eaBleReminderItem.setMonth(month);
                    eaBleReminderItem.setDay(day);
                    eaBleReminderItem.setWeek_cycle_bit(cycleTime);
                    eaBleReminderItem.setE_type(EABleReminder.ReminderType.alarm);
                    eaBleReminderItem.setSec_sw(secondSwitch);
                    if (tempMode.equalsIgnoreCase(getString(R.string.common_action_no_action))) {
                        eaBleReminderItem.setE_action(CommonAction.no_action);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_one_long_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.one_long_vibration);

                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_one_short_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.one_short_vibration);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_two_long_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.two_long_vibration);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_two_short_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.two_short_vibration);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_long_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.long_vibration);

                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_long_short_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.long_short_vibration);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_one_ring))) {
                        eaBleReminderItem.setE_action(CommonAction.one_ring);

                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_two_ring))) {
                        eaBleReminderItem.setE_action(CommonAction.two_ring);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_ring))) {
                        eaBleReminderItem.setE_action(CommonAction.ring);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_one_vibration_ring))) {
                        eaBleReminderItem.setE_action(CommonAction.one_vibration_ring);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_vibration_ring))) {
                        eaBleReminderItem.setE_action(CommonAction.vibration_ring);
                    }
                    eaBleReminderItem.setHour(alarmTimeHour);
                    eaBleReminderItem.setMinute(alarmTimeMinute);
                    eaBleReminderItem.setSleep_duration(sleepTime);
                    List<EABleReminder.EABleReminderItem> indexList = new ArrayList<>();
                    indexList.add(eaBleReminderItem);
                    eaBleReminder.setS_index(indexList);
                    EABleManager.getInstance().setReminderOrder(eaBleReminder, new EditAttentionCallback() {
                        @Override
                        public void editResult(EABleRemindRespond eaBleRemindRespond) {
                            if (eaBleRemindRespond.getRemindRespondOps() != EABleRemindRespond.RemindRespondResult.success) {
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x41);
                                }
                            } else {
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x40);
                                }
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
        if (remindDialog != null) {
            remindDialog.dismiss();
            remindDialog.destroyDialog();
            remindDialog = null;
        }
        if (timeDialog != null) {
            timeDialog.dismiss();
            timeDialog.destroyDialog();
            timeDialog = null;
        }
        if (sleepDialog != null) {
            sleepDialog.dismiss();
            sleepDialog.destroyDialog();
            sleepDialog = null;
        }
        super.onDestroy();
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
}
