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
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.model.EABleDev;
import com.apex.sdk.R;
import com.apex.sdk.dialog.DeviceActionDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DeviceActionActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private DeviceActionDialog deviceActionDialog;
    private String tempRest;
    @BindView(R.id.rest_screen)
    TextView restText;
    @BindView(R.id.tool)
    Toolbar toolbar;
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
                int brightness = (int) msg.obj;
                restText.setText(brightness + "");
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(DeviceActionActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                restText.setText(tempRest);
                if (tempRest.equalsIgnoreCase(getString(R.string.device_action_restore)) || tempRest.equalsIgnoreCase(getString(R.string.device_action_restart))) {
                    Toast.makeText(DeviceActionActivity.this, getString(R.string.device_action_restore_hint), Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessageDelayed(0x44, 3000);
                }

            } else if (msg.what == 43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(DeviceActionActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x44) {
                System.exit(0);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_action);
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
        restText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (deviceActionDialog == null) {
                        deviceActionDialog = new DeviceActionDialog(DeviceActionActivity.this);
                        deviceActionDialog.setSelectListener(new DeviceActionDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(DeviceActionActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempRest = sex;
                                EABleDev eaBleDev = new EABleDev();
                                if (sex.equalsIgnoreCase(getString(R.string.device_action_bright_screen))) {
                                    eaBleDev.setE_ops(EABleDev.DevOps.light_up_the_screen);
                                } else if (sex.equalsIgnoreCase(getString(R.string.device_action_rest_screen))) {
                                    eaBleDev.setE_ops(EABleDev.DevOps.turn_off_the_screen);
                                } else if (sex.equalsIgnoreCase(getString(R.string.device_action_restore))) {
                                    eaBleDev.setE_ops(EABleDev.DevOps.restore_factory);
                                } else if (sex.equalsIgnoreCase(getString(R.string.device_action_restart))) {
                                    eaBleDev.setE_ops(EABleDev.DevOps.reset);
                                }
                                EABleManager.getInstance().setDeviceOps(eaBleDev, new GeneralCallback() {
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
                    if (!deviceActionDialog.isShowing()) {
                        deviceActionDialog.show();
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
        if (deviceActionDialog != null) {
            deviceActionDialog.dismiss();
            deviceActionDialog.destroyDialog();
            deviceActionDialog = null;
        }
        super.onDestroy();
    }
}
