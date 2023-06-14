package com.apex.sdk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apex.bluetooth.callback.HabitCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleHabit;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.model.HabitItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HabitSettingActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.event_list)
    RecyclerView listView;
    HabitAdapter habitAdapter;
    List<HabitItem> itemList;
    private WaitingDialog waitingDialog;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x45) {
                habitAdapter.notifyDataSetChanged();
                waitingDialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(HabitSettingActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        listView.setLayoutManager(layoutManager);
        itemList = new ArrayList<>();
        HabitItem habitItem = new HabitItem();
        habitItem.setContent(getString(R.string.Add_habit));
        habitItem.setE_icon_id(-1);
        itemList.add(habitItem);
        habitAdapter = new HabitAdapter(itemList, HabitSettingActivity.this);
        listView.setAdapter(habitAdapter);
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
        if (listView != null) {
            listView.setLayoutManager(null);
            listView.setAdapter(null);
            listView.clearAnimation();
            listView.removeAllViews();
            listView = null;
        }
        if (habitAdapter != null) {
            habitAdapter.destroy();
            habitAdapter = null;
        }
        itemList = null;
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取习惯
        if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(HabitSettingActivity.this);
            }
            if (!waitingDialog.isShowing()) {
                waitingDialog.show();
            }
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.habit, new HabitCallback() {
                @Override
                public void habitInfo(EABleHabit eaBleHabit) {
                    itemList.clear();
                    Log.e(TAG, "查询成功");
                    if (eaBleHabit != null) {
                        List<EABleHabit.HabitItem> eaBleHabitList = eaBleHabit.getItemList();
                        if (eaBleHabitList != null && !eaBleHabitList.isEmpty()) {
                            List<HabitItem> habitItems = new ArrayList<>();
                            for (int i = 0; i < eaBleHabitList.size(); i++) {
                                EABleHabit.HabitItem item = eaBleHabitList.get(i);
                                HabitItem habitItem = new HabitItem();
                                habitItem.setBegin_hour(item.getBegin_hour());
                                habitItem.setBegin_minute(item.getBegin_minute());
                                habitItem.setDuration(item.getDuration());
                                habitItem.setId(item.getId());
                                habitItem.setEnd_hour(item.getEnd_hour());
                                habitItem.setEnd_minute(item.getEnd_minute());
                                habitItem.setContent(item.getContent());
                                habitItem.setBlueColor(item.getBlueColor());
                                habitItem.setE_icon_id(item.getE_icon_id().getValue());
                                habitItem.setE_action(item.getE_action().getValue());
                                habitItem.setGreenColor(item.getGreenColor());
                                habitItem.setRedColor(item.getRedColor());
                                habitItems.add(habitItem);
                            }
                            itemList.addAll(habitItems);
                        }
                    }
                    HabitItem habitItem = new HabitItem();
                    habitItem.setE_icon_id(-1);
                    habitItem.setId(-1);
                    habitItem.setContent(getString(R.string.Add_habit));
                    itemList.add(habitItem);
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x45);
                    }

                }

                @Override
                public void mutualFail(int errorCode) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x45);
                    }
                }
            });

        }
    }

    class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitHold> {
        private final String TAG = this.getClass().getSimpleName();
        private List<HabitItem> itemList;
        private Context mContext;

        public HabitAdapter(List<HabitItem> itemList, Context mContext) {
            this.itemList = itemList;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public HabitHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType >= 0) {
                View childView = LayoutInflater.from(mContext).inflate(R.layout.adapter_habit_item, parent, false);
                return new HabitHold(childView, viewType);
            } else {
                View childView = LayoutInflater.from(mContext).inflate(R.layout.adapter_habit_item_add, parent, false);
                return new HabitHold(childView, viewType);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull HabitHold holder, int position) {
            if (itemList == null || itemList.isEmpty()) {
                return;
            }
            HabitItem habitItem = itemList.get(position);
            int habitIcon = habitItem.getE_icon_id();
            holder.nameText.setText(habitItem.getContent());
            if (habitIcon >= 0) {
                int startHour = habitItem.getBegin_hour();
                int startMinute = habitItem.getBegin_minute();
                int endHour = habitItem.getEnd_hour();
                int endMinute = habitItem.getEnd_minute();
                holder.timeText.setText((startHour < 10 ? "0" + startHour : startHour) + ":" + (startMinute < 10 ? "0" + startMinute : startMinute + "") + " - " + (endHour < 10 ? "0" + endHour : endHour + "") + ":" + (endMinute < 10 ? "0" + endMinute : endMinute + ""));
                if (habitIcon == 0) {
                    holder.habitIcon.setImageResource(R.drawable.habit_1_icon);
                } else if (habitIcon == 1) {
                    holder.habitIcon.setImageResource(R.drawable.habit_2_icon);
                } else if (habitIcon == 2) {
                    holder.habitIcon.setImageResource(R.drawable.habit_3_icon);
                } else if (habitIcon == 3) {
                    holder.habitIcon.setImageResource(R.drawable.habit_4_icon);
                } else if (habitIcon == 4) {
                    holder.habitIcon.setImageResource(R.drawable.habit_5_icon);
                } else if (habitIcon == 5) {
                    holder.habitIcon.setImageResource(R.drawable.habit_6_icon);
                } else if (habitIcon == 6) {
                    holder.habitIcon.setImageResource(R.drawable.habit_7_icon);
                } else if (habitIcon == 7) {
                    holder.habitIcon.setImageResource(R.drawable.habit_8_icon);
                } else if (habitIcon == 8) {
                    holder.habitIcon.setImageResource(R.drawable.habit_9_icon);
                } else if (habitIcon == 9) {
                    holder.habitIcon.setImageResource(R.drawable.habit_10_icon);
                } else if (habitIcon == 10) {
                    holder.habitIcon.setImageResource(R.drawable.habit_11_icon);
                } else if (habitIcon == 11) {
                    holder.habitIcon.setImageResource(R.drawable.habit_12_icon);
                } else if (habitIcon == 12) {
                    holder.habitIcon.setImageResource(R.drawable.habit_13_icon);
                } else if (habitIcon == 13) {
                    holder.habitIcon.setImageResource(R.drawable.habit_14_icon);
                } else if (habitIcon == 14) {
                    holder.habitIcon.setImageResource(R.drawable.habit_15_icon);
                } else if (habitIcon == 15) {
                    holder.habitIcon.setImageResource(R.drawable.habit_16_icon);
                } else if (habitIcon == 16) {
                    holder.habitIcon.setImageResource(R.drawable.habit_17_icon);
                } else if (habitIcon == 17) {
                    holder.habitIcon.setImageResource(R.drawable.habit_18_icon);
                }
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HabitSettingActivity.this, AddHabitActivity.class);
                    intent.putExtra("habitInfo", habitItem);
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList == null ? 0 : itemList.size();
        }

        @Override
        public int getItemViewType(int position) {
            int type = itemList == null ? -1 : itemList.get(position).getE_icon_id();
            return type;
        }

        class HabitHold extends RecyclerView.ViewHolder {
            AppCompatImageView habitIcon;
            TextView nameText;
            TextView timeText;
            TableRow layout;
            RelativeLayout relativeLayout;
            int type;

            public HabitHold(@NonNull View itemView, int type) {
                super(itemView);
                if (type > -1) {
                    habitIcon = itemView.findViewById(R.id.habit_icon);
                    nameText = itemView.findViewById(R.id.habit_name);
                    timeText = itemView.findViewById(R.id.habit_time);
                    layout = itemView.findViewById(R.id.item_layout);
                } else {
                    nameText = itemView.findViewById(R.id.add_habit);
                    relativeLayout = itemView.findViewById(R.id.item_layout);
                }
            }
        }

        public void destroy() {
            mContext = null;
            itemList = null;
        }
    }

}
