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
import com.apex.bluetooth.callback.LanguageCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleDeviceLanguage;
import com.apex.sdk.R;
import com.apex.sdk.dialog.LanguageDialog;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LanguageActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.language)
    TextView languageText;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.supported_language)
    TextView supportedText;
    private LanguageDialog languageDialog;
    private String tempLanguage;
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
                EABleDeviceLanguage eaBleBatInfo = (EABleDeviceLanguage) msg.obj;
                List<EABleDeviceLanguage.LanguageType> typeList = eaBleBatInfo.getSupportList();
                if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.default_type) {
                    languageText.setText(getString(R.string.language_default));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.english) {
                    languageText.setText(getString(R.string.language_english));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.chinese_simplifid) {
                    languageText.setText(getString(R.string.language_chinese_simplifid));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.chinese_traditional) {
                    languageText.setText(getString(R.string.language_chinese_traditional));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.korean) {
                    languageText.setText(getString(R.string.language_korean));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.thai) {
                    languageText.setText(getString(R.string.language_thai));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.japanese) {
                    languageText.setText(getString(R.string.language_japanese));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.spanish) {
                    languageText.setText(getString(R.string.language_spanish));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.francais) {
                    languageText.setText(getString(R.string.language_francais));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.deutsch) {
                    languageText.setText(getString(R.string.language_deutsch));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.italiano) {
                    languageText.setText(getString(R.string.language_italiano));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.polski) {
                    languageText.setText(getString(R.string.language_polski));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.portuguese) {
                    languageText.setText(getString(R.string.language_portuguese));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.russian) {
                    languageText.setText(getString(R.string.language_russian));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.dutch) {
                    languageText.setText(getString(R.string.language_dutch));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.arabic) {
                    languageText.setText(getString(R.string.language_arabic));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.greek) {
                    languageText.setText(getString(R.string.language_greek));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.hebrew) {
                    languageText.setText(getString(R.string.language_hebrew));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.swedish) {
                    languageText.setText(getString(R.string.language_swedish));
                } else if (eaBleBatInfo.getE_type() == EABleDeviceLanguage.LanguageType.hindi) {
                    languageText.setText(getString(R.string.language_hindi));
                }
                String supportLanguage = "";
                if (typeList != null && !typeList.isEmpty()) {
                    for (int i = 0; i < typeList.size(); i++) {
                        supportLanguage += (typeList.get(i).toString() + " ");
                    }
                }
                supportedText.setText(supportLanguage);
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(LanguageActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                languageText.setText(tempLanguage);
            } else if (msg.what == 43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(LanguageActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
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
                waitingDialog = new WaitingDialog(LanguageActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.language, new LanguageCallback() {
                @Override
                public void languageInfo(EABleDeviceLanguage eaBleDeviceLanguage) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleDeviceLanguage;
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
        languageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (languageDialog == null) {
                        languageDialog = new LanguageDialog(LanguageActivity.this);
                        languageDialog.setSelectListener(new LanguageDialog.SelectListener() {
                            @Override
                            public void selectData(String sex) {
                                EABleDeviceLanguage deviceLanguage = new EABleDeviceLanguage();
                                if (sex.equalsIgnoreCase(getString(R.string.language_default))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.default_type);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_english))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.english);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_chinese_simplifid))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.chinese_simplifid);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_chinese_traditional))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.chinese_traditional);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_korean))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.korean);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_thai))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.thai);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_japanese))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.japanese);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_spanish))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.spanish);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_francais))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.francais);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_deutsch))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.deutsch);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_italiano))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.italiano);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_polski))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.polski);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_portuguese))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.portuguese);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_russian))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.russian);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_dutch))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.dutch);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_arabic))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.arabic);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_greek))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.greek);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_hebrew))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.hebrew);
                                } else if (sex.equalsIgnoreCase(getString(R.string.language_swedish))) {
                                    deviceLanguage.setE_type(EABleDeviceLanguage.LanguageType.swedish);
                                }
                                tempLanguage = sex;
                                EABleManager.getInstance().setDevLanguage(deviceLanguage, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success, int reason) {
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
                    if (!languageDialog.isShowing()) {
                        languageDialog.show();
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
        if (languageDialog != null) {
            languageDialog.dismiss();
            languageDialog.destroyDialog();
            languageDialog = null;
        }
        super.onDestroy();
    }
}
