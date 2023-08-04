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
import com.apex.bluetooth.callback.TimeCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.enumeration.TimeZone;
import com.apex.bluetooth.model.EABleSyncTime;
import com.apex.sdk.R;
import com.apex.sdk.dialog.HourSystemDialog;
import com.apex.sdk.dialog.ModeDialog;
import com.apex.sdk.dialog.MonthDialog;
import com.apex.sdk.dialog.TimeZoneDialog;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.dialog.YearDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SyncTimeActivity extends AppCompatActivity {
    private Unbinder unbinder;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.time_year)
    TextView yearText;
    @BindView(R.id.time_month)
    TextView monthText;
    @BindView(R.id.time_day)
    TextView dayText;
    @BindView(R.id.time_hour)
    TextView hourText;
    @BindView(R.id.time_minute)
    TextView minuteText;
    @BindView(R.id.time_second)
    TextView secondText;
    @BindView(R.id.hour_system)
    TextView systemText;
    @BindView(R.id.current_hour)
    TextView currentHourText;
    @BindView(R.id.current_minute)
    TextView currentMinuteText;
    @BindView(R.id.time_zone)
    TextView zoneText;
    @BindView(R.id.sync_mode)
    TextView modeText;
    @BindView(R.id.submit)
    AppCompatButton syncCurrentButton;
    private WaitingDialog waitingDialog;
    private EABleSyncTime eaBleSyncTime;
    private YearDialog yearDialog;
    private MonthDialog monthDialog, dayDialog, hourDialog, minuteDialog, secondDialog, currentHourDialog, currentMinuteDialog;
    private HourSystemDialog hourSystemDialog;
    private TimeZoneDialog timeZoneDialog;
    private ModeDialog modeDialog;
    private String tempHourSystem, tempZone, tempMode;
    private int tempYear, tempMonth, tempDay, tempHour, tempMinute, tempSecond, tempCurrentHour, tempCurrentMinute;
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
                eaBleSyncTime = (EABleSyncTime) msg.obj;
                yearText.setText(eaBleSyncTime.getYear() + "");
                monthText.setText(eaBleSyncTime.getMonth() + "");
                dayText.setText(eaBleSyncTime.getDay() + "");
                hourText.setText(eaBleSyncTime.getHour() + "");
                minuteText.setText(eaBleSyncTime.getMinute() + "");
                secondText.setText(eaBleSyncTime.getSecond() + "");
                EABleSyncTime.HourSystem hourSystem = eaBleSyncTime.getE_hour_system();
                if (hourSystem == EABleSyncTime.HourSystem.hour_12) {
                    systemText.setText(getString(R.string.twelve_hour_system));
                } else {
                    systemText.setText(getString(R.string.twenty_four_hour_system));
                }
                currentHourText.setText(eaBleSyncTime.getTime_zone_hour() + "");
                currentMinuteText.setText(eaBleSyncTime.getTime_zone_minute() + "");
                TimeZone timeZone = eaBleSyncTime.getE_time_zone();
                if (timeZone == TimeZone.east) {
                    zoneText.setText(getString(R.string.eastern_time_zone));
                } else if (timeZone == TimeZone.west) {
                    zoneText.setText(getString(R.string.west_time_zone));
                } else if (timeZone == TimeZone.zero) {
                    zoneText.setText(getString(R.string.zero_time_zone));
                }
                EABleSyncTime.SyncMode syncMode = eaBleSyncTime.getE_sync_mode();
                if (syncMode == EABleSyncTime.SyncMode.normal) {
                    modeText.setText(getString(R.string.general_sync));
                } else {
                    modeText.setText(getString(R.string.sync_to_movement));
                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SyncTimeActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                yearText.setText(tempYear + "");
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SyncTimeActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x44) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                monthText.setText(tempMonth + "");
            } else if (msg.what == 0x45) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                dayText.setText(tempDay + "");
            } else if (msg.what == 0x46) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                hourText.setText(tempHour + "");
            } else if (msg.what == 0x47) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                minuteText.setText(tempMinute + "");
            } else if (msg.what == 0x48) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                secondText.setText(tempSecond + "");
            } else if (msg.what == 0x49) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                systemText.setText(tempHourSystem);
            } else if (msg.what == 0x4A) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                currentHourText.setText(tempCurrentHour + "");
            } else if (msg.what == 0x4B) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                currentMinuteText.setText(tempCurrentMinute + "");
            } else if (msg.what == 0x4C) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                zoneText.setText(tempZone);
            } else if (msg.what == 0x4D) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                modeText.setText(tempMode);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_time);
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
                waitingDialog = new WaitingDialog(SyncTimeActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.sync_time, new TimeCallback() {
                @Override
                public void syncTime(EABleSyncTime eaBleSyncTime) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleSyncTime;
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
        yearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (yearDialog == null) {
                        yearDialog = new YearDialog(SyncTimeActivity.this);
                        yearDialog.setSelectListener(new YearDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                eaBleSyncTime.setYear(sex);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempYear = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
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
                        });
                    }
                    if (!yearDialog.isShowing()) {
                        yearDialog.show();
                    }
                }
            }
        });
        monthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (monthDialog == null) {
                        monthDialog = new MonthDialog(SyncTimeActivity.this, getString(R.string.month));
                        monthDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                eaBleSyncTime.setMonth(sex);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempMonth = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
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
                    if (!monthDialog.isShowing()) {
                        monthDialog.show();
                    }
                }
            }
        });
        dayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (dayDialog == null) {
                        dayDialog = new MonthDialog(SyncTimeActivity.this, getString(R.string.day));
                        dayDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                eaBleSyncTime.setDay(sex);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempDay = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
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
                    if (!dayDialog.isShowing()) {
                        dayDialog.show();
                    }
                }
            }
        });
        hourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (hourDialog == null) {
                        hourDialog = new MonthDialog(SyncTimeActivity.this, getString(R.string.hour));
                        hourDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                eaBleSyncTime.setHour(sex);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempHour = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
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
                        });
                    }
                    if (!hourDialog.isShowing()) {
                        hourDialog.show();
                    }
                }
            }
        });
        minuteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (minuteDialog == null) {
                        minuteDialog = new MonthDialog(SyncTimeActivity.this, getString(R.string.minute));
                        minuteDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                eaBleSyncTime.setMinute(sex);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempMinute = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x47);
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
                    if (!minuteDialog.isShowing()) {
                        minuteDialog.show();
                    }
                }
            }
        });
        secondText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (secondDialog == null) {
                        secondDialog = new MonthDialog(SyncTimeActivity.this, getString(R.string.second));
                        secondDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                eaBleSyncTime.setSecond(sex);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempSecond = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x48);
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
                    if (!secondDialog.isShowing()) {
                        secondDialog.show();
                    }
                }
            }
        });
        systemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (hourSystemDialog == null) {
                        hourSystemDialog = new HourSystemDialog(SyncTimeActivity.this);
                        hourSystemDialog.setSelectListener(new HourSystemDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.twelve_hour_system))) {
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_12);
                                } else {
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                }
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempHourSystem = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x49);
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
                    if (!hourSystemDialog.isShowing()) {
                        hourSystemDialog.show();
                    }
                }
            }
        });
        currentHourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (currentHourDialog == null) {
                        currentHourDialog = new MonthDialog(SyncTimeActivity.this, getString(R.string.current_hour));
                        currentHourDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                eaBleSyncTime.setTime_zone_hour(sex);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempCurrentHour = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x4A);
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
                    if (!currentHourDialog.isShowing()) {
                        currentHourDialog.show();
                    }
                }
            }
        });
        currentMinuteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (currentMinuteDialog == null) {
                        currentMinuteDialog = new MonthDialog(SyncTimeActivity.this, getString(R.string.current_minute));
                        currentMinuteDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                eaBleSyncTime.setTime_zone_minute(sex);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempCurrentMinute = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x4B);
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
                    if (!currentMinuteDialog.isShowing()) {
                        currentMinuteDialog.show();
                    }
                }
            }
        });
        zoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (timeZoneDialog == null) {
                        timeZoneDialog = new TimeZoneDialog(SyncTimeActivity.this);
                        timeZoneDialog.setSelectListener(new TimeZoneDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.zero_time_zone))) {
                                    eaBleSyncTime.setE_time_zone(TimeZone.zero);
                                } else if (sex.equalsIgnoreCase(getString(R.string.west_time_zone))) {
                                    eaBleSyncTime.setE_time_zone(TimeZone.west);
                                } else if (sex.equalsIgnoreCase(getString(R.string.eastern_time_zone))) {
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                }
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempZone = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x4C);
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
                    if (!timeZoneDialog.isShowing()) {
                        timeZoneDialog.show();
                    }
                }
            }
        });
        modeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (modeDialog == null) {
                        modeDialog = new ModeDialog(SyncTimeActivity.this);
                        modeDialog.setSelectListener(new ModeDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBleSyncTime == null) {
                                    eaBleSyncTime = new EABleSyncTime();
                                    eaBleSyncTime.setYear(2022);
                                    eaBleSyncTime.setMonth(3);
                                    eaBleSyncTime.setDay(3);
                                    eaBleSyncTime.setHour(11);
                                    eaBleSyncTime.setMinute(20);
                                    eaBleSyncTime.setSecond(20);
                                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                                    eaBleSyncTime.setTime_zone_hour(8);
                                    eaBleSyncTime.setTime_zone_minute(0);
                                    eaBleSyncTime.setE_time_zone(TimeZone.east);
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.general_sync))) {
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                                } else {
                                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.watch);
                                }
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempMode = sex;
                                EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x4D);
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
                        if (!modeDialog.isShowing()) {
                            modeDialog.show();
                        }
                    }
                }
            }
        });
        syncCurrentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED){
                    EABleSyncTime eaBleSyncTime = new EABleSyncTime();
                    Calendar calendar = Calendar.getInstance();
                    eaBleSyncTime.setYear(calendar.get(Calendar.YEAR));
                    eaBleSyncTime.setMonth(calendar.get(Calendar.MONTH) + 1);
                    eaBleSyncTime.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                    eaBleSyncTime.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                    eaBleSyncTime.setMinute(calendar.get(Calendar.MINUTE));
                    eaBleSyncTime.setSecond(calendar.get(Calendar.SECOND));
                    //  calendar1.setTimeZone(TimeZone.getTimeZone("GMT"));
                    long currentTime = calendar.getTime().getTime();
                    int timeOffset = calendar.getTimeZone().getOffset(currentTime);
                    if (timeOffset > 0) {
                        eaBleSyncTime.setE_time_zone(TimeZone.east);
                    } else if (timeOffset < 0) {
                        eaBleSyncTime.setE_time_zone(TimeZone.west);
                    } else {
                        eaBleSyncTime.setE_time_zone(TimeZone.zero);
                    }
                    int zoneHour = Math.abs(timeOffset) / 1000 / 60 / 60;
                    eaBleSyncTime.setTime_zone_hour(zoneHour);
                    int zoneMinute = (Math.abs(timeOffset) - zoneHour * 1000 * 60 * 60) / 1000 / 60;
                    eaBleSyncTime.setTime_zone_minute(zoneMinute <= 0 ? 0 : zoneMinute);
                    eaBleSyncTime.setE_sync_mode(EABleSyncTime.SyncMode.normal);
                    eaBleSyncTime.setE_hour_system(EABleSyncTime.HourSystem.hour_24);
                    EABleManager.getInstance().setTimeSync(eaBleSyncTime, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x4D);
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
        if (yearDialog != null) {
            yearDialog.dismiss();
            yearDialog.destroyDialog();
            yearDialog = null;
        }
        if (monthDialog != null) {
            monthDialog.dismiss();
            monthDialog.destroyDialog();
            monthDialog = null;
        }
        if (dayDialog != null) {
            dayDialog.dismiss();
            dayDialog.destroyDialog();
            dayDialog = null;
        }
        if (hourDialog != null) {
            hourDialog.dismiss();
            hourDialog.destroyDialog();
            hourDialog = null;
        }
        if (minuteDialog != null) {
            minuteDialog.dismiss();
            minuteDialog.destroyDialog();
            minuteDialog = null;
        }
        if (secondDialog != null) {
            secondDialog.dismiss();
            secondDialog.destroyDialog();
            secondDialog = null;
        }
        if (hourSystemDialog != null) {
            hourSystemDialog.dismiss();
            hourSystemDialog.destroyDialog();
            hourSystemDialog = null;
        }
        if (currentHourDialog != null) {
            currentHourDialog.dismiss();
            currentHourDialog.destroyDialog();
            currentHourDialog = null;
        }
        if (currentMinuteDialog != null) {
            currentMinuteDialog.dismiss();
            currentMinuteDialog.destroyDialog();
            currentMinuteDialog = null;
        }
        if (timeZoneDialog != null) {
            timeZoneDialog.dismiss();
            timeZoneDialog.destroyDialog();
            timeZoneDialog = null;
        }
        if (modeDialog != null) {
            modeDialog.dismiss();
            modeDialog.destroyDialog();
            modeDialog = null;
        }
        super.onDestroy();
    }
}
