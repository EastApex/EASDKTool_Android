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
import com.apex.bluetooth.enumeration.MotionReportType;
import com.apex.sdk.R;
import com.apex.sdk.db.GreenDaoManager;
import com.apex.sdk.db.daily.DailyData;
import com.apex.sdk.db.hr.HeartData;
import com.apex.sdk.db.multi.MultiData;
import com.apex.sdk.db.oxygen.BloodData;
import com.apex.sdk.db.pressure.PressData;
import com.apex.sdk.db.resting.RestingRateData;
import com.apex.sdk.dialog.PriorityDialog;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SyncMonitorActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.priority_sync)
    TextView syncText;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.calorie)
    TextView calorieText;
    @BindView(R.id.distance)
    TextView distanceText;
    @BindView(R.id.steps)
    TextView stepsText;
    @BindView(R.id.recent_heart_rate)
    TextView heartText;
    @BindView(R.id.recent_movement)
    TextView movementText;
    @BindView(R.id.recent_blood_oxygen)
    TextView bloodText;
    @BindView(R.id.recent_pressure)
    TextView pressureText;
    @BindView(R.id.recent_resting_heart_rate)
    TextView restingText;
    private PriorityDialog priorityDialog;
    private String tempRest;
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
                syncText.setText(tempRest);
                mHandler.sendEmptyMessageDelayed(0x42, 2000);
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SyncMonitorActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                /**
                 BloodData bloodData = GreenDaoManager.getInstance().queryLastBlood();
                 if (bloodData != null) {
                 bloodText.setText(bloodData.getBlood_oxygen_value() + "");
                 }
                 HeartData heartData = GreenDaoManager.getInstance().queryLastHeart();
                 if (heartData != null) {
                 heartText.setText(heartData.hr_value + "");
                 }
                 PressData pressData = GreenDaoManager.getInstance().questLastPressure();
                 if (pressData != null) {
                 pressureText.setText(pressData.getPress_value() + "");
                 }
                 RestingRateData restingRateData = GreenDaoManager.getInstance().queryLastRestingRate();
                 if (restingRateData != null) {
                 restingText.setText(restingRateData.getHeartRate() + "");
                 }
                 MultiData multiData = GreenDaoManager.getInstance().queryLastMulti();

                 if (multiData != null) {
                 int type = multiData.getE_type();
                 if (type == 1) {
                 movementText.setText(getString(R.string.outdoor_walking));
                 } else if (type == 2) {
                 movementText.setText(getString(R.string.outdoor_running));
                 } else if (type == 3) {
                 movementText.setText(getString(R.string.outdoor_hiking));
                 } else if (type == 4) {
                 movementText.setText(getString(R.string.outdoor_mountaineering));
                 } else if (type == 5) {
                 movementText.setText(getString(R.string.outdoor_cross_country));
                 } else if (type == 6) {
                 movementText.setText(getString(R.string.outdoor_cycling));
                 } else if (type == 7) {
                 movementText.setText(getString(R.string.swimming));
                 } else if (type == 8) {
                 movementText.setText(getString(R.string.indoor_walking));
                 } else if (type == 9) {
                 movementText.setText(getString(R.string.indoor_running));
                 } else if (type == 10) {
                 movementText.setText(getString(R.string.indoor_exercise));
                 } else if (type == 11) {
                 movementText.setText(getString(R.string.indoor_cycling));
                 } else if (type == 12) {
                 movementText.setText(getString(R.string.elliptical_machine));
                 } else if (type == 13) {
                 movementText.setText(getString(R.string.yoga));
                 } else if (type == 14) {
                 movementText.setText(getString(R.string.rowing_machine));
                 }
                 }
                 int calorie = 0, distance = 0, steps = 0;
                 DailyData dailyData = GreenDaoManager.getInstance().queryLastSport();
                 if (dailyData != null) {
                 Calendar calendar = Calendar.getInstance();
                 calendar.setTimeInMillis(dailyData.getCurrentTime() * 1000);
                 calendar.set(Calendar.HOUR, 0);
                 calendar.set(Calendar.MINUTE, 0);
                 calendar.set(Calendar.SECOND, 0);
                 calendar.set(Calendar.MILLISECOND, 0);
                 long startTime = calendar.getTimeInMillis();
                 calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
                 long endTime = calendar.getTimeInMillis() - 1;
                 List<DailyData> dataList = GreenDaoManager.getInstance().batchQuerySport(startTime / 1000, endTime / 1000);

                 if (dataList != null && !dataList.isEmpty()) {
                 for (int i = 0; i < dataList.size(); i++) {
                 calorie += dataList.get(i).getCalorie();
                 distance += dataList.get(i).getDistance();
                 steps += dataList.get(i).getSteps();
                 }

                 }
                 }
                 calorieText.setText(calorie + "");
                 distanceText.setText(distance + "");
                 stepsText.setText(steps + "");
                 */
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_monitor);
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

        syncText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (priorityDialog == null) {
                        priorityDialog = new PriorityDialog(SyncMonitorActivity.this);
                        priorityDialog.setSelectListener(new PriorityDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SyncMonitorActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempRest = sex;
                                MotionReportType motionReportType = null;
                                if (sex.equalsIgnoreCase(getString(R.string.priority_sync_daily))) {
                                    motionReportType = MotionReportType.sport_data_req;
                                } else if (sex.equalsIgnoreCase(getString(R.string.priority_sync_sleep))) {
                                    motionReportType = MotionReportType.sleep_data_req;
                                } else if (sex.equalsIgnoreCase(getString(R.string.priority_sync_heart_rate))) {
                                    motionReportType = MotionReportType.hr_data_req;
                                } else if (sex.equalsIgnoreCase(getString(R.string.priority_sync_gps))) {
                                    motionReportType = MotionReportType.gps_data_req;
                                } else if (sex.equalsIgnoreCase(getString(R.string.priority_sync_multi))) {
                                    motionReportType = MotionReportType.multi_sports_data_req;
                                } else if (sex.equalsIgnoreCase(getString(R.string.priority_sync_blood))) {
                                    motionReportType = MotionReportType.blood_oxygen_data_req;
                                } else if (sex.equalsIgnoreCase(getString(R.string.priority_sync_stress))) {
                                    motionReportType = MotionReportType.pressure_data_req;
                                } else if (sex.equalsIgnoreCase(getString(R.string.priority_sync_step_freq))) {
                                    motionReportType = MotionReportType.step_freq_data_req;
                                } else if (sex.equalsIgnoreCase(getString(R.string.priority_sync_pace))) {
                                    motionReportType = MotionReportType.pace_data_req;
                                } else if (sex.equalsIgnoreCase(getString(R.string.priority_sync_resting_heart_rate))) {
                                    motionReportType = MotionReportType.resting_hr_data_req;
                                }
                                EABleManager.getInstance().requestSyncMotionData(motionReportType, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success) {
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
                        });
                    }
                    if (!priorityDialog.isShowing()) {
                        priorityDialog.show();
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

        super.onDestroy();
    }
}
