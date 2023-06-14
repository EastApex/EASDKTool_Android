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

import com.apex.bluetooth.callback.DonDisturbCallback;
import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleNotDisturb;
import com.apex.sdk.R;
import com.apex.sdk.dialog.NotDisturbDialog;
import com.apex.sdk.dialog.SwitchDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class NoDisturbActivity extends AppCompatActivity {
    private NotDisturbDialog startDialog;
    private NotDisturbDialog endDialog;
    private WaitingDialog waitingDialog;
    private Unbinder unbinder;
    @BindView(R.id.rest_screen)
    TextView switchText;
    @BindView(R.id.start_time)
    TextView startText;
    @BindView(R.id.end_time)
    TextView endText;
    @BindView(R.id.tool)
    Toolbar toolbar;
    private EABleNotDisturb eaBleNotDisturb;
    private SwitchDialog switchDialog;
    private String tempState;
    private int tempStartHour, tempStartMinute, tempEndHour, tempEndMinute;
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
                eaBleNotDisturb = (EABleNotDisturb) msg.obj;
                if (eaBleNotDisturb.getSw() == 0) {
                    switchText.setText(getString(R.string.switch_state_close));
                } else {
                    switchText.setText(getString(R.string.switch_state_on));
                }
                startText.setText(eaBleNotDisturb.getBegin_hour() + ":" + eaBleNotDisturb.getBegin_minute());
                endText.setText(eaBleNotDisturb.getEnd_hour() + ":" + eaBleNotDisturb.getEnd_minute());
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(NoDisturbActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                switchText.setText(tempState);
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(NoDisturbActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
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
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disturb);
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
                waitingDialog = new WaitingDialog(NoDisturbActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.not_disturb, new DonDisturbCallback() {
                @Override
                public void donDisturbInfo(EABleNotDisturb eaBleNotDisturb) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleNotDisturb;
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
        switchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (switchDialog == null) {
                        switchDialog = new SwitchDialog(NoDisturbActivity.this);
                        switchDialog.setSelectListener(new SwitchDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBleNotDisturb == null) {
                                    eaBleNotDisturb = new EABleNotDisturb();
                                    eaBleNotDisturb.setEnd_hour(10);
                                    eaBleNotDisturb.setEnd_minute(10);
                                    eaBleNotDisturb.setBegin_hour(8);
                                    eaBleNotDisturb.setBegin_minute(8);
                                }
                                tempState = sex;
                                if (sex.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                                    eaBleNotDisturb.setSw(1);
                                } else {
                                    eaBleNotDisturb.setSw(0);
                                }
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(NoDisturbActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setNotDisturb(eaBleNotDisturb, new GeneralCallback() {
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
                    if (!switchDialog.isShowing()) {
                        switchDialog.show();
                    }
                }
            }
        });
        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (startDialog == null) {
                        startDialog = new NotDisturbDialog(NoDisturbActivity.this, getString(R.string.start_time));
                        startDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                            @Override
                            public void selectData(int hour, int minute) {
                                if (eaBleNotDisturb == null) {
                                    eaBleNotDisturb = new EABleNotDisturb();
                                    eaBleNotDisturb.setEnd_hour(10);
                                    eaBleNotDisturb.setEnd_minute(10);
                                    eaBleNotDisturb.setSw(0);
                                }
                                tempStartHour = hour;
                                tempStartMinute = minute;
                                eaBleNotDisturb.setBegin_hour(hour);
                                eaBleNotDisturb.setBegin_minute(minute);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(NoDisturbActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setNotDisturb(eaBleNotDisturb, new GeneralCallback() {
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
                        endDialog = new NotDisturbDialog(NoDisturbActivity.this, getString(R.string.end_time));
                        endDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                            @Override
                            public void selectData(int hour, int minute) {
                                if (eaBleNotDisturb == null) {
                                    eaBleNotDisturb = new EABleNotDisturb();
                                    eaBleNotDisturb.setBegin_hour(8);
                                    eaBleNotDisturb.setBegin_minute(8);
                                    eaBleNotDisturb.setSw(0);
                                }
                                tempEndHour = hour;
                                tempEndMinute = minute;
                                eaBleNotDisturb.setEnd_hour(hour);
                                eaBleNotDisturb.setEnd_minute(minute);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(NoDisturbActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setNotDisturb(eaBleNotDisturb, new GeneralCallback() {
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
        if (switchDialog != null) {
            switchDialog.dismiss();
            switchDialog.destroyDialog();
            switchDialog = null;
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
}
