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

import com.apex.bluetooth.callback.DistanceUnitCallback;
import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleDistanceFormat;
import com.apex.sdk.R;
import com.apex.sdk.dialog.DistanceUnitDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DistanceUnitActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private DistanceUnitDialog distanceUnitDialog;
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
                EABleDistanceFormat eaBleDistanceFormat = (EABleDistanceFormat) msg.obj;
                if (eaBleDistanceFormat != null) {
                    if (eaBleDistanceFormat.getE_format() == EABleDistanceFormat.DistanceUnit.kilometre) {
                        restText.setText(getString(R.string.distance_unit_kilometre));
                    } else {
                        restText.setText(getString(R.string.distance_unit_mile));
                    }
                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(DistanceUnitActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                restText.setText(tempRest);
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(DistanceUnitActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_unit);
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
                waitingDialog = new WaitingDialog(DistanceUnitActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.distance_unit, new DistanceUnitCallback() {
                @Override
                public void distanceUnitInfo(EABleDistanceFormat eaBleDistanceFormat) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleDistanceFormat;
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
        restText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (distanceUnitDialog == null) {
                        distanceUnitDialog = new DistanceUnitDialog(DistanceUnitActivity.this);
                        distanceUnitDialog.setSelectListener(new DistanceUnitDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(DistanceUnitActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempRest = sex;
                                EABleDistanceFormat eaBleDistanceFormat = new EABleDistanceFormat();
                                if (sex.equalsIgnoreCase(getString(R.string.distance_unit_mile))) {
                                    eaBleDistanceFormat.setE_format(EABleDistanceFormat.DistanceUnit.mile);
                                } else {
                                    eaBleDistanceFormat.setE_format(EABleDistanceFormat.DistanceUnit.kilometre);
                                }
                                EABleManager.getInstance().setDistanceUnit(eaBleDistanceFormat, new GeneralCallback() {
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
                    if (!distanceUnitDialog.isShowing()) {
                        distanceUnitDialog.show();
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
        if (distanceUnitDialog != null) {
            distanceUnitDialog.dismiss();
            distanceUnitDialog.destroyDialog();
            distanceUnitDialog = null;
        }
        super.onDestroy();
    }
}
