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
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.model.EABleCheckSwitch;
import com.apex.sdk.R;
import com.apex.sdk.dialog.CheckTypeDialog;
import com.apex.sdk.dialog.SwitchDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CheckActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.check_type)
    TextView typeText;
    @BindView(R.id.check_switch)
    TextView switchText;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    SwitchDialog switchDialog;
    CheckTypeDialog checkTypeDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x41) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                boolean success = (boolean) msg.obj;
                if (!success) {
                    Toast.makeText(CheckActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 0x42) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(CheckActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
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
        typeText.setText(getString(R.string.heart_Rate));
        switchText.setText(getString(R.string.switch_state_on));
        typeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkTypeDialog == null) {
                    checkTypeDialog = new CheckTypeDialog(CheckActivity.this);
                    checkTypeDialog.setSelectListener(new CheckTypeDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            typeText.setText(sex);
                        }
                    });
                }
                if (!checkTypeDialog.isShowing()) {
                    checkTypeDialog.show();
                }

            }
        });
        switchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchDialog == null) {
                    switchDialog = new SwitchDialog(CheckActivity.this);
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
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EABleConnectState eaBleConnectState = EABleManager.getInstance().getDeviceConnectState();
                if (eaBleConnectState == EABleConnectState.STATE_CONNECTED) {
                    String type = typeText.getText().toString();
                    String on = switchText.getText().toString();
                    if (TextUtils.isEmpty(type) || TextUtils.isEmpty(on)) {
                        return;
                    }
                    EABleCheckSwitch eaBleCheckSwitch = new EABleCheckSwitch();
                    if (type.equalsIgnoreCase(getString(R.string.heart_Rate))) {
                        eaBleCheckSwitch.setCheckType(EABleCheckSwitch.CheckType.hr);
                    } else if (type.equalsIgnoreCase(getString(R.string.stress))) {
                        eaBleCheckSwitch.setCheckType(EABleCheckSwitch.CheckType.stress);
                    } else if (type.equalsIgnoreCase(getString(R.string.blood_oxygen))) {
                        eaBleCheckSwitch.setCheckType(EABleCheckSwitch.CheckType.blood_oxygen);
                    } else if (type.equalsIgnoreCase(getString(R.string.breathe))) {
                        eaBleCheckSwitch.setCheckType(EABleCheckSwitch.CheckType.breathe);
                    }
                    if (on.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                        eaBleCheckSwitch.setSw(1);
                    } else {
                        eaBleCheckSwitch.setSw(0);
                    }
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(CheckActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().startOrEndCheck(eaBleCheckSwitch, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
                            Message message = new Message();
                            message.what = 0x41;
                            message.obj = success;
                            if (mHandler != null) {
                                mHandler.sendMessage(message);
                            }

                        }

                        @Override
                        public void mutualFail(int errorCode) {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x42);
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (checkTypeDialog != null) {
            checkTypeDialog.destroyDialog();
            checkTypeDialog = null;
        }
        if (switchDialog != null) {
            switchDialog.destroyDialog();
            switchDialog = null;
        }
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroy();
    }
}
