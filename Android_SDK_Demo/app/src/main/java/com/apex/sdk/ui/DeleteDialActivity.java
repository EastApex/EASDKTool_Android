package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActivityChooserView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.HeartCheckCallback;
import com.apex.bluetooth.callback.WatchFaceCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleWatchFace;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DeleteDialActivity extends AppCompatActivity {
    final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.dial_list)
    RecyclerView dialList;
    @BindView(R.id.submit)
    AppCompatButton deleteButton;
    List<String> dial;
    DialAdapter dialAdapter;
    private Handler mHandler = new Handler() {
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
                Log.e(TAG, "获取的表盘信息:" + JSONObject.toJSONString(eaBleWatchFace));
                dial = new ArrayList<>();
                if (!TextUtils.isEmpty(eaBleWatchFace.getUser_wf_id_0())) {
                    dial.add(eaBleWatchFace.getUser_wf_id_0());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.getUser_wf_id_1())) {
                    dial.add(eaBleWatchFace.getUser_wf_id_1());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.getUser_wf_id_2())) {
                    dial.add(eaBleWatchFace.getUser_wf_id_2());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.getUser_wf_id_3())) {
                    dial.add(eaBleWatchFace.getUser_wf_id_3());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.getUser_wf_id_4())) {
                    dial.add(eaBleWatchFace.getUser_wf_id_4());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.getUser_wf_id_5())) {
                    dial.add(eaBleWatchFace.getUser_wf_id_5());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.getUser_wf_id_6())) {
                    dial.add(eaBleWatchFace.getUser_wf_id_6());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.getUser_wf_id_7())) {
                    dial.add(eaBleWatchFace.getUser_wf_id_7());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.getUser_wf_id_8())) {
                    dial.add(eaBleWatchFace.getUser_wf_id_8());
                }
                if (!TextUtils.isEmpty(eaBleWatchFace.getUser_wf_id_9())) {
                    dial.add(eaBleWatchFace.getUser_wf_id_9());
                }
                dialAdapter.notifyDataSetChanged();
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
            } else if (msg.what == 0x42) {
                dialAdapter.notifyDataSetChanged();
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(DeleteDialActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_dial);
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DeleteDialActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        dialAdapter = new DialAdapter();
        dialList.setLayoutManager(linearLayoutManager);
        dialList.setAdapter(dialAdapter);
        EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
        if (state == EABleConnectState.STATE_CONNECTED) {
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(DeleteDialActivity.this);
            }
            waitingDialog.show();
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
                public void mutualFail(int i) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x41);
                    }
                }
            });
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (dial == null || dial.isEmpty()) {
                        return;
                    }
                    EABleManager.getInstance().deleteCustomDial(dial.get(0), new GeneralCallback() {
                        @Override
                        public void result(boolean b, int reason) {
                            dial.remove(0);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x42);
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

    class DialAdapter extends RecyclerView.Adapter<DialAdapter.DialHold> {
        @NonNull
        @Override
        public DialHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View childView = LayoutInflater.from(DeleteDialActivity.this).inflate(R.layout.adapter_menu_set, parent, false);
            return new DialHold(childView);
        }

        @Override
        public void onBindViewHolder(@NonNull DialHold holder, int position) {
            if (dial == null || dial.isEmpty()) {
                return;
            }
            holder.showText.setText(dial.get(position));

        }

        @Override
        public int getItemCount() {
            return dial == null ? 0 : dial.size();
        }

        class DialHold extends RecyclerView.ViewHolder {
            TextView showText;

            public DialHold(@NonNull View itemView) {
                super(itemView);
                showText = itemView.findViewById(R.id.menu_name);
            }
        }

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
