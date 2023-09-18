package com.apex.sdk.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apex.bluetooth.callback.ChangeWatchIdCallback;
import com.apex.bluetooth.callback.OtaCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.model.EABleOta;
import com.apex.bluetooth.utils.LogUtils;
import com.apex.sdk.R;
import com.apex.sdk.dialog.DialSelectPicDialog;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.model.WatchInfo;
import com.apex.sdk.utils.SizeTransform;
import com.bumptech.glide.Glide;
import com.example.custom_dial.CustomDialCallback;
import com.example.custom_dial.CustomDiffTxtColorDialParam;
import com.example.custom_dial.CustomPointDialParam;
import com.example.custom_dial.DialStyle;
import com.example.custom_dial.NewRGBAPlatformDiffTxtUtils;
import com.example.custom_dial.RGBAPlatformDiffTxtUtils;
import com.example.custom_dial.RGBAPointUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CustomDialActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.preview)
    AppCompatImageView previewImage;
    @BindView(R.id.select_back)
    AppCompatImageView backImage;
    @BindView(R.id.zodiac_list)
    RecyclerView styleList;
    @BindView(R.id.install)
    AppCompatButton installButton;
    private List<StyleItem> styleData;
    private Bitmap backBitmap;
    private WatchInfo watchInfo;
    private DialSelectPicDialog dialSelectPicDialog;
    private final String DIAL_BG = "dial_bg.png";
    private final int IMAGE_REQUEST_CODE = 0;
    private final int CAMERA_REQUEST_CODE = 1;
    private final int RESULT_REQUEST_CODE = 2;
    private final int PERMISSION_CODE = 3;
    private Uri imageUri;
    private DialStyle currentDialStyle = DialStyle.blackTxt;
    private boolean isInstalling;
    private String filePath;
    NewRGBAPlatformDiffTxtUtils rgbaPlatformDiffTxtUtils;
    int currentTxtColor = Color.BLACK;
    RGBAPointUtils rgbaPointUtils;
    Bitmap pBitmap;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x51) {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                pBitmap = (Bitmap) msg.obj;
                if (pBitmap != null) {
                    Log.e(TAG, "生成缩略图大图的宽:" + pBitmap.getWidth() + ",高:" + pBitmap.getHeight());
                    Glide.with(CustomDialActivity.this).load(pBitmap).into(previewImage);
                }
            } else if (msg.what == 0x52) {
                //如果需要修改表盘,请使用修改表盘的api,如果不需要修改表盘,可以直接使用OTA
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            // byte[] fileByte = file2Byte();
                            // if (fileByte != null) {
                            List<EABleOta> otaList = new ArrayList<>();
                            EABleOta eaBleOta = new EABleOta();
                            eaBleOta.setOtaType(EABleOta.OtaType.user_wf);
                            eaBleOta.setFilePath(filePath);
                            eaBleOta.setPop(true);
                            //  eaBleOta.setFileByte(fileByte);
                            otaList.add(eaBleOta);
                            EABleManager.getInstance().otaUpdate(otaList, new OtaCallback() {
                                @Override
                                public void success() {
                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(0x53);
                                    }
                                }

                                @Override
                                public void progress(int progress) {
                                    LogUtils.e(TAG, "当前进度:" + progress + "%");
                                }

                                @Override
                                public void mutualFail(int errorCode) {
                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(0x54);
                                    }
                                }
                            });
                            //   } else {
                            //       Log.e(TAG, "文件转换的字节数组不存在");
                            //   }


                        }
                    }.start();
                } else {
                    isInstalling = false;
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    installButton.setVisibility(View.VISIBLE);
                    if (waitingDialog != null && waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    Toast.makeText(CustomDialActivity.this, getString(R.string.disconnect), Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 0x53) {
                isInstalling = false;
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                installButton.setVisibility(View.VISIBLE);
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(CustomDialActivity.this, getString(R.string.ota_success), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x54) {
                isInstalling = false;
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                installButton.setVisibility(View.VISIBLE);
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(CustomDialActivity.this, getString(R.string.ota_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x55) {
                EABleManager.getInstance().modifyWatchFaceId("10", filePath, new ChangeWatchIdCallback() {
                    @Override
                    public void watchFace(String s) {
                        if (TextUtils.isEmpty(s) || "".equalsIgnoreCase(s)) {
                            if (waitingDialog != null) {
                                waitingDialog.dismiss();
                            }
                            Toast.makeText(CustomDialActivity.this, getString(R.string.Create_dial_fail), Toast.LENGTH_SHORT).show();
                        } else {
                            filePath = s;
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x52);
                            }
                        }
                    }
                });
            }
        }
    };

    private byte[] file2Byte() {
        byte[] fileByte = null;
        if (!TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "文件地址存在");
            try {
                FileInputStream fis = new FileInputStream(filePath);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                fis.close();
                bos.close();
                fileByte = bos.toByteArray();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Log.e(TAG, "文件地址不存在");
        }

        return fileByte;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dial);
        watchInfo = (WatchInfo) getIntent().getSerializableExtra("param");
        unbinder = ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.mipmap.exit_page);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        rgbaPlatformDiffTxtUtils = new NewRGBAPlatformDiffTxtUtils(CustomDialActivity.this, watchInfo.lcd_pixel_type == 1 ? true : false);
        rgbaPlatformDiffTxtUtils.showData(true);
        Log.e(TAG, "显示日期");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    initDialog();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                }
            }
        });
        installButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (backBitmap == null || currentDialStyle == null) {
                    return;
                }

                installButton.setVisibility(View.GONE);
                isInstalling = true;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                if (waitingDialog == null) {
                    waitingDialog = new WaitingDialog(CustomDialActivity.this);
                }
                if (!waitingDialog.isShowing()) {
                    waitingDialog.show();
                }


                if (currentDialStyle == DialStyle.blackTxt || currentDialStyle == DialStyle.whiteTxt) {
                    CustomDiffTxtColorDialParam customDiffTxtColorDialParam = new CustomDiffTxtColorDialParam();
                    customDiffTxtColorDialParam.setBackBitmap(backBitmap);
                    customDiffTxtColorDialParam.setTxtColor(currentTxtColor);
                    customDiffTxtColorDialParam.setpHigh(watchInfo.lcd_preview_h);
                    customDiffTxtColorDialParam.setpWidth(watchInfo.lcd_preview_w);
                    customDiffTxtColorDialParam.setScreenHigh(watchInfo.lcd_full_h);
                    customDiffTxtColorDialParam.setScreenWidth(watchInfo.lcd_full_w);
                    customDiffTxtColorDialParam.setCornerRadius(watchInfo.lcd_preview_radius);
                    customDiffTxtColorDialParam.setScreenType(watchInfo.lcd_full_type);
                    customDiffTxtColorDialParam.setPreviewBitmap(pBitmap);
                    if (watchInfo.lcd_full_w == 240) {
                        if (watchInfo.lcd_full_type == 1) {
                            customDiffTxtColorDialParam.setStartX(58);
                            customDiffTxtColorDialParam.setStartY(30);
                            customDiffTxtColorDialParam.setWx(80);
                            customDiffTxtColorDialParam.setWy(84);
                            customDiffTxtColorDialParam.setDateX(133);
                        } else {
                            customDiffTxtColorDialParam.setStartX(47);
                            customDiffTxtColorDialParam.setStartY(20);
                            customDiffTxtColorDialParam.setWx(75);
                            customDiffTxtColorDialParam.setWy(70);
                            customDiffTxtColorDialParam.setDateX(131);
                        }

                    } else if (watchInfo.lcd_full_w == 320) {
                        customDiffTxtColorDialParam.setStartX(85);
                        customDiffTxtColorDialParam.setStartY(20);
                        customDiffTxtColorDialParam.setWx(90);
                        customDiffTxtColorDialParam.setWy(95);
                        customDiffTxtColorDialParam.setDateX(150);
                    } else if (watchInfo.lcd_full_w == 356) {
                        customDiffTxtColorDialParam.setStartX(88);
                        customDiffTxtColorDialParam.setStartY(20);
                        customDiffTxtColorDialParam.setWx(75);
                        customDiffTxtColorDialParam.setWy(95);
                        customDiffTxtColorDialParam.setDateX(155);
                    } else if (watchInfo.lcd_full_w == 368) {
                        customDiffTxtColorDialParam.setStartX(93);
                        customDiffTxtColorDialParam.setStartY(20);
                        customDiffTxtColorDialParam.setWx(95);
                        customDiffTxtColorDialParam.setWy(95);
                        customDiffTxtColorDialParam.setDateX(160);
                    } else if (watchInfo.lcd_full_w == 412) {
                        customDiffTxtColorDialParam.setStartX(107);
                        customDiffTxtColorDialParam.setStartY(20);
                        customDiffTxtColorDialParam.setWx(115);
                        customDiffTxtColorDialParam.setWy(110);
                        customDiffTxtColorDialParam.setDateX(195);
                    } else if (watchInfo.lcd_full_w == 466) {
                        customDiffTxtColorDialParam.setStartX(127);
                        customDiffTxtColorDialParam.setStartY(30);
                        customDiffTxtColorDialParam.setWx(140);
                        customDiffTxtColorDialParam.setWy(140);
                        customDiffTxtColorDialParam.setDateX(260);
                    }
                    rgbaPlatformDiffTxtUtils.produceDialBin(customDiffTxtColorDialParam, new CustomDialCallback() {
                        @Override
                        public void dialPath(String dPath) {
                            if (TextUtils.isEmpty(dPath)) {
                                if (waitingDialog != null) {
                                    waitingDialog.dismiss();
                                }
                                Toast.makeText(CustomDialActivity.this, getString(R.string.Create_dial_fail), Toast.LENGTH_SHORT).show();
                            } else {
                                filePath = dPath;
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x55);
                                }

                            }
                        }
                    });
                } else if (currentDialStyle == DialStyle.blackPointer || currentDialStyle == DialStyle.whitePointer) {
                    if (rgbaPointUtils == null) {
                        rgbaPointUtils = new RGBAPointUtils(CustomDialActivity.this, watchInfo.lcd_pixel_type == 1 ? true : false);
                    }
                    CustomPointDialParam customPointDialParam = new CustomPointDialParam();
                    customPointDialParam.setBackBitmap(backBitmap);
                    customPointDialParam.setCornerRadius(watchInfo.lcd_preview_radius);
                    customPointDialParam.setPreviewHigh(watchInfo.lcd_preview_h);
                    customPointDialParam.setPreviewWidth(watchInfo.lcd_preview_w);
                    customPointDialParam.setScreenHigh(watchInfo.lcd_full_h);
                    customPointDialParam.setScreenType(watchInfo.lcd_full_type);
                    customPointDialParam.setScreenWidth(watchInfo.lcd_full_w);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;
                    Bitmap tBit = null, hourBit = null, minuteBit = null, secondBit = null;

                    if (watchInfo.lcd_full_w == 240) {
                        if (currentDialStyle == DialStyle.blackPointer) {
                            if (watchInfo.lcd_full_h == 240) {
                                tBit = BitmapFactory.decodeResource(getResources(), R.drawable.thum_black_point_240, options);
                                hourBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_hour_black_240_circle, options);
                                minuteBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_minute_black_240_circle, options);
                                secondBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_second_black_240_circle, options);
                                customPointDialParam.sethTop(65);
                                customPointDialParam.setmTop(23);
                                customPointDialParam.setsTop(9);
                            } else {
                                tBit = BitmapFactory.decodeResource(getResources(), R.drawable.thum_black_point_240, options);
                                hourBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_hour_240_black, options);
                                minuteBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_minute_black_240, options);
                                secondBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_second_black_240, options);
                                customPointDialParam.sethTop(83);
                                customPointDialParam.setmTop(40);
                                customPointDialParam.setsTop(27);
                            }


                        } else {
                            if (watchInfo.lcd_full_h != 240) {
                                tBit = BitmapFactory.decodeResource(getResources(), R.drawable.thum_white_point_240, options);
                                hourBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_hour_white_240, options);
                                minuteBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_minute_white_240, options);
                                secondBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_second_white_240, options);
                                customPointDialParam.sethTop(83);
                                customPointDialParam.setmTop(40);
                                customPointDialParam.setsTop(27);
                            } else {
                                tBit = BitmapFactory.decodeResource(getResources(), R.drawable.thum_white_point_240, options);
                                hourBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_hour_white_240_circle, options);
                                minuteBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_minute_white_240_circle, options);
                                secondBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_second_white_240_circle, options);
                                customPointDialParam.sethTop(65);
                                customPointDialParam.setmTop(23);
                                customPointDialParam.setsTop(9);
                            }
                        }

                    } else if (watchInfo.lcd_full_w == 356) {
                        if (currentDialStyle == DialStyle.blackPointer) {
                            tBit = BitmapFactory.decodeResource(getResources(), R.drawable.thum_black_point_356, options);
                            hourBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_hour_black_356, options);
                            minuteBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_minute_black_356, options);
                            secondBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_second_black_356, options);
                        } else {
                            tBit = BitmapFactory.decodeResource(getResources(), R.drawable.thum_white_point_356, options);
                            hourBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_hour_white_356, options);
                            minuteBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_minute_white_356, options);
                            secondBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_second_white_356, options);
                        }
                        customPointDialParam.sethTop(104);
                        customPointDialParam.setmTop(64);
                        customPointDialParam.setsTop(36);
                    } else if (watchInfo.lcd_full_w == 368) {
                        if (currentDialStyle == DialStyle.blackPointer) {
                            tBit = BitmapFactory.decodeResource(getResources(), R.drawable.thum_black_point_356, options);
                            hourBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_hour_black_356, options);
                            minuteBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_minute_black_356, options);
                            secondBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_second_black_356, options);
                        } else {
                            tBit = BitmapFactory.decodeResource(getResources(), R.drawable.thum_white_point_356, options);
                            hourBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_hour_white_356, options);
                            minuteBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_minute_white_356, options);
                            secondBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_second_white_356, options);
                        }
                        customPointDialParam.sethTop(128);
                        customPointDialParam.setmTop(88);
                        customPointDialParam.setsTop(60);
                    } else if (watchInfo.lcd_full_w == 466) {
                        if (currentDialStyle == DialStyle.blackPointer) {
                            tBit = BitmapFactory.decodeResource(getResources(), R.drawable.thum_black_point_466, options);
                            hourBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_hour_black_466, options);
                            minuteBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_minute_black_466, options);
                            secondBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_second_black_466, options);
                        } else {
                            tBit = BitmapFactory.decodeResource(getResources(), R.drawable.thum_white_point_466, options);
                            hourBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_hour_white_466, options);
                            minuteBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_minute_white_466, options);
                            secondBit = BitmapFactory.decodeResource(getResources(), R.drawable.point_second_white_466, options);
                        }
                        customPointDialParam.sethTop(126);
                        customPointDialParam.setmTop(41);
                        customPointDialParam.setsTop(17);
                    }
                    customPointDialParam.setHourBitmap(hourBit);
                    customPointDialParam.setSecondBitmap(secondBit);
                    customPointDialParam.setMinuteBitmap(minuteBit);
                    customPointDialParam.setPreviewBitmap(tBit);
                    rgbaPointUtils.produceDialBin(customPointDialParam, new CustomDialCallback() {
                        @Override
                        public void dialPath(String dPath) {
                            if (TextUtils.isEmpty(dPath)) {
                                if (waitingDialog != null) {
                                    waitingDialog.dismiss();
                                }
                                Toast.makeText(CustomDialActivity.this, getString(R.string.Create_dial_fail), Toast.LENGTH_SHORT).show();
                            } else {
                                filePath = dPath;
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x52);
                                }
                            }
                        }
                    });

                }


            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CustomDialActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        styleList.setLayoutManager(linearLayoutManager);
        styleData = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            StyleItem styleItem = new StyleItem();
            if (i == 0) {
                styleItem.dialStyle = DialStyle.blackTxt;
                styleItem.resId = R.drawable.thum_txt_black_240;
            } else if (i == 1) {
                styleItem.dialStyle = DialStyle.whiteTxt;
                styleItem.resId = R.drawable.thum_txt_white_240;
            } else if (i == 2) {
                styleItem.dialStyle = DialStyle.blackPointer;
                styleItem.resId = R.drawable.thum_black_point_240;
            } else if (i == 3) {
                styleItem.dialStyle = DialStyle.whitePointer;
                styleItem.resId = R.drawable.thum_white_point_240;
            }
            styleData.add(styleItem);

        }

        StyleAdapter styleAdapter = new StyleAdapter();
        styleList.setAdapter(styleAdapter);

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

    @Override
    public void onBackPressed() {
        if (isInstalling) {
            return;
        }
        super.onBackPressed();
    }

    private void initDialog() {
        if (dialSelectPicDialog == null) {
            final DialSelectPicDialog.Builder builder = new DialSelectPicDialog.Builder(CustomDialActivity.this);
            builder.setAlbumonButton(new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                    dialog.dismiss();
                }
            });
            builder.setPhotoButton(new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (hasSdcard()) {
                        File outputImage = new File(getExternalCacheDir(), DIAL_BG);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            imageUri = FileProvider.getUriForFile(CustomDialActivity.this, getPackageName() + ".fileProvider", outputImage);
                        } else {
                            imageUri = Uri.fromFile(outputImage);
                        }
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    } else {
                        Log.e(TAG, "外置SDK不存在");
                    }
                    dialog.dismiss();
                }
            });
            builder.setCancelButton(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialSelectPicDialog = builder.create();
        }
        if (!dialSelectPicDialog.isShowing()) {
            dialSelectPicDialog.show();
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
        styleData = null;
        watchInfo = null;
        dialSelectPicDialog = null;
        backBitmap = null;
        imageUri = null;
        currentDialStyle = null;
        if (rgbaPlatformDiffTxtUtils != null) {
            rgbaPlatformDiffTxtUtils.destroy();
            rgbaPlatformDiffTxtUtils = null;
        }
        if (rgbaPointUtils != null) {
            rgbaPointUtils.destroy();
            rgbaPointUtils = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            initDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED) {

            switch (requestCode) {
                case IMAGE_REQUEST_CODE://
                    Log.e(TAG, "从相册获取图片并剪切");
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (hasSdcard()) {
                        startPhotoZoom(Uri.fromFile(new File(getExternalCacheDir(), DIAL_BG)));
                    } else {
                        startPhotoZoom(Uri.fromFile(new File(getCacheDir(), DIAL_BG)));
                    }

                    break;
                case RESULT_REQUEST_CODE:
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    if (resultUri == null) {
                        return;
                    }
                    BitmapFactory.Options options2 = new BitmapFactory.Options();
                    options2.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    backBitmap = BitmapFactory.decodeFile(resultUri.getPath(), options2);
                    if (backBitmap == null) {
                        return;
                    }
                    PreviewTask previewTask = new PreviewTask();
                    previewTask.execute();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPhotoZoom(Uri uri) {

        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setRequestedSize(watchInfo.lcd_full_w, watchInfo.lcd_full_h, CropImageView.RequestSizeOptions.RESIZE_EXACT)//解决有些图片裁剪得出239*239而导致更换不了表盘背景的问题
                .setMinCropResultSize(watchInfo.lcd_full_w, watchInfo.lcd_full_h)
                .setAspectRatio(watchInfo.lcd_full_w, watchInfo.lcd_full_h)//根据手表屏幕长方形或正方形进行背景裁剪，避免背景图被拉伸
                .start(this);

    }

    private boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    class StyleItem {
        public DialStyle dialStyle;
        public int resId;
    }

    class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.StyleHold> {
        private int selectPosition;

        @NonNull
        @Override
        public StyleHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(CustomDialActivity.this).inflate(R.layout.adapter_custom_style, parent, false);
            return new StyleHold(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StyleHold holder, int position) {
            if (styleData == null || styleData.isEmpty()) {
                return;
            }
            final StyleItem zodiacInfo = styleData.get(position);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.imageBack.getLayoutParams();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
            params.width = (int) SizeTransform.dp2px(69, CustomDialActivity.this);
            params.height = (int) SizeTransform.dp2px(78, CustomDialActivity.this);
            if (position == styleData.size() - 1) {
                layoutParams.rightMargin = 0;
            } else {
                layoutParams.rightMargin = (int) SizeTransform.dp2px(10, CustomDialActivity.this);
            }
            Glide.with(CustomDialActivity.this).load(zodiacInfo.resId).into(holder.imageView);
            if (selectPosition == position) {
                holder.imageBack.setBackground(getDrawable(R.drawable.dial_zodiac_back));
            } else {
                holder.imageBack.setBackground(null);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectPosition != position) {
                        notifyItemChanged(position, TAG);
                        selectPosition = position;
                        currentDialStyle = zodiacInfo.dialStyle;
                        if (currentDialStyle == DialStyle.blackTxt) {
                            currentTxtColor = Color.BLACK;
                        } else if (currentDialStyle == DialStyle.whiteTxt) {
                            currentTxtColor = Color.WHITE;
                        }
                        holder.imageBack.setBackground(getDrawable(R.drawable.dial_zodiac_back));
                        if (backBitmap == null) {
                            Glide.with(CustomDialActivity.this).load(zodiacInfo.resId).into(previewImage);
                        } else {
                            if (waitingDialog == null) {
                                waitingDialog = new WaitingDialog(CustomDialActivity.this);
                            }
                            if (!waitingDialog.isShowing()) {
                                waitingDialog.show();
                            }
                            PreviewTask previewTask = new PreviewTask();
                            previewTask.execute();
                        }
                    }
                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull StyleHold holder, int position, @NonNull List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
            final StyleItem zodiacInfo = styleData.get(position);
            holder.imageBack.setBackground(null);

        }

        @Override
        public int getItemCount() {
            return styleData.size();
        }

        class StyleHold extends RecyclerView.ViewHolder {
            RelativeLayout imageBack;
            AppCompatImageView imageView;

            public StyleHold(@NonNull View itemView) {
                super(itemView);
                imageBack = itemView.findViewById(R.id.zodiac_back);
                imageView = itemView.findViewById(R.id.zodiac_image);
            }
        }
    }

    class PreviewTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... integers) {
            if (currentDialStyle == DialStyle.blackTxt || currentDialStyle == DialStyle.whiteTxt) {


                if (currentDialStyle == DialStyle.blackTxt) {
                    currentTxtColor = Color.BLACK;
                } else if (currentDialStyle == DialStyle.whiteTxt) {
                    currentTxtColor = Color.WHITE;
                }
                int startX = 0;
                int startY = 0;
                int weekX = 0;
                int weekY = 0;
                int dataX = 0;
                if (watchInfo.lcd_full_w == 240) {
                    if (watchInfo.lcd_full_type == 1) {
                        startX = 58;
                        startY = 30;
                        weekX = 80;
                        weekY = 84;
                        dataX = 133;
                    } else {
                        startX = 47;
                        startY = 20;
                        weekX = 90;
                        weekY = 70;
                        dataX = 116;
                    }
                }else if (watchInfo.lcd_full_w==320){
                    startX=85;
                    startY = 20;
                    weekX = 110;
                    weekY = 95;
                    dataX = 160;
                }else if (watchInfo.lcd_full_w == 356) {
                    startX = 88;
                    startY = 20;
                    weekX = 75;
                    weekY = 95;
                    dataX = 155;
                } else if (watchInfo.lcd_full_w == 368) {
                    startX = 93;
                    startY = 20;
                    weekX = 95;
                    weekY = 95;
                    dataX = 220;
                } else if (watchInfo.lcd_full_w == 466) {
                    startX = 127;
                    startY = 30;
                    weekX = 140;
                    weekY = 140;
                    dataX = 260;
                }
                Log.e(TAG, "开始坐标" + startX + "," + startY + ",日期时间坐标:" + weekX + "," + weekY + "," + dataX);
                return rgbaPlatformDiffTxtUtils.produceDialThumbnail(backBitmap, watchInfo.lcd_full_w, watchInfo.lcd_full_h, watchInfo.lcd_preview_radius,
                        watchInfo.lcd_full_type, startX, startY, weekX, weekY, currentTxtColor, dataX, watchInfo.lcd_preview_w, watchInfo.lcd_preview_h);
            } else if (currentDialStyle == DialStyle.blackPointer) {
                if (rgbaPointUtils == null) {
                    rgbaPointUtils = new RGBAPointUtils(CustomDialActivity.this, watchInfo.lcd_pixel_type == 1 ? true : false);
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap bBitMap = null;
                if (watchInfo.lcd_full_w == 240) {
                    bBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.thum_black_point_240, options);

                } else if (watchInfo.lcd_full_w == 356) {
                    bBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.thum_black_point_356, options);
                } else if (watchInfo.lcd_full_w == 368) {
                    bBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.thum_black_point_356, options);
                } else if (watchInfo.lcd_full_w == 466) {
                    bBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.thum_black_point_466, options);
                }
                if (bBitMap != null) {
                    return rgbaPointUtils.produceDialThumbnail(backBitmap, watchInfo.lcd_full_w, watchInfo.lcd_full_h, watchInfo.lcd_preview_radius, watchInfo.lcd_full_type, bBitMap);
                }

            } else if (currentDialStyle == DialStyle.whitePointer) {
                if (rgbaPointUtils == null) {
                    rgbaPointUtils = new RGBAPointUtils(CustomDialActivity.this, watchInfo.lcd_pixel_type == 1 ? true : false);
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap bBitMap = null;
                if (watchInfo.lcd_full_w == 240) {
                    bBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.thum_white_point_240, options);

                } else if (watchInfo.lcd_full_w == 356) {
                    bBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.thum_white_point_356, options);
                } else if (watchInfo.lcd_full_w == 368) {
                    bBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.thum_white_point_356, options);
                } else if (watchInfo.lcd_full_w == 466) {
                    bBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.thum_white_point_466, options);
                }
                if (bBitMap != null) {
                    return rgbaPointUtils.produceDialThumbnail(backBitmap, watchInfo.lcd_full_w, watchInfo.lcd_full_h, watchInfo.lcd_preview_radius, watchInfo.lcd_full_type, bBitMap);
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mHandler != null) {
                Message message = new Message();
                message.what = 0x51;
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }

        }
    }
}
