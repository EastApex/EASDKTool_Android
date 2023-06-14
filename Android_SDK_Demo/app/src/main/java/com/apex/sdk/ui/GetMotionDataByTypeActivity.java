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
import com.apex.bluetooth.model.EABleDataType;
import com.apex.sdk.R;
import com.apex.sdk.dialog.MotionTypeDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GetMotionDataByTypeActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.data_type)
    TextView typeText;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    MotionTypeDialog motionTypeDialog;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x41) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                boolean success = (boolean) msg.obj;
                if (!success) {
                    Toast.makeText(GetMotionDataByTypeActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 0x42) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(GetMotionDataByTypeActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_with_type);
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
        typeText.setText(getString(R.string.Today_steps));
        typeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (motionTypeDialog == null) {
                    motionTypeDialog = new MotionTypeDialog(GetMotionDataByTypeActivity.this);
                    motionTypeDialog.setSelectListener(new MotionTypeDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            typeText.setText(sex);
                        }
                    });
                }
                if (!motionTypeDialog.isShowing()) {
                    motionTypeDialog.show();
                }

            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = typeText.getText().toString();
                if (TextUtils.isEmpty(type)) {
                    return;
                }
                EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
                if (state == EABleConnectState.STATE_CONNECTED) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(GetMotionDataByTypeActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleDataType eaBleDataType = EABleDataType.default_data;
                    if (type.equalsIgnoreCase(getString(R.string.Today_steps))) {
                        eaBleDataType = EABleDataType.steps_data;
                    } else if (type.equalsIgnoreCase(getString(R.string.sleep))) {
                        eaBleDataType = EABleDataType.sleep_data;
                    } else if (type.equalsIgnoreCase(getString(R.string.heart_Rate))) {
                        eaBleDataType = EABleDataType.hr_data;
                    } else if (type.equalsIgnoreCase(getString(R.string.gps))) {
                        eaBleDataType = EABleDataType.gps_data;
                    } else if (type.equalsIgnoreCase(getString(R.string.multi_sport))) {
                        eaBleDataType = EABleDataType.multi_sports_data;
                    } else if (type.equalsIgnoreCase(getString(R.string.blood_oxygen))) {
                        eaBleDataType = EABleDataType.blood_oxygen_data_data;
                    } else if (type.equalsIgnoreCase(getString(R.string.stress))) {
                        eaBleDataType = EABleDataType.stress_data;
                    } else if (type.equalsIgnoreCase(getString(R.string.step_freq))) {
                        eaBleDataType = EABleDataType.step_freq_data;
                    } else if (type.equalsIgnoreCase(getString(R.string.pace))) {
                        eaBleDataType = EABleDataType.step_pace_data;
                    } else if (type.equalsIgnoreCase(getString(R.string.resting_heart_rate))) {
                        eaBleDataType = EABleDataType.resting_hr_data;
                    } else if (type.equalsIgnoreCase(getString(R.string.habit))) {
                        eaBleDataType = EABleDataType.habit_tracker_data;
                    }
                    EABleManager.getInstance().getMotionDataWithType(eaBleDataType, new GeneralCallback() {
                        @Override
                        public void result(boolean success) {
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
        if (motionTypeDialog != null) {
            motionTypeDialog.destroyDialog();
            motionTypeDialog = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        super.onDestroy();
    }
}
