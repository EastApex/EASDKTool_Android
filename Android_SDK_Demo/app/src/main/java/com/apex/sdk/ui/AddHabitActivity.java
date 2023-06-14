package com.apex.sdk.ui;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apex.bluetooth.callback.HabitResultCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.CommonAction;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.HabitIcon;
import com.apex.bluetooth.model.EABleHabit;
import com.apex.bluetooth.model.EABleHabitRespond;
import com.apex.sdk.R;
import com.apex.sdk.dialog.HabitEndTimeDialog;
import com.apex.sdk.dialog.HabitStartTimeDialog;
import com.apex.sdk.dialog.RemindDialog;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.model.HabitColor;
import com.apex.sdk.model.HabitItem;
import com.apex.sdk.model.HabitType;
import com.apex.sdk.utils.SizeTransform;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddHabitActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.habit_type_color)
    RelativeLayout colorLayout;
    @BindView(R.id.habit_icon)
    AppCompatImageView iconView;
    @BindView(R.id.habit_name)
    EditText nameEdit;
    @BindView(R.id.habit_type)
    RecyclerView typeListView;
    @BindView(R.id.habit_color)
    RecyclerView colorListView;
    @BindView(R.id.habit_time)
    TextView timeView;
    @BindView(R.id.habit_remind_mode)
    TextView remindView;
    @BindView(R.id.add_habit)
    AppCompatButton addButton;
    @BindView(R.id.delete_habit)
    AppCompatButton deleteButton;
    List<HabitType> typeList;
    List<HabitColor> colorList;
    private RemindDialog habitRemindDialog;
    int[] habitIcon = new int[]{R.drawable.habit_1_icon, R.drawable.habit_2_icon, R.drawable.habit_3_icon, R.drawable.habit_4_icon, R.drawable.habit_5_icon, R.drawable.habit_6_icon
            , R.drawable.habit_7_icon, R.drawable.habit_8_icon, R.drawable.habit_9_icon, R.drawable.habit_10_icon, R.drawable.habit_11_icon, R.drawable.habit_12_icon
            , R.drawable.habit_13_icon, R.drawable.habit_14_icon, R.drawable.habit_15_icon, R.drawable.habit_16_icon, R.drawable.habit_17_icon, R.drawable.habit_18_icon};
    int[] habitColor = new int[]{R.color.habit_color_1, R.color.habit_color_2, R.color.habit_color_3, R.color.habit_color_4, R.color.habit_color_5, R.color.habit_color_6, R.color.habit_color_7,
            R.color.habit_color_8, R.color.habit_color_9, R.color.habit_color_10, R.color.habit_color_11, R.color.habit_color_12, R.color.habit_color_13, R.color.habit_color_14,
            R.color.habit_color_15, R.color.habit_color_16, R.color.habit_color_17, R.color.habit_color_18, R.color.habit_color_19, R.color.habit_color_20, R.color.habit_color_21,
            R.color.habit_color_22, R.color.habit_color_23, R.color.habit_color_24, R.color.habit_color_25, R.color.habit_color_26, R.color.habit_color_27, R.color.habit_color_28,
            R.color.habit_color_29, R.color.habit_color_30, R.color.habit_color_31, R.color.habit_color_32, R.color.habit_color_33, R.color.habit_color_34, R.color.habit_color_35,
            R.color.habit_color_36};
    private HabitStartTimeDialog startTimeDialog;
    private HabitEndTimeDialog endTimeDialog;
    private int startHour, endHour, startMinute, endMinute;
    private int remindMethod;
    private int selectHabitType = -1;
    private int greenColor = -1;
    private int blueColor = -1;
    private int redColor = -1;
    private int selectColor = -1;
    private final int METHODREQUEST = 0x3A;
    private WaitingDialog waitingDialog;
    private HabitItem mHabitItem;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x41) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(AddHabitActivity.this, 7) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };

                gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
                typeListView.setNestedScrollingEnabled(false);
                typeListView.setHasFixedSize(true);
                typeListView.setFocusable(false);
                typeListView.setLayoutManager(gridLayoutManager);
                typeListView.setAdapter(new TypeAdapter());

            } else if (msg.what == 0x42) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(AddHabitActivity.this, 7) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };
                gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
                colorListView.setNestedScrollingEnabled(false);
                colorListView.setHasFixedSize(true);
                colorListView.setFocusable(false);
                colorListView.setLayoutManager(gridLayoutManager);
                colorListView.setAdapter(new ColorAdapter());
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
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
        mHabitItem = (HabitItem) getIntent().getSerializableExtra("habitInfo");
        if (mHabitItem != null) {
            if (mHabitItem.getE_icon_id() >= 0) {
                if (!TextUtils.isEmpty(mHabitItem.getContent())) {
                    nameEdit.setText(mHabitItem.getContent());
                }
                startMinute = mHabitItem.getBegin_minute();
                startHour = mHabitItem.getBegin_hour();
                endMinute = mHabitItem.getEnd_minute();
                endHour = mHabitItem.getEnd_hour();
                remindMethod = mHabitItem.getE_action();
                if (remindMethod == 0) {
                    remindView.setText(getString(R.string.common_action_no_action));
                } else if (remindMethod == 1) {
                    remindView.setText(getString(R.string.common_action_one_long_vibration));
                } else if (remindMethod == 2) {
                    remindView.setText(getString(R.string.common_action_one_short_vibration));
                } else if (remindMethod == 4) {
                    remindView.setText(getString(R.string.common_action_two_short_vibration));
                } else if (remindMethod == 3) {
                    remindView.setText(getString(R.string.common_action_two_long_vibration));
                } else if (remindMethod == 5) {
                    remindView.setText(getString(R.string.common_action_long_vibration));
                } else if (remindMethod == 6) {
                    remindView.setText(getString(R.string.common_action_long_short_vibration));
                }
                timeView.setText((startHour < 10 ? "0" + startHour : startHour + "") + ":" + (startMinute < 10 ? "0" + startMinute : startMinute + "") + " - " + (endHour < 10 ? "0" + endHour : endHour + "") + ":" + (endMinute < 10 ? "0" + endMinute : endMinute + ""));
            } else {
                deleteButton.setVisibility(View.GONE);
            }
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                typeList = new ArrayList<>();
                for (int i = 0; i < habitIcon.length; i++) {
                    HabitType hType = new HabitType();
                    hType.habit_icon = habitIcon[i];
                    if (mHabitItem != null) {
                        if (mHabitItem.getE_icon_id() >= 0) {
                            if (mHabitItem.getE_icon_id() == 0 && habitIcon[i] == R.drawable.habit_1_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 1 && habitIcon[i] == R.drawable.habit_2_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 2 && habitIcon[i] == R.drawable.habit_3_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 3 && habitIcon[i] == R.drawable.habit_4_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 4 && habitIcon[i] == R.drawable.habit_5_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 5 && habitIcon[i] == R.drawable.habit_6_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 6 && habitIcon[i] == R.drawable.habit_7_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 7 && habitIcon[i] == R.drawable.habit_8_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 8 && habitIcon[i] == R.drawable.habit_9_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 9 && habitIcon[i] == R.drawable.habit_10_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 10 && habitIcon[i] == R.drawable.habit_11_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 11 && habitIcon[i] == R.drawable.habit_12_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 12 && habitIcon[i] == R.drawable.habit_13_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 13 && habitIcon[i] == R.drawable.habit_14_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 14 && habitIcon[i] == R.drawable.habit_15_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 15 && habitIcon[i] == R.drawable.habit_16_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 16 && habitIcon[i] == R.drawable.habit_17_icon) {
                                hType.isSelect = true;
                            } else if (mHabitItem.getE_icon_id() == 17 && habitIcon[i] == R.drawable.habit_18_icon) {
                                hType.isSelect = true;
                            }
                        } else {
                            if (i == 0) {
                                hType.isSelect = true;
                            }
                        }
                    } else {
                        if (i == 0) {
                            hType.isSelect = true;
                        }
                    }
                    typeList.add(hType);
                }

                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0x41);
                }
                colorList = new ArrayList<>();
                for (int i = 0; i < habitColor.length; i++) {
                    HabitColor hColor = new HabitColor();
                    hColor.habit_color = habitColor[i];
                    if (mHabitItem != null) {
                        if (mHabitItem.getE_icon_id() >= 0) {
                            String mColor = Integer.toHexString(getColor(hColor.habit_color) & 0xFFFFFFFF);
                            String tColor = Integer.toHexString((mHabitItem.getRedColor() << 16 & 0xFFFFFFFF) | (mHabitItem.getGreenColor() << 8 & 0xFFFFFFFF) | (mHabitItem.getBlueColor() & 0xFFFFFFFF) | 0xFF000000);
                            if (mColor.equalsIgnoreCase(tColor)) {
                                hColor.isSelect = true;
                            }
                        } else {
                            if (i == 0) {
                                hColor.isSelect = true;
                            }
                        }
                    } else {
                        if (i == 0) {
                            hColor.isSelect = true;
                        }
                    }
                    colorList.add(hColor);
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0x42);
                }
            }
        }.start();
    }


    @OnClick(R.id.habit_time)
    public void selectTime(View v) {
        selectStartTime();

    }

    @OnClick(R.id.habit_remind_mode)
    public void selectRemindMethod(View v) {
        if (habitRemindDialog == null) {
            habitRemindDialog = new RemindDialog(AddHabitActivity.this);
            habitRemindDialog.setSelectListener(new RemindDialog.SelectListener() {
                @Override
                public void selectData(String sex) {
                    remindView.setText(sex);
                    if (!TextUtils.isEmpty(sex)) {
                        if (sex.equalsIgnoreCase(getString(R.string.common_action_no_action))) {
                            remindMethod = 0;
                        } else if (sex.equalsIgnoreCase(getString(R.string.common_action_one_short_vibration))) {
                            remindMethod = 2;
                        } else if (sex.equalsIgnoreCase(getString(R.string.common_action_two_short_vibration))) {
                            remindMethod = 4;
                        } else if (sex.equalsIgnoreCase(getString(R.string.common_action_long_vibration))) {
                            remindMethod = 1;
                        } else if (sex.equalsIgnoreCase(getString(R.string.common_action_two_long_vibration))) {
                            remindMethod = 3;
                        } else if (sex.equalsIgnoreCase(getString(R.string.common_action_long_vibration))) {
                            remindMethod = 5;
                        } else if (sex.equalsIgnoreCase(getString(R.string.common_action_long_short_vibration))) {
                            remindMethod = 6;
                        }
                    }
                }
            });
        }
        habitRemindDialog.show();

    }

    @OnClick(R.id.add_habit)
    public void addHabit(View v) {
        if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
            String habitName = nameEdit.getText().toString();
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(AddHabitActivity.this);
            }
            waitingDialog.show();
            EABleHabit eaBleHabit = new EABleHabit();
            if (mHabitItem != null) {
                if (mHabitItem.getE_icon_id() >= 0) {
                    eaBleHabit.setId(mHabitItem.getId());
                    eaBleHabit.setE_ops(EABleHabit.HabitualOperation.edit);
                } else {
                    eaBleHabit.setE_ops(EABleHabit.HabitualOperation.add);
                }
            } else {
                eaBleHabit.setE_ops(EABleHabit.HabitualOperation.add);
            }
            List<EABleHabit.HabitItem> itemList = new ArrayList<>();
            EABleHabit.HabitItem habitItem = new EABleHabit.HabitItem();
            itemList.add(habitItem);
            eaBleHabit.setItemList(itemList);
            if (selectHabitType == 0) {
                habitItem.setE_icon_id(HabitIcon.study_01);
            } else if (selectHabitType == 1) {
                habitItem.setE_icon_id(HabitIcon.sleep_02);
            } else if (selectHabitType == 2) {
                habitItem.setE_icon_id(HabitIcon.study_03);
            } else if (selectHabitType == 3) {
                habitItem.setE_icon_id(HabitIcon.chores_04);
            } else if (selectHabitType == 4) {
                habitItem.setE_icon_id(HabitIcon.havefun_05);
            } else if (selectHabitType == 5) {
                habitItem.setE_icon_id(HabitIcon.drink_06);
            } else if (selectHabitType == 6) {
                habitItem.setE_icon_id(HabitIcon.sun_07);
            } else if (selectHabitType == 7) {
                habitItem.setE_icon_id(HabitIcon.teeth_08);
            } else if (selectHabitType == 8) {
                habitItem.setE_icon_id(HabitIcon.calendar_09);
            } else if (selectHabitType == 9) {
                habitItem.setE_icon_id(HabitIcon.piano_10);
            } else if (selectHabitType == 10) {
                habitItem.setE_icon_id(HabitIcon.fruit_11);
            } else if (selectHabitType == 11) {
                habitItem.setE_icon_id(HabitIcon.medicine_12);
            } else if (selectHabitType == 12) {
                habitItem.setE_icon_id(HabitIcon.draw_13);
            } else if (selectHabitType == 13) {
                habitItem.setE_icon_id(HabitIcon.target_14);
            } else if (selectHabitType == 14) {
                habitItem.setE_icon_id(HabitIcon.dog_15);
            } else if (selectHabitType == 15) {
                habitItem.setE_icon_id(HabitIcon.exercise_16);
            } else if (selectHabitType == 16) {
                habitItem.setE_icon_id(HabitIcon.bed_17);
            } else if (selectHabitType == 17) {
                habitItem.setE_icon_id(HabitIcon.tidyup_18);
            }
            int color = getColor(habitColor[selectColor]);
            greenColor = (color >> 8) & 0xFF;
            redColor = (color >> 16) & 0xFF;
            blueColor = (color >> 0) & 0xFF;
            habitItem.setGreenColor(greenColor);
            habitItem.setRedColor(redColor);
            habitItem.setBlueColor(blueColor);
            habitItem.setDuration(30);
            habitItem.setEnd_minute(endMinute);
            habitItem.setEnd_hour(endHour);
            habitItem.setBegin_minute(startMinute);
            habitItem.setBegin_hour(startHour);
            habitItem.setContent(habitName);
            if (remindMethod == 0) {
                habitItem.setE_action(CommonAction.no_action);
            } else if (remindMethod == 1) {
                habitItem.setE_action(CommonAction.one_long_vibration);
            } else if (remindMethod == 2) {
                habitItem.setE_action(CommonAction.one_short_vibration);
            } else if (remindMethod == 3) {
                habitItem.setE_action(CommonAction.two_long_vibration);
            } else if (remindMethod == 4) {
                habitItem.setE_action(CommonAction.two_short_vibration);
            } else if (remindMethod == 5) {
                habitItem.setE_action(CommonAction.long_vibration);
            } else {
                habitItem.setE_action(CommonAction.long_short_vibration);
            }
            EABleManager.getInstance().setHabit(eaBleHabit, new HabitResultCallback() {
                @Override
                public void mutualFail(int errorCode) {

                }

                @Override
                public void editResult(EABleHabitRespond eaBleHabitRespond) {
                    waitingDialog.dismiss();
                    if (eaBleHabitRespond != null) {
                        EABleHabitRespond.Result result = eaBleHabitRespond.getResult();
                        if (result == EABleHabitRespond.Result.time_conflict) {
                            Log.e(TAG, "时间重复");

                        } else if (result == EABleHabitRespond.Result.success) {
                            Log.e(TAG, "成功");
                            finish();
                        } else if (result == EABleHabitRespond.Result.fail) {
                            Log.e(TAG, "失败");

                        } else if (result == EABleHabitRespond.Result.mem_full) {

                        }
                    }

                }

            });
        }
    }

    @OnClick(R.id.delete_habit)
    public void deleteHabit(View v) {
        if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
            if (mHabitItem != null) {
                if (mHabitItem.getE_icon_id() >= 0) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(AddHabitActivity.this);
                    }
                    waitingDialog.show();
                    EABleHabit eaBleHabit = new EABleHabit();
                    eaBleHabit.setId(mHabitItem.getId());
                    eaBleHabit.setE_ops(EABleHabit.HabitualOperation.del);
                    EABleManager.getInstance().setHabit(eaBleHabit, new HabitResultCallback() {
                        @Override
                        public void mutualFail(int errorCode) {

                        }

                        @Override
                        public void editResult(EABleHabitRespond eaBleHabitRespond) {
                            waitingDialog.dismiss();
                            if (eaBleHabitRespond != null) {
                                EABleHabitRespond.Result result = eaBleHabitRespond.getResult();
                                if (result == EABleHabitRespond.Result.time_conflict) {
                                    Log.e(TAG, "时间重复");
                                } else if (result == EABleHabitRespond.Result.success) {
                                    Log.e(TAG, "删除成功");
                                    finish();
                                } else if (result == EABleHabitRespond.Result.fail) {

                                } else if (result == EABleHabitRespond.Result.mem_full) {
                                    Log.e(TAG, "超过支持的最大数");
                                }
                            }
                        }


                    });
                }
            }
        }
    }

    private void selectEndTime() {
        if (endTimeDialog == null) {
            endTimeDialog = new HabitEndTimeDialog(AddHabitActivity.this, getString(R.string.end_time));
            endTimeDialog.setBirthdayTimeListener(new HabitEndTimeDialog.BirthdayTimeListener() {
                @Override
                public void birthdayTime(int hour, int minute) {
                    endHour = hour;
                    endMinute = minute;
                    endTimeDialog.dismiss();
                    timeView.setText((startHour < 10 ? "0" + startHour : startHour + "") + ":" + (startMinute < 10 ? "0" + startMinute : startMinute + "") + " - " + (endHour < 10 ? "0" + endHour : endHour + "") + ":" + (endMinute < 10 ? "0" + endMinute : endMinute + ""));
                }

                @Override
                public void previous() {
                    selectStartTime();
                    endTimeDialog.dismiss();
                }
            });
        }
        endTimeDialog.showDialog();
    }

    private void selectStartTime() {
        Log.e(TAG, "选择开始时间");
        if (startTimeDialog == null) {
            startTimeDialog = new HabitStartTimeDialog(AddHabitActivity.this, getString(R.string.start_time));
            startTimeDialog.setBirthdayTimeListener(new HabitStartTimeDialog.BirthdayTimeListener() {
                @Override
                public void birthdayTime(int hour, int minute) {
                    startHour = hour;
                    startMinute = minute;
                    selectEndTime();
                    startTimeDialog.dismiss();
                }
            });

        }
        startTimeDialog.showDialog();
    }

    class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHold> {
        private int selectPosition = -1;

        @NonNull
        @Override
        public TypeViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(AddHabitActivity.this).inflate(R.layout.adapter_add_habit_type_item, parent, false);
            return new TypeViewHold(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TypeViewHold holder, int position) {
            if (typeList == null || typeList.isEmpty()) {
                return;
            }
            HabitType habitType = typeList.get(position);

            holder.iconImage.setImageResource(habitType.habit_icon);

            if (habitType.isSelect) {
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setCornerRadius(SizeTransform.dp2px(18, AddHabitActivity.this));
                gradientDrawable.setColor(getColor(R.color.frame_color));
                holder.itemView.setBackground(gradientDrawable);
                selectPosition = position;
                selectHabitType = position;
                iconView.setImageResource(habitType.habit_icon);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    habitType.isSelect = true;
                    selectHabitType = position;
                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setCornerRadius(SizeTransform.dp2px(18, AddHabitActivity.this));
                    gradientDrawable.setColor(getColor(R.color.frame_color));
                    holder.itemView.setBackground(gradientDrawable);
                    iconView.setImageResource(habitType.habit_icon);
                    if (selectPosition != -1) {
                        typeList.get(position).isSelect = false;
                        notifyItemChanged(selectPosition, TAG);
                    }
                    selectPosition = position;
                }
            });

        }


        @Override
        public void onBindViewHolder(@NonNull TypeViewHold holder, int position, @NonNull List<Object> payloads) {
            if (payloads == null || payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads);
            } else {
                holder.itemView.setBackground(null);
            }

        }

        @Override
        public int getItemCount() {
            return typeList == null ? 0 : typeList.size();
        }

        class TypeViewHold extends RecyclerView.ViewHolder {
            AppCompatImageView iconImage;

            public TypeViewHold(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.icon);
            }
        }
    }

    class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHold> {
        private int selectPosition = -1;

        @NonNull
        @Override
        public ColorViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(AddHabitActivity.this).inflate(R.layout.adapter_add_habit_type_item, parent, false);
            return new ColorViewHold(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ColorViewHold holder, int position) {
            if (colorList == null || colorList.isEmpty()) {
                return;
            }
            HabitColor habitColor = colorList.get(position);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(SizeTransform.dp2px(12, AddHabitActivity.this));
            drawable.setColor(getResources().getColor(habitColor.habit_color, null));
            holder.iconImage.setBackground(drawable);

            if (habitColor.isSelect) {
                selectPosition = position;
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setCornerRadius(SizeTransform.dp2px(18, AddHabitActivity.this));
                gradientDrawable.setColor(getColor(R.color.frame_color));
                holder.itemView.setBackground(gradientDrawable);
                GradientDrawable backDrawable = new GradientDrawable();
                backDrawable.setCornerRadius(SizeTransform.dp2px(45, AddHabitActivity.this));
                backDrawable.setColor(getResources().getColor(habitColor.habit_color, null));
                colorLayout.setBackground(backDrawable);
                selectColor = position;
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    habitColor.isSelect = true;
                    selectColor = position;
                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setCornerRadius(SizeTransform.dp2px(18, AddHabitActivity.this));
                    gradientDrawable.setColor(getColor(R.color.frame_color));
                    holder.itemView.setBackground(gradientDrawable);
                    GradientDrawable backDrawable = new GradientDrawable();
                    backDrawable.setCornerRadius(SizeTransform.dp2px(45, AddHabitActivity.this));
                    backDrawable.setColor(getResources().getColor(habitColor.habit_color, null));
                    colorLayout.setBackground(backDrawable);
                    if (selectPosition != -1) {
                        colorList.get(selectPosition).isSelect = false;
                        notifyItemChanged(selectPosition, TAG);
                    }
                    selectPosition = position;
                }
            });

        }


        @Override
        public void onBindViewHolder(@NonNull ColorViewHold holder, int position, @NonNull List<Object> payloads) {
            if (payloads == null || payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads);
            } else {
                holder.itemView.setBackground(null);
            }


        }

        @Override
        public int getItemCount() {
            return colorList == null ? 0 : colorList.size();
        }

        class ColorViewHold extends RecyclerView.ViewHolder {
            AppCompatImageView iconImage;

            public ColorViewHold(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.icon);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (typeListView != null) {
            typeListView.setLayoutManager(null);
            typeListView.setAdapter(null);
            typeListView.clearAnimation();
            typeListView.removeAllViews();
            typeListView = null;
        }
        if (colorListView != null) {
            colorListView.setLayoutManager(null);
            colorListView.setAdapter(null);
            colorListView.clearAnimation();
            colorListView.removeAllViews();
            colorListView = null;
        }
        if (startTimeDialog != null) {
            startTimeDialog.destroyDialog();
            startTimeDialog = null;
        }
        if (endTimeDialog != null) {
            endTimeDialog.destroyDialog();
            endTimeDialog = null;
        }
        if (habitRemindDialog != null) {
            habitRemindDialog.destroyDialog();
            habitRemindDialog = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroy();
        habitColor = null;
        habitIcon = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mHabitItem = null;
    }


}
