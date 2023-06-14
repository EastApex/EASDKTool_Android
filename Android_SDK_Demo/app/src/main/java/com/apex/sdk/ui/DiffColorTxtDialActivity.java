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
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.apex.bluetooth.callback.OtaCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.model.EABleOta;
import com.apex.bluetooth.utils.LogUtils;
import com.apex.sdk.R;
import com.apex.sdk.dialog.DialSelectPicDialog;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.model.WatchInfo;
import com.example.custom_dial.CustomDialCallback;
import com.example.custom_dial.CustomDiffTxtColorDialParam;
import com.example.custom_dial.RGBAImageInfo;
import com.example.custom_dial.RGBAPlatformDiffTxtUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DiffColorTxtDialActivity extends AppCompatActivity {
    final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.picture)
    AppCompatImageView pictureImageView;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    @BindView(R.id.color_radio)
    RadioGroup radioGroup;
    @BindView(R.id.select_back)
    AppCompatImageView selectImage;
    private DialSelectPicDialog dialSelectPicDialog;
    private final int IMAGE_REQUEST_CODE = 0;
    private final int CAMERA_REQUEST_CODE = 1;
    private final int RESULT_REQUEST_CODE = 2;
    private final String DIAL_BG = "dial_bg.png";
    int currentColor;
    private Uri imageUri;
    private Bitmap backBitmap;
    private WatchInfo watchInfo;
    private final int PERMISSION_CODE = 3;
    private WaitingDialog waitingDialog;
    RGBAPlatformDiffTxtUtils drawDiffTxtUtils;
    private String filePath;
    private boolean isInstalling;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x40) {
                Bitmap drawable = (Bitmap) msg.obj;
                if (drawable != null) {
                    pictureImageView.setImageBitmap(drawable);
                }
            } else if (msg.what == 0x52) {
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
                    if (waitingDialog != null && waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    Toast.makeText(DiffColorTxtDialActivity.this, getString(R.string.disconnect), Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 0x53) {
                isInstalling = false;
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(DiffColorTxtDialActivity.this, getString(R.string.ota_success), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x54) {
                isInstalling = false;
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(DiffColorTxtDialActivity.this, getString(R.string.ota_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diff_color_txt);
        unbinder = ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.mipmap.exit_page);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        watchInfo = (WatchInfo) getIntent().getSerializableExtra("param");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        drawDiffTxtUtils = new RGBAPlatformDiffTxtUtils(DiffColorTxtDialActivity.this, watchInfo.lcd_pixel_type == 1 ? true : false);
        getCurrentColor(radioGroup.getCheckedRadioButtonId());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getCurrentColor(checkedId);
                if (backBitmap != null) {
                    PreviewTask previewTask = new PreviewTask();
                    previewTask.execute();
                }
            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    initDialog();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backBitmap == null || currentColor == 0) {
                    return;
                }
                if (waitingDialog == null) {
                    waitingDialog = new WaitingDialog(DiffColorTxtDialActivity.this);
                }
                if (!waitingDialog.isShowing()) {
                    waitingDialog.show();
                }
                isInstalling = true;
                CustomDiffTxtColorDialParam customDiffTxtColorDialParam = new CustomDiffTxtColorDialParam();
                customDiffTxtColorDialParam.setTxtColor(currentColor);
                customDiffTxtColorDialParam.setBackBitmap(backBitmap);
                customDiffTxtColorDialParam.setCornerRadius(watchInfo.lcd_preview_radius);
                customDiffTxtColorDialParam.setScreenType(watchInfo.lcd_full_type);
                customDiffTxtColorDialParam.setScreenWidth(watchInfo.lcd_full_w);
                customDiffTxtColorDialParam.setScreenHigh(watchInfo.lcd_full_h);
                customDiffTxtColorDialParam.setpWidth(watchInfo.lcd_preview_w);
                customDiffTxtColorDialParam.setpHigh(watchInfo.lcd_preview_h);
                customDiffTxtColorDialParam.setStartX(60);
                customDiffTxtColorDialParam.setStartY(30);
                customDiffTxtColorDialParam.setWx(60);
                customDiffTxtColorDialParam.setWy(90);
                customDiffTxtColorDialParam.setDateX(150);
                drawDiffTxtUtils.produceDialBin(customDiffTxtColorDialParam, new CustomDialCallback() {
                    @Override
                    public void dialPath(String dPath) {
                        if (TextUtils.isEmpty(dPath)) {
                            isInstalling = false;
                            if (waitingDialog != null) {
                                waitingDialog.dismiss();
                            }
                            Toast.makeText(DiffColorTxtDialActivity.this, getString(R.string.Create_dial_fail), Toast.LENGTH_SHORT).show();
                        } else {
                            filePath = dPath;
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x52);
                            }
                        }
                    }
                });


            }
        });
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
            final DialSelectPicDialog.Builder builder = new DialSelectPicDialog.Builder(DiffColorTxtDialActivity.this);
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
                            imageUri = FileProvider.getUriForFile(DiffColorTxtDialActivity.this, getPackageName() + ".fileProvider", outputImage);
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

    private void getCurrentColor(int rid) {
        if (rid == R.id.red) {
            currentColor = Color.RED;
        } else if (rid == R.id.green) {
            currentColor = Color.GREEN;
        } else if (rid == R.id.blue) {
            currentColor = Color.BLUE;
        } else {
            currentColor = Color.DKGRAY;
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
        if (drawDiffTxtUtils != null) {
            drawDiffTxtUtils.destroy();
            drawDiffTxtUtils = null;
        }
        backBitmap = null;
        watchInfo = null;
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        super.onDestroy();
    }

    private boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
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
                    if (backBitmap == null || currentColor == 0) {
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

    class PreviewTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... integers) {
            Log.e(TAG, "开始生成缩略图");
            return drawDiffTxtUtils.produceDialThumbnail(backBitmap, watchInfo.lcd_full_w, watchInfo.lcd_full_h, watchInfo.lcd_preview_radius, watchInfo.lcd_full_type,
                    60, 30, 60, 90, currentColor, 150);

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mHandler != null) {
                Message message = new Message();
                message.what = 0x40;
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }

        }
    }
}
