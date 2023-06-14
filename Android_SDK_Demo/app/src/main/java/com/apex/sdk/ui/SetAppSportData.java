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
import com.apex.bluetooth.model.EABleAppSportData;
import com.apex.sdk.R;
import com.apex.sdk.dialog.DistanceDialog;
import com.apex.sdk.dialog.DurationTimeDialog;
import com.apex.sdk.dialog.PaceDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SetAppSportData extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.exercise_duration)
    TextView durationText;
    @BindView(R.id.distance)
    TextView distanceText;
    @BindView(R.id.pace)
    TextView paceText;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    DurationTimeDialog durationTimeDialog;
    DistanceDialog distanceDialog;
    PaceDialog paceDialog;
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
                    Toast.makeText(SetAppSportData.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 0x42) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(SetAppSportData.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_sport_data);
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
        durationText.setText(80 + "");
        durationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (durationTimeDialog == null) {
                    durationTimeDialog = new DurationTimeDialog(SetAppSportData.this);
                    durationTimeDialog.setSelectListener(new DurationTimeDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            durationText.setText(sex + "");
                        }
                    });
                }
                if (!durationTimeDialog.isShowing()) {
                    durationTimeDialog.show();
                }

            }
        });
        distanceText.setText(260 + "");
        distanceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distanceDialog == null) {
                    distanceDialog = new DistanceDialog(SetAppSportData.this);
                    distanceDialog.setSelectListener(new DistanceDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            distanceText.setText(sex + "");
                        }
                    });
                }
                if (!distanceDialog.isShowing()) {
                    distanceDialog.show();
                }

            }
        });
        paceText.setText(60 + "");
        paceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paceDialog == null) {
                    paceDialog = new PaceDialog(SetAppSportData.this);
                    paceDialog.setSelectListener(new PaceDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            paceText.setText(sex + "");
                        }
                    });
                }
                if (!paceDialog.isShowing()) {
                    paceDialog.show();
                }

            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String duration = durationText.getText().toString();
                String distance = distanceText.getText().toString();
                String pace = paceText.getText().toString();
                if (TextUtils.isEmpty(duration) || TextUtils.isEmpty(distance) || TextUtils.isEmpty(pace)) {
                    return;
                }
                EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
                if (state == EABleConnectState.STATE_CONNECTED) {
                    EABleAppSportData eaBleAppSportData = new EABleAppSportData();
                    eaBleAppSportData.setDistance(Integer.valueOf(distance));
                    eaBleAppSportData.setDuration(Integer.valueOf(duration));
                    eaBleAppSportData.setPace(Integer.valueOf(pace));
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(SetAppSportData.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().setData2Watch(eaBleAppSportData, new GeneralCallback() {
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
        if (durationTimeDialog != null) {
            durationTimeDialog.destroyDialog();
            durationTimeDialog = null;
        }
        if (distanceDialog != null) {
            distanceDialog.destroyDialog();
            distanceDialog = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroy();
    }
}
