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
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.HeartCheckCallback;
import com.apex.bluetooth.callback.HeartLimitCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleHr;
import com.apex.sdk.R;
import com.apex.sdk.dialog.MonthDialog;
import com.apex.sdk.dialog.SwitchDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HeartReteActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private SwitchDialog heartDialog, limitDialog;
    private MonthDialog monthDialog, maxDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.heart_switch)
    TextView heartText;
    @BindView(R.id.interval_time)
    TextView intervalText;
    @BindView(R.id.alarm_switch)
    TextView alarmText;
    @BindView(R.id.max_heart_rate)
    TextView maxText;
    @BindView(R.id.min_heart_rate)
    TextView minText;
    private EABleHr eaBleHr;
    private String tempHeart, tempLimit;
    private int tempInterval, tempMax;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x40) {
                int state = (int) msg.obj;
                if (state == 0) {
                    heartText.setText(getString(R.string.switch_state_close));
                } else {
                    heartText.setText(getString(R.string.switch_state_on));
                    intervalText.setText(state + "");
                }
                EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.heart_rate_limit, new HeartLimitCallback() {
                    @Override
                    public void heartLimitInfo(EABleHr eaBleHr) {
                        if (mHandler != null) {
                            Message message = mHandler.obtainMessage();
                            message.what = 0x42;
                            message.obj = eaBleHr;
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
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(HeartReteActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                eaBleHr = (EABleHr) msg.obj;
                if (eaBleHr.getSw() == 0) {
                    alarmText.setText(getString(R.string.switch_state_close));
                } else {
                    alarmText.setText(getString(R.string.switch_state_on));
                }
                maxText.setText(eaBleHr.getMax_hr() + "");
                minText.setText(eaBleHr.getMin_hr() + "");
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(HeartReteActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x44) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                heartText.setText(tempHeart);
                if (tempHeart.equalsIgnoreCase(getString(R.string.switch_state_close))) {
                    intervalText.setText(0 + "");
                }
            } else if (msg.what == 0x45) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                intervalText.setText(tempInterval + "");
                if (tempInterval == 0) {
                    heartText.setText(getString(R.string.switch_state_close));
                } else {
                    heartText.setText(getString(R.string.switch_state_on));
                }
            } else if (msg.what == 0x46) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                alarmText.setText(tempLimit);
            } else if (msg.what == 0x47) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                maxText.setText(tempMax + "");
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
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
                waitingDialog = new WaitingDialog(HeartReteActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.heart_rate_check, new HeartCheckCallback() {
                @Override
                public void heartInfo(int heartTime) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = heartTime;
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
        heartText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (heartDialog == null) {
                        heartDialog = new SwitchDialog(HeartReteActivity.this);
                        heartDialog.setSelectListener(new SwitchDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                tempHeart = sex;
                                if (sex.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                                    if (waitingDialog == null) {
                                        waitingDialog = new WaitingDialog(HeartReteActivity.this);
                                    }
                                    if (!waitingDialog.isShowing()) {
                                        waitingDialog.show();
                                    }
                                    EABleManager.getInstance().setHeartRateIntervalTime(10, new GeneralCallback() {
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
                                } else {
                                    if (waitingDialog == null) {
                                        waitingDialog = new WaitingDialog(HeartReteActivity.this);
                                    }
                                    if (!waitingDialog.isShowing()) {
                                        waitingDialog.show();
                                    }
                                    EABleManager.getInstance().setHeartRateIntervalTime(0, new GeneralCallback() {
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
                            }
                        });
                    }
                    if (!heartDialog.isShowing()) {
                        heartDialog.show();
                    }

                }
            }
        });
        intervalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (monthDialog == null) {
                        monthDialog = new MonthDialog(HeartReteActivity.this, getString(R.string.interval_time));
                        monthDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                tempInterval = sex;
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(HeartReteActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setHeartRateIntervalTime(sex, new GeneralCallback() {
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
                    if (!monthDialog.isShowing()) {
                        monthDialog.show();
                    }
                }
            }
        });
        alarmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (limitDialog == null) {
                        limitDialog = new SwitchDialog(HeartReteActivity.this);
                        limitDialog.setSelectListener(new SwitchDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                tempLimit = sex;
                                if (sex.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                                    if (waitingDialog == null) {
                                        waitingDialog = new WaitingDialog(HeartReteActivity.this);
                                    }
                                    if (!waitingDialog.isShowing()) {
                                        waitingDialog.show();
                                    }
                                    if (eaBleHr == null) {
                                        eaBleHr = new EABleHr();
                                        eaBleHr.setMax_hr(180);
                                        eaBleHr.setMin_hr(60);
                                    }
                                    eaBleHr.setSw(1);
                                    EABleManager.getInstance().setHeartRateLimit(eaBleHr, new GeneralCallback() {
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
                                } else {
                                    if (waitingDialog == null) {
                                        waitingDialog = new WaitingDialog(HeartReteActivity.this);
                                    }
                                    if (!waitingDialog.isShowing()) {
                                        waitingDialog.show();
                                    }
                                    if (eaBleHr == null) {
                                        eaBleHr = new EABleHr();
                                        eaBleHr.setMax_hr(180);
                                        eaBleHr.setMin_hr(60);
                                    }
                                    eaBleHr.setSw(0);
                                    EABleManager.getInstance().setHeartRateLimit(eaBleHr, new GeneralCallback() {
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
                        });
                    }
                    if (!limitDialog.isShowing()) {
                        limitDialog.show();
                    }
                }
            }
        });
        maxText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (maxDialog == null) {
                        maxDialog = new MonthDialog(HeartReteActivity.this, getString(R.string.max_heart_rate));
                        maxDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                tempMax = sex;
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(HeartReteActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                if (eaBleHr == null) {
                                    eaBleHr = new EABleHr();
                                    eaBleHr.setSw(0);
                                    eaBleHr.setMin_hr(60);
                                }
                                eaBleHr.setMax_hr(sex);
                                EABleManager.getInstance().setHeartRateLimit(eaBleHr, new GeneralCallback() {
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
                    if (!maxDialog.isShowing()) {
                        maxDialog.show();
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
        if (heartDialog != null) {
            heartDialog.dismiss();
            heartDialog.destroyDialog();
            heartDialog = null;
        }
        if (limitDialog != null) {
            limitDialog.dismiss();
            limitDialog.destroyDialog();
            limitDialog = null;
        }
        if (monthDialog != null) {
            monthDialog.dismiss();
            monthDialog.destroyDialog();
            monthDialog = null;
        }
        if (maxDialog != null) {
            maxDialog.dismiss();
            maxDialog.destroyDialog();
            maxDialog = null;
        }
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        super.onDestroy();
    }
}
