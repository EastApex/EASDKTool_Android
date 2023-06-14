package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.WatchInfoCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleWatchInfo;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WatchInfoActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    Unbinder unbinder;
    @BindView(R.id.watch_id)
    TextView idText;
    @BindView(R.id.watch_type)
    TextView typeText;
    @BindView(R.id.firmware_version)
    TextView versionText;
    @BindView(R.id.binding_status)
    TextView statusText;
    @BindView(R.id.user_id)
    TextView userText;
    @BindView(R.id.agps_update_time)
    TextView updateText;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.blue_address)
    TextView addressText;
    @BindView(R.id.required_bind)
    TextView bindText;
    @BindView(R.id.function_list)
    TextView functionText;
    @BindView(R.id.screen_width)
    TextView widthText;
    @BindView(R.id.screen_height)
    TextView heightText;
    @BindView(R.id.screen_type)
    TextView screenTypeText;
    @BindView(R.id.preview_width)
    TextView previewWidthText;
    @BindView(R.id.preview_height)
    TextView previewHeightText;
    @BindView(R.id.thumbnail_fillet)
    TextView filletText;
    @BindView(R.id.sn_supported)
    TextView snText;
    @BindView(R.id.dial_size)
    TextView dialText;
    private WaitingDialog waitingDialog;
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
                idText.setText(eaBleWatchInfo.getWatchId());
                typeText.setText(eaBleWatchInfo.getWatchType());
                versionText.setText(eaBleWatchInfo.getFirmwareVersion());
                statusText.setText(eaBleWatchInfo.getBindingInfo() == EABleWatchInfo.BindingInfo.bound ? getString(R.string.bound) : getString(R.string.unbound));
                userText.setText(eaBleWatchInfo.getUserId());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(eaBleWatchInfo.getAgps_update_timestamp() * 1000);
                updateText.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
                addressText.setText(eaBleWatchInfo.getBle_mac_addr());
                bindText.setText(eaBleWatchInfo.getIs_wait_for_binding() == 0 ? getString(R.string.not_supported) : getString(R.string.support));
                functionText.setText(eaBleWatchInfo.getProj_settings() == 1 ? getString(R.string.support) : getString(R.string.not_supported));
                widthText.setText(eaBleWatchInfo.getLcd_full_w() + "");
                heightText.setText(eaBleWatchInfo.getLcd_full_h() + "");
                screenTypeText.setText(eaBleWatchInfo.getLcd_full_type() == 0 ? getString(R.string.square_screen) : getString(R.string.circular_screen));
                previewWidthText.setText(eaBleWatchInfo.getLcd_preview_w() + "");
                previewHeightText.setText(eaBleWatchInfo.getLcd_preview_h() + "");
                filletText.setText(eaBleWatchInfo.getLcd_preview_radius() + "");
                snText.setText(eaBleWatchInfo.getNot_support_sn() == 0 ? getString(R.string.support) : getString(R.string.not_supported));
                dialText.setText(eaBleWatchInfo.getMax_watch_size() <= 0 ? "-KB" : eaBleWatchInfo.getMax_watch_size() + "KB");

            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(WatchInfoActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_info);
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
                waitingDialog = new WaitingDialog(WatchInfoActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.watch_info, new WatchInfoCallback() {
                @Override
                public void watchInfo(EABleWatchInfo eaBleWatchInfo) {
                    Log.e(TAG, "手表信息:" + eaBleWatchInfo.getIs_wait_for_binding());
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
