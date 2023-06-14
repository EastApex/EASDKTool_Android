package com.apex.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.apex.sdk.R;
import com.apex.sdk.utils.SizeTransform;


public class WaitingDialog extends Dialog {
    public WaitingDialog(@NonNull Context context) {
        super(context, R.style.waiteDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_waite, null, false);
        setContentView(dialogView);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.setLayout((int) SizeTransform.dp2px(200, getContext()), (int) SizeTransform.dp2px(100, getContext()));
        window.setBackgroundDrawableResource(R.color.frame_color);

    }

}
