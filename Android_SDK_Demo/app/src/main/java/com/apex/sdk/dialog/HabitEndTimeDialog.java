package com.apex.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.apex.sdk.R;
import com.contrarywind.adapter.WheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;

import java.util.Arrays;
import java.util.Calendar;

public class HabitEndTimeDialog extends Dialog {
    private int[] hour = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
    private int[] minute = new int[60];
    private BirthdayTimeListener birthdayTimeListener;
    private int disturbHour;
    private int disturbMinute;
    private String title;
    private Calendar calendar;
    private WheelView selectYear, selectMonth;
    private TextView titleView;
    private AppCompatButton submit, cancel;

    public HabitEndTimeDialog(@NonNull Context context, @NonNull String title) {
        super(context, R.style.waiteDialog);
        this.title = title;
        for (int i = 0; i < minute.length; i++) {
            minute[i] = i;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_habit_end, null, false);
        setContentView(view);
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);

        }
        titleView = view.findViewById(R.id.title);
        titleView.setText(title);
        selectYear = view.findViewById(R.id.select_year);
        //  selectYear.setLabel(getContext().getString(R.string.hour));
        selectMonth = view.findViewById(R.id.select_month);
        //   selectMonth.setLabel(getContext().getString(R.string.min));
        initWheel(selectYear, disturbHour);
        initWheel(selectMonth, disturbMinute);
        selectYear.setAdapter(new WheelAdapter() {
            @Override
            public int getItemsCount() {
                return hour.length;
            }

            @Override
            public Object getItem(int index) {
                return hour[index];
            }

            @Override
            public int indexOf(Object o) {
                return Arrays.asList(new int[hour.length]).indexOf(o);
            }
        });
        selectMonth.setAdapter(new WheelAdapter() {
            @Override
            public int getItemsCount() {
                return minute.length;
            }

            @Override
            public Object getItem(int index) {
                return minute[index];
            }

            @Override
            public int indexOf(Object o) {
                return Arrays.asList(new int[minute.length]).indexOf(o);
            }
        });
        selectYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                disturbHour = index;
            }
        });
        selectMonth.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                disturbMinute = index;
            }
        });
        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (birthdayTimeListener != null) {
                    birthdayTimeListener.birthdayTime(hour[disturbHour], minute[disturbMinute]);
                }
                // dismiss();
            }
        });
        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (birthdayTimeListener != null) {
                    birthdayTimeListener.previous();
                }
                // dismiss();
            }
        });
    }

    public void setBirthdayTimeListener(@NonNull BirthdayTimeListener birthdayTimeListener) {
        this.birthdayTimeListener = birthdayTimeListener;
    }

    public void showDialog() {
        show();
        calendar = Calendar.getInstance();
        int tempHour = calendar.get(Calendar.HOUR_OF_DAY);
        int tempMinute = calendar.get(Calendar.MINUTE);
        for (int i = 0; i < hour.length; i++) {
            if (tempHour == hour[i]) {
                disturbHour = i;
                break;
            }
        }
        for (int i = 0; i < minute.length; i++) {
            if (tempMinute == minute[i]) {
                disturbMinute = i;
                break;
            }
        }
        selectYear.setCurrentItem(disturbHour);
        selectMonth.setCurrentItem(disturbMinute);
    }

    private void initWheel(@NonNull WheelView wheelView, int index) {
        wheelView.setCyclic(false);
        wheelView.setGravity(Gravity.CENTER);
        wheelView.setDividerColor(Color.TRANSPARENT);
        // wheelView.setDividerType(WheelView.DividerType.FILL);
        wheelView.setItemsVisibleCount(5);
        wheelView.setCurrentItem(index);
        wheelView.setTextColorOut(getContext().getResources().getColor(R.color.dialog_outSide_color));
        wheelView.setTextSize(18);
        wheelView.setTextColorCenter(getContext().getResources().getColor(R.color.person_info_title_color));
        wheelView.setTextXOffset(14);
        // wheelView.setDividerWidth(1);
        wheelView.setLineSpacingMultiplier(2);
        ;
    }

    public void destroyDialog() {
        hour = null;
        minute = null;
        selectYear.setAdapter(null);
        selectMonth.setAdapter(null);
        selectYear.setOnItemSelectedListener(null);
        selectMonth.setOnItemSelectedListener(null);
        calendar = null;
        birthdayTimeListener = null;

    }

    public interface BirthdayTimeListener {
        void birthdayTime(int hour, int minute);

        void previous();
    }
}
