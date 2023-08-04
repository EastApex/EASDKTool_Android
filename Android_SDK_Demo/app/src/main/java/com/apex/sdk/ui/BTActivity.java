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

import com.apex.bluetooth.callback.BtStatusCallback;
import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.VibrateCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.enumeration.VibrationIntensity;
import com.apex.sdk.R;
import com.apex.sdk.dialog.SwitchDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BTActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.bt_status)
    TextView statusView;
    SwitchDialog switchDialog;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x40) {
                if (waitingDialog != null) {
                    waitingDialog.dismiss();
                }
                int status = msg.arg1;
                if (status <= 0) {
                    statusView.setText(getString(R.string.switch_state_close));
                } else {
                    statusView.setText(getString(R.string.switch_state_on));
                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(BTActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
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
                if (msg.arg1 == 4) {
                    Toast.makeText(BTActivity.this, getString(R.string.bt_fail_1), Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == 5) {
                    Toast.makeText(BTActivity.this, getString(R.string.bt_fail_2), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BTActivity.this, getString(R.string.bt_fail_3), Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 0x44) {
                Toast.makeText(BTActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
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
                waitingDialog = new WaitingDialog(BTActivity.this);
            }
            waitingDialog.show();

            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.bt_status, new BtStatusCallback() {
                @Override
                public void btStatus(int i) {
                    if (mHandler != null) {
                        Message message = new Message();
                        message.what = 0x40;
                        message.arg1 = i;
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
        statusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchDialog == null) {
                    switchDialog = new SwitchDialog(BTActivity.this);
                    switchDialog.setSelectListener(new SwitchDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            statusView.setText(sex);
                            if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(BTActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                int status;
                                if (sex.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                                    status = 1;
                                } else {
                                    status = 0;
                                }
                                EABleManager.getInstance().setBTSwitch(status, new GeneralCallback() {
                                    @Override
                                    public void result(boolean b, int i) {
                                        if (b) {
                                            if (mHandler != null) {
                                                mHandler.sendEmptyMessage(0x42);
                                            }
                                        } else {
                                            if (mHandler != null) {
                                                Message message = new Message();
                                                message.what = 0x43;
                                                message.arg1 = i;
                                                mHandler.sendEmptyMessage(0x43);
                                            }
                                        }
                                    }

                                    @Override
                                    public void mutualFail(int i) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x44);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                if (!switchDialog.isShowing()) {
                    switchDialog.show();
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

        super.onDestroy();
    }
}
