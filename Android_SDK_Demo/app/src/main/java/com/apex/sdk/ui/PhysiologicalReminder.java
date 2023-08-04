package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.LanguageCallback;
import com.apex.bluetooth.callback.PeriodReminderCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleDeviceLanguage;
import com.apex.bluetooth.model.EABlePeriodReminder;
import com.apex.sdk.R;
import com.apex.sdk.dialog.SwitchDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PhysiologicalReminder extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.period_start_switch)
    TextView periodStartText;
    @BindView(R.id.period_end_switch)
    TextView periodEndText;
    @BindView(R.id.danger_start_switch)
    TextView dangerStartText;
    @BindView(R.id.danger_end_switch)
    TextView dangerEndText;
    @BindView(R.id.ovulation_switch)
    TextView ovulationText;
    EABlePeriodReminder eaBlePeriodReminder;
    private SwitchDialog periodStartDialog, periodEndDialog, dangerStartDialog, dangerEndDialog, ovulationDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x40) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                if (eaBlePeriodReminder.getPeriodStart() > 0) {
                    periodStartText.setText(getString(R.string.switch_state_on));
                } else {
                    periodStartText.setText(getString(R.string.switch_state_close));
                }
                if (eaBlePeriodReminder.getPeriodEnd() > 0) {
                    periodEndText.setText(getString(R.string.switch_state_on));
                } else {
                    periodEndText.setText(getString(R.string.switch_state_close));
                }
                if (eaBlePeriodReminder.getPregnancyStart() > 0) {
                    dangerStartText.setText(getString(R.string.switch_state_on));
                } else {
                    dangerStartText.setText(getString(R.string.switch_state_close));
                }
                if (eaBlePeriodReminder.getPregnancyEnd() > 0) {
                    dangerEndText.setText(getString(R.string.switch_state_on));
                } else {
                    dangerEndText.setText(getString(R.string.switch_state_close));
                }
                if (eaBlePeriodReminder.getOvulation_day_sw() > 0) {
                    ovulationText.setText(getString(R.string.switch_state_on));
                } else {
                    ovulationText.setText(getString(R.string.switch_state_close));
                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(PhysiologicalReminder.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                mHandler.sendEmptyMessage(0x40);

            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(PhysiologicalReminder.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physiological_reminder);
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
                waitingDialog = new WaitingDialog(PhysiologicalReminder.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.period_reminder, new PeriodReminderCallback() {
                @Override
                public void periodReminderInfo(EABlePeriodReminder periodReminder) {
                    eaBlePeriodReminder = periodReminder;
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        mHandler.sendMessage(message);
                    }
                }

                @Override
                public void mutualFail(int i) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x41);
                    }
                }
            });
        }
        periodStartText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (periodStartDialog == null) {
                        periodStartDialog = new SwitchDialog(PhysiologicalReminder.this);
                        periodStartDialog.setSelectListener(new SwitchDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBlePeriodReminder == null) {
                                    eaBlePeriodReminder = new EABlePeriodReminder();
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                                    eaBlePeriodReminder.setPeriodStart(1);
                                } else {
                                    eaBlePeriodReminder.setPeriodStart(0);
                                }
                                EABleManager.getInstance().setPeriodReminder(eaBlePeriodReminder, new GeneralCallback() {
                                    @Override
                                    public void result(boolean b,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x42);
                                        }
                                    }

                                    @Override
                                    public void mutualFail(int i) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!periodStartDialog.isShowing()) {
                        periodStartDialog.show();
                    }
                }
            }
        });
        periodEndText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (periodEndDialog == null) {
                        periodEndDialog = new SwitchDialog(PhysiologicalReminder.this);
                        periodEndDialog.setSelectListener(new SwitchDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBlePeriodReminder == null) {
                                    eaBlePeriodReminder = new EABlePeriodReminder();
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                                    eaBlePeriodReminder.setPeriodEnd(1);
                                } else {
                                    eaBlePeriodReminder.setPeriodEnd(0);
                                }
                                EABleManager.getInstance().setPeriodReminder(eaBlePeriodReminder, new GeneralCallback() {
                                    @Override
                                    public void result(boolean b,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x42);
                                        }
                                    }

                                    @Override
                                    public void mutualFail(int i) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!periodEndDialog.isShowing()) {
                        periodEndDialog.show();
                    }
                }
            }
        });
        dangerStartText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (dangerStartDialog == null) {
                        dangerStartDialog = new SwitchDialog(PhysiologicalReminder.this);
                        dangerStartDialog.setSelectListener(new SwitchDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBlePeriodReminder == null) {
                                    eaBlePeriodReminder = new EABlePeriodReminder();
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                                    eaBlePeriodReminder.setPregnancyStart(1);
                                } else {
                                    eaBlePeriodReminder.setPregnancyStart(0);
                                }
                                EABleManager.getInstance().setPeriodReminder(eaBlePeriodReminder, new GeneralCallback() {
                                    @Override
                                    public void result(boolean b,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x42);
                                        }
                                    }

                                    @Override
                                    public void mutualFail(int i) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!dangerStartDialog.isShowing()) {
                        dangerStartDialog.show();
                    }
                }
            }
        });
        dangerEndText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (dangerEndDialog == null) {
                        dangerEndDialog = new SwitchDialog(PhysiologicalReminder.this);
                        dangerEndDialog.setSelectListener(new SwitchDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBlePeriodReminder == null) {
                                    eaBlePeriodReminder = new EABlePeriodReminder();
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                                    eaBlePeriodReminder.setPregnancyEnd(1);
                                } else {
                                    eaBlePeriodReminder.setPregnancyEnd(0);
                                }
                                EABleManager.getInstance().setPeriodReminder(eaBlePeriodReminder, new GeneralCallback() {
                                    @Override
                                    public void result(boolean b,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x42);
                                        }
                                    }

                                    @Override
                                    public void mutualFail(int i) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!dangerEndDialog.isShowing()) {
                        dangerEndDialog.show();
                    }
                }

            }
        });
        ovulationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (ovulationDialog == null) {
                        ovulationDialog = new SwitchDialog(PhysiologicalReminder.this);
                        ovulationDialog.setSelectListener(new SwitchDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBlePeriodReminder == null) {
                                    eaBlePeriodReminder = new EABlePeriodReminder();
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                                    eaBlePeriodReminder.setOvulation_day_sw(1);
                                } else {
                                    eaBlePeriodReminder.setOvulation_day_sw(0);
                                }
                                EABleManager.getInstance().setPeriodReminder(eaBlePeriodReminder, new GeneralCallback() {
                                    @Override
                                    public void result(boolean b,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x42);
                                        }
                                    }

                                    @Override
                                    public void mutualFail(int i) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });

                            }
                        });
                    }
                    if (!ovulationDialog.isShowing()) {
                        ovulationDialog.show();
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
        if (periodEndDialog != null) {
            periodEndDialog.destroyDialog();
            periodEndDialog = null;
        }
        if (periodStartDialog != null) {
            periodStartDialog.destroyDialog();
            periodStartDialog = null;
        }
        if (dangerEndDialog != null) {
            dangerEndDialog.destroyDialog();
            dangerEndDialog = null;
        }
        if (dangerStartDialog != null) {
            dangerStartDialog.destroyDialog();
            dangerStartDialog = null;
        }
        if (ovulationDialog != null) {
            ovulationDialog.destroyDialog();
            ovulationDialog = null;
        }
        super.onDestroy();
    }
}
