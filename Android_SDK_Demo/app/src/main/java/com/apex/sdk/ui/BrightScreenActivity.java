package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.RaiseHandBrightScreenCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleGesturesBrightScreen;
import com.apex.sdk.R;
import com.apex.sdk.dialog.HandUpDialog;
import com.apex.sdk.dialog.NotDisturbDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BrightScreenActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private HandUpDialog ageDialog;
    private NotDisturbDialog startDialog;
    private NotDisturbDialog endDialog;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private String tempRest;
    @BindView(R.id.screen_brightness)
    TextView restText;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.start_time)
    TextView startView;
    @BindView(R.id.end_time)
    TextView endView;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
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
                EABleGesturesBrightScreen brightness = (EABleGesturesBrightScreen) msg.obj;
                if (brightness != null) {
                    EABleGesturesBrightScreen.BrightScreenSwitch brightScreenSwitch = brightness.getBrightScreenSwitch();
                    if (brightScreenSwitch != null) {
                        if (brightScreenSwitch == EABleGesturesBrightScreen.BrightScreenSwitch.all_day) {
                            restText.setText(getString(R.string.Open_all_day));
                            // startView.setVisibility(View.GONE);
                            // endView.setVisibility(View.GONE);
                        } else if (brightScreenSwitch == EABleGesturesBrightScreen.BrightScreenSwitch.select_time) {
                            restText.setText(getString(R.string.Select_period_on));
                            // startView.setVisibility(View.VISIBLE);
                            // endView.setVisibility(View.VISIBLE);
                        } else {
                            restText.setText(getString(R.string.switch_state_close));
                            // startView.setVisibility(View.VISIBLE);
                            // endView.setVisibility(View.VISIBLE);
                        }
                    }
                    startHour = brightness.getBegin_hour();
                    startMinute = brightness.getBegin_minute();
                    endHour = brightness.getEnd_hour();
                    endMinute = brightness.getEnd_minute();
                    startView.setText((startHour < 10 ? "0" + startHour : startHour + "") + ":" + (startMinute < 10 ? "0" + startMinute : startMinute + ""));
                    endView.setText((endHour < 10 ? "0" + endHour : endHour + "") + ":" + (endMinute < 10 ? "0" + endMinute : endMinute + ""));
                }

            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(BrightScreenActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                Log.e(TAG, "成功");
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                restText.setText(tempRest);
            } else if (msg.what == 0x43) {
                Log.e(TAG, "失败");
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(BrightScreenActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_brightness);
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
                waitingDialog = new WaitingDialog(BrightScreenActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.gestures, new RaiseHandBrightScreenCallback() {
                @Override
                public void switchInfo(EABleGesturesBrightScreen eaBleGesturesBrightScreen) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleGesturesBrightScreen;
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
                    if (ageDialog == null) {
                        ageDialog = new HandUpDialog(BrightScreenActivity.this);
                        ageDialog.setSelectListener(new HandUpDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                restText.setText(sex);
                                tempRest = sex;

                            }
                        });
                    }
                    if (!ageDialog.isShowing()) {
                        ageDialog.show();
                    }
                }
            }
        });
        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDialog == null) {
                    startDialog = new NotDisturbDialog(BrightScreenActivity.this, getString(R.string.start_time));
                    startDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                        @Override
                        public void selectData(int hour, int minute) {
                            if (endHour > 0) {
                                if (endHour > hour) {
                                    startHour = hour;
                                    startMinute = minute;
                                    startView.setText((startHour < 10 ? "0" + startHour : startHour + "") + ":" + (startMinute < 10 ? "0" + startMinute : startMinute + ""));
                                } else if (endHour == hour) {
                                    if (minute < endMinute) {
                                        startHour = hour;
                                        startMinute = minute;
                                        startView.setText((startHour < 10 ? "0" + startHour : startHour + "") + ":" + (startMinute < 10 ? "0" + startMinute : startMinute + ""));
                                    } else {
                                        Toast.makeText(BrightScreenActivity.this, getString(R.string.Time_hint), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(BrightScreenActivity.this, getString(R.string.Time_hint), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (endMinute > 0) {
                                    if (hour > 0) {
                                        Toast.makeText(BrightScreenActivity.this, getString(R.string.Time_hint), Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (minute < endMinute) {
                                            startHour = hour;
                                            startMinute = minute;
                                            startView.setText((startHour < 10 ? "0" + startHour : startHour + "") + ":" + (startMinute < 10 ? "0" + startMinute : startMinute + ""));
                                        } else {
                                            Toast.makeText(BrightScreenActivity.this, getString(R.string.Time_hint), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    startHour = hour;
                                    startMinute = minute;
                                    startView.setText((startHour < 10 ? "0" + startHour : startHour + "") + ":" + (startMinute < 10 ? "0" + startMinute : startMinute + ""));
                                }
                            }

                        }
                    });
                }
                startDialog.show();
            }
        });
        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (endDialog == null) {
                    endDialog = new NotDisturbDialog(BrightScreenActivity.this, getString(R.string.end_time));
                    endDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                        @Override
                        public void selectData(int hour, int minute) {
                            if (startHour > 0) {
                                if (hour > startHour) {
                                    endHour = hour;
                                    endMinute = minute;
                                    endView.setText((endHour < 10 ? "0" + endHour : endHour + "") + ":" + (endMinute < 10 ? "0" + endMinute : endMinute + ""));
                                } else if (hour == startHour) {
                                    if (minute > startMinute) {
                                        endMinute = minute;
                                        endHour = hour;
                                        endView.setText((endHour < 10 ? "0" + endHour : endHour + "") + ":" + (endMinute < 10 ? "0" + endMinute : endMinute + ""));
                                    } else {
                                        Toast.makeText(BrightScreenActivity.this, getString(R.string.Time_hint), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(BrightScreenActivity.this, getString(R.string.Time_hint), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (startMinute > 0) {
                                    if (hour > 0) {
                                        endHour = hour;
                                        endMinute = minute;
                                        endView.setText((endHour < 10 ? "0" + endHour : endHour + "") + ":" + (endMinute < 10 ? "0" + endMinute : endMinute + ""));
                                    } else {
                                        if (minute > startMinute) {
                                            endMinute = minute;
                                            endHour = hour;
                                            endView.setText((endHour < 10 ? "0" + endHour : endHour + "") + ":" + (endMinute < 10 ? "0" + endMinute : endMinute + ""));
                                        } else {
                                            Toast.makeText(BrightScreenActivity.this, getString(R.string.Time_hint), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    endHour = hour;
                                    endMinute = minute;
                                    endView.setText((endHour < 10 ? "0" + endHour : endHour + "") + ":" + (endMinute < 10 ? "0" + endMinute : endMinute + ""));
                                }
                            }

                        }
                    });
                }
                endDialog.show();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    EABleGesturesBrightScreen eaBleGesturesBrightScreen = new EABleGesturesBrightScreen();
                    String stateText = restText.getText().toString();
                    if (TextUtils.isEmpty(stateText)) {
                        Log.e(TAG, "抬手亮屏类型不存在");
                        return;
                    }
                    if (stateText.equalsIgnoreCase(getString(R.string.switch_state_close))) {
                        eaBleGesturesBrightScreen.setBrightScreenSwitch(EABleGesturesBrightScreen.BrightScreenSwitch.close);
                    } else if (stateText.equalsIgnoreCase(getString(R.string.Open_all_day))) {
                        eaBleGesturesBrightScreen.setBrightScreenSwitch(EABleGesturesBrightScreen.BrightScreenSwitch.all_day);
                    } else {
                        eaBleGesturesBrightScreen.setBrightScreenSwitch(EABleGesturesBrightScreen.BrightScreenSwitch.select_time);
                    }
                    eaBleGesturesBrightScreen.setBegin_hour(startHour);
                    eaBleGesturesBrightScreen.setBegin_minute(startMinute);
                    eaBleGesturesBrightScreen.setEnd_hour(endHour);
                    eaBleGesturesBrightScreen.setEnd_minute(endMinute);
                    waitingDialog.show();
                    EABleManager.getInstance().setGesturesSwitch(eaBleGesturesBrightScreen, new GeneralCallback() {
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
        if (ageDialog != null) {
            ageDialog.dismiss();
            ageDialog.destroyDialog();
            ageDialog = null;
        }
        if (startDialog != null) {
            startDialog.destroyDialog();
            startDialog = null;
        }
        if (endDialog != null) {
            endDialog.destroyDialog();
            endDialog = null;
        }
        super.onDestroy();
    }

}
