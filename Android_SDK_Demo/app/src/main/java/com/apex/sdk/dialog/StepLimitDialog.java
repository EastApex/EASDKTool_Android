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

public class StepLimitDialog extends Dialog {
    private int[] ageData = new int[]{20, 50, 100, 150, 160, 170, 180, 190};
    private WheelView wheelView;
    private AppCompatButton cancel, submit;
    private SelectListener selectListener;
    private TextView titleText;
    int currentIndex;



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
        titleText.setText(getContext().getString(R.string.steps));
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
                return ageData.length;
            }

            @Override
            public Object getItem(int index) {
                return ageData[index];
            }

            @Override
            public int indexOf(Object o) {
                return Arrays.asList(new int[ageData.length]).indexOf(o);
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
                    selectListener.selectData(ageData[currentIndex]);
                }

            }
        });
    }

    public interface SelectListener {
        void selectData(int sex);
    }

    public void destroyDialog() {
        ageData = null;
        selectListener = null;
        wheelView.setAdapter(null);
        wheelView.setOnItemSelectedListener(null);
    }
    public StepLimitDialog(@NonNull Context context) {
        super(context, R.style.waiteDialog);
    }
}
