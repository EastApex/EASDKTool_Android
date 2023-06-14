package com.apex.sdk.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.model.EABleContact;
import com.apex.sdk.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddContactActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    @BindView(R.id.delete)
    AppCompatButton deleteButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
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
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        List<EABleContact> eaBleContactList = new ArrayList<>();
                        for (int i = 0; i < 20; i++) {
                            EABleContact eaBleContact = new EABleContact();
                            eaBleContact.setContactName("zhangsan " + i);
                            eaBleContact.setContactNum("1789654321" + i);
                            eaBleContactList.add(eaBleContact);
                        }
                        if (eaBleContactList.size() <= 10) {
                            EABleManager.getInstance().addBookList(eaBleContactList, 0, new GeneralCallback() {
                                @Override
                                public void result(boolean success) {
                                    Log.e(TAG, "联系人添加成功");
                                }

                                @Override
                                public void mutualFail(int errorCode) {

                                }
                            });
                        } else {
                            final List<EABleContact> firstContact = new ArrayList<>();
                            final List<EABleContact> secondContact = new ArrayList<>();
                            for (int i = 0; i < eaBleContactList.size(); i++) {
                                if (i < 10) {
                                    firstContact.add(eaBleContactList.get(i));
                                } else {
                                    secondContact.add(eaBleContactList.get(i));
                                }
                            }
                            EABleManager.getInstance().addBookList(firstContact, 0, new GeneralCallback() {
                                @Override
                                public void result(boolean success) {
                                    if (success) {
                                        EABleManager.getInstance().addBookList(secondContact, 1, new GeneralCallback() {
                                            @Override
                                            public void result(boolean success) {

                                            }

                                            @Override
                                            public void mutualFail(int errorCode) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void mutualFail(int errorCode) {

                                }
                            });
                        }
                    }
                }.start();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<EABleContact> eaBleContactList = new ArrayList<>();
                EABleManager.getInstance().addBookList(eaBleContactList, 0, new GeneralCallback() {
                    @Override
                    public void result(boolean success) {

                    }

                    @Override
                    public void mutualFail(int errorCode) {

                    }
                });
            }
        });
    }
}
