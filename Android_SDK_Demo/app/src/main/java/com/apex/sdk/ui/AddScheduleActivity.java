package com.apex.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.EditAttentionCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.CommonAction;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.model.EABleRemindRespond;
import com.apex.bluetooth.model.EABleReminder;
import com.apex.sdk.R;
import com.apex.sdk.dialog.InputDialog;
import com.apex.sdk.dialog.NotDisturbDialog;
import com.apex.sdk.dialog.RemindDialog;
import com.apex.sdk.dialog.TypeDialog;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddScheduleActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.alarm_switch)
    Switch aSwitch;
    @BindView(R.id.schedule_type)
    TextView typeText;
    @BindView(R.id.schedule_name)
    TextView nameText;
    @BindView(R.id.cycle)
    TextView cycleText;
    @BindView(R.id.layout_custom)
    TableRow customRaw;
    @BindView(R.id.remind_time)
    TextView timeText;
    @BindView(R.id.reminder_method)
    TextView modeText;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    private final int CYCLE_CODE = 0xFF;
    private NotDisturbDialog timeDialog;
    private TypeDialog typeDialog;
    private RemindDialog remindDialog;
    private String tempMode;
    private int alarmTimeHour, alarmTimeMinute, cycleTime;
    private String mContent, typeString;
    private InputDialog inputDialog;
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
                finish();
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(AddScheduleActivity.this, getString(R.string.add_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
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
        cycleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddScheduleActivity.this, CycleActivity.class), CYCLE_CODE);
            }
        });
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeDialog == null) {
                    timeDialog = new NotDisturbDialog(AddScheduleActivity.this, getString(R.string.alarm_clock));
                    timeDialog.setSelectListener(new NotDisturbDialog.SelectListener() {
                        @Override
                        public void selectData(int hour, int minute) {
                            timeText.setText(hour + ":" + minute);
                            alarmTimeHour = hour;
                            alarmTimeMinute = minute;
                        }
                    });
                }
                if (!timeDialog.isShowing()) {
                    timeDialog.show();
                }
            }
        });
        typeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeDialog == null) {
                    typeDialog = new TypeDialog(AddScheduleActivity.this);
                    typeDialog.setSelectListener(new TypeDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            typeString = sex;
                            typeText.setText(sex);
                            if (sex.equalsIgnoreCase(getString(R.string.custom))) {
                                customRaw.setVisibility(View.VISIBLE);
                            } else {
                                customRaw.setVisibility(View.GONE);
                                mContent = null;
                            }
                        }
                    });
                }
                if (!typeDialog.isShowing()) {
                    typeDialog.show();
                }


            }
        });
        nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputDialog == null) {
                    inputDialog = new InputDialog(AddScheduleActivity.this);
                    inputDialog.setInputDataListener(new InputDialog.InputDataListener() {
                        @Override
                        public void inputData(String data) {
                            nameText.setText(data);
                            mContent = data;

                        }
                    });
                }
                if (!inputDialog.isShowing()) {
                    inputDialog.show();
                }
            }
        });
        modeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remindDialog == null) {
                    remindDialog = new RemindDialog(AddScheduleActivity.this);
                    remindDialog.setSelectListener(new RemindDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            tempMode = sex;
                            modeText.setText(sex);
                        }
                    });
                }
                if (!remindDialog.isShowing()) {
                    remindDialog.show();
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (TextUtils.isEmpty(typeString) || (typeString.equalsIgnoreCase(getString(R.string.custom)) && TextUtils.isEmpty(mContent))) {
                        return;
                    }
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(AddScheduleActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleReminder eaBleReminder = new EABleReminder();
                    eaBleReminder.setId(0);
                    eaBleReminder.setE_ops(EABleReminder.ReminderOps.add);
                    EABleReminder.EABleReminderItem eaBleReminderItem = new EABleReminder.EABleReminderItem();
                    eaBleReminderItem.setSw(aSwitch.isChecked() ? 1 : 0);
                    eaBleReminderItem.setYear(Calendar.getInstance().get(Calendar.YEAR));
                    eaBleReminderItem.setMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
                    eaBleReminderItem.setDay(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
                    eaBleReminderItem.setWeek_cycle_bit(cycleTime);
                    if (typeString.equalsIgnoreCase(getString(R.string.sleep))) {
                        eaBleReminderItem.setE_type(EABleReminder.ReminderType.sleep);
                    } else if (typeString.equalsIgnoreCase(getString(R.string.custom))) {
                        eaBleReminderItem.setE_type(EABleReminder.ReminderType.user);
                        eaBleReminderItem.setContent(mContent);
                    } else if (typeString.equalsIgnoreCase(getString(R.string.sport))) {
                        eaBleReminderItem.setE_type(EABleReminder.ReminderType.sport);
                    } else if (typeString.equalsIgnoreCase(getString(R.string.drinking))) {
                        eaBleReminderItem.setE_type(EABleReminder.ReminderType.drink);
                    } else if (typeString.equalsIgnoreCase(getString(R.string.takeTheMedicine))) {
                        eaBleReminderItem.setE_type(EABleReminder.ReminderType.medicine);
                    } else if (typeString.equalsIgnoreCase(getString(R.string.meeting))) {
                        eaBleReminderItem.setE_type(EABleReminder.ReminderType.meeting);
                    }

                    // eaBleReminderItem.setSec_sw(1);
                    if (tempMode.equalsIgnoreCase(getString(R.string.common_action_no_action))) {
                        eaBleReminderItem.setE_action(CommonAction.no_action);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_one_long_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.one_long_vibration);

                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_one_short_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.one_short_vibration);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_two_long_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.two_long_vibration);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_two_short_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.two_short_vibration);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_long_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.long_vibration);

                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_long_short_vibration))) {
                        eaBleReminderItem.setE_action(CommonAction.long_short_vibration);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_one_ring))) {
                        eaBleReminderItem.setE_action(CommonAction.one_ring);

                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_two_ring))) {
                        eaBleReminderItem.setE_action(CommonAction.two_ring);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_ring))) {
                        eaBleReminderItem.setE_action(CommonAction.ring);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_one_vibration_ring))) {
                        eaBleReminderItem.setE_action(CommonAction.one_vibration_ring);
                    } else if (tempMode.equalsIgnoreCase(getString(R.string.common_action_vibration_ring))) {
                        eaBleReminderItem.setE_action(CommonAction.vibration_ring);
                    }
                    eaBleReminderItem.setHour(alarmTimeHour);
                    eaBleReminderItem.setMinute(alarmTimeMinute);
                    List<EABleReminder.EABleReminderItem> indexList = new ArrayList<>();
                    indexList.add(eaBleReminderItem);
                    eaBleReminder.setS_index(indexList);
                    EABleManager.getInstance().setReminderOrder(eaBleReminder, new EditAttentionCallback() {
                        @Override
                        public void editResult(EABleRemindRespond eaBleRemindRespond) {
                            if (eaBleRemindRespond.getRemindRespondOps() != EABleRemindRespond.RemindRespondResult.success) {
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x41);
                                }
                            } else {
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x40);
                                }
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
        if (remindDialog != null) {
            remindDialog.dismiss();
            remindDialog.destroyDialog();
            remindDialog = null;
        }
        if (timeDialog != null) {
            timeDialog.dismiss();
            timeDialog.destroyDialog();
            timeDialog = null;
        }
        if (typeDialog != null) {
            typeDialog.dismiss();
            typeDialog.destroyDialog();
            typeDialog = null;

        }
        if (inputDialog != null) {
            inputDialog.dismiss();
            inputDialog = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED && requestCode == CYCLE_CODE) {
            cycleTime = data.getIntExtra("circle", 0);
            if (cycleTime == 0x7F) {
                cycleText.setText(getText(R.string.every_day));

            } else {
                cycleText.setText(data.getStringExtra("circleTime"));
            }


        }
    }
}
