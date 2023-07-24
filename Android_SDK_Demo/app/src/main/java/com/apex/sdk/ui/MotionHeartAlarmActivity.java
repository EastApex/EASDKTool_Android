package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
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
import com.apex.bluetooth.callback.MotionAlarmHrCallback;
import com.apex.bluetooth.callback.WatchFaceCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleMotionAlarmHr;
import com.apex.bluetooth.model.EABleWatchFace;
import com.apex.sdk.R;
import com.apex.sdk.dialog.MonthDialog;
import com.apex.sdk.dialog.SwitchDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MotionHeartAlarmActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.heart_switch)
    TextView switchText;
    @BindView(R.id.max_heart_rate)
    TextView maxText;
    @BindView(R.id.min_heart_rate)
    TextView minText;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    EABleMotionAlarmHr eaBleMotionAlarmHr;
    SwitchDialog switchDialog;
    private MonthDialog monthDialog, maxDialog;
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
                if (eaBleMotionAlarmHr.getSw() > 0) {
                    switchText.setText(getString(R.string.switch_state_on));
                } else {
                    switchText.setText(getString(R.string.switch_state_close));
                }
                if (eaBleMotionAlarmHr.getMax_hr() <= 0) {
                    maxText.setText("--");
                } else {
                    maxText.setText(eaBleMotionAlarmHr.getMax_hr() + "");
                }
                if (eaBleMotionAlarmHr.getMin_hr() <= 0) {
                    minText.setText("--");
                } else {
                    minText.setText(eaBleMotionAlarmHr.getMin_hr() + "");
                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }

            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(MotionHeartAlarmActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_heart_alarm);
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
                waitingDialog = new WaitingDialog(MotionHeartAlarmActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.motion_heart_alarm, new MotionAlarmHrCallback() {
                @Override
                public void mutualFail(int i) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x41);
                    }
                }

                @Override
                public void alarmHr(EABleMotionAlarmHr motionAlarmHr) {
                    eaBleMotionAlarmHr = motionAlarmHr;
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        mHandler.sendMessage(message);
                    }
                }
            });
        }
        switchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchDialog == null) {
                    switchDialog = new SwitchDialog(MotionHeartAlarmActivity.this);
                    switchDialog.setSelectListener(new SwitchDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            switchText.setText(sex);
                        }
                    });
                }
                if (!switchDialog.isShowing()) {
                    switchDialog.show();
                }
            }
        });
        maxText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maxDialog == null) {
                    maxDialog = new MonthDialog(MotionHeartAlarmActivity.this, getString(R.string.max_heart_rate));
                    maxDialog.setSelectListener(new MonthDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            maxText.setText(sex + "");
                        }
                    });
                }
                if (!maxDialog.isShowing()) {
                    maxDialog.show();
                }

            }
        });
        minText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monthDialog == null) {
                    monthDialog = new MonthDialog(MotionHeartAlarmActivity.this, getString(R.string.min_heart_rate));
                    monthDialog.setSelectListener(new MonthDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            minText.setText(sex + "");
                        }
                    });
                }
                if (!monthDialog.isShowing()) {
                    monthDialog.show();
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (eaBleMotionAlarmHr == null) {
                        eaBleMotionAlarmHr = new EABleMotionAlarmHr();
                    }
                    String off = switchText.getText().toString();
                    if (!TextUtils.isEmpty(off) && off.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                        eaBleMotionAlarmHr.setSw(1);
                    } else {
                        eaBleMotionAlarmHr.setSw(0);
                    }
                    String max = maxText.getText().toString();
                    if (TextUtils.isEmpty(max)) {
                        eaBleMotionAlarmHr.setMax_hr(180);
                    } else {
                        try {
                            int maxValue = Integer.parseInt(max);
                            eaBleMotionAlarmHr.setMax_hr(maxValue);
                        } catch (Exception e) {
                            eaBleMotionAlarmHr.setMax_hr(180);
                        }
                    }
                    String min = minText.getText().toString();
                    if (TextUtils.isEmpty(min)) {
                        eaBleMotionAlarmHr.setMax_hr(100);
                    } else {
                        try {
                            int minValue = Integer.parseInt(min);
                            eaBleMotionAlarmHr.setMin_hr(minValue);
                        } catch (Exception e) {
                            eaBleMotionAlarmHr.setMin_hr(100);
                        }
                    }
                    EABleManager.getInstance().setMotionAlarmHr(eaBleMotionAlarmHr, new GeneralCallback() {
                        @Override
                        public void result(boolean b) {
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
            switchDialog.destroyDialog();
            switchDialog = null;
        }
        if (maxDialog != null) {
            maxDialog.destroyDialog();
            maxDialog = null;
        }
        if (monthDialog != null) {
            monthDialog.destroyDialog();
            monthDialog = null;
        }

        super.onDestroy();
    }
}
