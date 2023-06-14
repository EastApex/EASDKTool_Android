package com.apex.sdk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apex.sdk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CycleActivity extends AppCompatActivity {
    private Unbinder unbinder;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.monday_check)
    CheckBox monBox;
    @BindView(R.id.tuesday_check)
    CheckBox tueBox;
    @BindView(R.id.wednesday_check)
    CheckBox wedBox;
    @BindView(R.id.thursday_check)
    CheckBox thurBox;
    @BindView(R.id.friday_check)
    CheckBox friBox;
    @BindView(R.id.saturday_check)
    CheckBox satBox;
    @BindView(R.id.sunday_check)
    CheckBox sunBox;
    @BindView(R.id.once_check)
    CheckBox onceBox;
    @BindView(R.id.submit)
    AppCompatButton submit;
    private final int CYCLE_CODE = 0xFF;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
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
        onceBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sunBox.setChecked(false);
                    monBox.setChecked(false);
                    tueBox.setChecked(false);
                    wedBox.setChecked(false);
                    thurBox.setChecked(false);
                    friBox.setChecked(false);
                    satBox.setChecked(false);
                }
            }
        });
        satBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (onceBox.isChecked()) {
                        onceBox.setChecked(false);
                    }
                }

            }
        });
        sunBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (onceBox.isChecked()) {
                        onceBox.setChecked(false);
                    }
                }
            }
        });
        monBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (onceBox.isChecked()) {
                        onceBox.setChecked(false);
                    }
                }
            }
        });
        tueBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (onceBox.isChecked()) {
                        onceBox.setChecked(false);
                    }
                }
            }
        });
        wedBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (onceBox.isChecked()) {
                        onceBox.setChecked(false);
                    }
                }
            }
        });
        thurBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (onceBox.isChecked()) {
                        onceBox.setChecked(false);
                    }
                }
            }
        });
        friBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (onceBox.isChecked()) {
                        onceBox.setChecked(false);
                    }
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "";
                int work = 0x00;
                if (monBox.isChecked()) {
                    result += getString(R.string.mon) + ",";
                    work = (work | 0x02);
                }
                if (tueBox.isChecked()) {
                    result += getString(R.string.tue) + ",";
                    work = (work | 0x04);
                }
                if (wedBox.isChecked()) {
                    result += getString(R.string.wed) + ",";
                    work = (work | 0x08);
                }
                if (thurBox.isChecked()) {
                    result += getString(R.string.thur) + ",";
                    work = (work | 0x10);
                }
                if (friBox.isChecked()) {
                    result += getString(R.string.fri) + ",";
                    work = (work | 0x20);
                }
                if (satBox.isChecked()) {
                    result += getString(R.string.sat) + ",";
                    work = (work | 0x40);
                }
                if (sunBox.isChecked()) {
                    result += getString(R.string.sun) + ",";
                    work = (work | 0x01);
                }
                if (onceBox.isChecked()) {
                    result = "";
                    result += getString(R.string.only_once);
                    work = 0x00;
                }
                if (result.contains(",")) {
                    result = result.substring(0, result.length() - 1);
                }
                if (TextUtils.isEmpty(result)) {
                    return;
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("circleTime", result);
                    intent.putExtra("circle", work);
                    setResult(CYCLE_CODE, intent);
                    finish();
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
        super.onDestroy();
    }
}
