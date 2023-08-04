package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
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
import com.apex.bluetooth.model.EABleStartAppSports;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AppSports extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.start_moving)
    AppCompatButton startButton;
    @BindView(R.id.end_moving)
    AppCompatButton endButton;
    @BindView(R.id.pause_moving)
    AppCompatButton pauseButton;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x41) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                boolean success = (boolean) msg.obj;
                if (success) {
                    Toast.makeText(AppSports.this, getString(R.string.start_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppSports.this, getString(R.string.existing_sports), Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 0x42) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(AppSports.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x43 || msg.what == 0x44) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                boolean success = (boolean) msg.obj;
                if (!success) {
                    Toast.makeText(AppSports.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_sports);
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
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
                if (state == EABleConnectState.STATE_CONNECTED) {
                    EABleStartAppSports eaBleStartAppSports = new EABleStartAppSports();
                    eaBleStartAppSports.setAppSportType(EABleStartAppSports.AppSportType.ourdoor_walking);
                    eaBleStartAppSports.setSportStatus(EABleSportStatus.start);
                    eaBleStartAppSports.setReportInterval(10);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(AppSports.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().startAppSport(eaBleStartAppSports, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
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
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
                if (state == EABleConnectState.STATE_CONNECTED) {
                    EABleStartAppSports eaBleStartAppSports = new EABleStartAppSports();
                    //  eaBleStartAppSports.setAppSportType(EABleStartAppSports.AppSportType.ourdoor_walking);
                    eaBleStartAppSports.setSportStatus(EABleSportStatus.close);
                    eaBleStartAppSports.setReportInterval(10);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(AppSports.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().startAppSport(eaBleStartAppSports, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
                            Message message = new Message();
                            message.what = 0x43;
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
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
                if (state == EABleConnectState.STATE_CONNECTED) {
                    EABleStartAppSports eaBleStartAppSports = new EABleStartAppSports();
                    //   eaBleStartAppSports.setAppSportType(EABleStartAppSports.AppSportType.ourdoor_walking);
                    eaBleStartAppSports.setSportStatus(EABleSportStatus.pause);
                    eaBleStartAppSports.setReportInterval(10);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(AppSports.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().startAppSport(eaBleStartAppSports, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
                            Message message = new Message();
                            message.what = 0x44;
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
