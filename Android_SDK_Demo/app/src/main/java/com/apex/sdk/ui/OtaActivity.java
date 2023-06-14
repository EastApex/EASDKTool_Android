package com.apex.sdk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.OtaCallback;
import com.apex.bluetooth.callback.WatchInfoCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleAgps;
import com.apex.bluetooth.model.EABleOta;
import com.apex.bluetooth.model.EABleWatchInfo;
import com.apex.bluetooth.utils.AgpsUtils;
import com.apex.bluetooth.utils.LogUtils;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.model.WatchInfo;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OtaActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.firm_update)
    AppCompatButton firmButton;
    @BindView(R.id.dial_update)
    AppCompatButton dialButton;
    @BindView(R.id.agps_update)
    AppCompatButton agpsButton;
    @BindView(R.id.custom_dial)
    AppCompatButton customButton;
    @BindView(R.id.custom_diff_color_txt)
    AppCompatButton diffButton;
    private OkHttpClient okHttpClient;
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
                Toast.makeText(OtaActivity.this, getString(R.string.ota_success), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(OtaActivity.this, getString(R.string.ota_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(OtaActivity.this, getString(R.string.no_update), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(OtaActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x45) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
            } else if (msg.what == 0x46) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota);
        unbinder = ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.mipmap.exit_page);
        okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(OtaActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                List<EABleOta> otaList = new ArrayList<>();

                                //  if (!TextUtils.isEmpty(filePath)) {
                                //      Log.e(TAG, "文件大小:" + new File(filePath).length());
                                //  }
                                //  byte[] apolloByte = stream2Byte(getResources().openRawResource(R.raw.apollo));
                                //  Log.e(TAG, "转换之后的字节大小:" + apolloByte.length);
                                //将输入流转文件
/**
 InputStream apolloStream3 = getResources().openRawResource(R.raw.g03ap28);
 String filePath3 = save2File(apolloStream3, System.currentTimeMillis() + ".bin");
 EABleOta eaBleOta3 = new EABleOta();
 eaBleOta3.setOtaType(EABleOta.OtaType.apollo);
 eaBleOta3.setFilePath(filePath3);
 eaBleOta3.setVersion("AP0.1B2.8");
 eaBleOta3.setPop(true);
 otaList.add(eaBleOta3);


 InputStream apolloStream = getResources().openRawResource(R.raw.g03ap25);
 String filePath = save2File(apolloStream, System.currentTimeMillis() + ".bin");
 EABleOta eaBleOta = new EABleOta();
 eaBleOta.setOtaType(EABleOta.OtaType.apollo);
 eaBleOta.setFilePath(filePath);
 eaBleOta.setVersion("AP0.1B2.5");
 eaBleOta.setPop(true);
 otaList.add(eaBleOta);
 InputStream apolloStream2 = getResources().openRawResource(R.raw.g03ap27);
 String filePath2 = save2File(apolloStream2, System.currentTimeMillis() + ".bin");
 EABleOta eaBleOta2 = new EABleOta();
 eaBleOta2.setOtaType(EABleOta.OtaType.apollo);
 eaBleOta2.setFilePath(filePath2);
 eaBleOta2.setVersion("AP0.1B2.7");
 eaBleOta2.setPop(true);
 otaList.add(eaBleOta2);
 InputStream apolloStream1 = getResources().openRawResource(R.raw.g03ap26);
 String filePath1 = save2File(apolloStream1, System.currentTimeMillis() + ".bin");
 EABleOta eaBleOta1 = new EABleOta();
 eaBleOta1.setOtaType(EABleOta.OtaType.apollo);
 eaBleOta1.setFilePath(filePath1);
 eaBleOta1.setVersion("AP0.1B2.6");
 eaBleOta1.setPop(true);
 otaList.add(eaBleOta1);
 */
                                InputStream apolloStream5 = getResources().openRawResource(R.raw.g08ap22);
                                String filePath5 = save2File(apolloStream5, System.currentTimeMillis() + ".bin");
                                EABleOta eaBleOta5 = new EABleOta();
                                eaBleOta5.setOtaType(EABleOta.OtaType.apollo);
                                eaBleOta5.setFilePath(filePath5);
                                eaBleOta5.setVersion("AP0.1B2.2");
                                eaBleOta5.setPop(true);
                                otaList.add(eaBleOta5);

/**
 InputStream apolloStream4 = getResources().openRawResource(R.raw.zkg03r5);
 String filePath4 = save2File(apolloStream4, System.currentTimeMillis() + ".bin");
 EABleOta eaBleOta4 = new EABleOta();
 eaBleOta4.setOtaType(EABleOta.OtaType.res);
 eaBleOta4.setFilePath(filePath4);
 eaBleOta4.setVersion("R0.5");
 eaBleOta4.setPop(true);
 otaList.add(eaBleOta4);
 */

                                EABleManager.getInstance().otaUpdate(otaList, new OtaCallback() {
                                    @Override
                                    public void success() {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x40);
                                        }
                                    }

                                    @Override
                                    public void progress(int progress) {
                                        LogUtils.e(TAG, "当前进度:" + progress + "%");

                                    }

                                    @Override
                                    public void mutualFail(int errorCode) {
                                        Log.e(TAG, "失败:" + errorCode);
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x41);
                                        }
                                    }
                                });


                            } catch (Exception e) {
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x41);
                                }
                            }
                        }
                    }.start();

                }

            }
        });
        dialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(OtaActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                InputStream inputStream = getResources().openRawResource(R.raw.dial_240x240);
                                String filePath = save2File(inputStream, System.currentTimeMillis() + ".bin");
                                List<EABleOta> otaList = new ArrayList<>();
                                EABleOta eaBleOta = new EABleOta();
                                eaBleOta.setOtaType(EABleOta.OtaType.user_wf);
                                eaBleOta.setFilePath(filePath);
                                //  eaBleOta.setFileByte(dialByte);
                                otaList.add(eaBleOta);
                                EABleManager.getInstance().otaUpdate(otaList, new OtaCallback() {
                                    @Override
                                    public void success() {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x40);
                                        }
                                    }

                                    @Override
                                    public void progress(int progress) {
                                        LogUtils.e(TAG, "当前进度:" + progress + "%");
                                    }

                                    @Override
                                    public void mutualFail(int errorCode) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x41);
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x41);
                                }
                            }
                        }
                    }.start();

                }
            }
        });
        agpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {

                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(OtaActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                        LogUtils.e(TAG, "显示对话框");
                    }

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                InputStream qzssStream = getAssets().open("qzss-lle_qzss.lle");
                                InputStream gpsStream = getAssets().open("gps-lle_gps.lle");
                                InputStream glonassStream = getAssets().open("glonass-lle_glo.lle");
                                InputStream galineoStream = getAssets().open("galineo-lle_gal.lle");
                                InputStream bdStream = getAssets().open("bd-lle_bds.lle");
                                byte[] qzByte = stream2Byte(qzssStream);
                                byte[] gpsByte = stream2Byte(gpsStream);
                                byte[] gloByte = stream2Byte(glonassStream);
                                byte[] galByte = stream2Byte(galineoStream);
                                byte[] bdByte = stream2Byte(bdStream);
                                List<EABleAgps> gpsList = new ArrayList<>();
                                EABleAgps eaBlegps = new EABleAgps();
                                eaBlegps.setAgpsData(gpsByte);
                                eaBlegps.setAgpsType(EABleAgps.AgpsType.gps);
                                gpsList.add(eaBlegps);
                                EABleAgps eaBleBd = new EABleAgps();
                                eaBleBd.setAgpsData(bdByte);
                                eaBleBd.setAgpsType(EABleAgps.AgpsType.beidou);
                                gpsList.add(eaBleBd);
                                EABleAgps eaBleGa = new EABleAgps();
                                eaBleGa.setAgpsData(galByte);
                                eaBleGa.setAgpsType(EABleAgps.AgpsType.galileo);
                                gpsList.add(eaBleGa);
                                EABleAgps eaBleGl = new EABleAgps();
                                eaBleGl.setAgpsData(gloByte);
                                eaBleGl.setAgpsType(EABleAgps.AgpsType.glonass);
                                gpsList.add(eaBleGl);
                                EABleAgps eaBleQZ = new EABleAgps();
                                eaBleQZ.setAgpsData(qzByte);
                                eaBleQZ.setAgpsType(EABleAgps.AgpsType.qzss);
                                gpsList.add(eaBleQZ);
                                byte[] bytes = new AgpsUtils().packageAgpsFile(gpsList, 113.280637, 23.125178, 10);
                                if (bytes != null) {
                                    String filePath = save2File(bytes);
                                    if (!TextUtils.isEmpty(filePath)) {
                                        EABleOta eaBleOta = new EABleOta();
                                        //  eaBleOta.setFileByte(bytes);
                                        eaBleOta.setOtaType(EABleOta.OtaType.agps);
                                        eaBleOta.setFilePath(filePath);
                                        List<EABleOta> otaList = new ArrayList<>();
                                        otaList.add(eaBleOta);
                                        EABleManager.getInstance().otaUpdate(otaList, new OtaCallback() {
                                            @Override
                                            public void success() {
                                                if (mHandler != null) {
                                                    mHandler.sendEmptyMessage(0x40);
                                                }
                                            }

                                            @Override
                                            public void progress(int progress) {
                                                LogUtils.e(TAG, "当前进度:" + progress + "%");
                                            }

                                            @Override
                                            public void mutualFail(int errorCode) {
                                                LogUtils.e(TAG, "失败");
                                                if (mHandler != null) {
                                                    mHandler.sendEmptyMessage(0x41);
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(0x41);
                                    }
                                    LogUtils.e(TAG, "agps数据不存在");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x41);
                                }
                                LogUtils.e(TAG, "没有找到agps文件");
                            }
                        }
                    }.start();


                }

            }
        });
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 WatchInfo watchInfo = new WatchInfo();
                 watchInfo.lcd_full_w = 240;
                 watchInfo.lcd_full_h = 240;
                 watchInfo.lcd_preview_w = 160;
                 watchInfo.lcd_preview_h = 160;
                 watchInfo.lcd_preview_radius = watchInfo.lcd_preview_w / 2;
                 watchInfo.lcd_full_type = 1;
                 Intent intent = new Intent(OtaActivity.this, CustomDialActivity.class);
                 intent.putExtra("param", watchInfo);
                 startActivity(intent);
                 */

                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(OtaActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.watch_info, new WatchInfoCallback() {
                        @Override
                        public void watchInfo(EABleWatchInfo eaBleWatchInfo) {
                            Log.e(TAG, "当前手表信息:" + eaBleWatchInfo.toString());
                            WatchInfo watchInfo = new WatchInfo();
                            watchInfo.bindingInfo = eaBleWatchInfo.getBindingInfo();
                            watchInfo.watchId = eaBleWatchInfo.getWatchId();
                            watchInfo.watchType = eaBleWatchInfo.getWatchType();
                            watchInfo.agps_update_timestamp = eaBleWatchInfo.getAgps_update_timestamp();
                            watchInfo.ble_mac_addr = eaBleWatchInfo.getBle_mac_addr();
                            watchInfo.firmwareVersion = eaBleWatchInfo.getFirmwareVersion();
                            watchInfo.is_wait_for_binding = eaBleWatchInfo.getIs_wait_for_binding();
                            watchInfo.lcd_full_h = eaBleWatchInfo.getLcd_full_h();
                            watchInfo.lcd_full_w = eaBleWatchInfo.getLcd_full_w();
                            watchInfo.lcd_preview_radius = eaBleWatchInfo.getLcd_preview_radius();
                            watchInfo.lcd_full_type = eaBleWatchInfo.getLcd_full_type();
                            watchInfo.lcd_preview_h = eaBleWatchInfo.getLcd_preview_h();
                            watchInfo.lcd_preview_w = eaBleWatchInfo.getLcd_preview_w();
                            watchInfo.proj_settings = eaBleWatchInfo.getProj_settings();
                            watchInfo.lcd_pixel_type = eaBleWatchInfo.getLcd_pixel_type();
                            watchInfo.userId = eaBleWatchInfo.getUserId();
                            Intent intent = new Intent(OtaActivity.this, CustomDialActivity.class);
                            intent.putExtra("param", watchInfo);
                            startActivity(intent);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x45);
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
        diffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(OtaActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.watch_info, new WatchInfoCallback() {
                        @Override
                        public void watchInfo(EABleWatchInfo eaBleWatchInfo) {
                            Log.e(TAG, "当前手表信息:" + eaBleWatchInfo.toString());
                            WatchInfo watchInfo = new WatchInfo();
                            watchInfo.bindingInfo = eaBleWatchInfo.getBindingInfo();
                            watchInfo.watchId = eaBleWatchInfo.getWatchId();
                            watchInfo.watchType = eaBleWatchInfo.getWatchType();
                            watchInfo.agps_update_timestamp = eaBleWatchInfo.getAgps_update_timestamp();
                            watchInfo.ble_mac_addr = eaBleWatchInfo.getBle_mac_addr();
                            watchInfo.firmwareVersion = eaBleWatchInfo.getFirmwareVersion();
                            watchInfo.is_wait_for_binding = eaBleWatchInfo.getIs_wait_for_binding();
                            watchInfo.lcd_full_h = eaBleWatchInfo.getLcd_full_h();
                            watchInfo.lcd_full_w = eaBleWatchInfo.getLcd_full_w();
                            watchInfo.lcd_preview_radius = eaBleWatchInfo.getLcd_preview_radius();
                            watchInfo.lcd_full_type = eaBleWatchInfo.getLcd_full_type();
                            watchInfo.lcd_preview_h = eaBleWatchInfo.getLcd_preview_h();
                            watchInfo.lcd_preview_w = eaBleWatchInfo.getLcd_preview_w();
                            watchInfo.proj_settings = eaBleWatchInfo.getProj_settings();
                            watchInfo.lcd_pixel_type = eaBleWatchInfo.getLcd_pixel_type();
                            watchInfo.userId = eaBleWatchInfo.getUserId();
                            Intent intent = new Intent(OtaActivity.this, DiffColorTxtDialActivity.class);
                            intent.putExtra("param", watchInfo);
                            startActivity(intent);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x46);
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

    private String save2File(InputStream inputStream, String fName) {

        try {
            File file = new File(getExternalCacheDir(), fName);
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream os = new FileOutputStream(file);
            int read = 0;
            byte[] bytes = new byte[1024 * 1024];
            //先读后写
            while ((read = inputStream.read(bytes)) > 0) {
                byte[] wBytes = new byte[read];
                System.arraycopy(bytes, 0, wBytes, 0, read);
                os.write(wBytes);
            }
            os.flush();
            os.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String save2File(byte[] fileByte) {
        if (fileByte == null) {
            return null;
        }
        try {
            File file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileByte);
            fileOutputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] stream2Byte(@NonNull InputStream stream) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        try {
            while (-1 != (n = stream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        } catch (IOException e) {

        }
        return null;
    }

    private Map<String, Float> getWatchVersion(String watchVersion) {
        if (TextUtils.isEmpty(watchVersion)) {
            return null;
        }
        Map<String, Float> versionMap = new HashMap<String, Float>();
        versionMap.put("Apollo", Float.valueOf(watchVersion.substring(watchVersion.indexOf("AP") + 2, watchVersion.indexOf("B"))) * 1000 + Float.valueOf(watchVersion.substring(watchVersion.indexOf("B") + 1, watchVersion.indexOf("R"))) * 10);
        if (watchVersion.contains("T")) {
            versionMap.put("Res", Float.valueOf(watchVersion.substring(watchVersion.indexOf("R") + 1, watchVersion.indexOf("T"))));
            versionMap.put("Tp", Float.valueOf(watchVersion.substring(watchVersion.indexOf("T") + 1, watchVersion.indexOf("H"))));
        } else {
            versionMap.put("Res", Float.valueOf(watchVersion.substring(watchVersion.indexOf("R") + 1, watchVersion.indexOf("H"))));
        }
        // versionMap.put("Res", 0.4F);

        versionMap.put("Hr", Float.valueOf(watchVersion.substring(watchVersion.indexOf("H") + 1, watchVersion.indexOf("G"))));
        versionMap.put("Gps", Float.valueOf(watchVersion.substring(watchVersion.indexOf("G") + 1, watchVersion.length())));
        return versionMap;
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

    public Call get(@NonNull final String url) {
        Request request = new Request.Builder().url(url).build();
        Call requestCall = okHttpClient.newCall(request);
        requestCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // call.cancel();
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0x41);
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // call.cancel();
                if (response != null && response.code() == 200) {
                    try {
                        final String json = response.body().string();
                        if (!TextUtils.isEmpty(json)) {

                        }
                    } catch (Exception e) {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(0x41);
                        }
                    }

                } else {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x41);
                    }
                }
                response.close();
            }
        });
        return requestCall;
    }
}
