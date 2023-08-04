package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.MsgContentCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MsgActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.msg1)
    EditText msg1Text;
    @BindView(R.id.msg2)
    EditText msg2Text;
    @BindView(R.id.msg3)
    EditText msg3Text;
    @BindView(R.id.msg4)
    EditText msg4Text;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    List<String> msgList;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x40) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                if (msgList != null && !msgList.isEmpty()) {

                    if (msgList.size() >= 4) {
                        msg1Text.setText(msgList.get(0));
                        msg2Text.setText(msgList.get(1));
                        msg3Text.setText(msgList.get(2));
                        msg4Text.setText(msgList.get(3));
                    } else {
                        if (msgList.size() == 3) {
                            msg1Text.setText(msgList.get(0));
                            msg2Text.setText(msgList.get(1));
                            msg3Text.setText(msgList.get(2));
                        } else if (msgList.size() == 2) {
                            msg1Text.setText(msgList.get(0));
                            msg2Text.setText(msgList.get(1));
                        } else {
                            msg1Text.setText(msgList.get(0));
                        }
                    }

                }
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(MsgActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                submitButton.setEnabled(true);
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                submitButton.setEnabled(true);
                Toast.makeText(MsgActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
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
                waitingDialog = new WaitingDialog(MsgActivity.this);
            }

            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.msgContent, new MsgContentCallback() {
                @Override
                public void msgInfo(List<String> list) {
                    msgList = list;
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x40);
                    }

                }

                @Override
                public void mutualFail(int i) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x41);
                    }
                }
            });
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButton.setEnabled(false);
                String msg1 = msg1Text.getText().toString();
                String msg2 = msg2Text.getText().toString();
                String msg3 = msg3Text.getText().toString();
                String msg4 = msg4Text.getText().toString();
                if (TextUtils.isEmpty(msg1) && TextUtils.isEmpty(msg2) && TextUtils.isEmpty(msg3) && TextUtils.isEmpty(msg4)) {
                    submitButton.setEnabled(true);
                    return;
                }
                if (msgList == null) {
                    msgList = new ArrayList<>();
                }
                msgList.clear();
                if (!TextUtils.isEmpty(msg1)) {
                    msgList.add(msg1);
                }
                if (!TextUtils.isEmpty(msg2)) {
                    msgList.add(msg2);
                }
                if (!TextUtils.isEmpty(msg3)) {
                    msgList.add(msg3);
                }
                if (!TextUtils.isEmpty(msg4)) {
                    msgList.add(msg4);
                }
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    EABleManager.getInstance().setMsgContent(msgList, new GeneralCallback() {
                        @Override
                        public void result(boolean b, int i) {
                            if (b) {
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x42);
                                }
                            } else {
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x43);
                                }
                            }
                        }

                        @Override
                        public void mutualFail(int i) {
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

        super.onDestroy();
    }
}
