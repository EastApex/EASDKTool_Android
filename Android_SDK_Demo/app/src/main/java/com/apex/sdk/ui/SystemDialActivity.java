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
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.WatchFaceCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleWatchFace;
import com.apex.bluetooth.utils.LogUtils;
import com.apex.sdk.R;
import com.apex.sdk.dialog.MonthDialog;
import com.apex.sdk.dialog.OnlineDialDialog;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SystemDialActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private MonthDialog ageDialog;
    private int tempRest;
    @BindView(R.id.rest_screen)
    TextView restText;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.online_dial)
    TextView onlineText;
    List<String> dialList;
    OnlineDialDialog onlineDialDialog;
    private String onlineDial;
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
                EABleWatchFace eaBleWatchFace = (EABleWatchFace) msg.obj;
                if (eaBleWatchFace.getId() > 0) {
                    restText.setText(eaBleWatchFace.getId() + "");
                } else {
                    onlineText.setText(eaBleWatchFace.getUser_wf_id());
                }
                dialList = new ArrayList<>();
                if (!TextUtils.isEmpty(eaBleWatchFace.user_wf_id_0)) {
                    dialList.add(eaBleWatchFace.getUser_wf_id_0());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.user_wf_id_1)) {
                    dialList.add(eaBleWatchFace.getUser_wf_id_1());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.user_wf_id_2)) {
                    dialList.add(eaBleWatchFace.getUser_wf_id_2());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.user_wf_id_3)) {
                    dialList.add(eaBleWatchFace.getUser_wf_id_3());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.user_wf_id_4)) {
                    dialList.add(eaBleWatchFace.getUser_wf_id_4());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.user_wf_id_5)) {
                    dialList.add(eaBleWatchFace.getUser_wf_id_5());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.user_wf_id_6)) {
                    dialList.add(eaBleWatchFace.getUser_wf_id_6());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.user_wf_id_7)) {
                    dialList.add(eaBleWatchFace.getUser_wf_id_7());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.user_wf_id_8)) {
                    dialList.add(eaBleWatchFace.getUser_wf_id_8());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.user_wf_id_9)) {
                    dialList.add(eaBleWatchFace.getUser_wf_id_9());
                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SystemDialActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                restText.setText(tempRest + "");
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(SystemDialActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x44) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                onlineText.setText(onlineDial);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial_system);
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
                waitingDialog = new WaitingDialog(SystemDialActivity.this);
            }
            waitingDialog.show();
            LogUtils.e(TAG, "开始查询当前表盘");
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.dial, new WatchFaceCallback() {
                @Override
                public void watchFaceInfo(EABleWatchFace eaBleWatchFace) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleWatchFace;
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
                        ageDialog = new MonthDialog(SystemDialActivity.this, getString(R.string.dial_ID));
                        ageDialog.setSelectListener(new MonthDialog.SelectListener() {
                            @Override
                            public void selectData(int sex) {
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SystemDialActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempRest = sex;
                                EABleWatchFace eaBleWatchFace = new EABleWatchFace();
                                eaBleWatchFace.setId(sex);
                                EABleManager.getInstance().setWatchFace(eaBleWatchFace, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success, int reason) {
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
                    if (!ageDialog.isShowing()) {
                        ageDialog.show();
                    }
                }
            }
        });
        onlineText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (onlineDialDialog == null) {
                        onlineDialDialog = new OnlineDialDialog(SystemDialActivity.this, dialList);
                        onlineDialDialog.setSelectListener(new OnlineDialDialog.SelectListener() {
                            @Override
                            public void selectDial(String dial) {
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(SystemDialActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                onlineDial = dial;
                                EABleWatchFace eaBleWatchFace = new EABleWatchFace();
                                eaBleWatchFace.setUser_wf_id(dial);
                                EABleManager.getInstance().setWatchFace(eaBleWatchFace, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success, int reason) {
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
                    if (!onlineDialDialog.isShowing()) {
                        onlineDialDialog.show();
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
        if (ageDialog != null) {
            ageDialog.dismiss();
            ageDialog.destroyDialog();
            ageDialog = null;
        }
        if (onlineDialDialog != null) {
            onlineDialDialog.destroyDialog();
            onlineDialDialog = null;
        }
        super.onDestroy();
    }
}
