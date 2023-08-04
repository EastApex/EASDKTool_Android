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
import com.apex.bluetooth.callback.GoalCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleDailyGoal;
import com.apex.sdk.R;
import com.apex.sdk.dialog.MonthDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DailyObjectivesActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.steps)
    TextView stepsText;
    @BindView(R.id.calorie)
    TextView calorieText;
    @BindView(R.id.distance)
    TextView distanceText;
    @BindView(R.id.exercise_duration)
    TextView exerciseText;
    @BindView(R.id.sleep_duration)
    TextView sleepText;
    private EABleDailyGoal eaBleDailyGoal;
    private MonthDialog monthDialog;
    private int tempSteps;
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
                eaBleDailyGoal = (EABleDailyGoal) msg.obj;
                EABleDailyGoal.EABleDaily stepDaily = eaBleDailyGoal.getS_step();
                EABleDailyGoal.EABleDaily calorieDaily = eaBleDailyGoal.getS_calorie();
                EABleDailyGoal.EABleDaily distanceDaily = eaBleDailyGoal.getS_distance();
                EABleDailyGoal.EABleDaily durationDaily = eaBleDailyGoal.getS_duration();
                EABleDailyGoal.EABleDaily sleepDaily = eaBleDailyGoal.getS_sleep();
                if (stepDaily != null) {
                    stepsText.setText(stepDaily.getGoal() + "");
                }
                if (calorieDaily != null) {
                    calorieText.setText(calorieDaily.getGoal() + "");
                }
                if (distanceDaily != null) {
                    distanceText.setText(distanceDaily.getGoal() + "");
                }
                if (durationDaily != null) {
                    exerciseText.setText(durationDaily.getGoal() + "");
                }
                if (sleepDaily != null) {
                    sleepText.setText(sleepDaily.getGoal() + "");
                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(DailyObjectivesActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                stepsText.setText(tempSteps + "");
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(DailyObjectivesActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_objectives);
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
                waitingDialog = new WaitingDialog(DailyObjectivesActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.daily_goal, new GoalCallback() {
                @Override
                public void goalInfo(EABleDailyGoal eaBleDailyGoal) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleDailyGoal;
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
        stepsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (monthDialog == null) {
                        monthDialog = new MonthDialog(DailyObjectivesActivity.this, getString(R.string.steps));
                        monthDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (eaBleDailyGoal == null) {
                                    eaBleDailyGoal = new EABleDailyGoal();
                                    EABleDailyGoal.EABleDaily stepsDaily = new EABleDailyGoal.EABleDaily();
                                    stepsDaily.setSw(1);
                                    stepsDaily.setGoal(1000);
                                    EABleDailyGoal.EABleDaily calorieDaily = new EABleDailyGoal.EABleDaily();
                                    calorieDaily.setSw(1);
                                    calorieDaily.setGoal(1000);
                                    EABleDailyGoal.EABleDaily distanceDaily = new EABleDailyGoal.EABleDaily();
                                    distanceDaily.setSw(1);
                                    distanceDaily.setGoal(1000);
                                    EABleDailyGoal.EABleDaily durationDaily = new EABleDailyGoal.EABleDaily();
                                    durationDaily.setGoal(1000);
                                    durationDaily.setSw(1);
                                    EABleDailyGoal.EABleDaily sleepDaily = new EABleDailyGoal.EABleDaily();
                                    sleepDaily.setSw(1);
                                    sleepDaily.setGoal(60 * 60 * 8);
                                    eaBleDailyGoal.setS_calorie(calorieDaily);
                                    eaBleDailyGoal.setS_step(stepsDaily);
                                    eaBleDailyGoal.setS_sleep(sleepDaily);
                                    eaBleDailyGoal.setS_distance(distanceDaily);
                                    eaBleDailyGoal.setS_duration(durationDaily);

                                }
                                EABleDailyGoal.EABleDaily stepsDaily = eaBleDailyGoal.getS_step();
                                if (stepsDaily == null) {
                                    stepsDaily = new EABleDailyGoal.EABleDaily();
                                    stepsDaily.setSw(1);
                                }
                                stepsDaily.setGoal(sex);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(DailyObjectivesActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempSteps = sex;
                                EABleManager.getInstance().setDailyGoal(eaBleDailyGoal, new GeneralCallback() {
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
                    if (!monthDialog.isShowing()) {
                        monthDialog.show();
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
        if (monthDialog != null) {
            monthDialog.dismiss();
            monthDialog.destroyDialog();
            monthDialog = null;
        }
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        super.onDestroy();
    }
}
