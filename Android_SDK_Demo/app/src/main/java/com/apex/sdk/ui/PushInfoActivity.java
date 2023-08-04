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
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.model.EABleSocialContact;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PushInfoActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.face_book_title)
    TextView bookTitleText;
    @BindView(R.id.face_book_content)
    TextView bookContentText;
    @BindView(R.id.push_face_book)
    AppCompatButton bookButton;
    @BindView(R.id.call_title)
    TextView callTitleText;
    @BindView(R.id.call_content)
    TextView callContentText;
    @BindView(R.id.push_call)
    AppCompatButton callButton;
    @BindView(R.id.hang_up_title)
    TextView hangTitleText;
    @BindView(R.id.hang_up_content)
    TextView hangContentText;
    @BindView(R.id.hang_call)
    AppCompatButton hangButton;
    @BindView(R.id.sms_title)
    TextView smsTitleText;
    @BindView(R.id.sms_content)
    TextView smsContentText;
    @BindView(R.id.push_sms)
    AppCompatButton smsButton;
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
                Toast.makeText(PushInfoActivity.this, getString(R.string.push_successful), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(PushInfoActivity.this, getString(R.string.push_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_info);
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
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    EABleSocialContact eaBleSocialContact = new EABleSocialContact();
                    eaBleSocialContact.setContent(bookContentText.getText().toString());
                    eaBleSocialContact.setTitle(bookTitleText.getText().toString());
                    eaBleSocialContact.seteType(EABleSocialContact.SocialContactType.facebook);
                    eaBleSocialContact.setE_ops(EABleSocialContact.SocialContactOps.add);
                    Calendar calendar = Calendar.getInstance();
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    String hintTime = calendar.get(Calendar.YEAR) + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "") + "T" + (hour < 10 ? "0" + hour : hour + "") + (minute < 10 ? "0" + minute : minute + "") + (second < 10 ? "0" + second : second + "");
                    eaBleSocialContact.setDate(hintTime);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(PushInfoActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().pushInfo2Watch(eaBleSocialContact, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
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
            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    EABleSocialContact eaBleSocialContact = new EABleSocialContact();
                    eaBleSocialContact.setContent(callContentText.getText().toString());
                    eaBleSocialContact.setTitle(callTitleText.getText().toString());
                    eaBleSocialContact.seteType(EABleSocialContact.SocialContactType.incomingcall);
                    eaBleSocialContact.setE_ops(EABleSocialContact.SocialContactOps.add);
                    Calendar calendar = Calendar.getInstance();
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    String hintTime = calendar.get(Calendar.YEAR) + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "") + "T" + (hour < 10 ? "0" + hour : hour + "") + (minute < 10 ? "0" + minute : minute + "") + (second < 10 ? "0" + second : second + "");
                    eaBleSocialContact.setDate(hintTime);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(PushInfoActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().pushInfo2Watch(eaBleSocialContact, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
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
            }
        });
        hangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    EABleSocialContact eaBleSocialContact = new EABleSocialContact();
                    eaBleSocialContact.setContent(hangContentText.getText().toString());
                    eaBleSocialContact.setTitle(hangTitleText.getText().toString());
                    eaBleSocialContact.seteType(EABleSocialContact.SocialContactType.incomingcall);
                    eaBleSocialContact.setE_ops(EABleSocialContact.SocialContactOps.del);
                    Calendar calendar = Calendar.getInstance();
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    String hintTime = calendar.get(Calendar.YEAR) + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "") + "T" + (hour < 10 ? "0" + hour : hour + "") + (minute < 10 ? "0" + minute : minute + "") + (second < 10 ? "0" + second : second + "");
                    eaBleSocialContact.setDate(hintTime);
                    if (waitingDialog != null) {
                        waitingDialog = new WaitingDialog(PushInfoActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().pushInfo2Watch(eaBleSocialContact, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
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
            }
        });
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    EABleSocialContact eaBleSocialContact = new EABleSocialContact();
                    eaBleSocialContact.setContent(smsContentText.getText().toString());
                    eaBleSocialContact.setTitle(smsTitleText.getText().toString());
                    eaBleSocialContact.seteType(EABleSocialContact.SocialContactType.sms);
                    eaBleSocialContact.setE_ops(EABleSocialContact.SocialContactOps.add);
                    Calendar calendar = Calendar.getInstance();
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    String hintTime = calendar.get(Calendar.YEAR) + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "") + "T" + (hour < 10 ? "0" + hour : hour + "") + (minute < 10 ? "0" + minute : minute + "") + (second < 10 ? "0" + second : second + "");
                    eaBleSocialContact.setDate(hintTime);
                    if (waitingDialog != null) {
                        waitingDialog = new WaitingDialog(PushInfoActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().pushInfo2Watch(eaBleSocialContact, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
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
