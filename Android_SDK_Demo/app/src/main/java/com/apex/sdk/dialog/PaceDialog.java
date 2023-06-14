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

public class PaceDialog extends Dialog {
    private int[] paceData = new int[]{5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};
    private WheelView wheelView;
    private AppCompatButton cancel, submit;
    private SelectListener selectListener;
    private TextView titleText;
    int currentIndex;

    public PaceDialog(@NonNull Context context) {
        super(context, R.style.waiteDialog);
    }

    public void setSelectListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sex, null, false);
        setContentView(dialogView);
        titleText = dialogView.findViewById(R.id.title);
        titleText.setText(getContext().getString(R.string.pace));
        wheelView = dialogView.findViewById(R.id.select_year);
        wheelView.setCyclic(false);
        wheelView.setGravity(Gravity.CENTER);
        wheelView.setDividerColor(Color.TRANSPARENT);
        wheelView.setItemsVisibleCount(3);
        wheelView.setCurrentItem(0);
        wheelView.setTextColorOut(getContext().getResources().getColor(R.color.dialog_outSide_color));
        wheelView.setTextSize(18);
        wheelView.setTextColorCenter(getContext().getResources().getColor(R.color.person_info_title_color));
        wheelView.setTextXOffset(14);
        wheelView.setLineSpacingMultiplier(2);
        wheelView.setAdapter(new WheelAdapter() {
            @Override
            public int getItemsCount() {
                return paceData.length;
            }

            @Override
            public Object getItem(int index) {
                return paceData[index];
            }

            @Override
            public int indexOf(Object o) {
                return Arrays.asList(new int[paceData.length]).indexOf(o);
            }
        });
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                currentIndex = index;

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
                    selectListener.selectData(paceData[currentIndex]);
                }

            }
        });
    }

    public interface SelectListener {
        void selectData(int sex);
    }

    public void destroyDialog() {
        paceData = null;
        selectListener = null;
        wheelView.setAdapter(null);
        wheelView.setOnItemSelectedListener(null);
    }
}
