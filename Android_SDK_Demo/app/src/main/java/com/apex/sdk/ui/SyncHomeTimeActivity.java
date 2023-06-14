package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.TimeZoneCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.enumeration.TimeZone;
import com.apex.bluetooth.model.EABleHomeTimeZone;
import com.apex.sdk.R;
import com.apex.sdk.dialog.MonthDialog;
import com.apex.sdk.dialog.TimeZoneDialog;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SyncHomeTimeActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.time_zone)
    TextView zoneText;
    @BindView(R.id.current_hour)
    TextView currentHourText;
    @BindView(R.id.current_minute)
    TextView currentMinuteText;
    @BindView(R.id.town_name)
    TextView nameText;
    private MonthDialog currentHourDialog, currentMinuteDialog;
    private TimeZoneDialog timeZoneDialog;
    private EABleHomeTimeZone eaBleHomeTimeZone;
    private int tempCurrentHour, tempCurrentMinute;
    private String tempZone;
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
                eaBleHomeTimeZone = (EABleHomeTimeZone) msg.obj;
                List<EABleHomeTimeZone.EABleHomeZone> homeZones = eaBleHomeTimeZone.getS_home();
                if (homeZones != null && !homeZones.isEmpty()) {
                    nameText.setText(homeZones.get(0).getPlace());
                    currentHourText.setText(homeZones.get(0).getTime_zone_hour() + "");
                    currentMinuteText.setText(homeZones.get(0).getTime_zone_minute() + "");
                    TimeZone timeZone = homeZones.get(0).getE_time_zone();
                    if (timeZone == TimeZone.zero) {
                        zoneText.setText(getString(R.string.zero_time_zone));
                    } else if (timeZone == TimeZone.west) {
                        zoneText.setText(getString(R.string.west_time_zone));
                    } else if (timeZone == TimeZone.east) {
                        zoneText.setText(getString(R.string.eastern_time_zone));
                    }
                } else {
                    Log.e("TAG", "家乡时区数据不存在");
                }


            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SyncHomeTimeActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                currentHourText.setText(tempCurrentHour + "");
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SyncHomeTimeActivity.this, getString(R.string.add_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x44) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                currentMinuteText.setText(tempCurrentMinute + "");
            } else if (msg.what == 0x45) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                zoneText.setText(tempZone);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_home_time);
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
                waitingDialog = new WaitingDialog(SyncHomeTimeActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.time_zone, new TimeZoneCallback() {
                @Override
                public void timeZoneInfo(EABleHomeTimeZone eaBleHomeTimeZone) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleHomeTimeZone;
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
        currentHourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (currentHourDialog == null) {
                        currentHourDialog = new MonthDialog(SyncHomeTimeActivity.this, getString(R.string.current_hour));
                        currentHourDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleHomeTimeZone == null) {
                                    eaBleHomeTimeZone = new EABleHomeTimeZone();
                                }
                                List<EABleHomeTimeZone.EABleHomeZone> homeZoneList = new ArrayList<>();
                                EABleHomeTimeZone.EABleHomeZone homeZone = new EABleHomeTimeZone.EABleHomeZone();
                                homeZone.setE_time_zone(TimeZone.zero);
                                homeZone.setPlace("gu_xiang");
                                homeZone.setTime_zone_minute(10);
                                homeZone.setTime_zone_hour(sex);
                                homeZoneList.add(homeZone);
                                eaBleHomeTimeZone.setS_home(homeZoneList);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncHomeTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempCurrentHour = sex;
                                EABleManager.getInstance().setHomeTimeZone(eaBleHomeTimeZone, new GeneralCallback() {
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
                        currentMinuteDialog = new MonthDialog(SyncHomeTimeActivity.this, getString(R.string.current_minute));
                        currentMinuteDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleHomeTimeZone == null) {
                                    eaBleHomeTimeZone = new EABleHomeTimeZone();
                                }
                                List<EABleHomeTimeZone.EABleHomeZone> homeZoneList = new ArrayList<>();
                                EABleHomeTimeZone.EABleHomeZone homeZone = new EABleHomeTimeZone.EABleHomeZone();
                                homeZone.setE_time_zone(TimeZone.zero);
                                homeZone.setPlace("gu_xiang");
                                homeZone.setTime_zone_minute(sex);
                                homeZone.setTime_zone_hour(10);
                                homeZoneList.add(homeZone);
                                eaBleHomeTimeZone.setS_home(homeZoneList);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncHomeTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempCurrentMinute = sex;
                                EABleManager.getInstance().setHomeTimeZone(eaBleHomeTimeZone, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success) {
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
                        timeZoneDialog = new TimeZoneDialog(SyncHomeTimeActivity.this);
                        timeZoneDialog.setSelectListener(new TimeZoneDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBleHomeTimeZone == null) {
                                    eaBleHomeTimeZone = new EABleHomeTimeZone();
                                }
                                List<EABleHomeTimeZone.EABleHomeZone> homeZoneList = new ArrayList<>();
                                EABleHomeTimeZone.EABleHomeZone homeZone = new EABleHomeTimeZone.EABleHomeZone();
                                homeZone.setPlace("jia_xiang");
                                homeZone.setTime_zone_minute(10);
                                homeZone.setTime_zone_hour(10);
                                homeZoneList.add(homeZone);
                                eaBleHomeTimeZone.setS_home(homeZoneList);
                                if (sex.equalsIgnoreCase(getString(R.string.eastern_time_zone))) {
                                    homeZone.setE_time_zone(TimeZone.east);
                                } else if (sex.equalsIgnoreCase(getString(R.string.west_time_zone))) {
                                    homeZone.setE_time_zone(TimeZone.west);
                                } else if (sex.equalsIgnoreCase(getString(R.string.zero_time_zone))) {
                                    homeZone.setE_time_zone(TimeZone.zero);
                                }
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncHomeTimeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempZone = sex;
                                EABleManager.getInstance().setHomeTimeZone(eaBleHomeTimeZone, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success) {
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
                    if (!timeZoneDialog.isShowing()) {
                        timeZoneDialog.show();
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

        super.onDestroy();
    }
}
