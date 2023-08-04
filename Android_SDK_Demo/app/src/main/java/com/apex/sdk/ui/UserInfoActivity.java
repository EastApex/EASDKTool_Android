package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.PersonInfoCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.PersonHand;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABlePersonInfo;
import com.apex.bluetooth.utils.LogUtils;
import com.apex.sdk.R;
import com.apex.sdk.dialog.AgeDialog;
import com.apex.sdk.dialog.HeightDialog;
import com.apex.sdk.dialog.SexDialog;
import com.apex.sdk.dialog.SkinDialog;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.dialog.WearDialog;
import com.apex.sdk.dialog.WeightDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserInfoActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.user_sex)
    TextView sexText;
    @BindView(R.id.user_age)
    TextView ageText;
    @BindView(R.id.user_height)
    TextView heightText;
    @BindView(R.id.user_weight)
    TextView weightText;
    @BindView(R.id.user_wearing)
    TextView wearText;
    @BindView(R.id.user_skin)
    TextView skinText;
    private WaitingDialog waitingDialog;
    EABlePersonInfo eaBlePersonInfo;
    private SexDialog sexDialog;
    private String tempSex;
    private AgeDialog ageDialog;
    private int tempAge;
    private HeightDialog heightDialog;
    private int tempHeight;
    private WeightDialog weightDialog;
    private int tempWeight;
    private WearDialog wearDialog;
    private String tempWear;
    private SkinDialog skinDialog;
    private String tempSkin;
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
                eaBlePersonInfo = (EABlePersonInfo) msg.obj;
                sexText.setText(eaBlePersonInfo.getE_sex_info() == EABlePersonInfo.PersonSex.female ? getString(R.string.female) : getString(R.string.male));
                ageText.setText(eaBlePersonInfo.getAge() > 0 ? eaBlePersonInfo.getAge() + "" : null);
                heightText.setText(eaBlePersonInfo.getHeight() > 0 ? eaBlePersonInfo.getHeight() + "" : null);
                weightText.setText(eaBlePersonInfo.getWeight() > 0 ? eaBlePersonInfo.getWeight() + "" : null);
                wearText.setText(eaBlePersonInfo.getE_hand_info() == PersonHand.left ? getString(R.string.left) : getString(R.string.right));
                EABlePersonInfo.SkinColor skinColor = eaBlePersonInfo.getE_skin_color();
                if (skinColor != null) {
                    if (skinColor == EABlePersonInfo.SkinColor.skin_balck) {
                        skinText.setText(getString(R.string.skin_black));
                    } else if (skinColor == EABlePersonInfo.SkinColor.skin_white) {
                        LogUtils.i(TAG, "白色皮肤");
                        skinText.setText(getString(R.string.skin_white));
                    } else if (skinColor == EABlePersonInfo.SkinColor.skin_white_yellow) {
                        skinText.setText(getString(R.string.skin_white_yellow));
                    } else if (skinColor == EABlePersonInfo.SkinColor.skin_yellow) {
                        skinText.setText(getString(R.string.skin_yellow));
                    } else if (skinColor == EABlePersonInfo.SkinColor.skin_yellow_black) {
                        skinText.setText(getString(R.string.skin_yellow_black));
                    }
                }

            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(UserInfoActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                sexText.setText(tempSex);

            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(UserInfoActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x44) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                ageText.setText(tempAge + "");

            } else if (msg.what == 0x45) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                heightText.setText(tempHeight + "");

            } else if (msg.what == 0x46) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                weightText.setText(tempWeight + "");

            } else if (msg.what == 0x47) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                wearText.setText(tempWear);
            } else if (msg.what == 0x48) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                skinText.setText(tempSkin);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
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
                waitingDialog = new WaitingDialog(UserInfoActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.user_info, new PersonInfoCallback() {
                @Override
                public void personInfo(EABlePersonInfo eaBlePersonInfo) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBlePersonInfo;
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
        sexText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    LogUtils.i(TAG, "未连接");
                    if (sexDialog == null) {
                        sexDialog = new SexDialog(UserInfoActivity.this);
                        sexDialog.setSelectListener(new SexDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                if (eaBlePersonInfo == null) {
                                    eaBlePersonInfo = new EABlePersonInfo();
                                    eaBlePersonInfo.setE_skin_color(EABlePersonInfo.SkinColor.skin_yellow);
                                    eaBlePersonInfo.setAge(20);
                                    eaBlePersonInfo.setE_hand_info(PersonHand.left);
                                    eaBlePersonInfo.setHeight(170);
                                    eaBlePersonInfo.setWeight(100);
                                }
                                if (sex.equalsIgnoreCase(getString(R.string.female))) {
                                    eaBlePersonInfo.setE_sex_info(EABlePersonInfo.PersonSex.female);
                                } else {
                                    eaBlePersonInfo.setE_sex_info(EABlePersonInfo.PersonSex.male);
                                }
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(UserInfoActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                tempSex = sex;
                                EABleManager.getInstance().setUserInfo(eaBlePersonInfo, new GeneralCallback() {
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
                            }
                        });
                    }
                    if (!sexDialog.isShowing()) {
                        LogUtils.i(TAG, "显示对话框");
                        sexDialog.show();
                    }
                }

            }
        });
        ageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (ageDialog == null) {
                        ageDialog = new AgeDialog(UserInfoActivity.this);
                        ageDialog.setSelectListener(new AgeDialog.SelectListener() {
                            @Override
                            public void selectData(int age) {
                                if (eaBlePersonInfo == null) {
                                    eaBlePersonInfo = new EABlePersonInfo();
                                    eaBlePersonInfo.setE_skin_color(EABlePersonInfo.SkinColor.skin_yellow);
                                    eaBlePersonInfo.setE_hand_info(PersonHand.left);
                                    eaBlePersonInfo.setHeight(170);
                                    eaBlePersonInfo.setWeight(100);
                                    eaBlePersonInfo.setE_sex_info(EABlePersonInfo.PersonSex.male);
                                }
                                tempAge = age;
                                eaBlePersonInfo.setAge(age);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(UserInfoActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setUserInfo(eaBlePersonInfo, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x44);
                                        }

                                    }

                                    @Override
                                    public void mutualFail(int errorCode) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!ageDialog.isShowing()) {
                        ageDialog.show();
                    }
                }
            }
        });
        heightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (heightDialog == null) {
                        heightDialog = new HeightDialog(UserInfoActivity.this);
                        heightDialog.setSelectListener(new HeightDialog.SelectListener() {
                            @Override
                            public void selectData(int age) {
                                if (eaBlePersonInfo == null) {
                                    eaBlePersonInfo = new EABlePersonInfo();
                                    eaBlePersonInfo.setE_skin_color(EABlePersonInfo.SkinColor.skin_yellow);
                                    eaBlePersonInfo.setE_hand_info(PersonHand.left);
                                    eaBlePersonInfo.setAge(20);
                                    eaBlePersonInfo.setWeight(100);
                                    eaBlePersonInfo.setE_sex_info(EABlePersonInfo.PersonSex.male);
                                }
                                tempHeight = age;
                                eaBlePersonInfo.setHeight(age);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(UserInfoActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setUserInfo(eaBlePersonInfo, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x45);
                                        }

                                    }

                                    @Override
                                    public void mutualFail(int errorCode) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!heightDialog.isShowing()) {
                        heightDialog.show();
                    }
                }
            }
        });
        weightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (weightDialog == null) {
                        weightDialog = new WeightDialog(UserInfoActivity.this);
                        weightDialog.setSelectListener(new WeightDialog.SelectListener() {
                            @Override
                            public void selectData(int age) {
                                if (eaBlePersonInfo == null) {
                                    eaBlePersonInfo = new EABlePersonInfo();
                                    eaBlePersonInfo.setE_skin_color(EABlePersonInfo.SkinColor.skin_yellow);
                                    eaBlePersonInfo.setE_hand_info(PersonHand.left);
                                    eaBlePersonInfo.setAge(20);
                                    eaBlePersonInfo.setHeight(170);
                                    eaBlePersonInfo.setE_sex_info(EABlePersonInfo.PersonSex.male);
                                }
                                tempWeight = age;
                                eaBlePersonInfo.setWeight(age);
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(UserInfoActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setUserInfo(eaBlePersonInfo, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x46);
                                        }

                                    }

                                    @Override
                                    public void mutualFail(int errorCode) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!weightDialog.isShowing()) {
                        weightDialog.show();
                    }
                }
            }
        });
        wearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (wearDialog == null) {
                        wearDialog = new WearDialog(UserInfoActivity.this);
                        wearDialog.setSelectListener(new WearDialog.SelectListener() {
                            @Override
                            public void selectData(String age) {
                                if (eaBlePersonInfo == null) {
                                    eaBlePersonInfo = new EABlePersonInfo();
                                    eaBlePersonInfo.setE_skin_color(EABlePersonInfo.SkinColor.skin_yellow);
                                    eaBlePersonInfo.setAge(20);
                                    eaBlePersonInfo.setHeight(170);
                                    eaBlePersonInfo.setWeight(100);
                                    eaBlePersonInfo.setE_sex_info(EABlePersonInfo.PersonSex.male);
                                }
                                tempWear = age;
                                if (age.equalsIgnoreCase(getString(R.string.left))) {
                                    eaBlePersonInfo.setE_hand_info(PersonHand.left);
                                } else {
                                    eaBlePersonInfo.setE_hand_info(PersonHand.right);
                                }

                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(UserInfoActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setUserInfo(eaBlePersonInfo, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x47);
                                        }

                                    }

                                    @Override
                                    public void mutualFail(int errorCode) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!wearDialog.isShowing()) {
                        wearDialog.show();
                    }
                }
            }
        });
        skinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (skinDialog == null) {
                        skinDialog = new SkinDialog(UserInfoActivity.this);
                        skinDialog.setSelectListener(new SkinDialog.SelectListener() {
                            @Override
                            public void selectData(String age) {
                                if (eaBlePersonInfo == null) {
                                    eaBlePersonInfo = new EABlePersonInfo();
                                    eaBlePersonInfo.setAge(20);
                                    eaBlePersonInfo.setHeight(170);
                                    eaBlePersonInfo.setWeight(100);
                                    eaBlePersonInfo.setE_sex_info(EABlePersonInfo.PersonSex.male);
                                    eaBlePersonInfo.setE_hand_info(PersonHand.left);
                                }
                                if (age.equalsIgnoreCase(getString(R.string.skin_black))) {
                                    eaBlePersonInfo.setE_skin_color(EABlePersonInfo.SkinColor.skin_balck);
                                } else if (age.equalsIgnoreCase(getString(R.string.skin_white))) {
                                    eaBlePersonInfo.setE_skin_color(EABlePersonInfo.SkinColor.skin_white);
                                } else if (age.equalsIgnoreCase(getString(R.string.skin_yellow))) {
                                    eaBlePersonInfo.setE_skin_color(EABlePersonInfo.SkinColor.skin_yellow);
                                } else if (age.equalsIgnoreCase(getString(R.string.skin_white_yellow))) {
                                    eaBlePersonInfo.setE_skin_color(EABlePersonInfo.SkinColor.skin_white_yellow);
                                } else if (age.equalsIgnoreCase(getString(R.string.skin_yellow_black))) {
                                    eaBlePersonInfo.setE_skin_color(EABlePersonInfo.SkinColor.skin_yellow_black);
                                }

                                tempSkin = age;
                                if (waitingDialog == null) {
                                    waitingDialog = new WaitingDialog(UserInfoActivity.this);
                                }
                                if (!waitingDialog.isShowing()) {
                                    waitingDialog.show();
                                }
                                EABleManager.getInstance().setUserInfo(eaBlePersonInfo, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x48);
                                        }

                                    }

                                    @Override
                                    public void mutualFail(int errorCode) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(0x43);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    if (!skinDialog.isShowing()) {
                        skinDialog.show();
                    }
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
        if (skinDialog != null) {
            skinDialog.dismiss();
            skinDialog.destroyDialog();
            skinDialog = null;
        }
        if (sexDialog != null) {
            sexDialog.dismiss();
            sexDialog.destroyDialog();
            sexDialog = null;
        }
        if (ageDialog != null) {
            ageDialog.dismiss();
            ageDialog.destroyDialog();
            ageDialog = null;
        }
        if (heightDialog != null) {
            heightDialog.dismiss();
            heightDialog.destroyDialog();
            heightDialog = null;
        }
        if (wearDialog != null) {
            wearDialog.dismiss();
            wearDialog.destroyDialog();
            wearDialog = null;
        }
        if (weightDialog != null) {
            weightDialog.dismiss();
            weightDialog.destroyDialog();
            weightDialog = null;
        }
        super.onDestroy();
    }
}
