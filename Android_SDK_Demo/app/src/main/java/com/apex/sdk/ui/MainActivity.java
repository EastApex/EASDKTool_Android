package com.apex.sdk.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.listener.EABleScanListener;
import com.apex.bluetooth.model.EABleDevice;
import com.apex.sdk.BuildConfig;
import com.apex.sdk.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.device_list)
    RecyclerView deviceListView;
    private Unbinder unbinder;
    private List<EABleDevice> deviceList;
    private ItemAdapter itemAdapter;
    private final int CODE_PERMISSION = 0x22;
    private ScanDeviceListener scanDeviceListener;
    private final String TAG = this.getClass().getSimpleName();
    private int count;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x23) {
                finish();
            } else if (msg.what == 0x24) {
                if (deviceList == null || deviceList.isEmpty()) {
                    if (count >= 3) {
                        Toast.makeText(MainActivity.this, getString(R.string.no_equipment), Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessageDelayed(0x23, 3000);
                    } else {
                        count++;
                        mHandler.sendEmptyMessageDelayed(0x24, 1000);
                    }
                } else {
                    itemAdapter.notifyDataSetChanged();
                    mHandler.sendEmptyMessageDelayed(0x24, 1000);
                }
            } else if (msg.what == 0x25) {
                int flag = (int) msg.obj;
                switch (flag) {
                    case 0x09:
                        Toast.makeText(MainActivity.this, getString(R.string.unsupported), Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessageDelayed(0x23, 3000);
                        break;
                    case 0x07:
                        Toast.makeText(MainActivity.this, getString(R.string.closed), Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessageDelayed(0x23, 3000);
                        break;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
     //   Log.e(TAG,"R8版本:"+ BuildConfig.);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        deviceListView.setLayoutManager(linearLayoutManager);
        deviceList = new ArrayList<>();
        itemAdapter = new ItemAdapter();
        deviceListView.setAdapter(itemAdapter);
        if (!EABleManager.getInstance().isBLESupported(MainActivity.this)) {
            Toast.makeText(MainActivity.this, getString(R.string.unsupported), Toast.LENGTH_SHORT).show();
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(0x23, 3000);
            }
            return;
        }
        //   if (!EABleManager.getInstance().checkLocationPermission(MainActivity.this)) {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_PERMISSION);
        //       return;
        //   } else {
        //       scanBluetoothDevice();
        //   }


    }

    private void scanBluetoothDevice() {
        if (scanDeviceListener == null) {
            scanDeviceListener = new ScanDeviceListener();
        }
        EABleManager.getInstance().didDiscoverPeripheral(scanDeviceListener, MainActivity.this, true);
        //  if (mHandler != null) {
        //      mHandler.sendEmptyMessageDelayed(0x24, 1000);
        //  }
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (scanDeviceListener != null) {
            EABleManager.getInstance().stopScanPeripherals(MainActivity.this);
            scanDeviceListener = null;
        }
        itemAdapter = null;
        deviceList = null;
        super.onDestroy();
    }

    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHold> {
        @NonNull
        @Override
        public ItemHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.adapter_main_recyclerview, parent, false);
            return new ItemHold(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHold holder, int position) {
            if (deviceList == null && deviceList.isEmpty()) {
                return;
            }
            EABleDevice eaBleDevice = deviceList.get(position);
            if (TextUtils.isEmpty(eaBleDevice.getDeviceName())) {
                holder.nameText.setText(getString(R.string.unknown));
            } else {
                holder.nameText.setText(eaBleDevice.getDeviceName());
            }
            holder.serialText.setText(eaBleDevice.getDeviceSign());
            holder.addressText.setText(eaBleDevice.getDeviceAddress());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("blue_address", eaBleDevice.getDeviceAddress());
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return deviceList == null ? 0 : deviceList.size();
        }

        class ItemHold extends RecyclerView.ViewHolder {
            @BindView(R.id.device_name)
            TextView nameText;
            @BindView(R.id.device_address)
            TextView addressText;
            @BindView(R.id.device_serial)
            TextView serialText;

            public ItemHold(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_PHONE_NUMBERS}, 0x30);
            }
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, getString(R.string.location_permission), Toast.LENGTH_SHORT).show();
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0x23, 3000);
                    }
                    return;
                }
            }
            scanBluetoothDevice();
        }
    }

    class ScanDeviceListener implements EABleScanListener {
        @Override
        public void scanDevice(EABleDevice list) {
            if (deviceList == null) {
                return;
            }
            //  deviceList.clear();
            deviceList.add(list);
            // List<EABleDevice> dList = new ArrayList<>();
            // dList.addAll(list);
            // dList.removeAll(deviceList);
            // deviceList.addAll(dList);
            itemAdapter.notifyDataSetChanged();
        }

        @Override
        public void scanError(int i) {
            // if (mHandler != null) {
            //     Message message = mHandler.obtainMessage();
            //     message.obj = i;
            //     message.what = 0x25;
            //     mHandler.sendMessage(message);
            // }
        }
    }
}