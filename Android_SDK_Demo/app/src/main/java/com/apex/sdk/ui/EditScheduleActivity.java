package com.apex.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditScheduleActivity extends AppCompatActivity {
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
    @BindView(R.id.remind_time)
    TextView timeText;
    @BindView(R.id.reminder_method)
    TextView modeText;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    @BindView(R.id.layout_custom)
    TableRow customRow;
    private final int CYCLE_CODE = 0xFF;
    private NotDisturbDialog timeDialog;
    private TypeDialog typeDialog;
    private RemindDialog modeDialog;
    private String tempMode;
    private int alarmTimeHour, alarmTimeMinute, cycleTime;
    private String mContent, typeString;
    private InputDialog inputDialog;
    private int id;
    private int year;
    private int month;
    private int day;
    private int firstSwitch;
    private int secondSwitch;
    private int sleepTime;
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
                Toast.makeText(EditScheduleActivity.this, getString(R.string.add_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);
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
        id = getIntent().getIntExtra("alarmId", -1);
        int method = getIntent().getIntExtra("alarmMothed", 0);
        cycleTime = getIntent().getIntExtra("cycleTime", -1);
        mContent = getIntent().getStringExtra("customContent");
        year = getIntent().getIntExtra("year", -1);
        month = getIntent().getIntExtra("month", -1);
        day = getIntent().getIntExtra("day", -1);
        alarmTimeHour = getIntent().getIntExtra("hour", -1);
        alarmTimeMinute = getIntent().getIntExtra("minute", -1);
        firstSwitch = getIntent().getIntExtra("alarmSwitch", -1);
        secondSwitch = getIntent().getIntExtra("secondSwitch", -1);
        sleepTime = getIntent().getIntExtra("lazyTime", -1);
        int type = getIntent().getIntExtra("alarmType", -1);
        if (type <= 0) {
            customRow.setVisibility(View.GONE);
            mContent = null;
        } else {
            if (type == 1) {
                typeString = getString(R.string.sleep);
                customRow.setVisibility(View.GONE);
                mContent = null;
                typeText.setText(getString(R.string.sleep));
            } else if (type == 2) {
                typeString = getString(R.string.sport);
                customRow.setVisibility(View.GONE);
                mContent = null;
                typeText.setText(getString(R.string.sport));
            } else if (type == 3) {
                typeText.setText(getString(R.string.drinking));
                customRow.setVisibility(View.GONE);
                mContent = null;
                typeString = getString(R.string.drinking);
            } else if (type == 4) {
                typeText.setText(getString(R.string.takeTheMedicine));
                customRow.setVisibility(View.GONE);
                mContent = null;
                typeString = getString(R.string.takeTheMedicine);
            } else if (type == 5) {
                typeText.setText(getString(R.string.meeting));
                customRow.setVisibility(View.GONE);
                mContent = null;
                typeString = getString(R.string.meeting);
            } else {
                customRow.setVisibility(View.VISIBLE);
                nameText.setText(mContent);
                typeString = getString(R.string.custom);
                typeText.setText(getString(R.string.custom));
            }
        }
        String remindTime = "";
        byte cTime = (byte) (cycleTime & 0xFF);
        if (cTime == 0x7F) {
            remindTime = (getString(R.string.every_day) + ",");
        } else if (cTime == 0) {
            remindTime = (getString(R.string.only_once) + ",");
        } else {
            String timeString = Integer.toBinaryString(cTime);
            if (timeString.length() < 8) {
                int size = 8 - timeString.length();
                for (int i = 0; i < size; i++) {
                    timeString = ("0" + timeString);
                }
            }
            String sundayTag = timeString.substring(7, timeString.length());
            String mondayTag = timeString.substring(6, 7);
            String tuesdayTag = timeString.substring(5, 6);
            String wednesdayTag = timeString.substring(4, 5);
            String thursdayTag = timeString.substring(3, 4);
            String fridayTag = timeString.substring(2, 3);
            String saturday = timeString.substring(1, 2);
            if (sundayTag.equalsIgnoreCase("1")) {
                remindTime += (getString(R.string.sun) + ",");
            }
            if (mondayTag.equalsIgnoreCase("1")) {
                remindTime += (getString(R.string.mon) + ",");
            }
            if (tuesdayTag.equalsIgnoreCase("1")) {
                remindTime += (getString(R.string.tue) + ",");
            }
            if (wednesdayTag.equalsIgnoreCase("1")) {
                remindTime += (getString(R.string.wed) + ",");
            }
            if (thursdayTag.equalsIgnoreCase("1")) {
                remindTime += (getString(R.string.thur) + ",");
            }
            if (fridayTag.equalsIgnoreCase("1")) {
                remindTime += (getString(R.string.fri) + ",");
            }
            if (saturday.equalsIgnoreCase("1")) {
                remindTime += (getString(R.string.sat) + ",");
            } else {

            }
        }
        if (!TextUtils.isEmpty(remindTime)) {
            remindTime = (remindTime.substring(0, remindTime.length() - 1) + " ");
        }
        cycleText.setText(remindTime);
        timeText.setText(alarmTimeHour + ":" + alarmTimeMinute);
        if (method == 0) {
            tempMode = getString(R.string.common_action_no_action);
        } else if (method == 1) {
            tempMode = getString(R.string.common_action_one_long_vibration);
        } else if (method == 2) {
            tempMode = getString(R.string.common_action_one_short_vibration);
        } else if (method == 3) {
            tempMode = getString(R.string.common_action_two_long_vibration);
        } else if (method == 4) {
            tempMode = getString(R.string.common_action_two_short_vibration);
        } else if (method == 5) {
            tempMode = getString(R.string.common_action_long_vibration);
        } else if (method == 6) {
            tempMode = getString(R.string.common_action_long_short_vibration);
        } else if (method == 7) {
            tempMode = getString(R.string.common_action_one_ring);
        } else if (method == 8) {
            tempMode = getString(R.string.common_action_two_ring);
        } else if (method == 9) {
            tempMode = getString(R.string.common_action_ring);
        } else if (method == 10) {
            tempMode = getString(R.string.common_action_one_vibration_ring);
        } else if (method == 11) {
            tempMode = getString(R.string.common_action_vibration_ring);
        }
        modeText.setText(tempMode);
        aSwitch.setChecked(firstSwitch == 0 ? false : true);
        cycleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(EditScheduleActivity.this, CycleActivity.class), CYCLE_CODE);
            }
        });
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeDialog == null) {
                    timeDialog = new NotDisturbDialog(EditScheduleActivity.this, getString(R.string.alarm_clock));
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
                    typeDialog = new TypeDialog(EditScheduleActivity.this);
                    typeDialog.setSelectListener(new TypeDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            typeString = sex;
                            typeText.setText(sex);
                            if (sex.equalsIgnoreCase(getString(R.string.custom))) {
                                customRow.setVisibility(View.VISIBLE);
                            } else {
                                customRow.setVisibility(View.GONE);
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
                    inputDialog = new InputDialog(EditScheduleActivity.this);
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
                if (modeDialog == null) {
                    modeDialog = new RemindDialog(EditScheduleActivity.this);
                    modeDialog.setSelectListener(new RemindDialog.SelectListener() {
                        @Override
                        public void selectData(String sex) {
                            tempMode = sex;
                            modeText.setText(sex);
                        }
                    });
                }
                if (!modeDialog.isShowing()) {
                    modeDialog.show();
                }
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firstSwitch = 1;
                } else {
                    firstSwitch = 0;
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
                        waitingDialog = new WaitingDialog(EditScheduleActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleReminder eaBleReminder = new EABleReminder();
                    eaBleReminder.setId(id);
                    eaBleReminder.setE_ops(EABleReminder.ReminderOps.edit);
                    EABleReminder.EABleReminderItem eaBleReminderItem = new EABleReminder.EABleReminderItem();
                    eaBleReminderItem.setSw(firstSwitch);
                    eaBleReminderItem.setYear(year);
                    eaBleReminderItem.setMonth(month);
                    eaBleReminderItem.setDay(day);
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
        if (modeDialog != null) {
            modeDialog.dismiss();
            modeDialog.destroyDialog();
            modeDialog = null;
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
