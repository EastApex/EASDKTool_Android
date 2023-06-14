package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.WatchInfoCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleBindInfo;
import com.apex.bluetooth.model.EABleDev;
import com.apex.bluetooth.model.EABleWatchInfo;
import com.apex.bluetooth.utils.LogUtils;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BindActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.bind)
    AppCompatButton bindButton;
    @BindView(R.id.unbound)
    AppCompatButton unboundButton;
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
                EABleWatchInfo eaBleWatchInfo = (EABleWatchInfo) msg.obj;
                EABleWatchInfo.BindingInfo bindingInfo = eaBleWatchInfo.getBindingInfo();
                if (bindingInfo != null) {
                    if (bindingInfo == EABleWatchInfo.BindingInfo.unbound) {
                        bindButton.setVisibility(View.VISIBLE);
                        unboundButton.setVisibility(View.GONE);
                    } else {
                        bindButton.setVisibility(View.GONE);
                        unboundButton.setVisibility(View.VISIBLE);
                    }
                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(BindActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                bindButton.setVisibility(View.VISIBLE);
                unboundButton.setVisibility(View.GONE);
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(BindActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();

            } else if (msg.what == 0x44) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                bindButton.setVisibility(View.GONE);
                unboundButton.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
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
                waitingDialog = new WaitingDialog(BindActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.watch_info, new WatchInfoCallback() {
                @Override
                public void watchInfo(EABleWatchInfo eaBleWatchInfo) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleWatchInfo;
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
        bindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(BindActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleBindInfo eaBleBindInfo = new EABleBindInfo();
                    eaBleBindInfo.setUser_id("123456789123");
                    eaBleBindInfo.setE_ops(EABleBindInfo.BindingOps.end);
                    EABleManager.getInstance().setOpsBinding(eaBleBindInfo, new GeneralCallback() {
                        @Override
                        public void result(boolean success) {
                            LogUtils.e(TAG,"成功..........0x44");
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x44);
                            }
                        }

                        @Override
                        public void mutualFail(int errorCode) {
                            LogUtils.e(TAG,"成功..........0x43");
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x43);
                            }
                        }
                    });
                }
            }
        });
        unboundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(BindActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleDev eaBleDev = new EABleDev();
                    eaBleDev.setE_ops(EABleDev.DevOps.restore_factory);
                    EABleManager.getInstance().setDeviceOps(eaBleDev, new GeneralCallback() {
                        @Override
                        public void result(boolean success) {
                            LogUtils.e(TAG,"成功..........0x42");
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x42);
                            }
                        }

                        @Override
                        public void mutualFail(int errorCode) {
                            LogUtils.e(TAG,"成功..........0x45");
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x45);
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
