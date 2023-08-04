package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
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
import com.apex.bluetooth.enumeration.EABleSportStatus;
import com.apex.bluetooth.model.EABleAppScreenSport;
import com.apex.sdk.R;
import com.apex.sdk.dialog.AgeDialog;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WatchSportActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.interval_time)
    TextView timeText;
    private int intervalTime = 5;
    AgeDialog ageDialog;
    @BindView(R.id.start_moving)
    AppCompatButton startButton;
    @BindView(R.id.end_moving)
    AppCompatButton endButton;
    @BindView(R.id.pause_moving)
    AppCompatButton pauseButton;
    @BindView(R.id.process_moving)
    AppCompatButton processButton;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x41) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(WatchSportActivity.this, getString(R.string.operation_succeeded), Toast.LENGTH_SHORT).show();

            } else if (msg.what == 0x42) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(WatchSportActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x43 || msg.what == 0x44) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                boolean success = (boolean) msg.obj;
                if (!success) {
                    Toast.makeText(WatchSportActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_sport);
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
        timeText.setText(5 + getString(R.string.second));
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ageDialog == null) {
                    ageDialog = new AgeDialog(WatchSportActivity.this);
                    ageDialog.setSelectListener(new AgeDialog.SelectListener() {
                        @Override
                        public void selectData(int sex) {
                            intervalTime = sex;
                            timeText.setText(sex + getString(R.string.second));

                        }
                    });
                }
                if (!ageDialog.isShowing()) {
                    ageDialog.show();
                }
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    EABleAppScreenSport eaBleAppScreenSport = new EABleAppScreenSport();
                    eaBleAppScreenSport.setEaBleAppSportType(EABleAppScreenSport.EABleAppSportType.ourdoor_walking);
                    eaBleAppScreenSport.setEaBleSportStatus(EABleSportStatus.start);
                    eaBleAppScreenSport.setInterval(intervalTime);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(WatchSportActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().startAppScreenSport(eaBleAppScreenSport, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
                            if (mHandler != null) {
                                Message message = new Message();
                                message.what = 0x41;
                                message.arg2 = 1;
                                mHandler.sendMessage(message);
                            }
                        }

                        @Override
                        public void mutualFail(int errorCode) {
                            if (mHandler != null) {
                                Message message = new Message();
                                message.what = 0x42;
                                message.arg2 = 1;
                                mHandler.sendMessage(message);
                            }
                        }
                    });
                }
            }
        });
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                        EABleAppScreenSport eaBleAppScreenSport = new EABleAppScreenSport();
                        eaBleAppScreenSport.setEaBleAppSportType(EABleAppScreenSport.EABleAppSportType.ourdoor_walking);
                        eaBleAppScreenSport.setEaBleSportStatus(EABleSportStatus.close);
                        eaBleAppScreenSport.setInterval(intervalTime);
                        if (waitingDialog == null) {
                            waitingDialog = new WaitingDialog(WatchSportActivity.this);
                        }
                        if (!waitingDialog.isShowing()) {
                            waitingDialog.show();
                        }
                        EABleManager.getInstance().startAppScreenSport(eaBleAppScreenSport, new GeneralCallback() {
                            @Override
                            public void result(boolean success,int reason) {
                                if (mHandler != null) {
                                    Message message = new Message();
                                    message.what = 0x41;
                                    message.arg2 = 2;
                                    mHandler.sendMessage(message);
                                }
                            }

                            @Override
                            public void mutualFail(int errorCode) {
                                if (mHandler != null) {
                                    Message message = new Message();
                                    message.what = 0x42;
                                    message.arg2 = 2;
                                    mHandler.sendMessage(message);
                                }
                            }
                        });
                    }
                }
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    EABleAppScreenSport eaBleAppScreenSport = new EABleAppScreenSport();
                    eaBleAppScreenSport.setEaBleAppSportType(EABleAppScreenSport.EABleAppSportType.ourdoor_walking);
                    eaBleAppScreenSport.setEaBleSportStatus(EABleSportStatus.pause);
                    eaBleAppScreenSport.setInterval(intervalTime);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(WatchSportActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().startAppScreenSport(eaBleAppScreenSport, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
                            if (mHandler != null) {
                                Message message = new Message();
                                message.what = 0x41;
                                message.arg2 = 3;
                                mHandler.sendMessage(message);
                            }
                        }

                        @Override
                        public void mutualFail(int errorCode) {
                            if (mHandler != null) {
                                Message message = new Message();
                                message.what = 0x42;
                                message.arg2 = 3;
                                mHandler.sendMessage(message);
                            }
                        }
                    });
                }
            }
        });
        processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    EABleAppScreenSport eaBleAppScreenSport = new EABleAppScreenSport();
                    eaBleAppScreenSport.setEaBleAppSportType(EABleAppScreenSport.EABleAppSportType.ourdoor_walking);
                    eaBleAppScreenSport.setEaBleSportStatus(EABleSportStatus.processed);
                    eaBleAppScreenSport.setInterval(intervalTime);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(WatchSportActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().startAppScreenSport(eaBleAppScreenSport, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
                            if (mHandler != null) {
                                Message message = new Message();
                                message.what = 0x41;
                                message.arg2 = 3;
                                mHandler.sendMessage(message);
                            }
                        }

                        @Override
                        public void mutualFail(int errorCode) {
                            if (mHandler != null) {
                                Message message = new Message();
                                message.what = 0x42;
                                message.arg2 = 3;
                                mHandler.sendMessage(message);
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
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
        if (ageDialog != null) {
            ageDialog.destroyDialog();
            ageDialog = null;
        }
        super.onDestroy();
    }
}
