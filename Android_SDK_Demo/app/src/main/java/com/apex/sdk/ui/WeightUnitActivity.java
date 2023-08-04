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
import com.apex.bluetooth.callback.WeightUnitCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleWeightFormat;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.dialog.WeightUnitDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WeightUnitActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private WeightUnitDialog distanceUnitDialog;
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
                EABleWeightFormat eaBleWeightFormat = (EABleWeightFormat) msg.obj;
                if (eaBleWeightFormat != null) {
                    if (eaBleWeightFormat.getE_format() == EABleWeightFormat.WeightUnit.kilogram) {
                        restText.setText(getString(R.string.weight_unit_kilogram));
                    } else {
                        restText.setText(getString(R.string.weight_unit_pound));
                    }
                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(WeightUnitActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(WeightUnitActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_unit);
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
                waitingDialog = new WaitingDialog(WeightUnitActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.weight_unit, new WeightUnitCallback() {
                @Override
                public void weightUnitInfo(EABleWeightFormat eaBleWeightFormat) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleWeightFormat;
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
                        distanceUnitDialog = new WeightUnitDialog(WeightUnitActivity.this);
                        distanceUnitDialog.setSelectListener(new WeightUnitDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(WeightUnitActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempRest = sex;
                                EABleWeightFormat eaBleWeightFormat = new EABleWeightFormat();
                                if (sex.equalsIgnoreCase(getString(R.string.weight_unit_pound))) {
                                    eaBleWeightFormat.setE_format(EABleWeightFormat.WeightUnit.pound);
                                } else {
                                    eaBleWeightFormat.setE_format(EABleWeightFormat.WeightUnit.kilogram);
                                }
                                EABleManager.getInstance().setWeightUnit(eaBleWeightFormat, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success, int reason) {
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
