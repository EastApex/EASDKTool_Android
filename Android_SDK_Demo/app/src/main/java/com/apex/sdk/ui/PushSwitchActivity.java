package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.InfoPushCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleInfoPush;
import com.apex.sdk.R;
import com.apex.sdk.dialog.SwitchDialog;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.model.AppPush;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PushSwitchActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private SwitchDialog ageDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.push_list)
    RecyclerView listView;
    List<AppPush> appList;
    private EABleInfoPush eaBleInfoPush;
    AdapterSwitch adapterSwitch;
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
                eaBleInfoPush = (EABleInfoPush) msg.obj;
                adapterSwitch.notifyDataSetChanged();
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(PushSwitchActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(PushSwitchActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_switch);
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
        appList = new ArrayList<>();
        String[] appName = getResources().getStringArray(R.array.app_list);
        for (int i = 0; i < appName.length; i++) {
            AppPush appPush = new AppPush();
            appPush.setAppName(appName[i]);
            appList.add(appPush);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PushSwitchActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        listView.setLayoutManager(linearLayoutManager);
        adapterSwitch = new AdapterSwitch();
        listView.setAdapter(adapterSwitch);

        EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
        if (state == EABleConnectState.STATE_CONNECTED) {
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(PushSwitchActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.push_info, new InfoPushCallback() {
                @Override
                public void pushInfo(EABleInfoPush eaBleInfoPush) {

                    List<EABleInfoPush.EABlePushSwitch> switchList = eaBleInfoPush.getS_app_sw();
                    if (switchList != null && !switchList.isEmpty()) {
                        for (int i = 0; i < switchList.size(); i++) {
                            if (i < appList.size()) {
                                appList.get(i).setOpen(switchList.get(i).getSw() == 1 ? true : false);
                            }

                        }
                    }
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleInfoPush;
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
        super.onDestroy();
    }

    class AdapterSwitch extends RecyclerView.Adapter<AdapterSwitch.ViewHold> {
        @NonNull
        @Override
        public ViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(PushSwitchActivity.this).inflate(R.layout.adapter_push_recyclerview, parent, false);
            return new ViewHold(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHold holder, final int position) {
            if (appList == null || appList.isEmpty()) {
                return;
            }
            final AppPush appPush = appList.get(position);
            holder.aSwitch.setOnCheckedChangeListener(null);
            holder.nameText.setText(appPush.getAppName());
            holder.aSwitch.setChecked(appPush.isOpen());
            holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (eaBleInfoPush.getS_app_sw().size() > position) {
                        if (waitingDialog == null) {
                            waitingDialog = new WaitingDialog(PushSwitchActivity.this);
                        }
                        if (!waitingDialog.isShowing()) {
                            waitingDialog.show();
                        }
                        eaBleInfoPush.getS_app_sw().get(position).setSw(isChecked ? 1 : 0);
                        EABleManager.getInstance().setAppPushSwitch(eaBleInfoPush, new GeneralCallback() {
                            @Override
                            public void result(boolean success,int reason) {
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
                    } else {
                        Toast.makeText(PushSwitchActivity.this, getString(R.string.device_unsupported), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return appList == null ? 0 : appList.size();
        }

        class ViewHold extends RecyclerView.ViewHolder {
            @BindView(R.id.app_name)
            TextView nameText;
            @BindView(R.id.app_switch)
            Switch aSwitch;

            public ViewHold(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
