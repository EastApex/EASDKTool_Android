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
import com.apex.bluetooth.callback.VibrateCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.enumeration.VibrationIntensity;
import com.apex.sdk.R;
import com.apex.sdk.dialog.VibrationModeDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VibrateModeActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.rest_screen)
    TextView switchText;
    VibrationModeDialog vibrationModeDialog;
    String mode;
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
                switchText.setText(mode);
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(VibrateModeActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VibrateModeActivity.this, getString(R.string.add_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrate_mode);
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
                waitingDialog = new WaitingDialog(VibrateModeActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.vibrate_mode, new VibrateCallback() {
                @Override
                public void vibrateMode(VibrationIntensity vibrationIntensity) {
                    if (vibrationIntensity != null) {
                        if (vibrationIntensity == VibrationIntensity.light) {
                            mode = getString(R.string.vibration_mode_week);
                        } else if (vibrationIntensity == VibrationIntensity.medium) {
                            mode = getString(R.string.vibration_mode_normal);
                        } else if (vibrationIntensity == VibrationIntensity.strong) {
                            mode = getString(R.string.vibration_mode_strong);
                        } else {
                            mode = getString(R.string.vibration_mode_not_vibrate);
                        }
                    }
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x40);
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
                if (vibrationModeDialog == null) {
                    vibrationModeDialog = new VibrationModeDialog(VibrateModeActivity.this);
                    vibrationModeDialog.setSelectListener(new VibrationModeDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            switchText.setText(sex);
                            mode = sex;
                            if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(VibrateModeActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                VibrationIntensity vibrationIntensity;
                                if (sex.equalsIgnoreCase(getString(R.string.vibration_mode_not_vibrate))) {
                                    vibrationIntensity = VibrationIntensity.not_vibrate;
                                } else if (sex.equalsIgnoreCase(getString(R.string.vibration_mode_strong))) {
                                    vibrationIntensity = VibrationIntensity.strong;
                                } else if (sex.equalsIgnoreCase(getString(R.string.vibration_mode_week))) {
                                    vibrationIntensity = VibrationIntensity.light;
                                } else {
                                    vibrationIntensity = VibrationIntensity.medium;
                                }
                                EABleManager.getInstance().setVibrateMode(vibrationIntensity, new GeneralCallback() {
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
                        }
                    });
                }
                if (!vibrationModeDialog.isShowing()) {
                    vibrationModeDialog.show();
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
        if (vibrationModeDialog != null) {
            vibrationModeDialog.destroyDialog();
            vibrationModeDialog = null;
        }
        super.onDestroy();
    }
}
