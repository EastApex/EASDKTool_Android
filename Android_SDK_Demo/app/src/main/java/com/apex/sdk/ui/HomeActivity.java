package com.apex.sdk.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.apex.bluetooth.callback.BTMacCallback;
import com.apex.bluetooth.callback.DataReportCallback;
import com.apex.bluetooth.callback.DataResponseCallback;
import com.apex.bluetooth.callback.FeaturesCallback;
import com.apex.bluetooth.callback.MotionDataReportCallback;
import com.apex.bluetooth.callback.MotionDataResponseCallback;
import com.apex.bluetooth.callback.WatchInfoCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.CommonFlag;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.EABleSportStatus;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.listener.EABleConnectListener;
import com.apex.bluetooth.model.EABleBTSwitch;
import com.apex.bluetooth.model.EABleBloodOxygen;
import com.apex.bluetooth.model.EABleDailyData;
import com.apex.bluetooth.model.EABleExecutiveResponse;
import com.apex.bluetooth.model.EABleFeatures;
import com.apex.bluetooth.model.EABleGeneralSportRespond;
import com.apex.bluetooth.model.EABleGpsData;
import com.apex.bluetooth.model.EABleHabitRecord;
import com.apex.bluetooth.model.EABleHeartData;
import com.apex.bluetooth.model.EABleMotionHr;
import com.apex.bluetooth.model.EABleMtu;
import com.apex.bluetooth.model.EABleMultiData;
import com.apex.bluetooth.model.EABleMusicControl;
import com.apex.bluetooth.model.EABleMusicRespond;
import com.apex.bluetooth.model.EABlePaceData;
import com.apex.bluetooth.model.EABlePhoneResponse;
import com.apex.bluetooth.model.EABlePressureData;
import com.apex.bluetooth.model.EABleQueryMusic;
import com.apex.bluetooth.model.EABleReportMonitorData;
import com.apex.bluetooth.model.EABleReportSportData;
import com.apex.bluetooth.model.EABleRestingRateData;
import com.apex.bluetooth.model.EABleSleepData;
import com.apex.bluetooth.model.EABleSleepScore;
import com.apex.bluetooth.model.EABleSocialResponse;
import com.apex.bluetooth.model.EABleStepFrequencyData;
import com.apex.bluetooth.model.EABleSwitch;
import com.apex.bluetooth.model.EABleTimelyData;
import com.apex.bluetooth.model.EABleWatchInfo;
import com.apex.bluetooth.model.Sleep;
import com.apex.bluetooth.utils.LogData2File;
import com.apex.bluetooth.utils.LogUtils;
import com.apex.sdk.R;
import com.apex.sdk.dialog.QuickReplyDialog;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeActivity extends AppCompatActivity {
    private String blueAddress;
    private String btMacAddress;
    private Unbinder unbinder;
    @BindView(R.id.connect_state)
    TextView stateText;
    @BindView(R.id.function_list)
    RecyclerView functionListView;
    String[] apiList;
    private ItemAdapter itemAdapter;
    private final String TAG = this.getClass().getSimpleName();
    private boolean isConnected;
    private WaitingDialog waitingDialog;
    private QuickReplyDialog quickReplyDialog;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x31) {
                stateText.setText(getString(R.string.device_connected));
                //开始判断是否是新的BT
                queryWatchInfo();
            } else if (msg.what == 0x32) {
                stateText.setText(getString(R.string.connection_failed));
                Toast.makeText(HomeActivity.this, getString(R.string.not_found), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x33) {
                stateText.setText(getString(R.string.connection_failed));
                Toast.makeText(HomeActivity.this, getString(R.string.closed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x34) {
                stateText.setText(getString(R.string.connection_failed));
                Toast.makeText(HomeActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x35) {
                stateText.setText(getString(R.string.connection_failed));
                Toast.makeText(HomeActivity.this, getString(R.string.unsupported), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x36) {
                stateText.setText(getString(R.string.connection_failed));
                Toast.makeText(HomeActivity.this, getString(R.string.location_permission), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x37) {
                stateText.setText(getString(R.string.connection_failed));
                Toast.makeText(HomeActivity.this, getString(R.string.connection_timed_out), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x38) {
                stateText.setText(getString(R.string.disconnect));
                Toast.makeText(HomeActivity.this, getString(R.string.disconnect), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x39) {
                String info = (String) msg.obj;
                if (quickReplyDialog == null) {
                    quickReplyDialog = new QuickReplyDialog(HomeActivity.this);
                }
                if (!quickReplyDialog.isShowing()) {
                    quickReplyDialog.show();
                }
                quickReplyDialog.addContent(info);
            } else if (msg.what == 0x3A) {
                queryFeatureList();
            } else if (msg.what == 0x3B) {
                queryBTAddress();
            }
        }
    };

    private void queryWatchInfo() {
        if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.watch_info, new WatchInfoCallback() {
                @Override
                public void watchInfo(EABleWatchInfo eaBleWatchInfo) {
                    if (eaBleWatchInfo != null) {
                        if (eaBleWatchInfo.getProj_settings() == 1) {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x3A);
                            }
                        }
                    }
                }

                @Override
                public void mutualFail(int i) {
                    Log.e(TAG, "查询手表信息失败");

                }
            });
        }
    }

    private void queryFeatureList() {
        if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.features, new FeaturesCallback() {
                @Override
                public void featuresList(EABleFeatures eaBleFeatures) {
                    if (eaBleFeatures != null) {
                        if (eaBleFeatures.getBt_one_key_connect() == 1) {
                            if (eaBleFeatures.getSupport_get_bt_mac() == 1) {//需要初始化BT蓝牙
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x3B);
                                }
                            } else {
                                btMacAddress = blueAddress;
                            }
                        }
                    }
                }

                @Override
                public void mutualFail(int i) {
                    Log.e(TAG, "查询功能列表失败");
                }
            });
        }
    }

    private void queryBTAddress() {
        if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.bk_mac, new BTMacCallback() {
                @Override
                public void macData(String s) {
                    if (TextUtils.isEmpty(s) || "".equalsIgnoreCase(s) || !BluetoothAdapter.checkBluetoothAddress(s)) {
                        Log.e(TAG, "手表返回的mac地址错误");
                    } else {
                        btMacAddress = s;
                        Log.e(TAG, "蓝牙地址:" + btMacAddress);
                    }
                }

                @Override
                public void mutualFail(int i) {
                    Log.e(TAG, "查询bt地址失败");
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);
        blueAddress = getIntent().getStringExtra("blue_address");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        functionListView.setLayoutManager(linearLayoutManager);
        apiList = getResources().getStringArray(R.array.api_list);
        itemAdapter = new ItemAdapter();
        functionListView.setAdapter(itemAdapter);
        stateText.setText(getString(R.string.not_connected));
        LogData2File.getInstance().init(this);
        LogData2File.getInstance().setSaveLog(true);
        LogData2File.getInstance().setSaveOriginalData(true);
        if (TextUtils.isEmpty(blueAddress)) {
            Toast.makeText(HomeActivity.this, getString(R.string.device_address), Toast.LENGTH_SHORT).show();
        } else {
            EABleConnectState connectState = EABleManager.getInstance().getDeviceConnectState();
            if (connectState == EABleConnectState.STATE_IDLE || connectState == EABleConnectState.STATE_DISCONNECT) {
                try {
                    stateText.setText(getString(R.string.connecting));
                    //添加保存到数据库
                    EABleManager.getInstance().initDB(HomeActivity.this);
                    EABleManager.getInstance().saveBloodData(true);
                    EABleManager.getInstance().saveDailyData(true);
                    EABleManager.getInstance().saveHeartData(true);
                    EABleManager.getInstance().saveRestingHeartData(true);
                    EABleManager.getInstance().saveStressData(true);
                    EABleManager.getInstance().saveMultiData(true);
                    EABleManager.getInstance().saveSleepData(true);
                    EABleManager.getInstance().connectToPeripheral(blueAddress, HomeActivity.this, new DeviceConnectListener(), 128, new DataReportListener(), new MotionListener(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (connectState == EABleConnectState.STATE_CONNECTED) {
                    stateText.setText(getString(R.string.device_connected));
                    isConnected = true;
                }
            }

        }


    }

    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHold> {
        @NonNull
        @Override
        public ItemHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.adapter_home_recyclerview, parent, false);
            return new ItemHold(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHold holder, int position) {
            if (apiList == null) {
                return;
            }
            holder.apiName.setText(apiList[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isConnected) {
                        Toast.makeText(HomeActivity.this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String apiName = apiList[position];
                    if (getString(R.string.watch_information).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, WatchInfoActivity.class));

                    } else if (getString(R.string.user_information).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, UserInfoActivity.class));
                    } else if (getString(R.string.sync_time).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, SyncTimeActivity.class));
                    } else if (getString(R.string.binding).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, BindActivity.class));
                    } else if (getString(R.string.screen_brightness).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, ScreenBrightnessActivity.class));

                    } else if (getString(R.string.rest_screen_time).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, RestScreenActivity.class));

                    } else if (getString(R.string.power_information).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, PowerActivity.class));
                    } else if (getString(R.string.language_information).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, LanguageActivity.class));
                    } else if (getString(R.string.equipment_unit).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, DeviceUnitActivity.class));
                    } else if (getString(R.string.equipment_operation).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, DeviceActionActivity.class));
                    } else if (getString(R.string.do_not_disturb).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, NoDisturbActivity.class));
                    } else if (getString(R.string.sync_hometown_time).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, SyncHomeTimeActivity.class));
                    } else if (getString(R.string.daily_objectives).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, DailyObjectivesActivity.class));
                    } else if (getString(R.string.sleep_monitoring).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, SleepMonitoringActivity.class));
                    } else if (getString(R.string.heart_rate_monitoring).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, HeartReteActivity.class));
                    } else if (getString(R.string.sedentary_monitoring).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, SedentaryActivity.class));
                    } else if (getString(R.string.reminder_operation).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, PushInfoActivity.class));
                    } else if (getString(R.string.weather_information).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, WeatherActivity.class));
                    } else if (getString(R.string.reminder_switch).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, RemindActivity.class));
                    } else if (getString(R.string.alarm_clock).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, AlarmActivity.class));
                    } else if (getString(R.string.schedule).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, ScheduleActivity.class));
                    } else if (getString(R.string.distance_unit).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, DistanceUnitActivity.class));
                    } else if (getString(R.string.weight_unit).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, WeightUnitActivity.class));
                    } else if (getString(R.string.calorie_switch).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, CalorieSwitchActivity.class));
                    } else if (getString(R.string.bright_screen_switch).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, BrightScreenActivity.class));
                    } else if (getString(R.string.sync_motion_data).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, SyncMonitorActivity.class));
                    } else if (getString(R.string.combination_information).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, CombinationActivity.class));
                    } else if (getString(R.string.main_menu).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, MenuActivity.class));
                    } else if (getString(R.string.physiological_period).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, PeriodActivity.class));
                    } else if (getString(R.string.dial_switch).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, SystemDialActivity.class));
                    } else if (getString(R.string.push_switch).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, PushSwitchActivity.class));
                    } else if (getString(R.string.blood_pressure_calibration).equalsIgnoreCase(apiName)) {

                    } else if (getString(R.string.automatic_monitoring_information).equalsIgnoreCase(apiName)) {

                    } else if (getString(R.string.commissioning_information).equalsIgnoreCase(apiName)) {

                    } else if (getString(R.string.Habit_set).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, HabitSettingActivity.class));
                    } else if (getString(R.string.ota).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, OtaActivity.class));
                    } else if (getString(R.string.Today_sport_data).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, TodayTotalSportDataActivity.class));
                    } else if (getString(R.string.Add_contact).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, AddContactActivity.class));
                    } else if (getString(R.string.Add_reminder).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, MonitorReminderActivity.class));
                    } else if (getString(R.string.app_sport).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, AppSports.class));
                    } else if (getString(R.string.transfer_motion).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, SetAppSportData.class));
                    } else if (getString(R.string.check).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, CheckActivity.class));
                    } else if (getString(R.string.sync_motion_data_by_type).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, GetMotionDataByTypeActivity.class));
                    } else if (getString(R.string.sleep_blood_switch).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, SleepBloodMonitorActivity.class));
                    } else if (getString(R.string.stress_switch).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, StressMonitorActivity.class));
                    } else if (getString(R.string.time_data_report_switch).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, TimeDataSwitchActivity.class));
                    } else if (getString(R.string.vibration_mode).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, VibrateModeActivity.class));
                    } else if (getString(R.string.watch_sport).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, WatchSportActivity.class));
                    } else if (getString(R.string.physiological_reminder).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, PhysiologicalReminder.class));
                    } else if (getString(R.string.delete_custom_dial).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, DeleteDialActivity.class));
                    } else if (getString(R.string.motion_alarm_heart).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, MotionHeartAlarmActivity.class));
                    } else if (getString(R.string.social_contact).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, SosContactActivity.class));
                    } else if (getString(R.string.bt).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, BTActivity.class));
                    } else if (getString(R.string.reply_message).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, MsgActivity.class));
                    } else if (getString(R.string.bk_address).equalsIgnoreCase(apiName)) {
                        startActivity(new Intent(HomeActivity.this, GetBKAddressActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, getString(R.string.unknown), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return apiList == null ? 0 : apiList.length;
        }

        class ItemHold extends RecyclerView.ViewHolder {
            @BindView(R.id.api_name)
            TextView apiName;

            public ItemHold(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        LogUtils.e(TAG, "主页销毁");
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
        if (quickReplyDialog != null) {
            quickReplyDialog.destroy();
            quickReplyDialog = null;
        }
        EABleManager.getInstance().disconnectPeripheral();
        itemAdapter = null;
        apiList = null;
        super.onDestroy();
    }

    public class DeviceConnectListener implements EABleConnectListener {
        private final String TAG = this.getClass().getSimpleName();

        @Override
        public void deviceConnected() {
            isConnected = true;
            if (mHandler != null) {
                mHandler.sendEmptyMessage(0x31);
            }
        }

        @Override
        public void deviceDisconnect() {
            isConnected = false;
            LogUtils.e(TAG, "断开连接");
            if (mHandler != null) {
                mHandler.sendEmptyMessage(0x38);
            }

        }

        @Override
        public void deviceNotFind() {
            isConnected = false;
            if (mHandler != null) {
                mHandler.sendEmptyMessage(0x32);
            }

        }

        @Override
        public void unopenedBluetooth() {
            isConnected = false;
            if (mHandler != null) {
                mHandler.sendEmptyMessage(0x33);
            }

        }

        @Override
        public void connectError(int errorCode) {
            isConnected = false;
            if (mHandler != null) {
                mHandler.sendEmptyMessage(0x34);
            }

        }

        @Override
        public void unsupportedBLE() {
            isConnected = false;
            if (mHandler != null) {
                mHandler.sendEmptyMessage(0x35);
            }

        }

        @Override
        public void notOpenLocation() {
            isConnected = false;
            if (mHandler != null) {
                mHandler.sendEmptyMessage(0x36);
            }

        }

        @Override
        public void connectTimeOut() {
            isConnected = false;
            if (mHandler != null) {
                mHandler.sendEmptyMessage(0x37);
            }

        }
    }

    public class DataReportListener implements DataReportCallback {

        @Override
        public void searchPhone() {//Watch search mobile phone,The app does not need to reply
            Log.e(TAG, "search phone");

        }

        @Override
        public void stopSearchPhone() {//Watch search mobile phone,The app does not need to reply
            Log.e(TAG, " stop search phone");
        }

        @Override
        public void connectCamera() {//Need reply
            Log.e(TAG, "connect camera");
            EABlePhoneResponse eaBlePhoneResponse = new EABlePhoneResponse();
            eaBlePhoneResponse.setEaBleExecutiveResponse(EABleExecutiveResponse.success);
            eaBlePhoneResponse.setId(2);
            EABleManager.getInstance().mobileOperationResponse(eaBlePhoneResponse, new DataResponseCallback() {
                @Override
                public void mutualSuccess() {

                }

                @Override
                public void mutualFail(int errorCode) {

                }
            });
        }

        @Override
        public void takePhoto() {//Need reply
            Log.e(TAG, "take photo");
            EABlePhoneResponse eaBlePhoneResponse = new EABlePhoneResponse();
            eaBlePhoneResponse.setEaBleExecutiveResponse(EABleExecutiveResponse.success);
            eaBlePhoneResponse.setId(3);
            EABleManager.getInstance().mobileOperationResponse(eaBlePhoneResponse, new DataResponseCallback() {
                @Override
                public void mutualSuccess() {

                }

                @Override
                public void mutualFail(int errorCode) {

                }
            });
        }

        @Override
        public void endTakePhoto() {//Need reply
            Log.e(TAG, "end take photo");
            EABlePhoneResponse eaBlePhoneResponse = new EABlePhoneResponse();
            eaBlePhoneResponse.setEaBleExecutiveResponse(EABleExecutiveResponse.success);
            eaBlePhoneResponse.setId(4);
            EABleManager.getInstance().mobileOperationResponse(eaBlePhoneResponse, new DataResponseCallback() {
                @Override
                public void mutualSuccess() {

                }

                @Override
                public void mutualFail(int errorCode) {

                }
            });
        }

        @Override
        public void updateWeather() {//Need reply,Then synchronize the weather data to the device
            Log.e(TAG, "update weather");
            //replay

            //Synchronize the weather. Please check the weather information for details

        }

        @Override
        public void circadian() {//Need reply,Then Synchronize physiological period data to the watch
            Log.e(TAG, "Physiological period");
        }

        @Override
        public void updateAgps() {//Need reply, Then Update AGPs
            //reply

            //Update AGPs. For details, please see AGPs update in Ota
        }

        @Override
        public void transmissionComplete() {//The app does not need to reply,After receiving the entry command,
            // it indicates that the interaction between the watch motion data and the app is completed. At this time, the app can process the motion data
            Log.e(TAG, "transmissionComplete");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            List<Sleep> sleepList = EABleManager.getInstance().querySleepData(calendar.getTimeInMillis());
            if (sleepList != null && !sleepList.isEmpty()) {
                Log.e(TAG, "获取到的睡眠数据:" + JSONObject.toJSONString(sleepList));
            }


        }

        @Override
        public void stopSearchWatch() {
            Log.e(TAG, "停止查找手表");
        }

        @Override
        public void queryMusic(EABleQueryMusic eaBleQueryMusic) {
            Log.e(TAG, "query Music");
            EABleMusicRespond eaBleMusicRespond = new EABleMusicRespond();
            eaBleMusicRespond.setArtist("Singer");
            eaBleMusicRespond.setContent("Music name");
            eaBleMusicRespond.setDuration(250);
            eaBleMusicRespond.setVolume(30);
            eaBleMusicRespond.setE_status(EABleMusicRespond.MusicStatus.playing);
            eaBleMusicRespond.setElapsedtime(100);
            EABleManager.getInstance().musicQueryResponse(eaBleMusicRespond, new DataResponseCallback() {
                @Override
                public void mutualSuccess() {

                }

                @Override
                public void mutualFail(int errorCode) {

                }
            });
        }

        @Override
        public void musicControl(EABleMusicControl eaBleMusicControl) {
            Log.e(TAG, "music Control");
            //Watch control player
            EABleMusicControl.MusicControl musicControl = eaBleMusicControl.getE_ops();
            if (musicControl != null) {
                if (musicControl == EABleMusicControl.MusicControl.next_song) {
                    LogUtils.i(TAG, "Next song");
                } else if (musicControl == EABleMusicControl.MusicControl.previous_song) {
                    LogUtils.i(TAG, " Previous song");
                } else if (musicControl == EABleMusicControl.MusicControl.play_start) {
                    LogUtils.i(TAG, "Start playing");
                } else if (musicControl == EABleMusicControl.MusicControl.play_stop) {
                    LogUtils.i(TAG, "Stop playing");
                } else if (musicControl == EABleMusicControl.MusicControl.volume_up) {
                    LogUtils.i(TAG, "VOL UP");
                } else if (musicControl == EABleMusicControl.MusicControl.volume_reduction) {
                    LogUtils.i(TAG, "VOL DN");
                }
            }
            //reply
            EABleMusicRespond eaBleMusicRespond = new EABleMusicRespond();
            eaBleMusicRespond.setArtist("Singer");
            eaBleMusicRespond.setContent("Music name");
            eaBleMusicRespond.setDuration(250);
            eaBleMusicRespond.setVolume(30);
            eaBleMusicRespond.setE_status(EABleMusicRespond.MusicStatus.playing);
            eaBleMusicRespond.setElapsedtime(100);
            EABleManager.getInstance().musicQueryResponse(eaBleMusicRespond, new DataResponseCallback() {
                @Override
                public void mutualSuccess() {

                }

                @Override
                public void mutualFail(int errorCode) {

                }
            });
        }

        @Override
        public void socialResponse(EABleSocialResponse eaBleSocialResponse) {//The social Reply of the watch is a reserved item and is not used for the time being
            Log.e(TAG, "回复内容:" + eaBleSocialResponse.getContent());
            if (eaBleSocialResponse != null) {
                String msg = eaBleSocialResponse.getContent();
                if (!TextUtils.isEmpty(msg) && !"".equalsIgnoreCase(msg)) {
                    if (mHandler != null) {
                        Message message = new Message();
                        message.what = 0x39;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }

                }
            }

        }

        @Override
        public void mtu(EABleMtu eaBleMtu) {

        }

        /**
         * Answer the phone   You need to implement it yourself
         */
        @Override
        public void answerIncoming() {
            Log.e(TAG, "接听.....");

        }

        /**
         * Hang up  You need to implement it yourself
         */
        @Override
        public void hangUpIncoming() {
            Log.e(TAG, "挂断.....");
        }


        @Override
        public void appSportStatus(EABleSportStatus sportStatus) {
            Log.e(TAG, "当前上报的App运动状态:" + sportStatus.toString());

        }

        @Override
        public void timelyData(EABleTimelyData eaBleTimelyData) {
            Log.e(TAG, "时时数据:" + eaBleTimelyData.toString());

        }

        @Override
        public void appSportData(EABleReportSportData eaBleReportSportData) {
            Log.e(TAG, "app运动上报数据:" + eaBleReportSportData.toString());

        }

        @Override
        public void disturbStatus(EABleSwitch eaBleSwitch) {
            Log.e(TAG, "勿扰模式开关状态:" + eaBleSwitch.toString());

        }

        @Override
        public void brightScreenStatus(EABleSwitch eaBleSwitch) {
            Log.e(TAG, "抬手亮屏开关状态:" + eaBleSwitch.toString());
        }

        @Override
        public void reportMonitorData(EABleReportMonitorData eaBleReportMonitorData) {
            Log.e(TAG, "手表开启心率,血氧,压力,呼吸的结果上报");
        }

        @Override
        public void startBTConnect() {
            Log.e(TAG, "开始连接BT");
            if (TextUtils.isEmpty(btMacAddress) || "".equalsIgnoreCase(btMacAddress) || !BluetoothAdapter.checkBluetoothAddress(btMacAddress)) {
                Log.e(TAG, "bk地址不存在或者地址不合法");
            } else {
                Intent intent = new Intent("east.apex.bt.connect");
                intent.putExtra("bt_address", btMacAddress);
                intent.setPackage(getPackageName());
                sendBroadcast(intent);
            }
        }

        @Override
        public void btStatus(EABleBTSwitch eaBleSwitch) {
            Log.e(TAG, "BT状态:" + eaBleSwitch.getValue());
        }

        @Override
        public void mutualFail(int errorCode) {

        }
    }

    public class MotionListener implements MotionDataReportCallback {
        @Override
        public void dailyExerciseData(List<EABleDailyData> sportList, CommonFlag mCommon) {
            if (sportList != null && !sportList.isEmpty()) {
                Log.e(TAG, "日常:" + JSONObject.toJSONString(sportList));

            }
            replayWatch(3001, mCommon);

        }

        @Override
        public void sleepData(List<EABleSleepData> sleepList, CommonFlag mCommon) {
            if (sleepList != null && !sleepList.isEmpty()) {
                Log.e(TAG, "睡眠:" + JSONObject.toJSONString(sleepList));
            }
            replayWatch(3002, mCommon);

        }

        @Override
        public void heartData(List<EABleHeartData> heartList, CommonFlag mCommon) {
            if (heartList != null && !heartList.isEmpty()) {
                Log.e(TAG, "心率:" + JSONObject.toJSONString(heartList));

            }
            replayWatch(3003, mCommon);
        }

        @Override
        public void gpsData(List<EABleGpsData> gpsList, CommonFlag mCommon) {
            if (gpsList != null && !gpsList.isEmpty()) {
                Log.e(TAG, "GPS:" + JSONObject.toJSONString(gpsList));

            }
            replayWatch(3004, mCommon);
        }

        @Override
        public void multiMotionData(List<EABleMultiData> multiList, CommonFlag mCommon) {
            if (multiList != null && !multiList.isEmpty()) {
                Log.e(TAG, "多运动:" + JSONObject.toJSONString(multiList));
            }
            replayWatch(3005, mCommon);
        }

        @Override
        public void bloodOxygenData(List<EABleBloodOxygen> oxygenList, CommonFlag mCommon) {
            if (oxygenList != null && !oxygenList.isEmpty()) {
                Log.e(TAG, "血氧:" + JSONObject.toJSONString(oxygenList));

            }
            replayWatch(3006, mCommon);
        }

        @Override
        public void pressureData(List<EABlePressureData> pressureList, CommonFlag mCommon) {
            if (pressureList != null && !pressureList.isEmpty()) {
                Log.e(TAG, "压力:" + JSONObject.toJSONString(pressureList));

            }
            replayWatch(3007, mCommon);
        }

        @Override
        public void stepFrequencyData(List<EABleStepFrequencyData> stepFreqList, CommonFlag mCommon) {
            if (stepFreqList != null && !stepFreqList.isEmpty()) {
                Log.e(TAG, "步频:" + JSONObject.toJSONString(stepFreqList));

            }
            replayWatch(3008, mCommon);

        }

        @Override
        public void speedData(List<EABlePaceData> paceList, CommonFlag mCommon) {
            if (paceList != null && !paceList.isEmpty()) {
                Log.e(TAG, "配速:" + JSONObject.toJSONString(paceList));

            }
            replayWatch(3009, mCommon);
        }

        @Override
        public void restingHeartRateData(List<EABleRestingRateData> restingList, CommonFlag mCommon) {
            if (restingList != null && !restingList.isEmpty()) {
                Log.e(TAG, "静息心率:" + JSONObject.toJSONString(restingList));

            }
            replayWatch(3010, mCommon);
        }

        @Override
        public void getHabitData(List<EABleHabitRecord> habitList, CommonFlag commonFlag) {
            if (habitList != null && !habitList.isEmpty()) {
                for (int i = 0; i < habitList.size(); i++) {
                    Log.e(TAG, "习惯记录:" + habitList.get(i).toString());
                }
            }
            replayWatch(3011, commonFlag);
        }

        @Override
        public void sleepScore(List<EABleSleepScore> list, CommonFlag commonFlag) {
            if (list != null && !list.isEmpty()) {
                Log.e(TAG, "睡眠得分:" + JSONObject.toJSONString(list));
            }
            replayWatch(3012, commonFlag);
        }

        @Override
        public void motionHr(List<EABleMotionHr> list, CommonFlag commonFlag) {
            if (list != null && !list.isEmpty()) {
                Log.e(TAG, "运动心率:" + JSONObject.toJSONString(list));
            }
            replayWatch(3013, commonFlag);
        }

        @Override
        public void mutualFail(int errorCode) {

        }
    }

    private void replayWatch(final int flag, final CommonFlag commonFlag) {
        EABleGeneralSportRespond eaBleGeneralSportRespond = new EABleGeneralSportRespond();
        eaBleGeneralSportRespond.setRequest_id(flag);
        eaBleGeneralSportRespond.setE_common_flag(commonFlag);
        if (isConnected) {
            EABleManager.getInstance().motionDataResponse(eaBleGeneralSportRespond, new MotionDataResponseCallback() {
                @Override
                public void mutualSuccess() {
                    LogUtils.i(TAG, "大数据回应成功");
                }

                @Override
                public void mutualFail(int errorCode) {
                    LogUtils.i(TAG, "大数据回应失败,可以尝试再次回应或者重新同步数据");
                }
            });
        }
    }
}
