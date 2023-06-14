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
import com.apex.bluetooth.callback.RemindCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.CommonAction;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleAncsSw;
import com.apex.sdk.R;
import com.apex.sdk.dialog.RemindDialog;
import com.apex.sdk.dialog.SwitchDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RemindActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    private String mode;
    private RemindDialog remindDialog;
    private SwitchDialog callDialog, emailDialog, unknownDialog, smsDialog, scheduleDialog, socialDialog;
    @BindView(R.id.reminder_mode)
    TextView modeText;
    @BindView(R.id.call)
    TextView callText;
    @BindView(R.id.unknown_call)
    TextView unknownText;
    @BindView(R.id.sms)
    TextView smsText;
    @BindView(R.id.email)
    TextView emailText;
    @BindView(R.id.schedule)
    TextView scheduleText;
    @BindView(R.id.social_contact)
    TextView socialText;
    private EABleAncsSw eaBleAncsSw;
    private String tempMode, tempCall;
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
                eaBleAncsSw = (EABleAncsSw) msg.obj;
                EABleAncsSw.EABleAncsSwItem emailItem = eaBleAncsSw.getS_email();
                EABleAncsSw.EABleAncsSwItem callItem = eaBleAncsSw.getS_incomingcall();
                EABleAncsSw.EABleAncsSwItem missedItem = eaBleAncsSw.getS_missedcall();
                EABleAncsSw.EABleAncsSwItem scheduleItem = eaBleAncsSw.getS_schedule();
                EABleAncsSw.EABleAncsSwItem smsItem = eaBleAncsSw.getS_sms();
                EABleAncsSw.EABleAncsSwItem socialItem = eaBleAncsSw.getS_social();
                if (emailItem != null) {
                    int sw = emailItem.getSw();
                    if (sw == 0) {
                        emailText.setText(getString(R.string.switch_state_close));
                    } else {
                        emailText.setText(getString(R.string.switch_state_on));
                    }
                    CommonAction commonAction = emailItem.getE_action();
                    if (commonAction != null) {
                        if (commonAction == CommonAction.no_action) {
                            modeText.setText(getString(R.string.common_action_no_action));
                        } else if (commonAction == CommonAction.one_long_vibration) {
                            modeText.setText(getString(R.string.common_action_one_long_vibration));
                        } else if (commonAction == CommonAction.one_short_vibration) {
                            modeText.setText(getString(R.string.common_action_one_short_vibration));
                        } else if (commonAction == CommonAction.two_long_vibration) {
                            modeText.setText(getString(R.string.common_action_two_long_vibration));
                        } else if (commonAction == CommonAction.two_short_vibration) {
                            modeText.setText(getString(R.string.common_action_two_short_vibration));
                        } else if (commonAction == CommonAction.long_vibration) {
                            modeText.setText(getString(R.string.common_action_long_vibration));
                        } else if (commonAction == CommonAction.long_short_vibration) {
                            modeText.setText(getString(R.string.common_action_long_short_vibration));
                        } else if (commonAction == CommonAction.one_ring) {
                            modeText.setText(getString(R.string.common_action_one_ring));
                        } else if (commonAction == CommonAction.two_ring) {
                            modeText.setText(getString(R.string.common_action_two_ring));
                        } else if (commonAction == CommonAction.ring) {
                            modeText.setText(getString(R.string.common_action_ring));
                        } else if (commonAction == CommonAction.one_vibration_ring) {
                            modeText.setText(getString(R.string.common_action_one_vibration_ring));
                        } else if (commonAction == CommonAction.vibration_ring) {
                            modeText.setText(getString(R.string.common_action_vibration_ring));
                        }
                    }
                }
                if (callItem != null) {
                    int sw = callItem.getSw();
                    if (sw == 0) {
                        callText.setText(getString(R.string.switch_state_close));
                    } else {
                        callText.setText(getString(R.string.switch_state_on));
                    }
                }
                if (missedItem != null) {
                    int sw = missedItem.getSw();
                    if (sw == 0) {
                        unknownText.setText(getString(R.string.switch_state_close));
                    } else {
                        unknownText.setText(getString(R.string.switch_state_on));
                    }
                }
                if (scheduleItem != null) {
                    int sw = scheduleItem.getSw();
                    if (sw == 0) {
                        scheduleText.setText(getString(R.string.switch_state_close));
                    } else {
                        scheduleText.setText(getString(R.string.switch_state_on));
                    }
                }
                if (smsItem != null) {
                    int sw = smsItem.getSw();
                    if (sw == 0) {
                        smsText.setText(getString(R.string.switch_state_close));
                    } else {
                        smsText.setText(getString(R.string.switch_state_on));
                    }
                }
                if (socialItem != null) {
                    int sw = socialItem.getSw();
                    if (sw == 0) {
                        socialText.setText(getString(R.string.switch_state_close));
                    } else {
                        socialText.setText(getString(R.string.switch_state_on));
                    }
                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(RemindActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                modeText.setText(tempMode);
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(RemindActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x44) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                callText.setText(tempCall);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
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
        mode = getString(R.string.common_action_no_action);
        EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
        if (state == EABleConnectState.STATE_CONNECTED) {
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(RemindActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.ancs_sw, new RemindCallback() {
                @Override
                public void remindInfo(EABleAncsSw eaBleAncsSw) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleAncsSw;
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
        modeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (remindDialog == null) {
                        remindDialog = new RemindDialog(RemindActivity.this);
                        remindDialog.setSelectListener(new RemindDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                tempMode = sex;
                                if (eaBleAncsSw == null) {
                                    eaBleAncsSw = new EABleAncsSw();
                                    EABleAncsSw.EABleAncsSwItem callItem = new EABleAncsSw.EABleAncsSwItem();
                                    EABleAncsSw.EABleAncsSwItem unknownItem = new EABleAncsSw.EABleAncsSwItem();
                                    EABleAncsSw.EABleAncsSwItem smsItem = new EABleAncsSw.EABleAncsSwItem();
                                    EABleAncsSw.EABleAncsSwItem emailItem = new EABleAncsSw.EABleAncsSwItem();
                                    EABleAncsSw.EABleAncsSwItem socialItem = new EABleAncsSw.EABleAncsSwItem();
                                    EABleAncsSw.EABleAncsSwItem scheduleItem = new EABleAncsSw.EABleAncsSwItem();
                                    callItem.setSw(1);
                                    unknownItem.setSw(1);
                                    smsItem.setSw(1);
                                    emailItem.setSw(1);
                                    socialItem.setSw(1);
                                    scheduleItem.setSw(1);
                                    eaBleAncsSw.setS_email(emailItem);
                                    eaBleAncsSw.setS_incomingcall(callItem);
                                    eaBleAncsSw.setS_missedcall(unknownItem);
                                    eaBleAncsSw.setS_schedule(scheduleItem);
                                    eaBleAncsSw.setS_sms(smsItem);
                                    eaBleAncsSw.setS_social(socialItem);
                                }
                                EABleAncsSw.EABleAncsSwItem socialItem = eaBleAncsSw.getS_social();
                                EABleAncsSw.EABleAncsSwItem emailItem = eaBleAncsSw.getS_email();
                                EABleAncsSw.EABleAncsSwItem callItem = eaBleAncsSw.getS_incomingcall();
                                EABleAncsSw.EABleAncsSwItem unknownItem = eaBleAncsSw.getS_missedcall();
                                EABleAncsSw.EABleAncsSwItem smsItem = eaBleAncsSw.getS_sms();
                                EABleAncsSw.EABleAncsSwItem scheduleItem = eaBleAncsSw.getS_schedule();
                                if (socialItem == null) {
                                    socialItem = new EABleAncsSw.EABleAncsSwItem();
                                    eaBleAncsSw.setS_social(socialItem);
                                    socialItem.setSw(1);
                                }
                                if (unknownItem == null) {
                                    unknownItem = new EABleAncsSw.EABleAncsSwItem();
                                    eaBleAncsSw.setS_missedcall(unknownItem);
                                    unknownItem.setSw(1);
                                }
                                if (smsItem == null) {
                                    smsItem = new EABleAncsSw.EABleAncsSwItem();
                                    eaBleAncsSw.setS_sms(smsItem);
                                    smsItem.setSw(1);
                                }
                                if (emailItem == null) {
                                    emailItem = new EABleAncsSw.EABleAncsSwItem();
                                    eaBleAncsSw.setS_email(emailItem);
                                    emailItem.setSw(1);
                                }
                                if (callItem == null) {
                                    callItem = new EABleAncsSw.EABleAncsSwItem();
                                    eaBleAncsSw.setS_incomingcall(callItem);
                                    callItem.setSw(1);
                                }
                                if (scheduleItem == null) {
                                    scheduleItem = new EABleAncsSw.EABleAncsSwItem();
                                    eaBleAncsSw.setS_schedule(scheduleItem);
                                    scheduleItem.setSw(1);
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.common_action_no_action))) {
                                    callItem.setE_action(CommonAction.no_action);
                                    emailItem.setE_action(CommonAction.no_action);
                                    smsItem.setE_action(CommonAction.no_action);
                                    unknownItem.setE_action(CommonAction.no_action);
                                    socialItem.setE_action(CommonAction.no_action);
                                    scheduleItem.setE_action(CommonAction.no_action);
                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_one_long_vibration))) {
                                    callItem.setE_action(CommonAction.one_long_vibration);
                                    smsItem.setE_action(CommonAction.one_long_vibration);
                                    unknownItem.setE_action(CommonAction.one_long_vibration);
                                    emailItem.setE_action(CommonAction.one_long_vibration);
                                    socialItem.setE_action(CommonAction.one_long_vibration);
                                    scheduleItem.setE_action(CommonAction.one_long_vibration);

                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_one_short_vibration))) {
                                    callItem.setE_action(CommonAction.one_short_vibration);
                                    smsItem.setE_action(CommonAction.one_short_vibration);
                                    unknownItem.setE_action(CommonAction.one_short_vibration);
                                    emailItem.setE_action(CommonAction.one_short_vibration);
                                    socialItem.setE_action(CommonAction.one_short_vibration);
                                    scheduleItem.setE_action(CommonAction.one_short_vibration);
                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_two_long_vibration))) {
                                    callItem.setE_action(CommonAction.two_long_vibration);
                                    smsItem.setE_action(CommonAction.two_long_vibration);
                                    emailItem.setE_action(CommonAction.two_long_vibration);
                                    unknownItem.setE_action(CommonAction.two_long_vibration);
                                    socialItem.setE_action(CommonAction.two_long_vibration);
                                    scheduleItem.setE_action(CommonAction.two_long_vibration);
                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_two_short_vibration))) {
                                    callItem.setE_action(CommonAction.two_short_vibration);
                                    emailItem.setE_action(CommonAction.two_short_vibration);
                                    smsItem.setE_action(CommonAction.two_short_vibration);
                                    scheduleItem.setE_action(CommonAction.two_short_vibration);
                                    socialItem.setE_action(CommonAction.two_short_vibration);
                                    unknownItem.setE_action(CommonAction.two_short_vibration);
                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_long_vibration))) {
                                    callItem.setE_action(CommonAction.long_vibration);
                                    emailItem.setE_action(CommonAction.long_vibration);
                                    scheduleItem.setE_action(CommonAction.long_vibration);
                                    unknownItem.setE_action(CommonAction.long_vibration);
                                    socialItem.setE_action(CommonAction.long_vibration);
                                    smsItem.setE_action(CommonAction.long_vibration);

                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_long_short_vibration))) {
                                    callItem.setE_action(CommonAction.long_short_vibration);
                                    unknownItem.setE_action(CommonAction.long_short_vibration);
                                    emailItem.setE_action(CommonAction.long_short_vibration);
                                    scheduleItem.setE_action(CommonAction.long_short_vibration);
                                    smsItem.setE_action(CommonAction.long_short_vibration);
                                    socialItem.setE_action(CommonAction.long_short_vibration);
                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_one_ring))) {
                                    callItem.setE_action(CommonAction.one_ring);
                                    emailItem.setE_action(CommonAction.one_ring);
                                    unknownItem.setE_action(CommonAction.one_ring);
                                    scheduleItem.setE_action(CommonAction.one_ring);
                                    smsItem.setE_action(CommonAction.one_ring);
                                    socialItem.setE_action(CommonAction.one_ring);
                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_two_ring))) {
                                    callItem.setE_action(CommonAction.two_ring);
                                    emailItem.setE_action(CommonAction.two_ring);
                                    unknownItem.setE_action(CommonAction.two_ring);
                                    scheduleItem.setE_action(CommonAction.two_ring);
                                    smsItem.setE_action(CommonAction.two_ring);
                                    socialItem.setE_action(CommonAction.two_ring);
                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_ring))) {
                                    callItem.setE_action(CommonAction.ring);
                                    emailItem.setE_action(CommonAction.ring);
                                    unknownItem.setE_action(CommonAction.ring);
                                    scheduleItem.setE_action(CommonAction.ring);
                                    smsItem.setE_action(CommonAction.ring);
                                    socialItem.setE_action(CommonAction.ring);
                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_one_vibration_ring))) {
                                    callItem.setE_action(CommonAction.one_vibration_ring);
                                    emailItem.setE_action(CommonAction.one_vibration_ring);
                                    unknownItem.setE_action(CommonAction.one_vibration_ring);
                                    scheduleItem.setE_action(CommonAction.one_vibration_ring);
                                    smsItem.setE_action(CommonAction.one_vibration_ring);
                                    socialItem.setE_action(CommonAction.one_vibration_ring);
                                } else if (sex.equalsIgnoreCase(getString(R.string.common_action_vibration_ring))) {
                                    callItem.setE_action(CommonAction.vibration_ring);
                                    emailItem.setE_action(CommonAction.vibration_ring);
                                    unknownItem.setE_action(CommonAction.vibration_ring);
                                    scheduleItem.setE_action(CommonAction.vibration_ring);
                                    smsItem.setE_action(CommonAction.vibration_ring);
                                    socialItem.setE_action(CommonAction.vibration_ring);
                                }
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(RemindActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setAncsSwitch(eaBleAncsSw, new GeneralCallback() {
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
                        });
                    }
                    if (!remindDialog.isShowing()) {
                        remindDialog.show();
                    }
                }
            }
        });
        callText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (callDialog == null) {
                        callDialog = new SwitchDialog(RemindActivity.this);
                        callDialog.setSelectListener(new SwitchDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                tempCall = sex;
                                if (eaBleAncsSw == null) {
                                    eaBleAncsSw = new EABleAncsSw();
                                    EABleAncsSw.EABleAncsSwItem callItem = new EABleAncsSw.EABleAncsSwItem();
                                    eaBleAncsSw.setS_incomingcall(callItem);
                                    callItem.setE_action(CommonAction.no_action);
                                }
                                EABleAncsSw.EABleAncsSwItem callItem = eaBleAncsSw.getS_incomingcall();
                                if (callItem == null) {
                                    callItem = new EABleAncsSw.EABleAncsSwItem();
                                    eaBleAncsSw.setS_incomingcall(callItem);
                                    callItem.setE_action(CommonAction.no_action);
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.switch_state_on))) {
                                    callItem.setSw(1);
                                } else {
                                    callItem.setSw(0);
                                }
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(RemindActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setAncsSwitch(eaBleAncsSw, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x44);
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
                    if (!callDialog.isShowing()) {
                        callDialog.show();
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
        if (remindDialog != null) {
            remindDialog.dismiss();
            remindDialog.destroyDialog();
            remindDialog = null;
        }
        if (callDialog != null) {
            callDialog.dismiss();
            callDialog.destroyDialog();
            callDialog = null;
        }
        super.onDestroy();
    }
}
