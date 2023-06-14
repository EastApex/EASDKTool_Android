package com.apex.sdk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apex.bluetooth.callback.AttentionCallback;
import com.apex.bluetooth.callback.EditAttentionCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleRemindRespond;
import com.apex.bluetooth.model.EABleReminder;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.utils.SizeTransform;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ScheduleActivity extends AppCompatActivity {
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.add_event)
    AppCompatImageView eventView;
    @BindView(R.id.event_list)
    SwipeRecyclerView eventList;
    private List<EABleReminder.EABleReminderItem> itemList;
    AlarmListAdapter alarmListAdapter;
    private int delete = -1;
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
                alarmListAdapter.notifyDataSetChanged();
            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(ScheduleActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (delete >= 0) {
                    itemList.remove(delete);
                    alarmListAdapter.notifyItemRemoved(delete);
                    alarmListAdapter.notifyItemRangeChanged(delete, itemList.size());
                }

                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }

            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(ScheduleActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x44) {
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
        setContentView(R.layout.activity_schedule);
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ScheduleActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        eventList.setLayoutManager(linearLayoutManager);
        itemList = new ArrayList<>();
        eventList.addItemDecoration(new DefaultItemDecoration(getColor(android.R.color.transparent), getResources().getDisplayMetrics().widthPixels, (int) SizeTransform.dp2px(2, ScheduleActivity.this)));
        alarmListAdapter = new AlarmListAdapter();
        eventList.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                int width = getResources().getDimensionPixelSize(R.dimen.adapter_recyclerview_child_high);
                int height = getResources().getDimensionPixelOffset(R.dimen.row_height);
                SwipeMenuItem deleteItem = new SwipeMenuItem(ScheduleActivity.this).setBackground(R.color.dialog_submit_txt_color)
                        .setText(getString(R.string.Delete))
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                rightMenu.addMenuItem(deleteItem);
            }
        });
        eventList.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                menuBridge.closeMenu();
                int direction = menuBridge.getDirection();
                int menuPosition = menuBridge.getPosition();
                if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(ScheduleActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    delete = adapterPosition;
                    EABleReminder eaBleReminder = new EABleReminder();
                    eaBleReminder.setE_ops(EABleReminder.ReminderOps.del);
                    eaBleReminder.setId(itemList.get(adapterPosition).getId());
                    EABleManager.getInstance().setReminderOrder(eaBleReminder, new EditAttentionCallback() {
                        @Override
                        public void editResult(EABleRemindRespond eaBleRemindRespond) {
                            if (eaBleRemindRespond.getRemindRespondOps() == EABleRemindRespond.RemindRespondResult.success) {
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x42);
                                }
                            } else {
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(0x43);
                                }
                            }

                        }

                        @Override
                        public void mutualFail(int errorCode) {
                            mHandler.sendEmptyMessage(0x43);
                        }
                    });
                }

            }
        });
        eventList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                EABleReminder.EABleReminderItem remindEvent = itemList.get(adapterPosition);
                if (remindEvent != null) {
                    Intent intent = new Intent(ScheduleActivity.this, EditScheduleActivity.class);
                    intent.putExtra("alarmSwitch", remindEvent.getSw());
                    intent.putExtra("alarmId", remindEvent.getId());
                    intent.putExtra("lazyTime", remindEvent.getSleep_duration());
                    intent.putExtra("alarmMothed", remindEvent.getE_action().getValue());
                    intent.putExtra("secondSwitch", remindEvent.getSec_sw());
                    intent.putExtra("cycleTime", remindEvent.getWeek_cycle_bit());
                    intent.putExtra("customContent", remindEvent.getContent());
                    intent.putExtra("year", remindEvent.getYear());
                    intent.putExtra("month", remindEvent.getMonth());
                    intent.putExtra("day", remindEvent.getDay());
                    intent.putExtra("hour", remindEvent.getHour());
                    intent.putExtra("minute", remindEvent.getMinute());
                    intent.putExtra("alarmType", remindEvent.getE_type().getValue());
                    startActivity(intent);
                }
            }
        });
        eventList.setAdapter(alarmListAdapter);


        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
                if (state == EABleConnectState.STATE_CONNECTED) {
                    startActivity(new Intent(ScheduleActivity.this, AddScheduleActivity.class));
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
        if (state == EABleConnectState.STATE_CONNECTED) {
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(ScheduleActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.reminder, new AttentionCallback() {
                @Override
                public void attentionInfo(EABleReminder eaBleReminder) {
                    if (eaBleReminder != null) {
                        List<EABleReminder.EABleReminderItem> indexList = eaBleReminder.getS_index();
                        if (indexList != null && !indexList.isEmpty()) {
                            itemList.clear();

                            for (EABleReminder.EABleReminderItem index : indexList) {
                                EABleReminder.ReminderType reminderType = index.getE_type();
                                if (reminderType != EABleReminder.ReminderType.alarm) {
                                    itemList.add(index);

                                }
                            }
                        }
                    }
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x40);
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
        itemList = null;

        super.onDestroy();
    }

    public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHold> {

        @NonNull
        @Override
        public ViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.adapter_alarm_recyclerview, parent, false);
            return new ViewHold(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHold holder, int position) {
            if (itemList == null || itemList.isEmpty()) {
                return;
            }
            final EABleReminder.EABleReminderItem eaBleReminderItem = itemList.get(position);
            if (TextUtils.isEmpty(eaBleReminderItem.getContent())) {
                if (eaBleReminderItem.getE_type() == EABleReminder.ReminderType.sleep) {
                    holder.nameText.setText(getString(R.string.sleep));
                } else if (eaBleReminderItem.getE_type() == EABleReminder.ReminderType.meeting) {
                    holder.nameText.setText(getString(R.string.meeting));
                } else if (eaBleReminderItem.getE_type() == EABleReminder.ReminderType.medicine) {
                    holder.nameText.setText(getString(R.string.takeTheMedicine));
                } else if (eaBleReminderItem.getE_type() == EABleReminder.ReminderType.drink) {
                    holder.nameText.setText(getString(R.string.drinking));
                } else if (eaBleReminderItem.getE_type() == EABleReminder.ReminderType.sport) {
                    holder.nameText.setText(getString(R.string.sport));
                } else {
                    holder.nameText.setText(getString(R.string.custom));
                }

            } else {
                holder.nameText.setText(eaBleReminderItem.getContent());
            }
            holder.aSwitch.setOnCheckedChangeListener(null);
            String remindTime = "";
            byte cycle = (byte) eaBleReminderItem.getWeek_cycle_bit();
            if (cycle == 0x7F) {
                remindTime += (getString(R.string.every_day) + eaBleReminderItem.getHour() + ":" + eaBleReminderItem.getMinute());
            } else {
                String timeString = Integer.toBinaryString(cycle & 0xFF);
                if (timeString.length() < 8) {
                    int size = 8 - timeString.length();
                    for (int i = 0; i < size; i++) {
                        timeString = ("0" + timeString);
                    }
                }
                String sundayTag = timeString.substring(7, timeString.length());
                String mondayTag = timeString.substring(6, 7);
                String tuesdayTag = timeString.substring(5, 6);
                String wednesdayTag = timeString.substring(4, 5);
                String thursdayTag = timeString.substring(3, 4);
                String fridayTag = timeString.substring(2, 3);
                String saturday = timeString.substring(1, 2);
                if (sundayTag.equalsIgnoreCase("1")) {
                    remindTime += (getString(R.string.sun) + ",");
                }
                if (mondayTag.equalsIgnoreCase("1")) {
                    remindTime += (getString(R.string.mon) + ",");
                }
                if (tuesdayTag.equalsIgnoreCase("1")) {
                    remindTime += (getString(R.string.tue) + ",");
                }
                if (wednesdayTag.equalsIgnoreCase("1")) {
                    remindTime += (getString(R.string.wed) + ",");
                }
                if (thursdayTag.equalsIgnoreCase("1")) {
                    remindTime += (getString(R.string.thur) + ",");
                }
                if (fridayTag.equalsIgnoreCase("1")) {
                    remindTime += (getString(R.string.fri) + ",");
                }
                if (saturday.equalsIgnoreCase("1")) {
                    remindTime += (getString(R.string.sat) + ",");
                } else {

                }
                if (!TextUtils.isEmpty(remindTime)) {
                    remindTime = (remindTime.substring(0, remindTime.length() - 1) + " ");
                }
                int hour = eaBleReminderItem.getHour();
                int minute = eaBleReminderItem.getMinute();
                remindTime += ((hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute));
            }
            holder.timeText.setText(remindTime);
            holder.aSwitch.setChecked(eaBleReminderItem.getSw() > 0 ? true : false);
            holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int layoutPosition = holder.getLayoutPosition();
                    if (layoutPosition == position) {
                        eaBleReminderItem.setSw(isChecked ? 1 : 0);
                        EABleReminder eaBleReminder = new EABleReminder();
                        eaBleReminder.setE_ops(EABleReminder.ReminderOps.edit);
                        eaBleReminder.setId(eaBleReminderItem.getId());
                        List<EABleReminder.EABleReminderItem> indexList = new ArrayList<>();
                        indexList.add(eaBleReminderItem);
                        eaBleReminder.setS_index(indexList);
                        if (waitingDialog == null) {
                            waitingDialog = new WaitingDialog(ScheduleActivity.this);
                        }
                        if (!waitingDialog.isShowing()) {
                            waitingDialog.show();
                        }
                        EABleManager.getInstance().setReminderOrder(eaBleReminder, new EditAttentionCallback() {
                            @Override
                            public void editResult(EABleRemindRespond eaBleRemindRespond) {
                                if (eaBleRemindRespond.getRemindRespondOps() != EABleRemindRespond.RemindRespondResult.success) {
                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(0x43);
                                    }
                                } else {
                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(0x44);
                                    }
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
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return itemList == null ? 0 : itemList.size();
        }


        class ViewHold extends RecyclerView.ViewHolder {
            @BindView(R.id.event_name)
            TextView nameText;
            @BindView(R.id.remind_time)
            TextView timeText;
            @BindView(R.id.event_switch)
            Switch aSwitch;

            public ViewHold(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

    }

}
