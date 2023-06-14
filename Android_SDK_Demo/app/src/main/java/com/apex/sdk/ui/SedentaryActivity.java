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
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.SedentaryCheckCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleSedentariness;
import com.apex.sdk.R;
import com.apex.sdk.dialog.MonthDialog;
import com.apex.sdk.dialog.NotDisturbDialog;
import com.apex.sdk.dialog.SwitchDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SedentaryActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;

    @BindView(R.id.interval_time)
    TextView intervalText;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.cycle)
    TextView cycleText;
    @BindView(R.id.start_time)
    TextView startText;
    @BindView(R.id.end_time)
    TextView endText;
    @BindView(R.id.steps)
    TextView stepText;
    @BindView(R.id.start_sit)
    TextView sitText;
    @BindView(R.id.break_switch)
    TextView breakText;
    @BindView(R.id.break_start_hour)
    TextView noonStartHourText;
    @BindView(R.id.break_start_minute)
    TextView noonStartMinuteText;
    @BindView(R.id.break_end_hour)
    TextView noonEndHourText;
    @BindView(R.id.break_end_minute)
    TextView noonEndMinuteText;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    private int tempInterval, tempStartHour, tempStartMinute, tempEndHour, tempEndMinute, tempSteps;
    private MonthDialog monthDialog, stepDialog, noonStartHourDialog, noonStartMinuteDialog, noonEndHourDialog, noonEndMinuteDialog;
    private EABleSedentariness eaBleSedentariness;
    private SwitchDialog sitDialog, breakDialog;
    private NotDisturbDialog startDialog, endDialog;
    private final int CYCLE_CODE = 0xFF;

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
                eaBleSedentariness = (EABleSedentariness) msg.obj;
                if (eaBleSedentariness.getInterval() > 0) {
                    intervalText.setText(eaBleSedentariness.getInterval() + "");
                } else {
                    intervalText.setText(getString(R.string.switch_state_close));
                }
                int cycle = eaBleSedentariness.getWeek_cycle_bit();
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
                cycleText.setText(remindTime);
                startText.setText(eaBleSedentariness.getBegin_hour() + ":" + eaBleSedentariness.getBegin_minute());
                endText.setText(eaBleSedentariness.getEnd_hour() + ":" + eaBleSedentariness.getEnd_minute());
                stepText.setText(eaBleSedentariness.getStep_threshold() + "");
                int sitSwitch = eaBleSedentariness.getSw();
                if (sitSwitch == 10) {
                    sitText.setText(getString(R.string.switch_state_close));
                }
                if (sitSwitch == 11) {
                    sitText.setText(getString(R.string.switch_state_on));
                }
                breakText.setText(eaBleSedentariness.getNoon_sw() == 0 ? getString(R.string.switch_state_close) : getString(R.string.switch_state_on));
                noonStartHourText.setText(eaBleSedentariness.getNoon_begin_hour() + "");
                noonStartMinuteText.setText(eaBleSedentariness.getNoon_begin_minute() + "");
                noonEndHourText.setText(eaBleSedentariness.getNoon_end_hour() + "");
                noonEndMinuteText.setText(eaBleSedentariness.getNoon_end_minute() + "");
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SedentaryActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }

            } else if (msg.what == 43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SedentaryActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sedentary);
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
                waitingDialog = new WaitingDialog(SedentaryActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.sit_check, new SedentaryCheckCallback() {
                @Override
                public void mutualFail(int errorCode) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x41);
                    }
                }

                @Override
                public void sedentaryInfo(EABleSedentariness eaBleSedentariness) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleSedentariness;
                        mHandler.sendMessage(message);
                    }
                }
            });
        }
        intervalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (monthDialog == null) {
                    monthDialog = new MonthDialog(SedentaryActivity.this, getString(R.string.interval_time));
                    monthDialog.setSelectListener(new MonthDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            tempInterval = sex;
                            intervalText.setText(tempInterval + "");
                        }
                    });
                }
                if (!monthDialog.isShowing()) {
                    monthDialog.show();
                }

            }
        });
        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (startDialog == null) {
                    startDialog = new NotDisturbDialog(SedentaryActivity.this, getString(R.string.start_time));
                    startDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                        @Override
                        public void selectData(int hour, int minute) {
                            tempStartHour = hour;
                            tempStartMinute = minute;
                            startText.setText(tempStartHour + ":" + tempStartMinute);
                        }
                    });
                }
                if (!startDialog.isShowing()) {
                    startDialog.show();
                }
            }
        });
        endText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (endDialog == null) {
                    endDialog = new NotDisturbDialog(SedentaryActivity.this, getString(R.string.end_time));
                    endDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                        @Override
                        public void selectData(int hour, int minute) {
                            tempEndHour = hour;
                            tempEndMinute = minute;
                            endText.setText(tempEndHour + ":" + tempEndMinute);
                        }
                    });
                }
                if (!endDialog.isShowing()) {
                    endDialog.show();
                }
            }
        });
        cycleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SedentaryActivity.this, CycleActivity.class), CYCLE_CODE);

            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cTime = cycleText.getText().toString();
                String sText = startText.getText().toString();
                String eText = endText.getText().toString();
                if (TextUtils.isEmpty(sText) || TextUtils.isEmpty(eText)) {
                    return;
                }
                EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
                if (state == EABleConnectState.STATE_CONNECTED) {
                    if (eaBleSedentariness == null) {
                        eaBleSedentariness = new EABleSedentariness();
                    }
                    String sitSwitch = sitText.getText().toString();
                    if (!TextUtils.isEmpty(sitSwitch)) {
                        if (sitSwitch.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                            eaBleSedentariness.setSw(11);
                        } else {
                            eaBleSedentariness.setSw(10);
                        }
                    }
                    String noonStartHour = noonStartHourText.getText().toString();
                    if (!TextUtils.isEmpty(noonStartHour)) {
                        eaBleSedentariness.setNoon_begin_hour(Integer.valueOf(noonStartHour));
                    }
                    String noonStartMinute = noonStartMinuteText.getText().toString();
                    if (!TextUtils.isEmpty(noonStartMinute)) {
                        eaBleSedentariness.setNoon_begin_minute(Integer.valueOf(noonStartMinute));
                    }
                    String noonEndHour = noonEndHourText.getText().toString();
                    if (!TextUtils.isEmpty(noonEndHour)) {
                        eaBleSedentariness.setNoon_end_hour(Integer.valueOf(noonEndHour));
                    }
                    String noonEndMinute = noonEndMinuteText.getText().toString();
                    if (!TextUtils.isEmpty(noonEndMinute)) {
                        eaBleSedentariness.setNoon_end_minute(Integer.valueOf(noonEndMinute));
                    }
                    eaBleSedentariness.setEnd_hour(tempEndHour);
                    eaBleSedentariness.setEnd_minute(tempEndMinute);
                    eaBleSedentariness.setBegin_minute(tempStartMinute);
                    eaBleSedentariness.setBegin_hour(tempStartHour);
                    eaBleSedentariness.setInterval(tempInterval);
                    String bText = breakText.getText().toString();
                    if (!TextUtils.isEmpty(bText)) {
                        if (bText.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                            eaBleSedentariness.setNoon_sw(1);
                        } else {
                            eaBleSedentariness.setNoon_sw(0);
                        }
                    }
                    eaBleSedentariness.setStep_threshold(tempSteps);
                    eaBleSedentariness.setWeek_cycle_bit(cycleTime);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(SedentaryActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().setSitCheck(eaBleSedentariness, new GeneralCallback() {
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
        sitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sitDialog == null) {
                    sitDialog = new SwitchDialog(SedentaryActivity.this);
                    sitDialog.setSelectListener(new SwitchDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            sitText.setText(sex);
                        }
                    });
                }
                if (!sitDialog.isShowing()) {
                    sitDialog.show();
                }

            }
        });
        breakText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (breakDialog == null) {
                    breakDialog = new SwitchDialog(SedentaryActivity.this);
                    breakDialog.setSelectListener(new SwitchDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            breakText.setText(sex);
                        }
                    });
                }
                if (!breakDialog.isShowing()) {
                    breakDialog.show();
                }

            }
        });
        noonStartHourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noonStartHourDialog == null) {
                    noonStartHourDialog = new MonthDialog(SedentaryActivity.this, getString(R.string.start_time));
                    noonStartHourDialog.setSelectListener(new MonthDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            noonStartHourText.setText(sex + "");
                        }
                    });
                }
                if (!noonStartHourDialog.isShowing()) {
                    noonStartHourDialog.show();
                }
            }
        });
        noonStartMinuteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noonStartMinuteDialog == null) {
                    noonStartMinuteDialog = new MonthDialog(SedentaryActivity.this, getString(R.string.start_time));
                    noonStartMinuteDialog.setSelectListener(new MonthDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            noonStartMinuteText.setText(sex + "");
                        }
                    });
                }
                if (!noonStartMinuteDialog.isShowing()) {
                    noonStartMinuteDialog.show();
                }
            }
        });
        noonEndHourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noonEndHourDialog == null) {
                    noonEndHourDialog = new MonthDialog(SedentaryActivity.this, getString(R.string.end_time));
                    noonEndHourDialog.setSelectListener(new MonthDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            noonEndHourText.setText(sex + "");
                        }
                    });
                }
                if (!noonEndHourDialog.isShowing()) {
                    noonEndHourDialog.show();
                }
            }
        });
        noonEndMinuteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noonEndMinuteDialog == null) {
                    noonEndMinuteDialog = new MonthDialog(SedentaryActivity.this, getString(R.string.end_time));
                    noonEndMinuteDialog.setSelectListener(new MonthDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            noonEndMinuteText.setText(sex + "");
                        }
                    });
                }
                if (!noonEndMinuteDialog.isShowing()) {
                    noonEndMinuteDialog.show();
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
        if (sitDialog != null) {
            sitDialog.destroyDialog();
            sitDialog = null;
        }
        if (breakDialog != null) {
            breakDialog.destroyDialog();
            breakDialog = null;
        }
        if (noonStartHourDialog != null) {
            noonStartHourDialog.destroyDialog();
            noonStartHourDialog = null;
        }
        if (noonStartMinuteDialog != null) {
            noonStartMinuteDialog.destroyDialog();
            noonStartMinuteDialog = null;
        }
        if (noonEndHourDialog != null) {
            noonEndHourDialog.destroyDialog();
            noonEndHourDialog = null;
        }
        if (noonEndMinuteDialog != null) {
            noonEndMinuteDialog.destroyDialog();
            noonEndMinuteDialog = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        if (monthDialog != null) {
            monthDialog.dismiss();
            monthDialog.destroyDialog();
            monthDialog = null;
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

    private int cycleTime;
    private String tempRest;

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
            cycleText.setText(tempRest);


        }
    }
}
