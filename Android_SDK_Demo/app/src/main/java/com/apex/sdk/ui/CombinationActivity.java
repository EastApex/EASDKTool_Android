package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.CombinationCallback;
import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.BatInfoStatus;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.PersonHand;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.enumeration.UnitFormat;
import com.apex.bluetooth.enumeration.VibrationIntensity;
import com.apex.bluetooth.model.EABleCombination;
import com.apex.sdk.R;
import com.apex.sdk.dialog.DeviceUnitDialog;
import com.apex.sdk.dialog.SwitchDialog;
import com.apex.sdk.dialog.VibrationModeDialog;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.dialog.WearDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CombinationActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private SwitchDialog pressureDialog, sedentaryDialog, screenDialog, rateDialog, disturbDialog;
    private DeviceUnitDialog deviceUnitDialog;
    private VibrationModeDialog vibrationModeDialog;
    private WearDialog wearDialog;
    @BindView(R.id.state_of_charge)
    TextView chargeText;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.electric_quantity)
    TextView electricText;
    @BindView(R.id.pressure_switch)
    TextView pressureText;
    @BindView(R.id.sedentary_monitoring)
    TextView sedentaryText;
    @BindView(R.id.bright_screen_switch)
    TextView screenText;
    @BindView(R.id.vibration_mode)
    TextView modeText;
    @BindView(R.id.wearing_habits)
    TextView wearText;
    @BindView(R.id.equipment_unit)
    TextView unitText;
    @BindView(R.id.heart_rate_monitoring)
    TextView rateText;
    @BindView(R.id.do_not_disturb)
    TextView disturbText;
    @BindView(R.id.dial_ID)
    TextView dialText;
    @BindView(R.id.event_switch)
    Switch eventSwitch;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    private EABleCombination eaBleCombination;
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
                eaBleCombination = (EABleCombination) msg.obj;
                BatInfoStatus batInfoStatus = eaBleCombination.getE_status();
                if (batInfoStatus != null) {
                    if (batInfoStatus == BatInfoStatus.in_charging) {
                        chargeText.setText(getString(R.string.charging));
                    } else {
                        chargeText.setText(getString(R.string.normal));
                    }
                }
                electricText.setText(eaBleCombination.getBat_level() + "");
                if (eaBleCombination.getAuto_pressure_sw() == 0) {
                    pressureText.setText(getString(R.string.switch_state_close));
                } else {
                    pressureText.setText(getString(R.string.switch_state_on));
                }
                if (eaBleCombination.getGestures_sw() == 0) {
                    screenText.setText(getString(R.string.switch_state_close));
                } else {
                    screenText.setText(getString(R.string.switch_state_on));
                }
                if (eaBleCombination.getAuto_sedentariness_sw() == 0) {
                    sedentaryText.setText(getString(R.string.switch_state_close));
                } else {
                    sedentaryText.setText(getString(R.string.switch_state_on));
                }
                VibrationIntensity vibrationIntensity = eaBleCombination.getE_vibrate_intensity();
                if (vibrationIntensity != null) {
                    if (vibrationIntensity == VibrationIntensity.light) {
                        modeText.setText(getString(R.string.vibration_mode_week));
                    } else if (vibrationIntensity == VibrationIntensity.medium) {
                        modeText.setText(getString(R.string.vibration_mode_normal));
                    } else if (vibrationIntensity == VibrationIntensity.strong) {
                        modeText.setText(getString(R.string.vibration_mode_strong));
                    } else {
                        modeText.setText(getString(R.string.vibration_mode_not_vibrate));
                    }
                }
                PersonHand personHand = eaBleCombination.getE_hand_info();
                if (personHand != null) {
                    if (personHand == PersonHand.left) {
                        wearText.setText(getString(R.string.left));
                    } else {
                        wearText.setText(getString(R.string.right));
                    }
                }
                UnitFormat unitFormat = eaBleCombination.getE_unit_format();
                if (unitFormat != null) {
                    if (unitFormat == UnitFormat.british) {
                        unitText.setText(getString(R.string.device_unit_imperial));
                    } else {
                        unitText.setText(getString(R.string.device_unit_metric));
                    }
                }
                if (eaBleCombination.getAuto_check_hr_sw() == 0) {
                    rateText.setText(getString(R.string.switch_state_close));
                } else {
                    rateText.setText(getString(R.string.switch_state_on));
                }
                if (eaBleCombination.getNot_disturb_sw() == 0) {
                    disturbText.setText(getString(R.string.switch_state_close));
                } else {
                    disturbText.setText(getString(R.string.switch_state_on));
                }
                dialText.setText(eaBleCombination.getWf_id() > 0 ? (eaBleCombination.getWf_id() + "") : eaBleCombination.getUser_wf_id());
                eventSwitch.setChecked(eaBleCombination.getSet_vibrate_intensity() == 0 ? false : true);
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(CombinationActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CombinationActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combination);
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
                waitingDialog = new WaitingDialog(CombinationActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.combination, new CombinationCallback() {
                @Override
                public void combinationInfo(EABleCombination eaBleCombination) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleCombination;
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
        pressureText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pressureDialog == null) {
                    pressureDialog = new SwitchDialog(CombinationActivity.this);
                    pressureDialog.setSelectListener(new SwitchDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            pressureText.setText(sex);
                        }
                    });
                }
                if (!pressureDialog.isShowing()) {
                    pressureDialog.show();
                }

            }
        });
        sedentaryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sedentaryDialog == null) {
                    sedentaryDialog = new SwitchDialog(CombinationActivity.this);
                    sedentaryDialog.setSelectListener(new SwitchDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            sedentaryText.setText(sex);
                        }
                    });
                }
                if (!sedentaryDialog.isShowing()) {
                    sedentaryDialog.show();
                }

            }
        });
        screenText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screenDialog == null) {
                    screenDialog = new SwitchDialog(CombinationActivity.this);
                    screenDialog.setSelectListener(new SwitchDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            screenText.setText(sex);
                        }
                    });
                }
                if (!screenDialog.isShowing()) {
                    screenDialog.show();
                }
            }
        });
        modeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrationModeDialog == null) {
                    vibrationModeDialog = new VibrationModeDialog(CombinationActivity.this);
                    vibrationModeDialog.setSelectListener(new VibrationModeDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            modeText.setText(sex);
                        }
                    });
                }
                if (!vibrationModeDialog.isShowing()) {
                    vibrationModeDialog.show();
                }
            }
        });
        wearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wearDialog == null) {
                    wearDialog = new WearDialog(CombinationActivity.this);
                    wearDialog.setSelectListener(new WearDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            wearText.setText(sex);
                        }
                    });
                }
                if (!wearDialog.isShowing()) {
                    wearDialog.show();
                }
            }
        });
        unitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceUnitDialog == null) {
                    deviceUnitDialog = new DeviceUnitDialog(CombinationActivity.this);
                    deviceUnitDialog.setSelectListener(new DeviceUnitDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            unitText.setText(sex);
                        }
                    });
                }
                if (!deviceUnitDialog.isShowing()) {
                    deviceUnitDialog.show();
                }
            }
        });
        rateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rateDialog == null) {
                    rateDialog = new SwitchDialog(CombinationActivity.this);
                    rateDialog.setSelectListener(new SwitchDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            rateText.setText(sex);
                        }
                    });
                }
                if (!rateDialog.isShowing()) {
                    rateDialog.show();
                }

            }
        });
        disturbText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disturbDialog == null) {
                    disturbDialog = new SwitchDialog(CombinationActivity.this);
                    disturbDialog.setSelectListener(new SwitchDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            disturbText.setText(sex);
                        }
                    });
                }
                if (!disturbDialog.isShowing()) {
                    disturbDialog.show();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (eaBleCombination == null) {
                        eaBleCombination = new EABleCombination();
                    }
                    String chargeString = chargeText.getText().toString();
                    if (!TextUtils.isEmpty(chargeString)) {
                        if (chargeString.equalsIgnoreCase(getString(R.string.charging))) {
                            eaBleCombination.setE_status(BatInfoStatus.in_charging);
                        } else {
                            eaBleCombination.setE_status(BatInfoStatus.normal);
                        }
                    }
                    String batterLevel = electricText.getText().toString();
                    if (!TextUtils.isEmpty(batterLevel)) {
                        try {
                            eaBleCombination.setBat_level(Integer.parseInt(batterLevel));
                        } catch (Exception e) {

                        }
                    }
                    String pressureSwitch = pressureText.getText().toString();
                    if (!TextUtils.isEmpty(pressureSwitch)) {
                        if (pressureSwitch.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                            eaBleCombination.setAuto_pressure_sw(1);
                        } else {
                            eaBleCombination.setAuto_pressure_sw(0);
                        }
                    }
                    String sedentarySwitch = sedentaryText.getText().toString();
                    if (!TextUtils.isEmpty(sedentarySwitch)) {
                        if (sedentarySwitch.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                            eaBleCombination.setAuto_sedentariness_sw(1);
                        } else {
                            eaBleCombination.setAuto_sedentariness_sw(0);
                        }
                    }
                    String screenSwitch = screenText.getText().toString();
                    if (!TextUtils.isEmpty(screenSwitch)) {
                        if (screenSwitch.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                            eaBleCombination.setGestures_sw(1);
                        } else {
                            eaBleCombination.setGestures_sw(0);
                        }
                    }
                    String mode = modeText.getText().toString();
                    if (!TextUtils.isEmpty(mode)) {
                        if (mode.equalsIgnoreCase(getString(R.string.vibration_mode_strong))) {
                            eaBleCombination.setE_vibrate_intensity(VibrationIntensity.strong);
                        } else if (mode.equalsIgnoreCase(getString(R.string.vibration_mode_normal))) {
                            eaBleCombination.setE_vibrate_intensity(VibrationIntensity.medium);
                        } else if (mode.equalsIgnoreCase(getString(R.string.vibration_mode_week))) {
                            eaBleCombination.setE_vibrate_intensity(VibrationIntensity.light);
                        } else {
                            eaBleCombination.setE_vibrate_intensity(VibrationIntensity.not_vibrate);
                        }
                    }
                    String wear = wearText.getText().toString();
                    if (!TextUtils.isEmpty(wear)) {
                        if (wear.equalsIgnoreCase(getString(R.string.left))) {
                            eaBleCombination.setE_hand_info(PersonHand.left);
                        } else {
                            eaBleCombination.setE_hand_info(PersonHand.right);
                        }
                    }
                    String unit = unitText.getText().toString();
                    if (!TextUtils.isEmpty(unit)) {
                        if (unit.equalsIgnoreCase(getString(R.string.device_unit_metric))) {
                            eaBleCombination.setE_unit_format(UnitFormat.metric);
                        } else {
                            eaBleCombination.setE_unit_format(UnitFormat.british);
                        }
                    }
                    String rate = rateText.getText().toString();
                    if (!TextUtils.isEmpty(rate)) {
                        if (rate.equalsIgnoreCase(getString(R.string.switch_state_close))) {
                            eaBleCombination.setAuto_check_hr_sw(0);
                        } else {
                            eaBleCombination.setAuto_check_hr_sw(1);
                        }
                    }
                    String disturb = disturbText.getText().toString();
                    if (!TextUtils.isEmpty(disturb)) {
                        if (disturb.equalsIgnoreCase(getString(R.string.switch_state_close))) {
                            eaBleCombination.setNot_disturb_sw(0);
                        } else {
                            eaBleCombination.setNot_disturb_sw(1);
                        }
                    }
                    eaBleCombination.setSet_vibrate_intensity(eventSwitch.isChecked() ? 1 : 0);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(CombinationActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    /**
                    EABleManager.getInstance().setCombination(eaBleCombination, new GeneralCallback() {
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
                     */
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
        if (pressureDialog != null) {
            pressureDialog.dismiss();
            pressureDialog.destroyDialog();
            pressureDialog = null;
        }
        if (sedentaryDialog != null) {
            sedentaryDialog.dismiss();
            sedentaryDialog.destroyDialog();
            sedentaryDialog = null;
        }
        if (screenDialog != null) {
            screenDialog.dismiss();
            screenDialog.destroyDialog();
            screenDialog = null;
        }
        if (rateDialog != null) {
            rateDialog.dismiss();
            rateDialog.destroyDialog();
            rateDialog = null;
        }
        if (disturbDialog != null) {
            disturbDialog.dismiss();
            disturbDialog.destroyDialog();
            disturbDialog = null;
        }
        if (deviceUnitDialog != null) {
            deviceUnitDialog.dismiss();
            deviceUnitDialog.destroyDialog();
            deviceUnitDialog = null;
        }
        if (vibrationModeDialog != null) {
            vibrationModeDialog.dismiss();
            vibrationModeDialog.destroyDialog();
            vibrationModeDialog = null;
        }
        if (wearDialog != null) {
            wearDialog.dismiss();
            wearDialog.destroyDialog();
            wearDialog = null;
        }

        super.onDestroy();
    }
}
