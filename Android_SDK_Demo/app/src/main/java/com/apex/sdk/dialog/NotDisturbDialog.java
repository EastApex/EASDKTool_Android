package com.apex.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.apex.sdk.R;
import com.contrarywind.adapter.WheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;

import java.util.Arrays;

public class NotDisturbDialog extends Dialog {
    private int[] hour = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,14,15,16,17,18,19,20,21,22,23};
    private int[] minute = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
    private SelectListener selectListener;
    private int dHour;
    private int dMinute;
    private String title;
    private WheelView selectHour, selectMinute;
    private TextView titleText;
    private AppCompatButton submit, cancel;

    public NotDisturbDialog(@NonNull Context context, @NonNull String title) {
        super(context, R.style.waiteDialog);
        this.title = title;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_disturb, null, false);
        setContentView(dialogView);
        titleText = dialogView.findViewById(R.id.title);
        titleText.setText(title);
        selectHour = dialogView.findViewById(R.id.select_hour);
        selectHour.setCyclic(false);
        selectHour.setGravity(Gravity.CENTER);
        selectHour.setDividerColor(Color.TRANSPARENT);
        selectHour.setItemsVisibleCount(3);
        selectHour.setCurrentItem(0);
        selectHour.setTextColorOut(getContext().getResources().getColor(R.color.dialog_outSide_color));
        selectHour.setTextSize(18);
        selectHour.setTextColorCenter(getContext().getResources().getColor(R.color.person_info_title_color));
        selectHour.setTextXOffset(14);
        selectHour.setLineSpacingMultiplier(2);
        selectMinute = dialogView.findViewById(R.id.select_minute);
        selectMinute.setCyclic(false);
        selectMinute.setGravity(Gravity.CENTER);
        selectMinute.setDividerColor(Color.TRANSPARENT);
        selectMinute.setItemsVisibleCount(3);
        selectMinute.setCurrentItem(0);
        selectMinute.setTextColorOut(getContext().getResources().getColor(R.color.dialog_outSide_color));
        selectMinute.setTextSize(18);
        selectMinute.setTextColorCenter(getContext().getResources().getColor(R.color.person_info_title_color));
        selectMinute.setTextXOffset(14);
        selectMinute.setLineSpacingMultiplier(2);
        selectHour.setAdapter(new WheelAdapter() {
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
        selectHour.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                dHour = index;

            }
        });
        selectMinute.setAdapter(new WheelAdapter() {
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
        selectMinute.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                dMinute = index;
            }
        });
        cancel = dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        submit = dialogView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (selectListener != null) {
                    selectListener.selectData(hour[dHour], minute[dMinute]);
                }

            }
        });
    }

    public void setSelectListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public void destroyDialog() {
        hour = null;
        minute = null;
        selectHour.setAdapter(null);
        selectMinute.setAdapter(null);
        selectMinute.setOnItemSelectedListener(null);
        selectHour.setOnItemSelectedListener(null);
        selectListener = null;

    }


    public interface SelectListener {
        void selectData(int hour, int minute);
    }
}
